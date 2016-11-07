/**
 * 
 */
package org.migdb.migdbclient.views.connectionmanager;

import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;

import org.migdb.migdbclient.resources.threads.CheckInternetConnectivity;

import javafx.application.Platform;
import javafx.fxml.Initializable;

/**
 * @author KANI
 *
 */
public class MainWindow implements Initializable {

	/**
	 * Initialize method Called to initialize a controller after its root
	 * element has been completely processed The location used to resolve
	 * relative paths for the root object, or null if the location is not known
	 * The resources used to localize the root object, or null if the root
	 * object was not localized
	 */
	public void initialize(URL location, ResourceBundle resources) {
		
		/*CheckInternetConnectivity internetStatus = new CheckInternetConnectivity();

		//Check internet connectivity in separate tread
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000); // just emulates some loading time
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// just updates the list view items at the
					// Application Thread
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							while(true) {
								try {
									
								} catch (Exception e2) {
									// TODO: handle exception
								}
								System.out.println(internetStatus.testIneternet("google.com"));
								
							}
						}
					});
				}
			}
		}).start();*/

	}

}
