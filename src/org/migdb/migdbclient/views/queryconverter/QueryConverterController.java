package org.migdb.migdbclient.views.queryconverter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;

public class QueryConverterController {

	@FXML
	private AnchorPane rootLayoutAnchorPane;
	@FXML
	private TextArea sqlQueryTxt;
	@FXML
	private TextArea mongoQueryTxt;

	@FXML
	private void convert() {

		String sqlQuery = sqlQueryTxt.getText().replace("\n", " ").replace("\t", " ");
		
		try {
			Statement statement = CCJSqlParserUtil.parse(sqlQuery);

			String mongoQuery = "";
			
			if(statement instanceof Insert) {
				Insert insertStatement = (Insert) statement;
				mongoQuery = convertInsert(insertStatement);
			} else if(statement instanceof CreateTable){
				CreateTable createStatement = (CreateTable) statement;
				mongoQuery = convertCreateTable(createStatement);
			}

			mongoQueryTxt.setText(mongoQuery);

		} catch (JSQLParserException e) {
			e.printStackTrace();
		} 
	}
	
	private String convertInsert(Insert insertStatement) {
		Table table = insertStatement.getTable();
		List<Column> columnList = insertStatement.getColumns();
		String values = insertStatement.getItemsList().toString().replace("(", "")
				.replace(")", "").replace(" ", "");
		
		LinkedHashMap<String, Object> pairs = new LinkedHashMap<String, Object>();

		StringTokenizer st = new StringTokenizer(values, ",");
		int count = 0;
		while (st.hasMoreElements()) {
			Object value = st.nextElement();
			
			if(columnList == null) {
				pairs.put("<Key"+(count+1)+">", value);
			} else {
				pairs.put(columnList.get(count).getColumnName(), value);
			}
			count++;
		}
		
		String mongoQuery = "db."+table.getName()+".insert("+pairs.toString().replace("=", ":")
				.replace(",", ",\n\t").replace("{", "{\n\t").replace("}", "\n}")+")";
		return mongoQuery;
	}
	
	private String convertCreateTable(CreateTable createStatement) {
		
		Table table = createStatement.getTable();
		List<ColumnDefinition> colDef = createStatement.getColumnDefinitions();
		
		LinkedHashMap<String, Object> pairs = new LinkedHashMap<String, Object>();
		
		for(int i=0; i<colDef.size();i++) {
			ColumnDefinition def = colDef.get(i);
			System.out.println(def.getColDataType());
			pairs.put(def.getColumnName(), "<Value"+(i+1)+">");
		}
		String mongoQuery = "db."+table.getName()+".insert("+pairs.toString().replace("=", ":")
				.replace(",", ",\n\t").replace("{", "{\n\t").replace("}", "\n}")+") \n\n"
						+ "OR \n\n"
						+ "db.createCollection(\""+table.getName()+"\")";
		return mongoQuery;
	}

	/*public static boolean isNumeric(String str) {
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}
	
	public static boolean isDouble(String str) {
		return str.matches("[-+]?[0-9]*\\.{1}[0-9]*");
	}*/

}
