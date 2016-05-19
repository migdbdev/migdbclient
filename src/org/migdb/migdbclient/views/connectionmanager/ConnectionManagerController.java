package org.migdb.migdbclient.views.connectionmanager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.main.MainApp;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ConnectionManagerController implements Initializable {

	private AnchorPane rootLayoutAnchorpane;
	@FXML
	private Label addConnectionLabel;

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Add connection label click event
		addConnectionLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				createNewConnectionPopup();
			}
		});

	}

	/**
	 * Create new connection manager pop up window method
	 */
	public void createNewConnectionPopup() {

		try {
			Stage newConnectionStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.NEWDBCONNECTION.getPath()));
			rootLayoutAnchorpane = loader.load();
			newConnectionStage.setTitle("Create a new connection");
			newConnectionStage.setAlwaysOnTop(true);
			newConnectionStage.setResizable(false);
			newConnectionStage.centerOnScreen();
			Scene scene = new Scene(rootLayoutAnchorpane);
			newConnectionStage.setScene(scene);
			newConnectionStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
