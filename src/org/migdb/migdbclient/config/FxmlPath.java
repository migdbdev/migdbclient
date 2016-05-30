package org.migdb.migdbclient.config;

public enum FxmlPath {
	
	ROOTLAYOUT("/org/migdb/migdbclient/views/root/RootLayout.fxml"),
	CONNECTIONMANAGER("/org/migdb/migdbclient/views/connectionmanager/ConnectionManager.fxml"),
	MODIFICATIONEVALUATOR("/org/migdb/migdbclient/views/modificationevaluator/ModificationEvaluator.fxml"),
	DATAMANAGER("/org/migdb/migdbclient/views/mongodatamanager/MongoDataManager.fxml"),
	NEWDBCONNECTION("/org/migdb/migdbclient/views/connectionmanager/NewDBConnection.fxml"),
	COLLECTIONMANAGER("/org/migdb/migdbclient/views/mongodatamanager/CollectionManager.fxml");
	
	private String path;
	
	FxmlPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
