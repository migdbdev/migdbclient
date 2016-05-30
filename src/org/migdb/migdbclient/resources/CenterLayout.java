package org.migdb.migdbclient.resources;

import javafx.scene.layout.AnchorPane;

public enum CenterLayout {
	INSTANCE;
	
	private AnchorPane root;
	
	public AnchorPane getRootContainer() {
		return this.root;
	}
	
	public void setRoot(AnchorPane root) {
		this.root = root;
	}
	
}
