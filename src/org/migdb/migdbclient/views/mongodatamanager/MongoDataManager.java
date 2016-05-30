package org.migdb.migdbclient.views.mongodatamanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bson.Document;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.controllers.MongoConnManager;
import org.migdb.migdbclient.main.MainApp;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

public class MongoDataManager implements Initializable {
	@FXML
	private Button btnTest;
	@FXML
	private ListView<String> collectionList;
	@FXML
	private ListView<String> databaseList;
	@FXML
	private AnchorPane rootAncPane;

	private MongoDatabase db;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			db = MongoConnManager.INSTANCE.connectToDatabase("test");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			displayCollections();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			ObservableList<String> list = FXCollections.observableArrayList(getDatabaseNames());
			databaseList.setItems(list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * try { // get list of collections MongoConnManager connManager = new
		 * MongoConnManager(); DB db = connManager.connect(); databaseResource
		 * databaseResource = new databaseResource();
		 * databaseResource.setCollections(db);
		 * System.out.println(databaseResource.getCollections());
		 * 
		 * collectionList.getItems().addAll(databaseResource.getCollections());
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	}


	@FXML
	public void display() throws Exception {
		System.out.println("button clicked");
		MongoCollection<Document> collection = db.getCollection("zips");

		List<Document> foundDocument = collection.find().into(new ArrayList<Document>());

		foundDocument.stream().forEach(e -> {
			System.out.println(e.toJson());
		});

	}

	@FXML
	public void displayCollections() throws Exception {
		System.out.println(getDatabaseNames());
	}

	@FXML
	public void testHandler() {
		AnchorPane root;
		root = MySession.INSTANCE.getRootContainer();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FxmlPath.COLLECTIONMANAGER.getPath()));
		AnchorPane collectionManager;
		try {
			collectionManager = loader.load();
			root.getChildren().clear();
			root.getChildren().add(collectionManager);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public List<String> getDatabaseNames() throws Exception {
		List<String> dbs = new ArrayList<String>();
		MongoClient client = MongoConnManager.INSTANCE.connect();
		MongoCursor<String> dbsCursor = client.listDatabaseNames().iterator();
		while (dbsCursor.hasNext()) {
			dbs.add(dbsCursor.next());
		}
		return dbs;
	}

}
