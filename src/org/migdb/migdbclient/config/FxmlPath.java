package org.migdb.migdbclient.config;

public enum FxmlPath {
	
	ROOTLAYOUT("/org/migdb/migdbclient/views/root/RootLayout.fxml");
	
	private String path;
	
	FxmlPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
