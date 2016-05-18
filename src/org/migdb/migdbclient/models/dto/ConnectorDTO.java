package org.migdb.migdbclient.models.dto;

public class ConnectorDTO {
	
	private String connectionName;
	private String hostName;
	private int port;
	private String userName;
	private String password;
	private String schemaName;
	
	public ConnectorDTO() {
	}

	public ConnectorDTO(String connectionName, String hostName, int port, String userName, String password,
			String schemaName) {
		super();
		this.connectionName = connectionName;
		this.hostName = hostName;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.schemaName = schemaName;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

}
