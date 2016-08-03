package org.migdb.migdbclient.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.migdb.migdbclient.resources.MappingRequestMessage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class ServiceAccessor {

	public JSONObject getMappingModel(String clientId, String requestId, String columnCount, String numericCount, String stringCount,
			String calenderCount){
		MappingRequestMessage messageBody = new MappingRequestMessage(clientId, requestId, columnCount, numericCount,stringCount, calenderCount);
		JSONObject body = messageBody.getMessageBody();
		Client client = Client.create();

		WebResource webResource = client
		   .resource("http://localhost:8080/migdbserver/services/mappingrequest");

		String input = body.toJSONString();

		client.addFilter(new HTTPBasicAuthFilter("fhgi8598ugh985yhob580uojg0t", "dfjgn984u608jb950o9bipj0945yjpbjmgi"));
		ClientResponse response = webResource.type("application/json")
				.accept("application/json")	  
				.post(ClientResponse.class, input);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			     + response.getStatus());
		}

		System.out.println("Output from Server .... \n");
		String output = response.getEntity(String.class);
		System.out.println(output);
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(output);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			 return json; 

	}
}
