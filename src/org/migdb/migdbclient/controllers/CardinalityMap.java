/**
 * 
 */
package org.migdb.migdbclient.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.controllers.mapping.onetoone.OneToOneMap;
import org.migdb.migdbclient.main.MainApp;
import org.migdb.migdbclient.models.dao.MysqlDAO;
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.models.dto.ReferenceDTO;
import org.migdb.migdbclient.resources.CenterLayout;
import org.migdb.migdbclient.resources.ConnectionParameters;
import org.migdb.migdbclient.resources.LayoutInstance;
import org.migdb.migdbclient.resources.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

/**
 * @author Kani
 *
 */
public class CardinalityMap {
	
	public void cardinalityMap(){
		
		try {
			String host = ConnectionParameters.SESSION.getMysqlHostName();
			int port = ConnectionParameters.SESSION.getMysqlPort();
			String database = Session.INSTANCE.getActiveDB();
			String username = ConnectionParameters.SESSION.getUserName();
			String password = ConnectionParameters.SESSION.getPassword();
			
			MysqlDAO dao = new MysqlDAO();
			ArrayList<ReferenceDTO> referencedList = dao.getReferencedList(host, port, database, username, password);
			
			File xmlDump = new File(FilePath.DOCUMENT.getPath()+FilePath.XMLPATH.getPath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlDump);
			doc.getDocumentElement().normalize();
			
			NodeList tableList = doc.getElementsByTagName("table_data");
			ArrayList<ReferenceDTO> manytomanytables = new ArrayList<ReferenceDTO>();
			ReferenceDTO dto1 = Session.INSTANCE.getManyToManyTables();
			manytomanytables.add(dto1);
			
			
			ArrayList<String> columnValue = null;
			String relationshipType = null;
			
			for(int m = 0; m < tableList.getLength(); m++){
				for(ReferenceDTO dto : referencedList){
					Node elementNode = tableList.item(m);
					if(elementNode.getNodeType() == Node.ELEMENT_NODE){
						Element eElement = (Element) elementNode;
						if(eElement.getAttribute("name").equals(dto.getTableName())){
							NodeList rowList = tableList.item(m).getChildNodes();
							columnValue = new ArrayList<String>();
							
							for(int n = 0; n < rowList.getLength(); n++){
								Node rowChildList = rowList.item(n);
								if("row".equals(rowChildList.getNodeName())){
									NodeList fieldList = rowList.item(n).getChildNodes();
									
									for(int k = 0; k < fieldList.getLength(); k++){
										Node fieldChildList = fieldList.item(k);
										if("field".equals(fieldChildList.getNodeName())){
											Element fElement = (Element) fieldChildList;
											if(dto.getColumnName().equals(fElement.getAttribute("name"))){
												columnValue.add(String.valueOf(fieldList.item(k).getTextContent().trim()));
											}
										}
									}
								}
							}
							
							Set<String> set = new HashSet<String>(columnValue);
							
							if (set.size() < columnValue.size()) {
								relationshipType = "1:M";
								for(ReferenceDTO dtos : manytomanytables) {
									if(dto.getTableName().equals(dtos.getTableName()) 
											&& dto.getColumnName().equals(dtos.getColumnName()) 
											&& dto.getReferencedTableName().equals(dtos.getReferencedTableName()) 
											&& dto.getReferencedColumnName().equals(dtos.getReferencedColumnName())){
										relationshipType = "M:M";
									}
								}
							} else {
								relationshipType = "1:1";
							}
							
							SqliteDAO sqliteDao = new SqliteDAO();
							sqliteDao.createRelationshipTypes();
							sqliteDao.insertRelationshipTypes(dto.getTableName(), dto.getColumnName(), dto.getReferencedTableName(), dto.getReferencedColumnName(), relationshipType);

						}
					}
				}
			}
			MySQLParser mp = new MySQLParser();
			mp.sqlParser();
			SqliteDAO sDao = new SqliteDAO();
			sDao.dropRelationshipTypes();
			
			AnchorPane root;
			root = CenterLayout.INSTANCE.getRootContainer();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FxmlPath.MODIFICATIONEVALUATOR.getPath()));
			AnchorPane modificationEvaluator = loader.load();
			root.getChildren().clear();
			root.getChildren().add(modificationEvaluator);
			
			/*OneToOneMap om = new OneToOneMap();
			om.oneToOneMapper();*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
