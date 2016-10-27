package org.migdb.migdbclient.controllers.mapping.changemapping;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.config.FilePath;

public class ChangeReferencing {

	JSONObject dbStructure;
	JSONObject collectionStructure;
	JSONObject changeStructure;

	public ChangeReferencing() {
		super();
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath()));
			this.dbStructure = (JSONObject) obj;
			obj = parser.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.COLLECTIONFILE.getPath()));
			this.collectionStructure = (JSONObject) obj;
			obj = parser.parse(new FileReader(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath()));
			this.changeStructure = (JSONObject) obj;
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void change() {

	}
}
