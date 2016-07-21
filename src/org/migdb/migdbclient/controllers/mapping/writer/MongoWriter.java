package org.migdb.migdbclient.controllers.mapping.writer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.controllers.dbconnector.MongoConnManager;
import org.migdb.migdbclient.resources.MongoDBResource;

import com.mongodb.client.MongoDatabase;

public class MongoWriter {

	private JSONObject mappedJson;

	public MongoWriter() {
		super();
		JSONParser parser = new JSONParser();
		try {
			Object object = parser
					.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath()));
			mappedJson = (JSONObject) object;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write() {
		System.out.println(mappedJson);
		JSONArray collections = (JSONArray) mappedJson.get("collections");
		System.out.println(collections);

		MongoDBResource.INSTANCE.setDB("testMongo");
		MongoDatabase database = MongoDBResource.INSTANCE.getDatabase();
		for (int i = 0; i < collections.size(); i++) {
			database.createCollection("collectionName");
		}
		

	}
}
