package org.migdb.migdbclient.config;

public enum ImagePath {
	
	FAVICON("/org/migdb/migdbclient/resources/images/MigDB.png"),
	DBICON("/org/migdb/migdbclient/resources/images/dbicon.png");
	
	private String path;
	
	ImagePath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
