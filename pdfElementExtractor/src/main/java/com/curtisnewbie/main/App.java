package com.curtisnewbie.main;

import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * <p>
 * JavaFX Application class
 * </p>
 */
public class App extends Application {

    static final Logger logger = LoggerProducer.getLogger(App.class.getName());
    static Stage primaryStage;

    private Parent root;

    @Override
    public void init() throws Exception {
        // get resources
        ClassLoader classLoader = this.getClass().getClassLoader();
        var in = classLoader.getResourceAsStream("ui.fxml");
        // load fxml
        FXMLLoader fxmlLoader = new FXMLLoader();
        root = (Parent) fxmlLoader.load(in);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        App.primaryStage = primaryStage;

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1060);
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    public static void main(String... args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
