package org.migdb.migdbclient.views.mongodatamanager;

import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.migdb.migdbclient.config.TreeviewSize;
import org.migdb.migdbclient.models.modificationevaluator.TableReference;
import org.migdb.migdbclient.resources.CenterLayout;
import org.migdb.migdbclient.resources.DatabaseResource;
import org.migdb.migdbclient.tablegen.CustomCellFactory;
import org.migdb.migdbclient.tablegen.TableBean;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.twitter.conversions.string;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class CollectionManager implements Initializable {

	@FXML
	private AnchorPane collectionAncPane;
	@FXML
	private TableView<TableBean> collectionTable;
	private String collectionName;
	private MongoDatabase db;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		db = DatabaseResource.INSTANCE.getDatabase();

	}

	private void generateTreeView() {

		try {

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader("C:\\Users\\Malki\\Desktop\\research\\json.txt"));
			JSONObject jsonObject = (JSONObject) obj;

			JSONArray tableList = (JSONArray) jsonObject.get("tables");

			Iterator<JSONObject> iterator = tableList.iterator();
			double x = 10;
			double y = 10;

			ArrayList<TableReference> fkArr = new ArrayList<TableReference>();

			ArrayList<Line> lineArr = new ArrayList<>();

			while (iterator.hasNext()) {
				JSONObject tbl = (JSONObject) iterator.next();
				String name = (String) tbl.get("name");

				CheckBoxTreeItem<String> root = new CheckBoxTreeItem<String>(name);
				root.setExpanded(true);

				JSONArray columnList = (JSONArray) tbl.get("columns");
				Iterator<JSONObject> cols = columnList.iterator();
				JSONArray refColumnList = (JSONArray) tbl.get("referencedBy");

				if (refColumnList != null) {
					Iterator<JSONObject> refCols = refColumnList.iterator();

					while (refCols.hasNext()) {
						JSONObject ref = (JSONObject) refCols.next();
						String referencedCol = (String) ref.get("referencedCol");
						String referencingTbl = (String) ref.get("referencingTab");
						String referencingCol = (String) ref.get("referencingCol");
						String relationship = (String) ref.get("relationshipType");

						TableReference reference = new TableReference(name, referencedCol, referencingTbl,
								referencingCol, relationship);
						fkArr.add(reference);
					}
				}

				while (cols.hasNext()) {
					String colName = (String) cols.next().get("colName");
					CheckBoxTreeItem<String> column = new CheckBoxTreeItem<String>(colName);
					column.setGraphic(new Label(""));
					root.getChildren().add(column);

					if (refColumnList != null) {
						if (fkArr.contains(colName)) {
							System.out.println(colName);
						}
					}
				}

				TreeView<String> treeView = new TreeView<String>(root);
				treeView.setEditable(true);
				treeView.setCellFactory(CheckBoxTreeCell.forTreeView());

				treeView.setMaxHeight(TreeviewSize.TREEVIEWHEIGHT.getSize());
				treeView.setMaxWidth(TreeviewSize.TREEVIEWIDTH.getSize());
				treeView.setLayoutX(x);
				treeView.setLayoutY(y);

				collectionAncPane.getChildren().add(treeView);

				/*
				 * System.out.println("Current Parent :" + root.getValue());
				 * for(TreeItem<String> child: root.getChildren()){
				 * if(child.getChildren().isEmpty()){
				 * child.getGraphic().getLayoutX();
				 * System.out.println(child.getGraphic().getBoundsInLocal()); }
				 * }
				 */

				x = x + (TreeviewSize.TREEVIEWHEIGHT.getSize());
			}

			ArrayList<Line> lines = new ArrayList<>();
			for (TableReference ref : fkArr) {
				String referencedTab = ref.getReferencedTab();
				String referencingTab = ref.getReferencingTab();
				System.out.println(referencedTab + " " + referencingTab);
				Line line = new Line();

				for (Node tbl : collectionAncPane.getChildren()) {
					TreeView<String> tree = (TreeView) tbl;
					String tableName = tree.getRoot().getValue().toString();
					System.out.println(tableName);
					if (tableName.equals(referencedTab)) {
						line.setStartX(tree.getLayoutX() + tree.getMaxWidth());
						line.setStartY(tree.getLayoutY() + (tree.getMaxHeight() / 2));
					}
					if (tableName.equals(referencingTab)) {
						line.setEndX(tree.getLayoutX());
						line.setEndY(tree.getLayoutY());
					}

					// for(TreeItem col: tbl.getC)
				}

				line.setStrokeWidth(2);
				line.setStroke(Color.BLACK);

				lines.add(line);
			}

			for (Line line : lines) {
				collectionAncPane.getChildren().add(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void setCollection(String collectionName) {
		MongoCollection<Document> collection = db.getCollection(collectionName);
		this.collectionName = collectionName;

	
		System.out.println(collectionName);

		List<Document> foundDocument = collection.find().into(new ArrayList<Document>());

		Document document = collection.find().first();
		System.out.println(document.toJson());
		JSONObject jsonObject = new JSONObject(document);

		System.out.println("aaaaaaaaaaa" + jsonObject);
		System.out.println(document.keySet().toString());
		setTable(foundDocument, document.keySet());
		getCommonColumns(foundDocument);

	}

	public Set<String> getCommonColumns(List<Document> doc) {
		JSONObject commonColumns = new JSONObject();
		for (Document document : doc) {
			JSONObject object = new JSONObject(document);

			for (Object key : object.keySet()) {
				// System.out.println(key);
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
		// System.out.println(commonColumns);
		Set<String> set = null;
		Iterator<?> keys = commonColumns.keySet().iterator();
		while (keys.hasNext()) {
			System.out.println(commonColumns.get(keys.next()));
		}
		return set;
	}

	public void setTable(List<Document> documents, Set<String> keySet) {

		for (String col : keySet) {
			TableColumn<TableBean, String> column = new TableColumn<>();
			column.setCellValueFactory(new CustomCellFactory<TableBean, String>(col));
			column.setText(col);

			collectionTable.getColumns().add(column);
		}
		ObservableList<TableBean> tableList = FXCollections.observableArrayList();

		for (Document doc : documents) {
			TableBean bean = new TableBean();
			for (String key : keySet) {
				// if(key.equals("loc")) continue;
				bean.setCellData(key, String.valueOf(doc.get(key)));
			}

			tableList.add(bean);
		}

		collectionTable.getItems().addAll(tableList);

	}
	@FXML
	public void deleteDocument(){
//		SimpleStringProperty st = (SimpleStringProperty) collectionTable.getSelectionModel().getSelectedItem().getCellValue("_id");
//		String selectedId = st.getValue();
//		DeleteResult result = db.getCollection(collectionName).deleteMany(new Document("_id", selectedId));
//		System.out.println(result.toString());
		
	}

}
