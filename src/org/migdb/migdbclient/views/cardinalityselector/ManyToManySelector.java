/**
 * 
 */
package org.migdb.migdbclient.views.cardinalityselector;

import java.util.ArrayList;
import java.util.List;

import org.migdb.migdbclient.controllers.CardinalityMap;
import org.migdb.migdbclient.models.dao.MysqlDAO;
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.models.dto.ReferenceDTO;
import org.migdb.migdbclient.resources.ConnectionParameters;
import org.migdb.migdbclient.resources.Session;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * @author Kani
 *
 */
public class ManyToManySelector {
	
	@FXML private TableView<ReferenceDTO> relationshipTableview;
	@FXML private TableColumn<ReferenceDTO, Boolean> selectTablecolumn;
	@FXML private TableColumn<ReferenceDTO, String> tableTablecolumn;
	@FXML private TableColumn<ReferenceDTO, String> columnTablecolumn;
	@FXML private TableColumn<ReferenceDTO, String> referencedTableTablecolumn;
	@FXML private TableColumn<ReferenceDTO, String> referencedColumnTablecolumn;
	
	@FXML private Button okButton;
	
	private ObservableList<ReferenceDTO> referenceList = FXCollections.observableArrayList();
	
	public ManyToManySelector(){
		
	}
	
	@FXML
	public void initialize() {
		MysqlDAO dao = new MysqlDAO();
		String host = ConnectionParameters.SESSION.getMysqlHostName();
		int port = ConnectionParameters.SESSION.getMysqlPort();
		String database = Session.INSTANCE.getActiveDB();
		String username = ConnectionParameters.SESSION.getUserName();
		String password = ConnectionParameters.SESSION.getPassword();
		
		for(ReferenceDTO dto : dao.getReferencedList(host, port, database, username, password)){
			referenceList.add(dto);
		}
		
		selectTablecolumn.setCellFactory(new Callback<TableColumn<ReferenceDTO,Boolean>, TableCell<ReferenceDTO,Boolean>>() {
			
			@Override
			public TableCell<ReferenceDTO, Boolean> call(TableColumn<ReferenceDTO, Boolean> param) {
				return new CheckBoxCell();
			}
		});
		tableTablecolumn.setCellValueFactory(new PropertyValueFactory<ReferenceDTO, String>("tableName"));
		columnTablecolumn.setCellValueFactory(new PropertyValueFactory<ReferenceDTO, String>("columnName"));
		referencedTableTablecolumn.setCellValueFactory(new PropertyValueFactory<ReferenceDTO, String>("referencedTableName"));
		referencedColumnTablecolumn.setCellValueFactory(new PropertyValueFactory<ReferenceDTO, String>("referencedColumnName"));
		
		relationshipTableview.setItems(referenceList);
		
		okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				List<Integer> selectedList = CheckBoxCell.getSelectedList();
				for(int mm : selectedList){
					ReferenceDTO manyToManyTables = relationshipTableview.getItems().get(mm);
					Session.INSTANCE.setManyToManyTables(manyToManyTables);
				}
				CardinalityMap cmObj = new CardinalityMap();
				cmObj.cardinalityMap();
				Stage stage = (Stage) okButton.getScene().getWindow();
				stage.close();
			}
		});
		
	}
	
}

class CheckBoxCell extends TableCell<ReferenceDTO, Boolean> {
	final CheckBox cellCheckBox = new CheckBox();
	private static List<CheckBox> checkList = new ArrayList<CheckBox>();
	static int index = -1;
	private static List<Integer> selectedIndexes = new ArrayList<Integer>();
	private Integer indexOfmine;

	public CheckBoxCell() {
		indexOfmine = (Integer)index++;
		checkList.add(cellCheckBox);
		cellCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if(newValue) {
					selectedIndexes.add(indexOfmine);
				}else {
					selectedIndexes.remove(indexOfmine);
				}
			}
		});
	}

	@Override
	protected void updateItem(Boolean t, boolean empty) {
		super.updateItem(t, empty);
		if(!empty){
			setAlignment(Pos.CENTER);
			setGraphic(cellCheckBox);
		}
	}

	public static List<CheckBox> getList() {
		return checkList;
	}

	public static List<Integer> getSelectedList() {
		return selectedIndexes;
	}
}
