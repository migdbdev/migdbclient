/**
 * 
 */
package org.migdb.migdbclient.config;

/**
 * @author Kani
 *
 */
public enum FilePath {

	DOCUMENT(System.getProperty("user.home") + "\\" + "Documents\\MigDB"),
	XMLPATH("\\Xmldump.xml"),
	DBSTRUCTUREFILE("\\Database structure.json"),
	DELETEDITEMFILE("\\Item deleted.json"),
	COLLECTIONFILE("\\Collection.json");

	private String path;

	FilePath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
