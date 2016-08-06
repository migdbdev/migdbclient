package org.migdb.migdbclient.main;

import java.io.File;

import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.config.ImagePath;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainApp extends Application {

	private BorderPane rootLayout;
	private AnchorPane mainAnchorpane = new AnchorPane();
	private VBox splashLayout;
	private VBox progressTextLayout;
	private ProgressBar loadProgress;
	private Label progressText;
	private Stage primaryStage;
	private static final int SPLASH_WIDTH = 520;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void init() {
		ImageView splash = new ImageView(new Image(ImagePath.SPLASHIMAGE.getPath()));
		loadProgress = new ProgressBar();
		loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
		loadProgress.setStyle("-fx-accent: green;");
		progressText = new Label("Will find friends for peanuts . . .");
		progressText.setStyle("-fx-text-fill : white");
		splashLayout = new VBox();
		progressTextLayout = new VBox();
		splashLayout.getChildren().addAll(splash, loadProgress);
		progressTextLayout.getChildren().add(progressText);
		progressText.setAlignment(Pos.CENTER);
		splashLayout.setStyle("-fx-background-color: transparent; ");
		progressTextLayout.setLayoutX(5);
		progressTextLayout.setLayoutY(280);
		mainAnchorpane.getChildren().addAll(splashLayout, progressTextLayout);
		splashLayout.setEffect(new DropShadow());
	}

	@Override
	public void start(final Stage initStage) throws Exception {
		final Task<ObservableList<String>> friendTask = new Task<ObservableList<String>>() {
			@Override
			protected ObservableList<String> call() throws InterruptedException {
				ObservableList<String> foundFriends = FXCollections.<String> observableArrayList();
				ObservableList<String> availableFriends = FXCollections.observableArrayList("java", "javafx", "server",
						"j.lib", "gson", "json", "sql", "mongo", "rt.jar", "maven.lib", "apache", "neural", "request");

				updateMessage("Finding friends . . .");
				for (int i = 0; i < availableFriends.size(); i++) {
					Thread.sleep(400);
					updateProgress(i + 1, availableFriends.size());
					String nextFriend = availableFriends.get(i);
					foundFriends.add(nextFriend);
					updateMessage("Loading . . . migdb " + nextFriend);
				}
				Thread.sleep(400);
				updateMessage("MigDB loaded.");

				return foundFriends;
			}
		};

		showSplash(initStage, friendTask, () -> showMainStage());
		new Thread(friendTask).start();
	}

	private void showMainStage() {
		try {
			// Create application folder in a user's document
			File migDB = new File(FilePath.DOCUMENT.getPath());
			if (!migDB.exists()) {
				migDB.mkdir();
			}

			primaryStage = new Stage(StageStyle.DECORATED);
			primaryStage.setTitle("MigDB");
			primaryStage.getIcons().add(new Image(ImagePath.FAVICON.getPath()));

			// Load the root layout from the fxml file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.ROOTLAYOUT.getPath()));
			rootLayout = loader.load();

			// Show the scene containing root layout
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showSplash(final Stage initStage, Task<?> task, InitCompletionHandler initCompletionHandler) {
		progressText.textProperty().bind(task.messageProperty());
		loadProgress.progressProperty().bind(task.progressProperty());
		task.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				loadProgress.progressProperty().unbind();
				loadProgress.setProgress(1);
				initStage.toFront();
				FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), mainAnchorpane);
				fadeSplash.setFromValue(1.0);
				fadeSplash.setToValue(0.0);
				fadeSplash.setOnFinished(actionEvent -> initStage.hide());
				fadeSplash.play();

				initCompletionHandler.complete();
			} // todo add code to gracefully handle other task states.
		});

		Scene splashScene = new Scene(mainAnchorpane, Color.TRANSPARENT);
		initStage.setScene(splashScene);
		initStage.centerOnScreen();
		/*
		 * initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 -
		 * SPLASH_WIDTH / 2); initStage.setY(bounds.getMinY() +
		 * bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
		 */
		initStage.initStyle(StageStyle.TRANSPARENT);
		initStage.setAlwaysOnTop(true);
		initStage.show();
	}

	public interface InitCompletionHandler {
		void complete();
	}
}
