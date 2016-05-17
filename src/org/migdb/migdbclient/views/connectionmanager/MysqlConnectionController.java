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

public class MysqlConnectionController implements Initializable {

	@FXML
	private AnchorPane rootLayoutAnchorpane;
	@FXML
	private Label backLabel;

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// back label click event
		backLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				back();
			}
		});
	}

	public void back() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.SETUPNEWDBCONNECTION.getPath()));
			AnchorPane connection = loader.load();
			rootLayoutAnchorpane.getChildren().clear();
			rootLayoutAnchorpane.getChildren().add(connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
