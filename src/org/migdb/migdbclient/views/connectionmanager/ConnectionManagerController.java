package org.migdb.migdbclient.views.connectionmanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.controllers.cachemanager.MemoryCacheManager;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.models.dto.ConnectorDTO;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ConnectionManagerController implements Initializable {

	@FXML
	private AnchorPane rootLayoutAnchorpane;
	@FXML
	AnchorPane connectionAnchorpane;
	@FXML
	private Label addConnectionLabel;

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Add connection label click event
		addConnectionLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				createNewConnectionPopup();
			}
		});
		
		connectionInfoSet();

	}

	/**
	 * Create new connection manager pop up window method
	 */
	public void createNewConnectionPopup() {

		try {
			Stage newConnectionStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.NEWDBCONNECTION.getPath()));
			rootLayoutAnchorpane = loader.load();
			newConnectionStage.setTitle("Create a new connection");
			newConnectionStage.setAlwaysOnTop(true);
			newConnectionStage.setResizable(false);
			newConnectionStage.centerOnScreen();
			Scene scene = new Scene(rootLayoutAnchorpane);
			newConnectionStage.setScene(scene);
			newConnectionStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void connectionInfoSet(){
		String connName, uName, mysqlHost, mongoHost = null;
		int mysqlPort, mongoPort = 0;
		Double x = 10.0;
		Double y = 10.0;

		SqliteDAO dao = new SqliteDAO();
		ArrayList<ConnectorDTO> connection = dao.getConnectionInfo();

		if (!connection.isEmpty()) {
			for (ConnectorDTO dto : connection) {
				connName = dto.getConnectionName();
				uName = dto.getUserName();
				mysqlHost = dto.getMysqlHostName();
				mongoHost = dto.getMongoHostName();
				mysqlPort = dto.getMysqlPort();
				mongoPort = dto.getMongoPort();

				connectionAnchorpane.getChildren()
						.add(displayConnectionInfo(x, y, connName, uName, mysqlHost, mongoHost, mysqlPort, mongoPort));

				if (x > 800) {
					x = 10.0;
					y = y + 120.0;
				} else {
					x = x + 120;
				}
			}
		} else {
			Label noConn = new Label("No connections available");
			noConn.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
			connectionAnchorpane.getChildren().add(noConn);
			// connectionAnchorpane.setStyle("-fx-background-image:url(/org/migdb/migdbclient/resources/images/connection_add_hover.png)");
		}
	}

	public VBox displayConnectionInfo(Double x, Double y, String connName, String uName, String mysqlHost,
			String mongoHost, int mysqlPort, int mongoPort) {
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10));
		vbox.setSpacing(8);
		vbox.setLayoutX(x);
		vbox.setLayoutY(y);
		vbox.setStyle("-fx-background-color: #336699;");

		Text connectionName = new Text(connName);
		connectionName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		vbox.getChildren().add(connectionName);

		Label userName = new Label(uName, new Glyph("FontAwesome", FontAwesome.Glyph.USER));
		userName.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
		Label mysqlHostPort = new Label(mysqlHost + ":" + mysqlPort, new Glyph("FontAwesome", FontAwesome.Glyph.RSS));
		mysqlHostPort.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
		Label mongoHostPort = new Label(mongoHost + ":" + mongoPort, new Glyph("FontAwesome", FontAwesome.Glyph.RSS));
		mongoHostPort.setFont(Font.font("Arial", FontWeight.NORMAL, 10));

		vbox.getChildren().add(userName);
		vbox.getChildren().add(mysqlHostPort);
		vbox.getChildren().add(mongoHostPort);

		// Mouse clicked event
		vbox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				calla(connName);
				rootLayoutAnchorpane.getChildren().clear();
			}
		});

		// Mouse entered event
		// Add hover effect
		vbox.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				vbox.setStyle("-fx-background-color: #1815DE;");
			}
		});

		// Mouse exit event
		// Remove hover effect
		vbox.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseevent) {
				vbox.setStyle("-fx-background-color: #336699;");
			}
		});

		return vbox;
	}

	public void calla(String conn) {
		String re = null;
		re = conn;
		System.out.println(re);
	}
	


}
