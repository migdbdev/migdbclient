package org.migdb.migdbclient.models.mongodatamanager;

import java.util.ArrayList;

public class ObjectContainer implements JsonGeneratable {

	private ArrayList<JsonGeneratable> objectContainer = new ArrayList<>();
	
	public ArrayList<JsonGeneratable> getObjectContainer() {
		return objectContainer;
	}
	
	public void setObjectContainer(ArrayList<JsonGeneratable> objectContainer) {
		this.objectContainer = objectContainer;
	}
	
	@Override
	public String generateJson() {
		String result = "{";
		for (JsonGeneratable item : objectContainer) {
			result += item.generateJson()+" , ";
			// add comma
		}
		return result + "}";
	}

}
