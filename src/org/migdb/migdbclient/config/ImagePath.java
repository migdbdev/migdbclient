package org.migdb.migdbclient.config;

public enum ImagePath {
	
	FAVICON("/org/migdb/migdbclient/resources/images/MigDB.png");
	
	private String path;
	
	ImagePath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
