package com.curtisnewbie.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.curtisnewbie.io.IOManager;
import com.curtisnewbie.main.App;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.registerChooseFileEventHandler();
        this.registerChooseDirEventHandler();
        this.registerExtractAllEventHandler();
    }

    /**
     * Register event handler for chooseFileBtn
     */
    public void registerChooseFileEventHandler() {
        this.chooseFileBtn.setOnAction(e -> {
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
            File selectedFile = useDirChooser("Select directory/folder");
            if (selectedFile != null) {
                setToPath(selectedFile.getAbsolutePath());
            }
        });
    }

    /**
     * Register event handler for extractAllBtn
     */
    public void registerExtractAllEventHandler() {
        this.extractAllBtn.setOnAction(e -> {
            var from = getFromPath();
            var to = getToPath();
            if (from != null && to != null) {
                Platform.runLater(() -> {
                    PdfProcessor pdfProcessor = null;
                    try {
                        var document = IOManager.readPdfFile(getFromPath());
                        pdfProcessor = new PdfProcessor(document);

                        var rootNode = setRootToOutputTreeView("ExtractedFiles");
                        var textNode = new TreeItem<String>("Text:");
                        addChildToParent(rootNode, textNode);

                        var allText = pdfProcessor.extractText(1);
                        var allImages = pdfProcessor.extractImages();

                        if (allText != null) {
                            int count = 0;
                            for (var txt : allText) {
                                var path = IOManager.writeElementToFile(to, txt, "page" + (count++) + ".txt");
                                addChildToParent(textNode, new TreeItem<String>(path));
                            }
                        }

                        var imgNode = new TreeItem<String>("Images:");
                        addChildToParent(rootNode, imgNode);

                        if (allImages != null) {
                            int count = 0;
                            for (var img : allImages) {
                                var path = IOManager.writeElementToFile(to, img, "img" + (count++));
                                addChildToParent(imgNode, new TreeItem<String>(path));
                            }
                        }
                    } catch (Exception excep) {
                        var alert = new Alert(AlertType.ERROR);
                        alert.setContentText(excep.getMessage());
                        alert.showAndWait();
                    } finally {
                        if (pdfProcessor != null)
                            pdfProcessor.close();
                    }
                });
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
}