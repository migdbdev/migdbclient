package org.migdb.migdbclient.resources;

import javafx.scene.layout.AnchorPane;

public enum LayoutInstance {
	INSTANCE;
	
	private AnchorPane sidebar;

	public AnchorPane getSidebar() {
		return sidebar;
	}

	public void setSidebar(AnchorPane sidebar) {
		this.sidebar = sidebar;
	}

}
