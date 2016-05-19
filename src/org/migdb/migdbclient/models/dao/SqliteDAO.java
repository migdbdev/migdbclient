package org.migdb.migdbclient.models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.migdb.migdbclient.controllers.dbconnector.SqliteDbConnManager;
import org.migdb.migdbclient.models.dto.ConnectorDTO;

public class SqliteDAO {
	
	SqliteDbConnManager dbConnManager = null;
	
	/**
	 * Create database connection manager object from SqliteDbConnManager class
	 */
	public SqliteDAO(){
		dbConnManager = new SqliteDbConnManager();
	}
	
	/**
	 * Create table called connections if not exists
	 */
	public void createTable(){
		Connection dbConn = null;
		
		String query = "CREATE TABLE IF NOT EXISTS `CONNECTIONS` (`ConnectionName` TEXT,`mysqlHostName` TEXT,`mongoHostName` TEXT,`mysqlPort` INTEGER, `mongoPort` INTEGER,`UserName` TEXT,`Password` TEXT,`SchemaName` TEXT,PRIMARY KEY(ConnectionName));";
		
		try {
			dbConn = dbConnManager.getConnection();
			PreparedStatement ps = dbConn.prepareStatement(query);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
	}
	
	/**
	 * Insert connection informations into database
	 * @param dto
	 * @return
	 */
	public boolean insertConnection(ConnectorDTO dto){
		boolean result = false;
		Connection dbConn = null;
		
		try {
			dbConn = dbConnManager.getConnection();
			String query = "INSERT INTO CONNECTIONS(ConnectionName,mysqlHostName,mongoHostName,mysqlPort,mongoPort,UserName,Password,SchemaName) VALUES(?,?,?,?,?,?,?,?)";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ps.setString(1, dto.getConnectionName());
			ps.setString(2, dto.getMysqlHostName());
			ps.setString(3, dto.getMongoHostName());
			ps.setInt(4, dto.getMysqlPort());
			ps.setInt(5, dto.getMongoPort());
			ps.setString(6, dto.getUserName());
			ps.setString(7, dto.getPassword());
			ps.setString(8, dto.getSchemaName());
			int val = ps.executeUpdate();
			result = (val==1) ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		
		return result;
	}

}
