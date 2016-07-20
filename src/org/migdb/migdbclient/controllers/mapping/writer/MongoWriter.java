package org.migdb.migdbclient.controllers.mapping.writer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.FilePath;

public class MongoWriter {

	private JSONObject mappedJson;

	
	
	public MongoWriter() {
		super();
		JSONParser parser = new JSONParser();
		try {
			Object object = parser.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath()));
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
		}	}



	public void write() {
		System.out.println(mappedJson);
	}
}
