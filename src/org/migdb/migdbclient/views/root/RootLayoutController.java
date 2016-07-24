package org.migdb.migdbclient.views.root;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.config.ImagePath;
import org.migdb.migdbclient.controllers.dbconnector.MongoConnManager;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.resources.CenterLayout;
import org.migdb.migdbclient.resources.ConnectionParameters;
import org.migdb.migdbclient.resources.MongoDBResource;
import org.migdb.migdbclient.resources.LayoutInstance;
import org.migdb.migdbclient.views.mongodatamanager.MongoDataManager;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
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
	@FXML
	private Label queryConverterLabel;
	@FXML
	private Label queryGeneratorLabel;
	@FXML
	private ListView<String> mongoDatabaseList;
	@FXML
	private ContextMenu mongoDatabaseContextMenu;

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	public void initialize(URL location, ResourceBundle resources) {

		CenterLayout.INSTANCE.setRoot(rootContainerAncpane);
		LayoutInstance.INSTANCE.setSidebar(sideBarAnchorpane);
		
		connectionManagerLabel.setGraphic(new ImageView(ImagePath.TABDBCONNECTION.getPath()));
		modificationEvaluatorLabel.setGraphic(new ImageView(ImagePath.TABMIGRATION.getPath()));
		queryConverterLabel.setGraphic(new ImageView(ImagePath.TABCONVERTER.getPath()));
		queryGeneratorLabel.setGraphic(new ImageView(ImagePath.TABGENERATOR.getPath()));
		datamanagerLabel.setGraphic(new ImageView(ImagePath.TABDATAMANAGER.getPath()));
		
		
		showConnectionManager();

//		ObservableList<String> list;
//		try {
//			list = FXCollections.observableArrayList(getDatabaseNames());
//			mongoDatabaseList.setItems(list);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

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

		// Query converter navigation label click event
		queryConverterLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				showQueryConverter();
			}
		});

		// Query generator navigation label click event
		queryGeneratorLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				showQueryGenerator();
			}
		});
	}

	/**
	 * Method for add connection manager layout to the root container anchor
	 * pane
	 */
	public void showConnectionManager() {
		try {
			AnchorPane root;
			root = CenterLayout.INSTANCE.getRootContainer();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.CONNECTIONMANAGER.getPath()));
			AnchorPane connectionManager = loader.load();
			root.getChildren().clear();
			root.getChildren().add(connectionManager);

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
			AnchorPane root;
			root = CenterLayout.INSTANCE.getRootContainer();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.MODIFICATIONEVALUATOR.getPath()));
			AnchorPane modificationEvaluator = loader.load();
			root.getChildren().clear();
			root.getChildren().add(modificationEvaluator);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for add data manager layout to the root container anchor pane
	 */
	public void showDataManager() {
		try {
			AnchorPane root;
			root = CenterLayout.INSTANCE.getRootContainer();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.DATAMANAGER.getPath()));
			AnchorPane dataManager = loader.load();
			root.getChildren().clear();
			root.getChildren().add(dataManager);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for add query converter layout to the root container anchor pane
	 */
	public void showQueryConverter() {
		try {
			AnchorPane root;
			root = CenterLayout.INSTANCE.getRootContainer();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.QUERYCONVERTER.getPath()));
			AnchorPane queryConverter = loader.load();
			root.getChildren().clear();
			root.getChildren().add(queryConverter);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for add query generator layout to the root container anchor pane
	 */
	public void showQueryGenerator() {
		try {
			AnchorPane root;
			root = CenterLayout.INSTANCE.getRootContainer();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.QUERYGENERATOR.getPath()));
			AnchorPane queryGenerator = loader.load();
			root.getChildren().clear();
			root.getChildren().add(queryGenerator);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void showMongoDataManager() throws Exception {
		String databaseName = mongoDatabaseList.getSelectionModel().getSelectedItem();
		MongoDBResource.INSTANCE.setDB(databaseName);
		AnchorPane root;
		root = CenterLayout.INSTANCE.getRootContainer();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FxmlPath.DATAMANAGER.getPath()));
		AnchorPane mongoDataManagerAncPane;
		try {
			mongoDataManagerAncPane = loader.load();
			MongoDataManager dataManager = (MongoDataManager) loader.getController();
			dataManager.setDatabase(databaseName);
			root.getChildren().clear();
			root.getChildren().add(mongoDataManagerAncPane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

//	public List<String> getDatabaseNames() throws Exception {
//		List<String> dbs = new ArrayList<String>();
//		String host = ConnectionParameters.SESSION.getMongoHostName();
//		int port = ConnectionParameters.SESSION.getMongoPort();
//		MongoClient client = MongoConnManager.INSTANCE.connect(host, port);
//		MongoCursor<String> dbsCursor = client.listDatabaseNames().iterator();
//		while (dbsCursor.hasNext()) {
//			dbs.add(dbsCursor.next());
//		}
//		return dbs;
//	}

}
