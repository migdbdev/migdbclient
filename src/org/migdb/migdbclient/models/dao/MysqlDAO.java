package org.migdb.migdbclient.models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.migdb.migdbclient.controllers.dbconnector.MySQLDbConnManager;

public class MysqlDAO {
	
	MySQLDbConnManager dbConnManager = null;
	
	public MysqlDAO(){
		dbConnManager = new MySQLDbConnManager();
	}
	
	public ArrayList<String> getDetails(String host, int port, String database, String username, String password){
		ArrayList<String> deta = null;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection(host, port, database, username, password);
			String query = "SELECT * FROM classified";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet re = ps.executeQuery();
			deta = new ArrayList<String>();
			
			while(re.next()){
				deta.add(re.getString("classifiedId"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		
		return deta;
	}

}
