package org.migdb.migdbclient.models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.migdb.migdbclient.controllers.dbconnector.SqliteDbConnManager;
import org.migdb.migdbclient.models.dto.ConnectorDTO;
import org.migdb.migdbclient.models.dto.ReferenceDTO;

public class SqliteDAO {

	SqliteDbConnManager dbConnManager = null;

	/**
	 * Create database connection manager object from SqliteDbConnManager class
	 */
	public SqliteDAO() {
		dbConnManager = new SqliteDbConnManager();
	}

	/**
	 * Create table called connections if not exists
	 */
	public void createTable() {
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
	 * 
	 * @param dto
	 * @return
	 */
	public boolean insertConnection(ConnectorDTO dto) {
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
			result = (val == 1) ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}

		return result;
	}

	/**
	 * Get connection information from database
	 * 
	 * @return
	 */
	public ArrayList<ConnectorDTO> getConnectionInfo() {
		ArrayList<ConnectorDTO> connectionInfo = null;
		Connection dbConn = null;

		try {
			dbConn = dbConnManager.getConnection();
			String query = "SELECT * FROM CONNECTIONS";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			connectionInfo = new ArrayList<ConnectorDTO>();
			while (rs.next()) {
				ConnectorDTO connector = new ConnectorDTO();
				connector.setConnectionName(rs.getString("ConnectionName"));
				connector.setMysqlHostName(rs.getString("mysqlHostName"));
				connector.setMongoHostName(rs.getString("mongoHostName"));
				connector.setMysqlPort(rs.getInt("mysqlPort"));
				connector.setMongoPort(rs.getInt("mongoPort"));
				connector.setUserName(rs.getString("UserName"));
				connector.setPassword(rs.getString("Password"));
				connector.setSchemaName(rs.getString("SchemaName"));
				connectionInfo.add(connector);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return connectionInfo;
	}
	
	public void createRelationshipTypes(){
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection();
			String query = "CREATE TABLE IF NOT EXISTS `REFERENCEDLIST` (`TABLE_NAME` TEXT,`COLUMN_NAME` TEXT,`REFERENCED_TABLE_NAME` TEXT,`REFERENCED_COLUMN_NAME` TEXT, `RELATIONSHIP_TYPE` TEXT);";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
	}
	
	public void insertRelationshipTypes(String tablesName, String columnName, String referencedTableName, String referencedColumnName, String relationshipType){
		boolean result = false;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection();
			String query = "INSERT INTO REFERENCEDLIST(TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME, RELATIONSHIP_TYPE) VALUES(?,?,?,?,?)";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ps.setString(1, tablesName);
			ps.setString(2, columnName);
			ps.setString(3, referencedTableName);
			ps.setString(4, referencedColumnName);
			ps.setString(5, relationshipType);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
	}

}
