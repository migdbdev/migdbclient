package org.migdb.migdbclient.views.connectionmanager;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;
import org.migdb.migdbclient.config.ConnectionManager;
import org.migdb.migdbclient.controllers.dbconnector.MySQLDbConnManager;
import org.migdb.migdbclient.models.dao.MysqlDAO;
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.models.dto.ConnectorDTO;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SetupNewDBConnectionController implements Initializable {

	@FXML
	private AnchorPane rootLayoutAnchorpane;
	@FXML
	private AnchorPane mysqlLayoutAnchorpane;
	@FXML
	private AnchorPane mongoLayoutAnchorpane;
	@FXML
	private TextField connectionNameTextField;
	@FXML
	private TextField mysqlHostTextField;
	@FXML
	private TextField mysqlPortTextField;
	@FXML
	private TextField mysqlUsernameTextField;
	@FXML
	private TextField mysqlPasswordTextField;
	@FXML
	private TextField mongoHostTexField;
	@FXML
	private TextField mongoPortTextField;
	@FXML
	private TextField mongoScematextField;
	@FXML
	private Label mysqlLabel;
	@FXML
	private Label mongoLabel;
	@FXML
	private Label mysqlBackLabel;
	@FXML
	private Label mongoBackLabel;
	@FXML
	private Button submitButton;

	FXMLLoader loader = new FXMLLoader();

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Set visible false when page loaded
		mysqlLayoutAnchorpane.setVisible(false);
		mongoLayoutAnchorpane.setVisible(false);

		// MySQL label click event
		mysqlLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				mysqlPopup();
				/*MysqlDAO dao = new MysqlDAO();
				String host ="localhost",database = "bawwadb",username="root",password="123";
				int port = 3306;
				System.out.println(dao.getDetails(host, port, database, username, password));*/
				/*MySQLDbConnManager db = new MySQLDbConnManager();
				String host ="localhost",database = "bawwadb",username="root",password="123";
				int port = 3306;
				System.out.println(db.getConnection(host, port, database, username, password));*/
			}
		});

		// Mongo label click event
		mongoLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				mongoPopup();
			}
		});

		// Mysql back label click event
		mysqlBackLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				back(mouseevent);
			}
		});

		// Mongo back label click event
		mongoBackLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				back(mouseevent);
			}
		});
		
		submitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				insertConnection();
			}
		});
	}

	/**
	 * MySQL layout popup method
	 */
	public void mysqlPopup() {
		try {
			rootLayoutAnchorpane.setVisible(false);
			mysqlLayoutAnchorpane.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Mongo layout popup method
	 */
	public void mongoPopup() {
		try {
			rootLayoutAnchorpane.setVisible(false);
			mongoLayoutAnchorpane.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Back to root layout anchor pane from mysql or mongo layout
	 * @param e
	 */
	public void back(Event e) {
		if ((((Node) e.getSource()).getId()).equals(ConnectionManager.MYSQLBACKLABELID.getConnManager())) {
			mysqlLayoutAnchorpane.setVisible(false);
		} else {
			mongoLayoutAnchorpane.setVisible(false);
		}
		rootLayoutAnchorpane.setVisible(true);
	}

	/**
	 * Connector data transfer model
	 * @return
	 */
	public ConnectorDTO connectionSave() {
		ConnectorDTO dto = new ConnectorDTO();
		dto.setConnectionName(connectionNameTextField.getText());
		dto.setMysqlHostName(mysqlHostTextField.getText());
		dto.setMongoHostName(mongoHostTexField.getText());
		dto.setMysqlPort(Integer.parseInt(mysqlPortTextField.getText()));
		dto.setMongoPort(Integer.parseInt(mongoPortTextField.getText()));
		dto.setUserName(mysqlUsernameTextField.getText());
		dto.setPassword(mysqlPasswordTextField.getText());
		dto.setSchemaName(mongoScematextField.getText());
		return dto;
	}
	
	/**
	 * Connection information save method
	 */
	public void insertConnection() {
		SqliteDAO dao = new SqliteDAO();
		boolean result = dao.insertConnection(connectionSave());
		if(result==true){
			Stage stage = (Stage) submitButton.getScene().getWindow();
			stage.close();
			Notifications.create().title("Attention").text("Succesfully inserted").darkStyle().showInformation();
		} else {
			Notifications.create().title("Attention").text("It seems to be error. Please check again").darkStyle().showError();
		}
	}

}
