package org.migdb.migdbclient.resources;

import javafx.scene.layout.VBox;

public enum LayoutInstance {
	INSTANCE;
	
	private VBox sidebar;

	public VBox getSidebar() {
		return sidebar;
	}

	public void setSidebar(VBox sidebar) {
		this.sidebar = sidebar;
	}

}
