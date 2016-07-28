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
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.models.dto.QueryDocumentDTO;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

/**
 * @author KANI
 *
 */
public class QueryGenerator implements Initializable {

	@FXML
	private ComboBox<String> queryDatabaseListComboBox;
	@FXML
	private ComboBox<String> queryCollectionListComboBox;
	@FXML
	private ComboBox<String> queryFieldListComboBox;
	@FXML
	private ComboBox<String> queryOperatorsComboBox;
	@FXML
	private RadioButton queryFindRadioButton;
	@FXML
	private RadioButton queryFindOneRadioButton;
	@FXML
	private Button queryBuildButton;
	@FXML
	private Button queryExecuteButton;
	@FXML
	private Button addQueryParamButton;
	@FXML
	private TextField queryValuesTextField;
	@FXML
	private TextField queryLimitTextField;
	@FXML
	private TextField querySkipTextField;
	@FXML
	private ComboBox<String> querySortColumnComboBox;
	@FXML
	private ComboBox<String> querySortOrderComboBox;
	@FXML
	private TextArea outputQuery;
	@FXML
	private TableView<QueryDocumentDTO> queryParametersTableView;

	@FXML
	private TableColumn<QueryDocumentDTO, String> fieldTableColumn;
	@FXML
	private TableColumn<QueryDocumentDTO, String> operatorsTableColumn;
	@FXML
	private TableColumn<QueryDocumentDTO, String> valuesTableColumn;
	@FXML
	private TableColumn<QueryDocumentDTO, String> conditionTableColumn;

	MongoCollection<Document> collectionDocument;
	List<Document> selectedDocument;
	private MongoDatabase db;
	private String collectionFElementHolder;
	private int counter;
	private final ToggleGroup radioGroup = new ToggleGroup();

	private SqliteDAO dao;

	private ObservableList<QueryDocumentDTO> queryParams = FXCollections.observableArrayList();
	private final ObservableList<String> conditionList = FXCollections.observableArrayList("AND","OR");

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		queryParametersTableView.setEditable(true);

		// Set database name list to the databaseListComboBox
		ArrayList<String> mongoCollections = (ArrayList<String>) getMongoDatabases();
		for (int i = 0; i < mongoCollections.size(); i++) {
			queryDatabaseListComboBox.getItems().add(mongoCollections.get(i));
		}
		queryDatabaseListComboBox.getSelectionModel().select(0);

		// Set collection list according to a selected databse when ui loaded
		queryCollectionListComboBox.getItems()
				.addAll(getCollectionOf(queryDatabaseListComboBox.getSelectionModel().getSelectedItem()));
		queryCollectionListComboBox.getSelectionModel().select(0);

		// Set values to variable when initializing a UI
		MongoDBResource.INSTANCE.setDB(queryDatabaseListComboBox.getSelectionModel().getSelectedItem().toString());
		db = MongoDBResource.INSTANCE.getDatabase();
		collectionDocument = db.getCollection(queryCollectionListComboBox.getSelectionModel().getSelectedItem().toString());
		selectedDocument = collectionDocument.find().into(new ArrayList<Document>());

		/**
		 * databaseListComboBox changed event When changing
		 * databaseListComboBox, according to that one set list of collections
		 * to the collectionListComboBox
		 */
		queryDatabaseListComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				counter = 0;
				collectionFElementHolder = getCollectionOf(queryDatabaseListComboBox.getSelectionModel().getSelectedItem())
						.get(0).toString();
				MongoDBResource.INSTANCE.setDB(queryDatabaseListComboBox.getSelectionModel().getSelectedItem().toString());
				db = MongoDBResource.INSTANCE.getDatabase();
				collectionDocument = db
						.getCollection(queryCollectionListComboBox.getSelectionModel().getSelectedItem().toString());
				selectedDocument = collectionDocument.find().into(new ArrayList<Document>());
			}
		});

		// databaseListComboBox click action event
		queryDatabaseListComboBox.setOnAction((event) -> {
			queryCollectionListComboBox.getItems().clear();
			queryCollectionListComboBox.getItems()
					.addAll(getCollectionOf(queryDatabaseListComboBox.getSelectionModel().getSelectedItem()));
			queryCollectionListComboBox.getSelectionModel().select(0);
		});

		// Fill queryFieldListComboBox and querySortColumnComboBox Combo boxe's when document combo box change
		if (!selectedDocument.isEmpty()) {
			queryFieldListComboBox.getItems().addAll(getCommonColumns(selectedDocument));
			queryFieldListComboBox.getSelectionModel().select(0);
			querySortColumnComboBox.getItems().addAll(getCommonColumns(selectedDocument));
		}
		
		// Fill querySortOrderComboBox Combo box when ui loaded
		querySortOrderComboBox.getItems().addAll("Ascending","Descending");
		querySortOrderComboBox.getSelectionModel().select(0);

		/**
		 * collectionListComboBox's value changing event action
		 */
		queryCollectionListComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue comboBoxVal, String t, String t1) {
				collectionDocument = (counter <= 0) ? db.getCollection(collectionFElementHolder)
						: db.getCollection((String) comboBoxVal.getValue());
				counter++;
				selectedDocument = collectionDocument.find().into(new ArrayList<Document>());
				queryFieldListComboBox.getItems().clear();
				querySortColumnComboBox.getItems().clear();
				if (!selectedDocument.isEmpty()) {
					queryFieldListComboBox.getItems().addAll(getCommonColumns(selectedDocument));
					queryFieldListComboBox.getSelectionModel().select(0);
					querySortColumnComboBox.getItems().addAll(getCommonColumns(selectedDocument));
				}

			}
		});

		// Set operators to the operatorsComboBox
		dao = new SqliteDAO();
		queryOperatorsComboBox.getItems().addAll(dao.getQueryOperators());

		// Set toggle group to radio button
		queryFindRadioButton.setToggleGroup(radioGroup);
		queryFindOneRadioButton.setToggleGroup(radioGroup);

		// addQueryParamButton button click event
		addQueryParamButton.addEventHandler(ActionEvent.ACTION, event -> addQueryParam());
		
		// queryBuildButton button click event
		queryBuildButton.addEventHandler(ActionEvent.ACTION, event -> queryBuild());
		
		// Initialize queryParameters table view
		fieldTableColumn.setCellValueFactory(new PropertyValueFactory<QueryDocumentDTO, String>("Field"));
		operatorsTableColumn.setCellValueFactory(new PropertyValueFactory<QueryDocumentDTO, String>("Operators"));
		valuesTableColumn.setCellValueFactory(new PropertyValueFactory<QueryDocumentDTO, String>("Values"));
		conditionTableColumn.setCellValueFactory(new PropertyValueFactory<QueryDocumentDTO, String>("condition"));
		conditionTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(conditionList));
		conditionTableColumn.setOnEditCommit(new EventHandler<CellEditEvent<QueryDocumentDTO,String>>() {
			@Override
			public void handle(CellEditEvent<QueryDocumentDTO, String> t) {
				((QueryDocumentDTO)t.getTableView().getItems().get(t.getTablePosition().getRow())).setCondition(t.getNewValue());
			};
		});
		
	}

	public void queryBuild() {
		try {
			String dbName = queryDatabaseListComboBox.getSelectionModel().getSelectedItem();
			String collection = queryCollectionListComboBox.getSelectionModel().getSelectedItem();
			RadioButton queryType = (RadioButton) radioGroup.selectedToggleProperty().get().getToggleGroup().getSelectedToggle();
			ObservableList<QueryDocumentDTO> param = (ObservableList<QueryDocumentDTO>) queryParametersTableView.getItems();
			String query = "";
			
			for(QueryDocumentDTO dto : param) {
				dao = new SqliteDAO();
				System.out.println(dto.getOperators().toString());
				String operator = dao.getQueryOperatorsKeyword(dto.getOperators().toString());
				System.out.println("Operstro : "+operator);
				System.out.println(operator.equals("") ? "empty" : "Not empty");
				if(dto.getCondition() == null || dto.getCondition().equals("AND")){
					operator = (operator.equals("")) ? "{"+dto.getField()+":"+dto.getValues()+"}" : "{"+dto.getField()+":{"+operator+":"+dto.getValues()+"}}";
					System.out.println(operator);
					query = operator;
				}
				
			}
			
			String method = queryType.getText().equals("find") ? "."+queryType.getText()+"("+query+").pretty()" : "."+queryType.getText()+"("+query+")" ;
			String limit = (!queryLimitTextField.getText().isEmpty()) ? ".limit("+queryLimitTextField.getText()+")" : "";
			String skip = (!querySkipTextField.getText().isEmpty()) ? ".skip("+querySkipTextField.getText()+")" : "";
			String sortOrder = (querySortOrderComboBox.getSelectionModel().getSelectedItem()).equals("Ascending") ? "1" : "-1";
			String sort = (querySortColumnComboBox.getSelectionModel().getSelectedItem() != null) ? ".sort({"+querySortColumnComboBox.getSelectionModel().getSelectedItem()+":"+sortOrder+"})" : "" ;
			
			
			String Structure = "db."+collection+method+limit+sort+skip;

			outputQuery.clear();
			outputQuery.appendText(Structure);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	/**
	 * Add query fields and operators into table
	 */
	private void addQueryParam() {
		try {
			QueryDocumentDTO dto = new QueryDocumentDTO();
			dto.setField(queryFieldListComboBox.getSelectionModel().getSelectedItem());
			dto.setOperators(queryOperatorsComboBox.getSelectionModel().getSelectedItem());
			dto.setValues(queryValuesTextField.getText());
			queryParams.add(dto);
			queryParametersTableView.setItems(queryParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Inner class for create custom combo box in table cell
	 * @author KANI
	 *
	 */
	class ComboBoxCell extends TableCell<QueryDocumentDTO, String> {

	    private ComboBox<String> comboBox;
	    private final ObservableList<String> typData
        = FXCollections.observableArrayList("AND","OR");

	    public ComboBoxCell() {
	    }

	    @Override
	    public void startEdit() {
	        super.startEdit();

	        if (comboBox == null) {
	            createComboBox();
	        }

	        setGraphic(comboBox);
	        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	    }

	    @Override
	    public void cancelEdit() {
	        super.cancelEdit();

	        setText(String.valueOf(getItem()));
	        setContentDisplay(ContentDisplay.TEXT_ONLY);
	    }

	    public void updateItem(String item, boolean empty) {
	        super.updateItem(item, empty);

	        if (empty) {
	            setText(null);
	            setGraphic(null);
	        } else {
	            if (isEditing()) {
	                if (comboBox != null) {
	                    comboBox.setValue(getString());
	                }
	                setGraphic(comboBox);
	                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	            } else {
	                setText(getString());
	                setContentDisplay(ContentDisplay.TEXT_ONLY);
	            }
	        }
	    }

	    private void createComboBox() {
	        // ClassesController.getLevelChoice() is the observable list of String
	        comboBox = new ComboBox<>(typData);
	        comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap()*2);
	        comboBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
	            @Override
	            public void handle(KeyEvent t) {
	                if (t.getCode() == KeyCode.ENTER) {
	                    commitEdit(comboBox.getSelectionModel().getSelectedItem());
	                } else if (t.getCode() == KeyCode.ESCAPE) {
	                    cancelEdit();
	                }
	            }
	        });
	    }

	    private String getString() {
	        return getItem() == null ? "" : getItem().toString();
	    }
	}

}
