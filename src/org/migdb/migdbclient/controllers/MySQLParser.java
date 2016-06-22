package org.migdb.migdbclient.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.models.dao.MysqlDAO;
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.models.dto.ColumnsDTO;
import org.migdb.migdbclient.models.dto.ReferenceDTO;
import org.migdb.migdbclient.models.dto.RelationshiCardinalityDTO;
import org.migdb.migdbclient.models.dto.TableDTO;
import org.migdb.migdbclient.resources.ConnectionParameters;
import org.migdb.migdbclient.resources.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MySQLParser {
	
	public void sqlParser() throws ParserConfigurationException, SAXException, IOException{
		
		String host = ConnectionParameters.SESSION.getMysqlHostName();
		int port = ConnectionParameters.SESSION.getMysqlPort();
		String username = ConnectionParameters.SESSION.getUserName();
		String password = ConnectionParameters.SESSION.getPassword();
		String database = Session.INSTANCE.getActiveDB();
		/*JsonWriter jw = null;*/
		
		File fXmlFile = new File(FilePath.DOCUMENT.getPath() + FilePath.XMLPATH.getPath());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("table_data");
		
		try {
			MysqlDAO dao = new MysqlDAO();
			SqliteDAO sDao = new SqliteDAO();
			ArrayList<RelationshiCardinalityDTO> dto = sDao.getReferencedList();
			ArrayList<TableDTO> arrTab = dao.getTables(host, port, database, username, password);
			ArrayList<ColumnsDTO> arrCol = dao.getColumnsAccordingTable(host, port, database, username, password);
			ArrayList<ReferenceDTO> arr = dao.getReferencedList(host, port, database, username, password);
			File jsonFiel = new File(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath());
			JSONObject jsonObj = new JSONObject();
			JSONArray jsonArr = new JSONArray();
			/*jw = new JsonWriter(new FileWriter(FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTUREFILE.getPath()));
			*//*jw.beginObject();

			jw.name("tables");

			jw.beginArray();*/

			if (!arrTab.isEmpty()) {
				for (TableDTO dtoTable : arrTab) {
					/*jw.beginObject();// begin
*/					JSONObject jo = new JSONObject();
					jo.put("name", dtoTable.getTableName());
					jo.put("colCount", dao.getColumnCount(host, port, database, username, password, dtoTable.getTableName().toString()));
					jo.put("primaryKey", dao.getPrimaryKey(host, port, database, username, password, dtoTable.getTableName().toString()));
					/*jw.name("name");
					jw.value(dtoTable.getTableName());*/
					/*jw.name("colCount");
					jw.value(dao.getColumnCount(host, port, database, username, password, dtoTable.getTableName().toString()));*/
					/*jw.name("primaryKey");
					jw.value(dao.getPrimaryKey(host, port, database, username, password, dtoTable.getTableName().toString()));*/
					JSONArray columns = new JSONArray();
					
					/*jw.name("columns");
					jw.beginArray(); // begin columns array
*/
					for (ColumnsDTO dtoColumn : arrCol) {
						if (dtoTable.getTableName().equals(dtoColumn.getTABLE_NAME())) {
							/*jw.beginObject();*/
							JSONObject columnObj = new JSONObject();
							columnObj.put("colName", dtoColumn.getCOLUMN_NAME());
							columnObj.put("dataType", dtoColumn.getDATA_TYPE());
							/*jw.name("colName");
							jw.value(dtoColumn.getCOLUMN_NAME());*/
							/*jw.name("dataType");
							jw.value(dtoColumn.getDATA_TYPE());*/
							/*jw.endObject();*/
							columns.add(columnObj);
						}
					}
					jo.put("columns", columns);
					/*jw.endArray(); // End columns array
					 * 
*/
					/*jw.name("referencingFrom");
					jw.beginArray(); // Begin reference
*/
					JSONArray referencingFrom = new JSONArray();
					
					
					for (ColumnsDTO dtoColumn : arrCol) {
						if (dtoTable.getTableName().equals(dtoColumn.getTABLE_NAME())) {
							for (ReferenceDTO dtoReference : arr) {
								if (dtoTable.getTableName().equals(dtoReference.getTableName())
										&& dtoColumn.getCOLUMN_NAME().equals(dtoReference.getColumnName())) {
									/*jw.beginObject();*/
									JSONObject referencingFromObj = new JSONObject();
									referencingFromObj.put("referencedCol", dtoColumn.getCOLUMN_NAME());
									referencingFromObj.put("referencedTab", dtoReference.getReferencedTableName());
									referencingFromObj.put("referencingCol", dtoReference.getReferencedColumnName());
									
									for(RelationshiCardinalityDTO rDto : dto){
										if(dtoReference.getReferencedTableName().equals(rDto.getREFERENCED_TABLE_NAME()) && dtoReference.getReferencedColumnName().equals(rDto.getREFERENCED_COLUMN_NAME()) && dtoTable.getTableName().equals(rDto.getTABLE_NAME()) && dtoColumn.getCOLUMN_NAME().equals(rDto.getCOLUMN_NAME())){
											referencingFromObj.put("relationshipType",rDto.getRELATIONSHIP_TYPE());
										}
									}
									
									/*jw.name("referencedCol");
									jw.value(dtoColumn.getCOLUMN_NAME());*/
									/*jw.name("referencedTab");
									jw.value(dtoReference.getReferencedTableName());*/
									/*jw.name("referencingCol");
									jw.value(dtoReference.getReferencedColumnName());*/
									// System.out.println(dao.getRelationshipType(database,
									// dtoTable.getTableName()));
									/*jw.name("relationshipType");
									jw.value("----- Coming soon ----");*/
									/*jw.endObject();*/
									referencingFrom.add(referencingFromObj);
								}
							}
						}
					}

					/*jw.endArray(); // End reference*/
					jo.put("referencingFrom", referencingFrom);
					
					/*jw.name("referencedBy");
					jw.beginArray(); // Begin reference*/
					JSONArray referencedBy = new JSONArray();
					
					for (ColumnsDTO dtoColumn : arrCol) {
						if (dtoTable.getTableName().equals(dtoColumn.getTABLE_NAME())) {
							for (ReferenceDTO dtoReference : arr) {
								if (dtoTable.getTableName().equals(dtoReference.getReferencedTableName()) && dtoColumn
										.getCOLUMN_NAME().equals(dtoReference.getReferencedColumnName())) {
									/*jw.beginObject();*/
									JSONObject referencedByObj = new JSONObject();
									referencedByObj.put("referencedCol", dtoColumn.getCOLUMN_NAME());
									referencedByObj.put("referencingTab", dtoReference.getTableName());
									referencedByObj.put("referencingCol", dtoReference.getColumnName());
									
									for(RelationshiCardinalityDTO rDto : dto){
										if(dtoReference.getTableName().equals(rDto.getTABLE_NAME()) && dtoReference.getColumnName().equals(rDto.getCOLUMN_NAME()) && dtoTable.getTableName().equals(rDto.getREFERENCED_TABLE_NAME()) && dtoColumn.getCOLUMN_NAME().equals(rDto.getREFERENCED_COLUMN_NAME())){
											referencedByObj.put("relationshipType",rDto.getRELATIONSHIP_TYPE());
										}
									}
									
									/*jw.name("referencedCol");
									jw.value(dtoColumn.getCOLUMN_NAME());*/
									/*jw.name("referencingTab");
									jw.value(dtoReference.getTableName());*/
									/*jw.name("referencingCol");
									jw.value(dtoReference.getColumnName());*/
									/*jw.name("relationshipType");
									jw.value("----- Coming soon ----");*/
									/*jw.endObject();*/
									referencedBy.add(referencedByObj);
								}
							}
						}
					}

					/*jw.endArray(); // End reference*/
					jo.put("referencedBy", referencedBy);
					
					/*jw.name("data");
					jw.beginArray(); // Data begin array*/	
					JSONArray data = new JSONArray();
					
					for (int temp = 0; temp < nList.getLength(); temp++) {

						Node nNode = nList.item(temp);

						if (nNode.getNodeType() == Node.ELEMENT_NODE) {

							Element eElement = (Element) nNode;
							
							if(dtoTable.getTableName().equals(eElement.getAttribute("name"))) {
							
							//jw.beginObject(); // Data begin object
							/*jw.name("tableName");
							jw.value(eElement.getAttribute("name"));*/
							//jw.name("values");
							//jw.beginArray(); // Values begin array

							NodeList rowlist = nList.item(temp).getChildNodes();
							for (int l = 0; l < rowlist.getLength(); l++) {
								Node rowChildList = rowlist.item(l);
								if ("row".equals(rowChildList.getNodeName())) {
									NodeList fieldList = rowlist.item(l).getChildNodes();

									/*jw.beginObject();*/
									JSONObject dataObj = new JSONObject();
									for (int k = 0; k < fieldList.getLength(); k++) {
										Node fieldChildList = fieldList.item(k);
										if ("field".equals(fieldChildList.getNodeName())) {
											Element fElement = (Element) fieldChildList;

											/*jw.name(fElement.getAttribute("name"));
											jw.value(fieldList.item(k).getTextContent().trim());*/
											dataObj.put(fElement.getAttribute("name"), fieldList.item(k).getTextContent().trim());

										}
									}
									/*jw.endObject();*/
									data.add(dataObj);

								}
							}

							//jw.endArray(); // Values end array
							//jw.endObject(); // Data end object
						}
						}

					}
					
					/*jw.endArray(); // Data end array*/
					jo.put("data", data);
					
					/*jw.endObject();// begin-end*/		
					jsonArr.add(jo);
					}
			}

			jsonObj.put("tables", jsonArr);

			/*jw.endObject();*/
			JSONObject json = new JSONObject();
			String updatedson = json.toJSONString(jsonObj);
			FileUtils.writeStringToFile(jsonFiel, updatedson);
			System.out.println("Finish");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
