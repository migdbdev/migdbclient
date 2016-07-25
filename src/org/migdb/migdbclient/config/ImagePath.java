package org.migdb.migdbclient.config;

public enum ImagePath {
	
	FAVICON("/org/migdb/migdbclient/resources/images/MigDB.png"),
	DBICON("/org/migdb/migdbclient/resources/images/dbicon.png"),
	TABDBCONNECTION("/org/migdb/migdbclient/resources/images/DBConn.png"),
	TABMIGRATION("/org/migdb/migdbclient/resources/images/Migrate.jpg"),
	TABCONVERTER("/org/migdb/migdbclient/resources/images/Convert.jpeg"),
	TABGENERATOR("/org/migdb/migdbclient/resources/images/Generator.png"),
	TABDATAMANAGER("/org/migdb/migdbclient/resources/images/Manager.jpeg");
	
	private String path;
	
	ImagePath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
