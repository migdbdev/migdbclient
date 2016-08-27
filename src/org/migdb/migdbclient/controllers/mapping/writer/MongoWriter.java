package org.migdb.migdbclient.controllers.mapping.writer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.controllers.dbconnector.MongoConnManager;
import org.migdb.migdbclient.resources.MongoDBResource;

import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

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
		try {
			MongoConnManager.INSTANCE.connect("localhost", 27017);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(mappedJson);
		JSONArray collections = (JSONArray) mappedJson.get("collections");
		System.out.println(collections);

		MongoDBResource.INSTANCE.setDB("testMongo1");
		MongoDatabase database = MongoDBResource.INSTANCE.getDatabase();
		for (int i = 0; i < collections.size(); i++) {
			JSONObject collection = (JSONObject) collections.get(i);
//			database.createCollection(collection.get("collectionName").toString());
			JSONArray documents = (JSONArray) collection.get("values");
			for (int j = 0; j < documents.size(); j++) {
//				DBObject dbObject = (DBObject) JSON.parse(documents.get(j).toString());
//				MongoCollection<Document> mongoCollection = database.getCollection(collection.get("collectionName").toString());
//				mongoCollection.insertOne((Document) dbObject);
				Document document = Document.parse(documents.get(j).toString());
				database.getCollection(collection.get("collectionName").toString()).insertOne(document);
			}
			System.out.println(collections.get(i));
		}
		

	}
}
