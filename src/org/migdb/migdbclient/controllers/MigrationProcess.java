/**
 * 
 */
package org.migdb.migdbclient.controllers;

import java.io.IOException;

import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.resources.CenterLayout;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * @author Kani
 *
 */
public class MigrationProcess {
	
	@FXML
	public void initialize() throws IOException {
		
		DumpGenerator obj = new DumpGenerator();
		obj.generateDump();
		
		Stage newConnectionStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FxmlPath.MANYTOMANYSELECTOR.getPath()));
		Scene scene = new Scene(loader.load());
		newConnectionStage.setScene(scene);
		newConnectionStage.setAlwaysOnTop(true);
		newConnectionStage.setResizable(false);
		newConnectionStage.centerOnScreen();
		newConnectionStage.show();
		
		
	}
	
}
