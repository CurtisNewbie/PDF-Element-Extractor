package com.curtisnewbie.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.awt.Desktop;
import java.awt.image.*;

import com.curtisnewbie.io.IOManager;
import com.curtisnewbie.main.App;
import com.curtisnewbie.main.LoggerProducer;
import com.curtisnewbie.pdfprocess.PdfProcessor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * 
 * <p>
 * Controller in MVC that is responsible for managing interaction with and
 * updating UI
 * </p>
 * 
 */
public class Controller implements Initializable {

    final Logger logger = LoggerProducer.getLogger(Controller.class.getName());

    @FXML
    private Button chooseFileBtn;

    @FXML
    private Button chooseDirBtn;

    @FXML
    private TextField pathFromTextField;

    @FXML
    private TextField pathToTextField;

    @FXML
    private Button extractAllBtn;

    @FXML
    private TreeView<String> outputTreeView;

    @FXML
    private Button openDirBtn;

    /** Currently used pdfProcessor */
    private PdfProcessor pdfProcessor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.registerChooseFileEventHandler();
        this.registerChooseDirEventHandler();
        this.registerExtractAllEventHandler();
        this.registerOpenDirEventHandler();
    }

    /**
     * Register event handler for chooseFileBtn
     */
    public void registerChooseFileEventHandler() {
        this.chooseFileBtn.setOnAction(e -> {
            logger.info("Choosing pdf file");
            File selectedFile = useFileChooser("Select PDF file");
            if (selectedFile != null) {
                setFromPath(selectedFile.getAbsolutePath());
            }
        });
    }

    /**
     * Register event handler for chooseDirBtn
     */
    public void registerChooseDirEventHandler() {
        this.chooseDirBtn.setOnAction(e -> {
            logger.info("Choosing directory");
            File selectedFile = useDirChooser("Select directory/folder");
            if (selectedFile != null) {
                setToPath(selectedFile.getAbsolutePath());
            }
        });
    }

    /**
     * Register event handler for openDirBtn
     */
    public void registerOpenDirEventHandler() {
        this.openDirBtn.setOnAction(e -> {
            logger.info("Opening directory");
            var dirPath = getToPath();
            var file = new File(dirPath);
            if (Desktop.isDesktopSupported() && dirPath != null && dirPath.length() > 0 && file.exists()) {
                new Thread(() -> {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(file);
                    } catch (Exception excep) {
                        showError("Failed to open directory.");
                    }
                }).start();
            }
        });
    }

    /**
     * Register event handler for extractAllBtn
     */
    public void registerExtractAllEventHandler() {
        this.extractAllBtn.setOnAction(e -> {
            logger.info("Extracting all elements");
            var from = getFromPath();
            var to = getToPath();
            if (from != null && to != null) {
                try {
                    // create pdf representation
                    var document = IOManager.readPdfFile(getFromPath());
                    this.pdfProcessor = new PdfProcessor(document);

                    // init treeview
                    var rootNode = setRootToOutputTreeView("ExtractedFiles");
                    var textNode = new TreeItem<String>("Extracted Text:");
                    addChildToParent(rootNode, textNode);
                    var imgNode = new TreeItem<String>("Extracted Images:");
                    addChildToParent(rootNode, imgNode);

                    // extract and update using multi-threading and async
                    try {
                        extractAllText(pdfProcessor).thenCompose(allText -> {
                            return writeAllTextFiles(allText, to);
                        }).thenAccept((pathsToTextFiles) -> {
                            for (var p : pathsToTextFiles) {
                                addChildToParent(textNode, new TreeItem<String>(p));
                            }
                            showInfo("Text Files Extraction Completed");
                        });
                    } catch (Exception ex) {
                        showError(
                                "Error occured while displaying extracted text files, they may have aleady been created in your specified directory.");
                    }
                    try {
                        extractAllImages(pdfProcessor).thenCompose(allImages -> {
                            return writeAllImgFiles(allImages, to);
                        }).thenAccept(pathsOfImg -> {
                            for (var p : pathsOfImg) {
                                addChildToParent(imgNode, new TreeItem<String>(p));
                            }
                            showInfo("Images Extraction Completed");
                        });
                    } catch (Exception ex) {
                        showError(
                                "Error occured while displaying extracted images, they may have aleady been created in your specified directory.");
                    }
                } catch (Exception excep) {
                    showError(excep.getMessage());
                }
            }
        });

    }

    /**
     * Get text in pathFromTextField
     * 
     * @return text
     */
    public String getFromPath() {
        return this.pathFromTextField.getText();
    }

    /**
     * Get text in pathToTextField
     * 
     * @return text
     */
    public String getToPath() {
        return this.pathToTextField.getText();
    }

    /**
     * Set text in pathFromTextField
     * 
     * @param text
     */
    public void setFromPath(String text) {
        this.pathFromTextField.setText(text);
    }

    /**
     * Set text in pathToTextField
     * 
     * @param text
     */
    public void setToPath(String text) {
        this.pathToTextField.setText(text);
    }

    /**
     * Add a root node to outputTreeView. Any operation on returned root node should
     * only be undertaken by method {@link Controller#addChildernToParentNode}
     * 
     * @param root name of the root node
     * @return root node
     */
    public TreeItem<String> setRootToOutputTreeView(String root) {
        TreeItem<String> rootNode = new TreeItem<String>(root);
        rootNode.setExpanded(true);
        Platform.runLater(() -> {
            this.outputTreeView.setRoot(rootNode);
        });
        return rootNode;
    }

    /**
     * Add a child node to a parent node in outputTreeView
     * 
     * @param parentNode parent node
     * @param childNode  child nodes
     */
    public void addChildToParent(TreeItem<String> parentNode, TreeItem<String> childNode) {
        synchronized (parentNode) {
            Platform.runLater(() -> {
                parentNode.setExpanded(true);
                var childrenNodes = parentNode.getChildren();
                childrenNodes.add(childNode);
            });
        }
    }

    /**
     * Create a FileChooser for selecting file
     * 
     * @param title title of the created FileChooser
     * @return selected file
     */
    public File useFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        File selectedFile = fileChooser.showOpenDialog(App.getPrimaryStage());
        return selectedFile;
    }

    /**
     * Create a DirectoryChooser for selecting a directory
     * 
     * @param title title of the created DirectoryChooser
     * @return selected directory
     */
    public File useDirChooser(String title) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(title);
        File selectedDir = dirChooser.showDialog(App.getPrimaryStage());
        return selectedDir;
    }

    /**
     * Extract all text in the pdf file
     * 
     * @param processor PdfProcessor
     * @return a list of String, where each represents all the text in the page
     */
    private CompletableFuture<List<String>> extractAllText(PdfProcessor processor) {
        CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();
        synchronized (processor) {
            new Thread(() -> {
                var res = processor.extractText(1);
                completableFuture.complete(res);
            }).start();
        }
        return completableFuture;
    }

    /**
     * Extract all images in the pdf file
     * 
     * @param processor PdfProcessor
     * @return a list of buffered images
     */
    private CompletableFuture<List<BufferedImage>> extractAllImages(PdfProcessor processor) {
        CompletableFuture<List<BufferedImage>> completableFuture = new CompletableFuture<>();
        synchronized (processor) {
            new Thread(() -> {
                var res = processor.extractImages();
                completableFuture.complete(res);
            }).start();
        }
        return completableFuture;
    }

    /**
     * Write all text to a specified directory, this method takes advantage of
     * multi-threading
     * 
     * @param allText a list of text
     * @param to      an absolute path to a directory
     * @return a list of absolute paths of these text files
     */
    private CompletableFuture<List<String>> writeAllTextFiles(List<String> allText, String to) {
        CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();
        new Thread(() -> {
            List<String> paths = new ArrayList<>();
            if (allText != null) {
                int count = 0;
                for (var txt : allText) {
                    try {
                        var path = IOManager.writeElementToFile(to, txt, "page" + (count++) + ".txt");
                        paths.add(path);
                    } catch (Exception e) {
                        logger.severe(e.getMessage());
                    }
                }
            }
            completableFuture.complete(paths);
        }).start();
        return completableFuture;
    }

    /**
     * Write all images to a specified directory, this method takes advantage of
     * multi-threading
     * 
     * @param allImages a list of buffered images
     * @param to        an absolute path to a directory
     * @return a list of absolute paths of these images
     */
    private CompletableFuture<List<String>> writeAllImgFiles(List<BufferedImage> allImages, String to) {
        CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();
        new Thread(() -> {
            List<String> paths = new ArrayList<>();
            if (allImages != null) {
                int count = 0;
                for (var img : allImages) {
                    try {
                        var path = IOManager.writeElementToFile(to, img, "img" + (count++));
                        paths.add(path);
                    } catch (Exception e) {
                        logger.severe(e.getMessage());
                    }
                }
            }
            completableFuture.complete(paths);
        }).start();
        return completableFuture;
    }

    private void showError(String msg) {
        Platform.runLater(() -> {
            var alert = new Alert(AlertType.ERROR);
            alert.setContentText(msg);
            alert.show();
        });
    }

    private void showInfo(String msg) {
        Platform.runLater(() -> {
            var alert = new Alert(AlertType.INFORMATION);
            alert.setContentText(msg);
            alert.show();
        });
    }
}