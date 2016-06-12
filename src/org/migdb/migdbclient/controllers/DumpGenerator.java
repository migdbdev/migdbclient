/**
 * 
 */
package org.migdb.migdbclient.controllers;

import java.io.File;

import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.resources.ConnectionParameters;
import org.migdb.migdbclient.resources.Session;

/**
 * @author Kani
 *
 */
public class DumpGenerator {

	public void generateDump() {
		// Returns the runtime object associated with the current Java
		// application
		Runtime rt = Runtime.getRuntime();
		String path = FilePath.DOCUMENT.getPath() + FilePath.XMLPATH.getPath();
		String database = Session.INSTANCE.getActiveDB();
		String host = ConnectionParameters.SESSION.getMysqlHostName();
		String userName = ConnectionParameters.SESSION.getUserName();
		try {
			System.out.println(path);
			Process p = Runtime.getRuntime()
					.exec("cmd.exe /c start /wait cmd.exe /k \"mysqldump -h "+host+" -u "+userName+" --no-create-db --no-create-info --skip-triggers --xml "+database+" > "
							+ path + "\"& /stop cmd.exe");
			System.out.println("Waiting for batch file ...");
			p.waitFor();
			System.out.println("Batch file done.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
