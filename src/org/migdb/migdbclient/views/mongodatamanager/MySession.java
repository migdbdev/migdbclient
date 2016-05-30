package org.migdb.migdbclient.views.mongodatamanager;

import javafx.scene.layout.AnchorPane;

public enum MySession {
	INSTANCE;
	
	private AnchorPane root;
	
	public AnchorPane getRootContainer() {
		return this.root;
	}
	
	public void setRoot(AnchorPane root) {
		this.root = root;
	}
	
}
