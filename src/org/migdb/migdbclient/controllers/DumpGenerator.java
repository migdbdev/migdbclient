/**
 * 
 */
package org.migdb.migdbclient.controllers;

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
		String path = FilePath.DOCUMENT.getPath() + FilePath.XMLPATH.getPath();
		String database = Session.INSTANCE.getActiveDB();
		String host = ConnectionParameters.SESSION.getMysqlHostName();
		String userName = ConnectionParameters.SESSION.getUserName();
		String password = ConnectionParameters.SESSION.getPassword();
		try {
			System.out.println(path);
			Process p;
			
			if(password.isEmpty()) {
				p = Runtime.getRuntime()
						.exec("cmd.exe /c start /wait cmd.exe /k \"mysqldump -h "+host+" -u "+userName+" --no-create-db --no-create-info --skip-triggers --xml "+database+" > "
								+ path + "\"& /stop cmd.exe");
			} else {
				p = Runtime.getRuntime()
						.exec("cmd.exe /c start /wait cmd.exe /k \"mysqldump -h "+host+" -u "+userName+" -p "+password+" --no-create-db --no-create-info --skip-triggers --xml "+database+" > "
								+ path + "\"& /stop cmd.exe");
			}
			
			System.out.println("Waiting for batch file ...");
			p.waitFor();
			System.out.println("Batch file done.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
