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
import org.migdb.migdbclient.resources.MongoDBResource;
import org.migdb.migdbclient.utils.MigDBNotifier;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tray.animations.AnimationType;
import tray.notification.NotificationType;

public class MongoDataManager implements Initializable {
	@FXML
	private Button btnAddCollection;
	@FXML
	private ListView<String> collectionList;
	@FXML
	private ListView<String> databaseList;
	@FXML
	private AnchorPane rootAncPane;
	@FXML
	private Label databaseNameLabel;

	private MongoDatabase db;
	// private String databaseName;

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

	@FXML
	public void showNewCollection() {
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setTitle("Add New Collection");

		VBox dialogVbox = new VBox(20);
		dialogVbox.setPadding(new Insets(20, 0, 20, 20));
		dialogVbox.setStyle("-fx-background-color : white");
		dialogVbox.setAlignment(Pos.CENTER);

		Label label = new Label("Collection Name ");
		TextField txtCollectionName = new TextField();

		HBox detailHbox = new HBox(30);

		detailHbox.getChildren().addAll(label, txtCollectionName);
		Button btn = new Button("Add");
		btn.setPrefWidth(100);
		btn.setOnAction((ActionEvent e) -> {
			String collectionname = txtCollectionName.getText();
			System.out.println(collectionname);
			 addNewCollection(collectionname);
			dialog.close();
		});
		
		dialogVbox.getChildren().addAll(detailHbox, btn);

		Scene dialogScene = new Scene(dialogVbox, 350, 150);
		dialog.setScene(dialogScene);
		dialog.show();
	}
	
	private void addNewCollection(String name){
		MongoDatabase db = MongoDBResource.INSTANCE.getDatabase();
		try{
		db.createCollection(name);
		String title = "Attention";
	    String message = "Successfully created!";
	    AnimationType animationType = AnimationType.FADE;
	    NotificationType notificationType = NotificationType.SUCCESS;
	    int showTime = 6;

	    MigDBNotifier notification = new MigDBNotifier(title, message, animationType, notificationType,showTime);
	    notification.createDefinedNotification();
		} catch (MongoCommandException ex){
			ex.printStackTrace();
			String title = "Attention";
		    String message = "Collection Already Exsist";
		    AnimationType animationType = AnimationType.FADE;
		    NotificationType notificationType = NotificationType.ERROR;
		    int showTime = 6;

		    MigDBNotifier notification = new MigDBNotifier(title, message, animationType, notificationType,showTime);
		    notification.createDefinedNotification();
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
			collectionList.setItems(getCollectionsOf(MongoDBResource.INSTANCE.getDatabaseName()));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(databaseName);

	}

}
