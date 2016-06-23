package org.migdb.migdbclient.views.mongodatamanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bson.Document;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.controllers.dbconnector.MongoConnManager;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.resources.CenterLayout;
import org.migdb.migdbclient.resources.DatabaseResource;

import com.mongodb.MongoClient;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
	@FXML
	private Label databaseNameLabel;

	private MongoDatabase db;
	private String databaseName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		/*
		 * try { db = MongoConnManager.INSTANCE.connectToDatabase("test"); }
		 * catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } try { displayCollections(); } catch (Exception
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * try { ObservableList<String> list =
		 * FXCollections.observableArrayList(getDatabaseNames());
		 * databaseList.setItems(list);
		 * collectionList.setItems(getCollectionNames());
		 * 
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } System.out.println(databaseName);
		 */

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

		// System.out.println(databaseName);

	}

	@FXML
	public void testHandler() {
		AnchorPane root;
		root = CenterLayout.INSTANCE.getRootContainer();
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

	@FXML
	public void showCollectionManger() {
		System.out.println(collectionList.getSelectionModel().getSelectedItem());
		AnchorPane root;
		root = CenterLayout.INSTANCE.getRootContainer();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FxmlPath.COLLECTIONMANAGER.getPath()));
		AnchorPane mongoCollectionManagerAncPane;
		try {
			mongoCollectionManagerAncPane = loader.load();
			CollectionManager collectionManager = (CollectionManager) loader.getController();
			String st = collectionList.getSelectionModel().getSelectedItem();
			collectionManager.setCollection(st.substring(0, st.indexOf(' ')));
			root.getChildren().clear();
			root.getChildren().add(mongoCollectionManagerAncPane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ObservableList<String> getCollectionsOf(String dbName) throws Exception {
		ObservableList<String> list = FXCollections.observableArrayList();
		databaseNameLabel.setText(dbName);
		MongoDatabase db = MongoConnManager.INSTANCE.connectToDatabase(dbName);
		MongoCursor<String> cursor = db.listCollectionNames().iterator();
		while (cursor.hasNext()) {
			String st = cursor.next();
			MongoCollection<Document> collection = db.getCollection(st);
			int count = (int) collection.count();
			list.add(st + "   (" + count + " Documents)");
		}
		System.out.println(list);
		return list;

	}

	public void setDatabase(String databaseName) throws Exception {
		try {
			collectionList.setItems(getCollectionsOf(DatabaseResource.INSTANCE.getDatabaseName()));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(databaseName);

	}

}
