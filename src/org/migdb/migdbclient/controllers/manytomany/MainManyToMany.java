package org.migdb.migdbclient.controllers.manytomany;

import org.json.simple.JSONObject;
import org.migdb.migdbclient.controllers.mapping.changemapping.ChangeReferencing;
import org.migdb.migdbclient.controllers.mapping.manytomany.ManyToMany;
import org.migdb.migdbclient.controllers.mapping.writer.MongoWriter;
import org.migdb.migdbclient.resources.ChangeStructure;
import org.migdb.migdbclient.utils.ServiceAccessor;

import com.google.gson.JsonObject;

public class MainManyToMany {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		System.out.println("mapping module started...");
//		ManyToMany many = new ManyToMany();
//		many.identifyM2M();
//		System.out.println("mapping module stopped...");
//		System.out.println("mapping module started...");
//		MongoWriter mongoWriter = new MongoWriter();
//		mongoWriter.write();
//		System.out.println("mapping module stopped...");
		System.out.println("change module started...");
		ChangeReferencing changeReferencing = new ChangeReferencing();
		changeReferencing.change("employee","projects");
		System.out.println("change module stopped...");
		
//		ServiceAccessor accessor = new ServiceAccessor();
//		JSONObject jsonObject = accessor.getMappingModel();
//		System.out.println("***"+jsonObject);
//		System.out.println("***"+jsonObject.get("complexity"));
	}

}
