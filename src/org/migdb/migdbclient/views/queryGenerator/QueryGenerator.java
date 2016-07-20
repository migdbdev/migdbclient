/**
 * 
 */
package org.migdb.migdbclient.views.queryGenerator;

import java.net.URL;
import java.util.ResourceBundle;

import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

/**
 * @author KANI
 *
 */
public class QueryGenerator implements Initializable {
	
	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		queryBuild();

	}
	
	public void queryBuild(){
		
		DBObject query = new QueryBuilder()
		         .start()
		         .and(new QueryBuilder().start().put("lname").is("Ford").get(),
		             new QueryBuilder().start().put("marks.english")
		                 .greaterThan(35).get()).get();
		
		System.out.println(query);
		
	}

}
