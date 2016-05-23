package org.migdb.migdbclient.controllers.dbconnector;

import java.sql.Connection;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class MongoDbConnManager {
	
	public MongoDatabase getConnection(String host, int port, String dbName){
		MongoDatabase dbConn = null;
		try {
			MongoClient mongoClient = new MongoClient(new ServerAddress(host,port));
			dbConn = mongoClient.getDatabase(dbName);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dbConn;
	}
	
	public void closeConnection(MongoDatabase dbConn){
		try {
			dbConn.drop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
