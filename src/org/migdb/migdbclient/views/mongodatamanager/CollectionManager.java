package org.migdb.migdbclient.views.mongodatamanager;

import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.migdb.migdbclient.config.TreeviewSize;
import org.migdb.migdbclient.models.modificationevaluator.TableReference;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class CollectionManager implements Initializable{


	@FXML private AnchorPane collectionAncPane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	private void generateTreeView(){

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

				if(refColumnList != null) {
					Iterator<JSONObject> refCols = refColumnList.iterator();

					while(refCols.hasNext()) {
						JSONObject ref = (JSONObject) refCols.next();
						String referencedCol = (String) ref.get("referencedCol");
						String referencingTbl = (String) ref.get("referencingTab");
						String referencingCol = (String) ref.get("referencingCol");
						String relationship = (String) ref.get("relationshipType");

						TableReference reference = new TableReference(name,referencedCol,referencingTbl,referencingCol,relationship);
						fkArr.add(reference);
					}
				}

				while (cols.hasNext()) {
					String colName = (String) cols.next().get("colName");
					CheckBoxTreeItem<String> column = new CheckBoxTreeItem<String>(colName);
					column.setGraphic(new Label(""));
					root.getChildren().add(column);

					if(refColumnList != null) {
						if(fkArr.contains(colName)) {
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

				/*System.out.println("Current Parent :" + root.getValue());
	            for(TreeItem<String> child: root.getChildren()){
	                if(child.getChildren().isEmpty()){
	                	child.getGraphic().getLayoutX();
	                    System.out.println(child.getGraphic().getBoundsInLocal());
	                }
	            }*/

				x = x + (TreeviewSize.TREEVIEWHEIGHT.getSize());
			}

			ArrayList<Line> lines = new ArrayList<>();
			for (TableReference ref : fkArr) {
				String referencedTab = ref.getReferencedTab();
				String referencingTab = ref.getReferencingTab();
				System.out.println(referencedTab+" "+referencingTab);
				Line line = new Line();

				for(Node tbl: collectionAncPane.getChildren()){
					TreeView<String> tree = (TreeView) tbl;
					String tableName = tree.getRoot().getValue().toString();
					System.out.println(tableName); 
					if(tableName.equals(referencedTab)) {
						line.setStartX(tree.getLayoutX()+tree.getMaxWidth());
						line.setStartY(tree.getLayoutY()+(tree.getMaxHeight()/2));
					}
					if(tableName.equals(referencingTab)) {
						line.setEndX(tree.getLayoutX());
						line.setEndY(tree.getLayoutY());
					}

					//for(TreeItem col: tbl.getC)
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

}
