package org.migdb.migdbclient.main;

import java.io.IOException;

import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.config.ImagePath;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private StackPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("MigDB");
		this.primaryStage.getIcons().add(new Image(ImagePath.FAVICON.getPath()));
		initRootLayout();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Initialize method for RootLayout
	 */
	public void initRootLayout() {

		try {

			//Load the root layout from the fxml file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.ROOTLAYOUT.getPath()));
			rootLayout = loader.load();

			//Show the scene containing root layout
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
