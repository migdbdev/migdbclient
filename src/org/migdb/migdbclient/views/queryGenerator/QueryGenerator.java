/**
 * 
 */
package org.migdb.migdbclient.views.queryGenerator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.migdb.migdbclient.controllers.dbconnector.MongoConnManager;
import org.migdb.migdbclient.resources.ConnectionParameters;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.client.MongoCursor;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

/**
 * @author KANI
 *
 */
public class QueryGenerator implements Initializable {
	
	@FXML private ComboBox<String> collectionListComboBox;
	@FXML private RadioButton findRadioButton;
	@FXML private RadioButton findOneRadioButton;

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		ArrayList<String> mongoCollections = (ArrayList<String>) getMongoCollections();
		for(int i = 0; i < mongoCollections.size(); i++){
			collectionListComboBox.getItems().add(mongoCollections.get(i));
		}
		collectionListComboBox.getSelectionModel().select(0);
		
		//Set toggle group to radio button
		final ToggleGroup radioGroup = new ToggleGroup();
		findRadioButton.setToggleGroup(radioGroup);
		findOneRadioButton.setToggleGroup(radioGroup);

	}

	public void queryBuild() {

	}

	public List<String> getMongoCollections() {
		List<String> collections = new ArrayList<String>();
		try {
			String host = ConnectionParameters.SESSION.getMongoHostName();
			int port = ConnectionParameters.SESSION.getMongoPort();
			MongoClient client = MongoConnManager.INSTANCE.connect(host, port);
			MongoCursor<String> dbCursor = client.listDatabaseNames().iterator();
			while (dbCursor.hasNext()) {
				collections.add(dbCursor.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collections;
	}

}
