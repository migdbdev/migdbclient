package org.migdb.migdbclient.views.connectionmanager;

import java.net.URL;
import java.util.ResourceBundle;

import org.migdb.migdbclient.config.ConnectionManager;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SetupNewDBConnectionController implements Initializable {

	@FXML
	private AnchorPane rootLayoutAnchorpane;
	@FXML
	private AnchorPane mysqlLayoutAnchorpane;
	@FXML
	private AnchorPane mongoLayoutAnchorpane;
	@FXML
	private Label mysqlLabel;
	@FXML
	private Label mongoLabel;
	@FXML
	private Label mysqlBackLabel;
	@FXML
	private Label mongoBackLabel;

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

		// Set visible false when page loaded
		mysqlLayoutAnchorpane.setVisible(false);
		mongoLayoutAnchorpane.setVisible(false);

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

		// Mysql back label click event
		mysqlBackLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				back(mouseevent);
			}
		});

		// Mongo back label click event
		mongoBackLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				back(mouseevent);
			}
		});
	}

	/**
	 * MySQL layout popup method
	 */
	public void mysqlPopup() {
		try {
			rootLayoutAnchorpane.setVisible(false);
			mysqlLayoutAnchorpane.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Mongo layout popup method
	 */
	public void mongoPopup() {
		try {
			rootLayoutAnchorpane.setVisible(false);
			mongoLayoutAnchorpane.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Back to root layout anchor pane from mysql or mongo layout
	 * @param e
	 */
	public void back(Event e) {
		if ((((Node) e.getSource()).getId()).equals(ConnectionManager.MYSQLBACKLABELID.getConnManager())) {
			mysqlLayoutAnchorpane.setVisible(false);
		} else {
			mongoLayoutAnchorpane.setVisible(false);
		}
		rootLayoutAnchorpane.setVisible(true);
	}

}
