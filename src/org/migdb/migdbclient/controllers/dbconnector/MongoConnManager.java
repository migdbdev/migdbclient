package org.migdb.migdbclient.controllers.dbconnector;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public enum MongoConnManager {
	
	INSTANCE;
	
	private MongoClient client = null;
	
	public MongoClient connect(String host, int port) throws Exception {
		if(client == null) {
			client = new MongoClient(new MongoClientURI("mongodb://"+host+":"+port));
		}
		
		return client;	
	}
	
	public MongoDatabase connectToDatabase(String database) throws Exception {
		//MongoClient client = connect();
		
		return client.getDatabase(database);
	}

}
