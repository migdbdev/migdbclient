package org.migdb.migdbclient.resources;

import java.util.Set;

import com.mongodb.DB;

public class databaseResource {
	private String name;
	private Set<String> collections;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<String> getCollections() {
		return collections;
	}
	public void setCollections(DB db ) {
		Set<String> collections = db.getCollectionNames();
		this.collections=collections;
	}

}
