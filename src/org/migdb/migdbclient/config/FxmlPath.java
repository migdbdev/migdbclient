package org.migdb.migdbclient.config;

public enum FxmlPath {
	
	ROOTLAYOUT("/org/migdb/migdbclient/views/root/RootLayout.fxml"),
	CONNECTIONMANAGER("/org/migdb/migdbclient/views/connectionmanager/ConnectionManager.fxml"),
	MODIFICATIONEVALUATOR("/org/migdb/migdbclient/views/modificationevaluator/ModificationEvaluator.fxml"),
	DATAMANAGER("/org/migdb/migdbclient/views/mongodatamanager/MongoDataManager.fxml"),
	NEWDBCONNECTION("/org/migdb/migdbclient/views/connectionmanager/NewDBConnection.fxml"),
	COLLECTIONMANAGER("/org/migdb/migdbclient/views/mongodatamanager/CollectionManager.fxml"),
	DOCUMENTMANAGER("/org/migdb/migdbclient/views/mongodatamanager/DocumentManager.fxml"),
	QUERYCONVERTER("/org/migdb/migdbclient/views/queryconverter/QueryConverter.fxml"),
	QUERYGENERATOR("/org/migdb/migdbclient/views/queryGenerator/QueryGenerator.fxml"),
	MONGODBPATHBROWSE("/org/migdb/migdbclient/views/filechooser/FileChooserDBPath.fxml"),
	INTERNETCONNECTIVITY("/org/migdb/migdbclient/views/connectivity/InternetConnectivity.fxml"),
	COLLECTIONSTRUCTURE("/org/migdb/migdbclient/views/collectionstructure/CollectionStructure.fxml"),
	NEW_DOC("/org/migdb/migdbclient/views/mongodatamanager/NewDocument.fxml");
	
	private String path;
	
	FxmlPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
