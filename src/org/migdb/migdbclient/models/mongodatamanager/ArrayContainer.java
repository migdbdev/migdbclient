package org.migdb.migdbclient.models.mongodatamanager;

import java.util.ArrayList;

import javafx.scene.control.TextField;

public class ArrayContainer implements JsonGeneratable{

	TextField key;
	ArrayList<TextField> array = new ArrayList<>();
	
	
	
	public ArrayContainer(TextField key, TextField value) {
		super();
		this.key = key;
		this.array.add(value);
	}
	
	public void addValue(TextField field){
		this.array.add(field);
	}



	@Override
	public String generateJson() {
		String result = "{ "+key.getText()+" : [";
		for(TextField item : array){
			// validate comma
			result += item.getText()+" , ";
		}
		result += "] }";
		return result;
	}

}
