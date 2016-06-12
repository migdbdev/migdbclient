/**
 * 
 */
package org.migdb.migdbclient.resources;

/**
 * @author Kani
 *
 */
public enum Session {
	INSTANCE;

	private String activeDB;

	public String getActiveDB() {
		return activeDB;
	}

	public void setActiveDB(String activeDB) {
		this.activeDB = activeDB;
	}

}
