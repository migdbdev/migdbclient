package org.migdb.migdbclient.views.mongodatamanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.resources.CenterLayout;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

public class DocumentManager implements Initializable {
	@FXML
	private AnchorPane documentAncPane;
	@FXML
	private Label collectionNameLabel;
	@FXML
	private Label objectIdLabel;
	@FXML
	private TreeView<String> documentTreeView;
	@FXML
	private Button backButton;
	MongoCollection<Document> collection;
	private String collectionName;
	private String documentId;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	public void setDocument(MongoCollection<Document> collection, String name, String id) {
		this.collectionName = name;
		this.collection = collection;
		this.documentId = id;
		System.out.println(collectionName + " " + documentId);
		showDocument();
	}

	@FXML
	public void showDocument() {
		collectionNameLabel.setText(collectionName);
		objectIdLabel.setText(documentId);
		Document filter;
		try {
			filter = new Document("_id", new ObjectId(documentId));
		} catch (NoSuchElementException e) {
			filter = new Document("_id", documentId);
		} catch (IllegalArgumentException e) {
			filter = new Document("_id", documentId);
		}
		MongoCursor<Document> cursor = collection.find(filter).iterator();
		Document document = cursor.next();
		Set<Entry<String, Object>> entrySet = document.entrySet();
		TreeItem<String> root = new TreeItem<String>("_id : " + document.get("_id").toString());
		root.setExpanded(true);
		resolveDocument(document, root);
		documentTreeView.setRoot(root);
		documentTreeView.setEditable(true);
	}

	@FXML
	public void showCollectionManger() {
		AnchorPane root;
		root = CenterLayout.INSTANCE.getRootContainer();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FxmlPath.COLLECTIONMANAGER.getPath()));
		AnchorPane mongoCollectionManagerAncPane;
		try {
			mongoCollectionManagerAncPane = loader.load();
			CollectionManager collectionManager = (CollectionManager) loader.getController();
			collectionManager.setCollection(collectionName);
			root.getChildren().clear();
			root.getChildren().add(mongoCollectionManagerAncPane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void resolveDocument(Document document, TreeItem<String> parent) {
		Set<Entry<String, Object>> entrySet = document.entrySet();

		for (Entry<String, Object> entry : entrySet) {

			if (entry.getValue() instanceof Document) {
				TreeItem<String> newParent = new TreeItem<String>(entry.getKey());
				resolveDocument((Document) entry.getValue(), newParent);
				newParent.setExpanded(true);
				parent.getChildren().add(newParent);

			} else if (entry.getValue() instanceof ArrayList) {
				TreeItem<String> newParent = new TreeItem<String>(entry.getKey());
				resolveArrayList((ArrayList<Object>) entry.getValue(), newParent);
				newParent.setExpanded(true);
				parent.getChildren().add(newParent);
			} else if (entry.getValue() instanceof ObjectId) {
				System.out.println("object id found");
			} else {
				parent.getChildren().add(new TreeItem<String>(entry.getKey() + " : " + entry.getValue()));
			}

		}
	}

	private void resolveArrayList(ArrayList<Object> list, TreeItem<String> parent) {
		for (Object object : list) {
			if (object instanceof Document) {
				resolveDocument((Document) object, parent);
			} else if (object instanceof ArrayList) {
				resolveArrayList((ArrayList<Object>) object, parent);
			} else if (object instanceof ObjectId) {
				parent.getChildren().add(new TreeItem<String>("_id : " + object.toString()));
			} else {
				parent.getChildren().add(new TreeItem<String>(object.toString()));
			}
		}
	}

}
