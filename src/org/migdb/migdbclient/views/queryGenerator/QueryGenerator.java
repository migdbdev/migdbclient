/**
 * 
 */
package org.migdb.migdbclient.views.queryGenerator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.bson.Document;
import org.json.simple.JSONObject;
import org.migdb.migdbclient.controllers.dbconnector.MongoConnManager;
import org.migdb.migdbclient.resources.ConnectionParameters;
import org.migdb.migdbclient.resources.MongoDBResource;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 * @author KANI
 *
 */
public class QueryGenerator implements Initializable {

	@FXML
	private ComboBox<String> databaseListComboBox;
	@FXML
	private ComboBox<String> collectionListComboBox;
	@FXML
	private ComboBox<String> fieldListComboBox;
	@FXML
	private RadioButton findRadioButton;
	@FXML
	private RadioButton findOneRadioButton;

	MongoCollection<Document> collectionDocument;
	List<Document> selectedDocument;
	private MongoDatabase db;
	private String collectionFElementHolder;
	private int counter;

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Set database name list to the databaseListComboBox
		ArrayList<String> mongoCollections = (ArrayList<String>) getMongoDatabases();
		for (int i = 0; i < mongoCollections.size(); i++) {
			databaseListComboBox.getItems().add(mongoCollections.get(i));
		}
		databaseListComboBox.getSelectionModel().select(0);

		// Set collection list according to a selected databse when ui loaded
		collectionListComboBox.getItems()
				.addAll(getCollectionOf(databaseListComboBox.getSelectionModel().getSelectedItem()));
		collectionListComboBox.getSelectionModel().select(0);

		// Set values to variable when initializing a UI
		MongoDBResource.INSTANCE.setDB(databaseListComboBox.getSelectionModel().getSelectedItem().toString());
		db = MongoDBResource.INSTANCE.getDatabase();
		collectionDocument = db.getCollection(collectionListComboBox.getSelectionModel().getSelectedItem().toString());
		selectedDocument = collectionDocument.find().into(new ArrayList<Document>());

		/**
		 * databaseListComboBox changed event When changing
		 * databaseListComboBox, according to that one set list of collections
		 * to the collectionListComboBox
		 */
		databaseListComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				counter = 0;
				collectionFElementHolder = getCollectionOf(databaseListComboBox.getSelectionModel().getSelectedItem())
						.get(0).toString();
				MongoDBResource.INSTANCE.setDB(databaseListComboBox.getSelectionModel().getSelectedItem().toString());
				db = MongoDBResource.INSTANCE.getDatabase();
				collectionDocument = db
						.getCollection(collectionListComboBox.getSelectionModel().getSelectedItem().toString());
				selectedDocument = collectionDocument.find().into(new ArrayList<Document>());
			}
		});

		// databaseListComboBox click action event
		databaseListComboBox.setOnAction((event) -> {
			collectionListComboBox.getItems().clear();
			collectionListComboBox.getItems()
					.addAll(getCollectionOf(databaseListComboBox.getSelectionModel().getSelectedItem()));
			collectionListComboBox.getSelectionModel().select(0);
		});

		if (!selectedDocument.isEmpty()) {
			fieldListComboBox.getItems().addAll(getCommonColumns(selectedDocument));
			fieldListComboBox.getSelectionModel().select(0);
		}

		/**
		 * collectionListComboBox's value changing event action
		 */
		collectionListComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue comboBoxVal, String t, String t1) {
				collectionDocument = (counter <= 0) ? db.getCollection(collectionFElementHolder)
						: db.getCollection((String) comboBoxVal.getValue());
				counter++;
				selectedDocument = collectionDocument.find().into(new ArrayList<Document>());
				fieldListComboBox.getItems().clear();
				if (!selectedDocument.isEmpty()) {
					fieldListComboBox.getItems().addAll(getCommonColumns(selectedDocument));
					fieldListComboBox.getSelectionModel().select(0);
				}

			}
		});

		// Set toggle group to radio button
		final ToggleGroup radioGroup = new ToggleGroup();
		findRadioButton.setToggleGroup(radioGroup);
		findOneRadioButton.setToggleGroup(radioGroup);

	}

	public void queryBuild() {

	}

	/**
	 * Get list of all the mongo databases
	 * 
	 * @return
	 */
	private List<String> getMongoDatabases() {
		List<String> databases = new ArrayList<String>();
		try {
			String host = ConnectionParameters.SESSION.getMongoHostName();
			int port = ConnectionParameters.SESSION.getMongoPort();
			MongoClient client = MongoConnManager.INSTANCE.connect(host, port);
			MongoCursor<String> dbCursor = client.listDatabaseNames().iterator();
			while (dbCursor.hasNext()) {
				databases.add(dbCursor.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return databases;
	}

	/**
	 * Get list of collections related to the database
	 * 
	 * @param database
	 * @return
	 */
	private ObservableList<String> getCollectionOf(String database) {
		ObservableList<String> collectionsOf = FXCollections.observableArrayList();
		try {
			MongoDatabase db = MongoConnManager.INSTANCE.connectToDatabase(database);
			MongoCursor<String> cursor = db.listCollectionNames().iterator();
			while (cursor.hasNext()) {
				collectionsOf.add(cursor.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collectionsOf;
	}

	/**
	 * Get common columns related to the particular document
	 * 
	 * @param doc
	 * @return
	 */
	public Set<String> getCommonColumns(List<Document> doc) {
		JSONObject commonColumns = new JSONObject();
		for (Document document : doc) {
			JSONObject object = new JSONObject(document);

			for (Object key : object.keySet()) {
				document.containsKey(key);
				if (commonColumns.containsKey(key)) {
					int cnt = Integer.parseInt(commonColumns.get(key).toString());
					cnt++;
					commonColumns.put(key, cnt);
				} else {
					commonColumns.put(key, "1");
				}
			}

		}

		Set<String> set = null;
		Iterator<?> keys = commonColumns.keySet().iterator();
		int[] arr = new int[commonColumns.keySet().size()];
		int cnt = 0;
		while (keys.hasNext()) {
			arr[cnt++] = Integer.parseInt(commonColumns.get(keys.next()).toString());
		}
		Document commonDoc = new Document();
		int commonCount = getMostPopularElement(arr);
		Iterator<?> keyset = commonColumns.keySet().iterator();
		while (keyset.hasNext()) {
			Object key = keyset.next();
			String st = commonColumns.get(key).toString();
			int value = Integer.parseInt(st);
			if (value == commonCount) {
				commonDoc.append((String) key, value);
			}

		}

		return commonDoc.keySet();
	}

	private static int getMostPopularElement(int[] a) {
		int maxElementIndex = getArrayMaximumElementIndex(a);
		int[] b = new int[a[maxElementIndex] + 1];
		for (int i = 0; i < a.length; i++) {
			++b[a[i]];
		}
		return getArrayMaximumElementIndex(b);
	}

	private static int getArrayMaximumElementIndex(int[] a) {
		int maxElementIndex = 0;
		for (int i = 1; i < a.length; i++) {
			if (a[i] >= a[maxElementIndex]) {
				maxElementIndex = i;
			}
		}
		return maxElementIndex;
	}

}
