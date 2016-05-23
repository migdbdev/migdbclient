package org.migdb.migdbclient.views.root;

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

public class RootLayoutController implements Initializable {

	@FXML
	private AnchorPane rootContainerAncpane;
	@FXML
	private AnchorPane sideBarAnchorpane;
	@FXML
	private Label datamanagerLabel;
	@FXML
	private Label connectionManagerLabel;
	@FXML
	private Label modificationEvaluatorLabel;

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	public void initialize(URL location, ResourceBundle resources) {

		// Data manager navigation label click event
		datamanagerLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				showDataManager();
			}
		});

		// Connection manager navigation label click event
		connectionManagerLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				showConnectionManager();
			}
		});

		// Connection manager navigation label click event
		modificationEvaluatorLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				showModificationEvaluator();
			}
		});
	}

	/**
	 * Method for add connection manager layout to the root container anchor
	 * pane
	 */
	public void showConnectionManager() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.CONNECTIONMANAGER.getPath()));
			AnchorPane connectionManager = loader.load();
			rootContainerAncpane.getChildren().clear();
			rootContainerAncpane.getChildren().add(connectionManager);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for add modification evaluator layout to the root container anchor
	 * pane
	 */
	public void showModificationEvaluator() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.MODIFICATIONEVALUATOR.getPath()));
			AnchorPane modificationEvaluator = loader.load();
			rootContainerAncpane.getChildren().clear();
			rootContainerAncpane.getChildren().add(modificationEvaluator);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for add data manager layout to the root container anchor pane
	 */
	public void showDataManager() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.DATAMANAGER.getPath()));
			AnchorPane dataManager = loader.load();
			rootContainerAncpane.getChildren().clear();
			rootContainerAncpane.getChildren().add(dataManager);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
