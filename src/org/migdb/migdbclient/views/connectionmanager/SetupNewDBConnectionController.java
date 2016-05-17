package org.migdb.migdbclient.views.connectionmanager;

import java.net.URL;
import java.util.ResourceBundle;

import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.main.MainApp;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SetupNewDBConnectionController implements Initializable {

	@FXML
	private AnchorPane rootLayoutAnchorpane;
	@FXML
	private Label mysqlLabel;
	@FXML
	private Label mongoLabel;
	
	FXMLLoader loader = new FXMLLoader();

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// MySQL label click event
		mysqlLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				mysqlPopup();
			}
		});

		// Mongo label click event
		mongoLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				mongoPopup();
			}
		});
	}

	// Method for mysql window pop up
	public void mysqlPopup() {
		try {
			loader.setLocation(MainApp.class.getResource(FxmlPath.MYSQLCONNECTION.getPath()));
			AnchorPane mysql = loader.load();
			rootLayoutAnchorpane.getChildren().clear();
			rootLayoutAnchorpane.getChildren().add(mysql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method for mongo window pop up
	public void mongoPopup() {
		try {
			loader.setLocation(MainApp.class.getResource(FxmlPath.MONGOCONNECTION.getPath()));
			AnchorPane mongo = loader.load();
			rootLayoutAnchorpane.getChildren().clear();
			rootLayoutAnchorpane.getChildren().add(mongo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
