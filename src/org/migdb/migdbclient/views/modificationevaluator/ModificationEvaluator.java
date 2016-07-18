package org.migdb.migdbclient.views.modificationevaluator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.controlsfx.control.CheckTreeView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.config.TreeviewSize;
import org.migdb.migdbclient.controllers.mapping.manytomany.ManyToMany;
import org.migdb.migdbclient.controllers.mapping.onetomany.OneToManyMapper;
import org.migdb.migdbclient.controllers.mapping.onetoone.OneToOneMap;
import org.migdb.migdbclient.models.modificationevaluator.TableReference;
import org.migdb.migdbclient.resources.CenterLayout;
import org.migdb.migdbclient.resources.LayoutInstance;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class ModificationEvaluator {

	@FXML
	private AnchorPane modificationEvalAnchor;
	@FXML
	private ScrollPane modificationEvalScroll;
	@FXML Button nextButton;

	JSONArray tableList = new JSONArray();
	ArrayList<TableReference> fkArr = new ArrayList<TableReference>();
	ArrayList<String> removedTbls = new ArrayList<String>();
	LinkedHashMap<String, ArrayList<String>> removedCols = new LinkedHashMap<String, ArrayList<String>>();
	JSONArray deletedItems = new JSONArray();

	JSONParser parser = new JSONParser();
	Object obj;
	JSONObject jsonObject;
	File file = new File(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath());

	@FXML
	private void initialize() {

		try {
			obj = parser.parse(new FileReader(file));
			jsonObject = (JSONObject) obj;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		generateTreeView();
		
		nextButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				AnchorPane root;
				root = CenterLayout.INSTANCE.getRootContainer();
				root.getChildren().clear();
				
				OneToOneMap ooObj = new OneToOneMap();
				ooObj.oneToOneMapper();
				
				/*OneToManyMapper omObj = new OneToManyMapper();
				omObj.mapOneToMany();
				
				ManyToMany many = new ManyToMany();
				many.identifyM2M();*/
			}
		});

	}

	EventHandler getCheckBoxSelectionHandler() {
		return new EventHandler() {
			@Override
			public void handle(Event e) {
				ArrayList<String> removedColArr = new ArrayList<String>();
				CheckBoxTreeItem<String> item = (CheckBoxTreeItem<String>) e.getSource();
				CheckBoxTreeItem<String> parent = (CheckBoxTreeItem<String>) item.getParent();
				System.out.println(parent.isSelected());
				if ((!parent.isSelected()) && (!removedTbls.contains(parent.getValue()))) {
					System.out.println("table deleted " + parent.getValue());
					removedTbls.add(parent.getValue());
				} else if (parent.isSelected()) {
					if (removedTbls.contains(parent.getValue())) {
						removedTbls.remove(parent.getValue());
						System.out.println("table added back " + parent.getValue());
					}
					if ((!item.isSelected()) && (item.isLeaf())) {
						if (removedCols.isEmpty()) {
							System.out.println("removed col object added " + item.getValue());
							removedColArr.add(item.getValue());
							removedCols.put(item.getParent().getValue(), removedColArr);
							System.out.println("Removed col " + removedCols.toString());
						} else {
							if (removedCols.keySet().contains(parent.getValue())) {
								System.out.println("removed col added " + item.getValue());
								removedCols.get(parent.getValue()).add(item.getValue());
								System.out.println(parent.getValue() + " " + removedCols.get(parent.getValue()));
							} else {
								System.out.println("removed col obj added " + item.getValue());
								removedColArr.add(item.getValue());
								removedCols.put(item.getParent().getValue(), removedColArr);
								System.out.println("Removed col " + removedCols.toString());
							}
						}
					} else if ((item.isSelected()) && (item.isLeaf())) {
						System.out.println("removed col added back " + item.getValue());
						for (String colKey : removedCols.keySet()) {
							if (colKey.equals(item.getParent().getValue())) {
								removedCols.get(colKey).remove(item.getValue());
							}
						}
					}
				}
			}
		};
	}

	private void generateTreeView() {

		try {

			tableList = (JSONArray) jsonObject.get("tables");

			Iterator<JSONObject> iterator = tableList.iterator();
			double x = TreeviewSize.XSTART.getSize();
			double y = TreeviewSize.YSTART.getSize();

			while (iterator.hasNext()) {
				JSONObject tbl = (JSONObject) iterator.next();
				String name = (String) tbl.get("name");

				CheckBoxTreeItem<String> root = new CheckBoxTreeItem<String>(name);
				root.setSelected(true);
				root.setExpanded(true);

				JSONArray columnList = (JSONArray) tbl.get("columns");
				Iterator<JSONObject> cols = columnList.iterator();
				JSONArray refColumnList = (JSONArray) tbl.get("referencedBy");

				if (!refColumnList.isEmpty()) {
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
					column.setSelected(true);
					root.getChildren().add(column);
					column.addEventHandler(column.checkBoxSelectionChangedEvent(), getCheckBoxSelectionHandler());
				}

				CheckTreeView<String> treeView = new CheckTreeView<String>(root);
				treeView.setEditable(true);
				treeView.setCellFactory(CheckBoxTreeCell.forTreeView());

				treeView.setMaxHeight(TreeviewSize.TREEVIEWHEIGHT.getSize());
				treeView.setMaxWidth(TreeviewSize.TREEVIEWIDTH.getSize());
				treeView.setLayoutX(x);
				treeView.setLayoutY(y);

				modificationEvalAnchor.getChildren().add(treeView);

				double nextX = x + (TreeviewSize.XSPACE.getSize()) + 2 * (TreeviewSize.TREEVIEWIDTH.getSize());
				if (modificationEvalAnchor.getPrefWidth() <= nextX) {
					x = TreeviewSize.XSTART.getSize();
					y = y + TreeviewSize.YSTART.getSize() + (TreeviewSize.YSPACE.getSize())
							+ (TreeviewSize.TREEVIEWHEIGHT.getSize());
				} else {
					x = x + (TreeviewSize.XSPACE.getSize()) + (TreeviewSize.TREEVIEWIDTH.getSize());
				}

				double nextY = y + (TreeviewSize.YSPACE.getSize()) + (TreeviewSize.TREEVIEWHEIGHT.getSize());
				if (modificationEvalAnchor.getPrefHeight() <= nextY) {
					modificationEvalAnchor.setPrefHeight(
							y + (TreeviewSize.YSPACE.getSize()) + (TreeviewSize.TREEVIEWHEIGHT.getSize()));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void drawLine() {
		ArrayList<Line> lines = new ArrayList<>();
		for (TableReference ref : fkArr) {
			String referencedTab = ref.getReferencedTab();
			String referencingTab = ref.getReferencingTab();
			System.out.println(referencedTab + " " + referencingTab);
			Line line = new Line();

			for (Node tbl : modificationEvalAnchor.getChildren()) {
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
			}

			line.setStrokeWidth(2);
			line.setStroke(Color.BLACK);

			lines.add(line);
		}

		for (Line line : lines) {
			modificationEvalAnchor.getChildren().add(line);
		}
	}

	@FXML
	private void evaluate() {
		try {
			File jsonFile = new File(FilePath.DOCUMENT.getPath() + FilePath.DELETEDITEMFILE.getPath());
			boolean exist = jsonFile.exists();
			int errorCount = 0;
			String message = "";
			List<Integer> removedTblIndex = new ArrayList<Integer>();
			LinkedHashMap<Integer, ArrayList<Integer>> removedColIndex = new LinkedHashMap<Integer, ArrayList<Integer>>();
			LinkedHashMap<Integer, ArrayList<Integer>> removedRefByIndex = new LinkedHashMap<Integer, ArrayList<Integer>>();
			LinkedHashMap<Integer, ArrayList<Integer>> removedRefFromIndex = new LinkedHashMap<Integer, ArrayList<Integer>>();

			// loop through the json table objects
			for (int i = 0; i < tableList.size(); i++) {
				JSONObject table = (JSONObject) tableList.get(i);
				String name = (String) table.get("name");
				JSONArray referenceByList = (JSONArray) table.get("referencedBy");
				JSONArray referenceFromList = (JSONArray) table.get("referencingFrom");
				String primaryKey = (String) table.get("primaryKey");

				if (!removedCols.isEmpty()) {

					JSONArray cols = (JSONArray) table.get("columns");
					int colCount = cols.size();
					// loop though the removed java column o0bjects
					for (String colKey : removedCols.keySet()) {
						if (colKey.equals(name)) {
							int removedColCount = removedCols.get(colKey).size();
							// if all columns are deleted, add to tables to be
							// removed
							if (removedColCount == colCount) {
								removedTbls.add(name);
							} else {
								ArrayList<Integer> colIndex = new ArrayList();
								ArrayList<Integer> refByIndex = new ArrayList();
								ArrayList<Integer> refFromIndex = new ArrayList();
								JSONArray removedColList = new JSONArray();
								JSONArray removedRefByList = new JSONArray();
								JSONArray removedRefFromList = new JSONArray();
								// loop through the json object columns array
								for (int k = 0; k < colCount; k++) {
									JSONObject column = (JSONObject) cols.get(k);
									String colName = (String) column.get("colName");
									System.out.println("Colllll " + colName);
									// check current column is to be removed
									if (removedCols.get(colKey).contains(colName)) {
										// remove column if the current table
										// object does not have references
										if (referenceByList.isEmpty()) {
											System.out.println("Adding col index " + k + " because list is empty");
											colIndex.add(k);
											removedColList.add(cols.get(k));
											System.out.println("size " + colIndex.size());
										} else {
											List<String> colReferencingTbls = new ArrayList<String>();
											// loop through the reference
											// objects
											for (int l = 0; l < referenceByList.size(); l++) {
												JSONObject ref = (JSONObject) referenceByList.get(l);
												String refCol = (String) ref.get("referencedCol");
												// check whether current column
												// is referenced
												if (refCol.equals(colName)) {
													String refByTbl = (String) ref.get("referencingTab");
													// if referencing table is
													// not removed, check for
													// referencing column
													// removal
													if (!removedTbls.contains(refByTbl)) {
														System.out.println("table not deleted");
														String refByCol = (String) ref.get("referencingCol");
														if (removedCols.keySet().contains(refByTbl)) {
															for (String c : removedCols.keySet()) {
																System.out.println(c);
																if (c.equals(refByTbl)) {
																	System.out.println(
																			"referencing table found " + refByTbl);
																	System.out.println(
																			"referencing deleted columns found "
																					+ refByTbl);
																	// if referencing column in not removed, add constraint error
																	if (!removedCols.get(c).contains(refByCol)) {
																		System.out.println("column not deleted");
																		colReferencingTbls.add(refByTbl);
																	} else {
																		// add ref object
																		removedRefByList.add(referenceByList.get(l));
																		System.out.println("removed RefBy 1 "
																				+ removedRefByList.toJSONString());
																		System.out.println("refByIndex "+refByIndex.toString());
																		if(!refByIndex.contains(l)) {
																			System.out.println("not existing "+l);
																			refByIndex.add(l);
																		} else {
																			System.out.println("existing "+l);
																		}
																	}																	}
															}
														} else {
															colReferencingTbls.add(refByTbl);
														}

														if (colReferencingTbls.isEmpty()) {
															System.out.println("Adding col index " + k
																	+ " because there are no references");
															if (!colIndex.contains(k)) {
																colIndex.add(k);
																removedColList.add(cols.get(k));
															}
														} else {
															// add ref object
															removedRefByList.add(referenceByList.get(l));
															System.out.println("removed RefBy 2 "
																	+ removedRefByList.toJSONString());
															if(!refByIndex.contains(l)) {
																refByIndex.add(l);
															}
														}
													}
												} else {
													if (!colIndex.contains(k)) {
														System.out.println("Adding col index " + k
																+ " because table index exists");
														colIndex.add(k);
														removedColList.add(cols.get(k));
													}
												}
											}
											if (!colReferencingTbls.isEmpty()) {
												message = setMessage(message, colName, "column", colReferencingTbls);
												errorCount++;
											}
										}
										
										if(primaryKey.equals(colName)) {
											putJsonField(name,primaryKey,"primaryKey");
										}
									}
								}
								if (!colIndex.isEmpty()) {
									
									for (int x = 0; x < referenceFromList.size(); x++) {
										JSONObject ref = (JSONObject) referenceFromList.get(x);
										String refCol = (String) ref.get("referencingCol");
										System.out.println("refCol " + refCol);
										for (int y = 0; y < colIndex.size(); y++) {
											JSONObject col = (JSONObject) cols.get(colIndex.get(y));
											String colName = (String) col.get("colName");
											System.out.println("Col index col " + colName);
											if (refCol.equals(colName)) {
												System.out.println("is equal");
												removedRefFromList.add(ref);
												if(!refFromIndex.contains(x)) {
													refFromIndex.add(x);
												}
											}
										}
									}
									removedColIndex.put(i, colIndex);
									System.out.println("refByIndex "+refByIndex.size());
									if(!refByIndex.isEmpty()) {
										System.out.println("adding "+i+" "+refByIndex);
										removedRefByIndex.put(i, refByIndex);
									}
									if(!refFromIndex.isEmpty()) {
										removedRefFromIndex.put(i, refFromIndex);
									}
									System.out.println("removedRefByIndex "+removedRefByIndex);
									System.out.println("removedColIndex : " + removedColIndex.toString());
									putJsonArray(name,removedColList,"columns");
									putJsonArray(name,removedRefByList,"referencedBy");
									putJsonArray(name,removedRefFromList,"referencingFrom");
									/*JSONObject delColObject = new JSONObject();
									delColObject.put("name", name);
									delColObject.put("columns", removedColList);
									delColObject.put("referencedBy", removedRefByList);*/
									System.out.println("removedRefByList " + removedRefByList);
									/*delColObject.put("referencingFrom", removedRefFromList);*/
									System.out.println("removedRefFromList " + removedRefFromList);
									//deletedItems.add(delColObject);
									System.out.println("Deleted Items : " + deletedItems.toJSONString());
								}
							}
						}
					}

					if ((!referenceByList.isEmpty()) && (!removedTbls.contains(name)) && (!removedCols.containsKey(i)) ) {
						ArrayList<Integer> refByIndex = new ArrayList();
						for (int k = 0; k < colCount; k++) {
							JSONObject column = (JSONObject) cols.get(k);
							String colName = (String) column.get("colName");
							JSONArray removedRefByList = new JSONArray();
							for (int l = 0; l < referenceByList.size(); l++) {
								JSONObject ref = (JSONObject) referenceByList.get(l);
								String refCol = (String) ref.get("referencedCol");
								if (refCol.equals(colName)) {
									String refByTbl = (String) ref.get("referencingTab");
									// if referencing table is not removed, check for referencing column removal
									if (!removedTbls.contains(refByTbl)) {
										String refByCol = (String) ref.get("referencingCol");
										if (removedCols.keySet().contains(refByTbl)) {
											for (String c : removedCols.keySet()) {
												System.out.println("inside else" + c);
												if (c.equals(refByTbl)) {
													if (removedCols.get(c).contains(refByCol)) {
														// add ref object
														removedRefByList.add(referenceByList.get(l));
														System.out.println("else i "+i);
														System.out.println("else removedRefByIndex "+removedRefByIndex.containsKey(i));
														if(!removedRefByIndex.containsKey(i)) {
															System.out.println("else adding refByIndex "+l);
															refByIndex.add(l);
														} else {
															if(!removedRefByIndex.get(i).contains(l)) {
																System.out.println("else adding "+i+ " "+l);
																removedRefByIndex.get(i).add(l);
															}
														}
													}
												}
											}
										}
									}
								}
							}
							if (!removedRefByList.isEmpty()) {
								if(!refByIndex.isEmpty()) {
									removedRefByIndex.put(i, refByIndex);
								}
								/*JSONObject delColObject = new JSONObject();
								delColObject.put("name", name);
								delColObject.put("referencedBy", removedRefByList);*/
								System.out.println("else "+removedRefByIndex);
							/*	deletedItems.add(delColObject);*/
								putJsonArray(name,removedRefByList,"referencedBy");
							}
						}
					} 
				}

				if (removedTbls.contains(name)) {
					if (referenceByList.isEmpty()) {
						removedTblIndex.add(i);
						deletedItems.add(table);
					} else {
						List<String> referencingTbls = new ArrayList<String>();
						for (int k = 0; k < referenceByList.size(); k++) {
							JSONObject ref = (JSONObject) referenceByList.get(k);
							String refTbl = (String) ref.get("referencingTab");
							if (!removedTbls.contains(refTbl)) {
								referencingTbls.add(refTbl);
							}
						}
						message = setMessage(message, name, "table", referencingTbls);
						errorCount++;
					}
				}
			}

			JSONObject json = new JSONObject();
			JSONObject delObject;
			if (exist) {
				delObject = (JSONObject) parser.parse(new FileReader(jsonFile));
				JSONArray deletedList = (JSONArray) delObject.get("deletedItems");
				for (int i = 0; i < deletedItems.size(); i++) {
					deletedList.add(deletedItems.get(i));
				}
			} else {
				delObject = new JSONObject();
				delObject.put("deletedItems", deletedItems);
			}

			if (errorCount == 0) {
				String resultingJson = json.toJSONString(delObject);
				FileUtils.writeStringToFile(jsonFile, resultingJson);

				boolean isFirst = true;
				for (int tableIndex : removedTblIndex) {
					if (isFirst) {
						System.out.println("Removed" + tableList.get(tableIndex));
						tableList.remove(tableIndex);
						isFirst = false;
					} else {
						tableList.remove(--tableIndex);
					}
				}

				for (int colKeyIndex : removedColIndex.keySet()) {
					JSONObject table = (JSONObject) tableList.get(colKeyIndex);
					JSONArray cols = (JSONArray) table.get("columns");
					Long colCount = (Long) table.get("colCount");
					String primaryKey = (String) table.get("primaryKey");
					boolean isFirstCol = true;
					for (int colIndex : removedColIndex.get(colKeyIndex)) {
						System.out.println("Removed" + cols.get(colIndex));
						JSONObject column = (JSONObject) cols.get(colIndex);
						if (primaryKey.equals(column.get("colName"))) {
							table.remove("primaryKey");
						}
						if(isFirstCol) {
							cols.remove(colIndex);
							isFirstCol = false;
						} else {
							cols.remove(--colIndex);
						}
						colCount--;
					}
					table.remove("colCount");
					table.put("colCount", colCount);
				}
				
				System.out.println("deleting refBy "+removedRefByIndex);
				System.out.println("deleting ref from "+removedRefFromIndex);
				
				for (int refKeyIndex : removedRefByIndex.keySet()) {
					JSONObject table = (JSONObject) tableList.get(refKeyIndex);
					JSONArray refBy = (JSONArray) table.get("referencedBy");
					boolean isFirstRef = true;
					for (int refIndex : removedRefByIndex.get(refKeyIndex)) {
						if(isFirstRef) {
							refBy.remove(refIndex);
							isFirstRef = false;
						} else {
							refBy.remove(--refIndex);
						}
					}
				}
				
				for (int refKeyIndex : removedRefFromIndex.keySet()) {
					JSONObject table = (JSONObject) tableList.get(refKeyIndex);
					JSONArray refBy = (JSONArray) table.get("referencingFrom");
					boolean isFirstRef = true;
					for (int refIndex : removedRefFromIndex.get(refKeyIndex)) {
						if(isFirstRef) {
							refBy.remove(refIndex);
							isFirstRef = false;
						} else {
							refBy.remove(--refIndex);
						}
					}
				}

				String updatedson = json.toJSONString(jsonObject);
				FileUtils.writeStringToFile(file, updatedson);
				
				generateTreeView();
				removedTbls.clear();
				removedCols.clear();
				deletedItems.clear();

			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText(null);
				alert.setContentText(message);
				alert.showAndWait();
			}

		} catch (IOException | ParseException e1) {
			e1.printStackTrace();
		}
	}

	private String setMessage(String message, String itemName, String itemType, List<String> tblArray) {
		message += "Cannot remove " + itemType + " " + itemName + " due to foreign key reference(s) on " + "table(s) ";
		boolean isFirst = true;
		for (String tbl : tblArray) {
			if (isFirst) {
				message += tbl;
				isFirst = false;
			} else {
				message += ", " + tbl;
			}
		}
		return message += "\n";
	}
	
	
	private void putJsonField(String name, String value, String keyName) {
		System.out.println("inside putJsonField "+deletedItems.size());
		if(deletedItems.isEmpty()) {
			JSONObject newObject = new JSONObject();
			newObject.put("name", name);
			newObject.put(keyName, value);
			deletedItems.add(newObject);
		} else {
			for(int i =0; i < deletedItems.size(); i++) {
				JSONObject obj = (JSONObject) deletedItems.get(i);
				if(obj.get("name").equals(name)) {
					obj.put(keyName, value);
				} else {
					JSONObject newObject = new JSONObject();
					newObject.put("name", name);
					newObject.put(keyName, value);
					deletedItems.add(newObject);
				}
			}
		}
	}
	
	private void putJsonArray(String name, JSONArray object, String keyName) {
		System.out.println("inside putJsonArray "+deletedItems.size());
		int delItemsSize = deletedItems.size();
		if(deletedItems.isEmpty()) {
			System.out.println("empty");
			JSONObject newObject = new JSONObject();
			newObject.put("name", name);
			newObject.put(keyName, object);
			deletedItems.add(newObject);
			System.out.println("adding new object "+newObject);
			System.out.println("inside deletedItems "+deletedItems);
		} else {
			System.out.println("not empty "+deletedItems);
			boolean isFound = false;
			for(int i =0; i < delItemsSize; i++) {
				System.out.println("i "+i);
				System.out.println("deletedItems "+deletedItems);
				JSONObject obj = (JSONObject) deletedItems.get(i);
				String tableName = (String) obj.get("name");
				if(tableName.equals(name)) {
					isFound = true;
					System.out.println("is equal "+name+" "+tableName);
					JSONArray objArr = (JSONArray) obj.get(keyName);
					System.out.println("objArr "+objArr);
					if((objArr == null) || (objArr.isEmpty())) {
						System.out.println("arr empty");
						obj.put(keyName, object);
					} else {
						System.out.println("object size "+object.size()+ " object "+object);
						for(int k=0; k<object.size(); k++) {
							objArr.add(object.get(k));
						}
					}
				} 
			}
			if(!isFound) {
				JSONObject newObject = new JSONObject();
				newObject.put("name", name);
				newObject.put(keyName, object);
				deletedItems.add(newObject);
				System.out.println("adding new object else "+newObject);
			}
			System.out.println("inside deletedItems "+deletedItems);
		}
	}

}
