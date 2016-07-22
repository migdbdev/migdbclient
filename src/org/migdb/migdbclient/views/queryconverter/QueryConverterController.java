package org.migdb.migdbclient.views.queryconverter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.ForeignKeyIndex;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.insert.Insert;

/**
 * @author Malki
 *
 */
public class QueryConverterController {

	@FXML
	private AnchorPane rootLayoutAnchorPane;
	@FXML
	private TextArea sqlQueryTxt;
	@FXML
	private TextArea mongoQueryTxt;
	
	/*List<String> textTypes = Arrays.asList("char","varchar","tinytext","text","blob","mediumtext"
			,"mediumblob","longtext","longblob","enum","set");*/
	
	List<String> numberTypes = Arrays.asList("tinyint","smallint","mediumint","int","bigint","float"
			,"double","decimal");
	
	List<String> dateTypes = Arrays.asList("date","datetime","timestamp","time","year");

	@FXML
	private void convert() {

		String sqlQuery = sqlQueryTxt.getText().replace("\n", " ").replace("\t", " ");
		System.out.println(sqlQuery);
		
		try {
			Statement statement = CCJSqlParserUtil.parse(sqlQuery);

			String mongoQuery = "";
			
			if(statement instanceof Insert) {
				Insert insertStatement = (Insert) statement;
				mongoQuery = convertInsert(insertStatement);
			} else if(statement instanceof CreateTable){
				CreateTable createStatement = (CreateTable) statement;
				mongoQuery = convertCreateTable(createStatement);
			} else if(statement instanceof Alter) {
				Alter alterStatement = (Alter) statement;
				mongoQuery = convertAlterTable(alterStatement);
			} else if(statement instanceof CreateIndex) {
				CreateIndex indexStatement = (CreateIndex) statement;
				mongoQuery = convertCreateIndex(indexStatement,sqlQuery);
			}

			mongoQueryTxt.setText(mongoQuery);

		} catch (JSQLParserException e) {
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * Converts a MySQL insert statement into MongoDB insert statement
	 * @param insertStatement
	 * @return
	 */
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
				.replace(",", ",\n\t").replace("{", "{\n\t").replace("}", "\n}").replace(" ", "")+")";
		return mongoQuery;
	}
	
	/**
	 * @param createStatement
	 * @return
	 */
	private String convertCreateTable(CreateTable createStatement) {
		
		Table table = createStatement.getTable();
		List<ColumnDefinition> colDef = createStatement.getColumnDefinitions();
		List<Index> indexes = createStatement.getIndexes();
		
		LinkedHashMap<String, Object> pairs = new LinkedHashMap<String, Object>();
		
		for(int i=0; i<colDef.size();i++) {
			ColumnDefinition def = colDef.get(i);
			String dataType = def.getColDataType().getDataType();	
			if(containsCaseInsensitive(dataType, numberTypes)) {
				if(dataType.equalsIgnoreCase("int")) {
					pairs.put(def.getColumnName(), "NumberInt(\"<Value"+(i+1)+">\")");
				} else if(dataType.equalsIgnoreCase("bigint")) {
					pairs.put(def.getColumnName(), "NumberLong(\"<Value"+(i+1)+">\")");
				} else {
					pairs.put(def.getColumnName(), "<Value"+(i+1)+">");
				}
			} else if(containsCaseInsensitive(dataType, dateTypes)) {
				pairs.put(def.getColumnName(), "new Date(\"<Value"+(i+1)+">\")");
			} else {
				pairs.put(def.getColumnName(), "\"<Value"+(i+1)+">\"");
			}			
		}
		
		for(int i=0; i<indexes.size();i++) {
			Index index = indexes.get(i);
			String keyType = index.getType();
			if(keyType.equalsIgnoreCase("foreign key")) {
				ForeignKeyIndex fk = (ForeignKeyIndex) index;
				LinkedHashMap<String, Object> fkPairs = new LinkedHashMap<String, Object>();
				fkPairs.put("$ref", fk.getTable().getName());
				fkPairs.put("$id", "ObjectId(\"<Id_Value>\")");
				pairs.put(fk.getColumnsNames().get(0), fkPairs);
			}
		}
		
		String mongoQuery = "db.createCollection(\""+table.getName()+"\") \n\n OR \n\n"
		+"db."+table.getName()+".insert("+pairs.toString().replace("$", "\t$").replace("=", ":").replace(",", ",\n\t")
		.replace("{", "{\n\t").replace("}", "\n}").replace("},", "\t},").replace(" ", "")+")";
		return mongoQuery;
	}
	
	/**
	 * @param alterStatement
	 * @return
	 */
	private String convertAlterTable(Alter alterStatement) {
		
		String mongoQuery = "";
		
		String operation = alterStatement.getOperation();
		Table table = alterStatement.getTable();
		
		if(operation.equalsIgnoreCase("add")) {
			Pair<String, Object> pair;
			String dataType = alterStatement.getDataType().toString();
			if(containsCaseInsensitive(dataType, numberTypes)) {
				if(dataType.equalsIgnoreCase("int")) {
					pair = new Pair<String, Object>(alterStatement.getColumnName(),"NumberInt(\"<Value>\")");
				} else if(dataType.equalsIgnoreCase("bigint")) {
					pair = new Pair<String, Object>(alterStatement.getColumnName(),"NumberLong(\"<Value>\")");
				} else {
					pair = new Pair<String, Object>(alterStatement.getColumnName(),"<Value>");
				}
			} else if(containsCaseInsensitive(dataType, dateTypes)) {
				pair = new Pair<String, Object>(alterStatement.getColumnName(),"new ISODate()");
			} else {
				pair = new Pair<String, Object>(alterStatement.getColumnName(),"\"<Value>\"");
			}		
			mongoQuery = "db."+table.getName()+".update( \n\t{ }, \n\t{ $set: { "+pair.toString()
			.replace("=", ":")+" } }, \n\t{ multi: true } \n)";
			
		} else if (operation.equalsIgnoreCase("drop")) {
			mongoQuery = "db."+table.getName()+".update( \n\t{ }, \n\t{ $unset: { "+alterStatement.getColumnName()
			+": \"\" } }, \n\t{ multi: true } \n)";
		}
		
		return mongoQuery;
	}
	
	/**
	 * @param indexStatement
	 * @return
	 */
	private String convertCreateIndex(CreateIndex indexStatement, String sqlQuery) {

		String[] indexFields = sqlQuery.substring(sqlQuery.indexOf("(")+1, sqlQuery.indexOf(")")).split(",");
		
		Table table = indexStatement.getTable();
		Index index = indexStatement.getIndex();
		String indexName = index.getName();
		List<String> columns = index.getColumnsNames();
		
		LinkedHashMap<String, Object> pairs = new LinkedHashMap<String, Object>();
		
		for(int i=0; i<columns.size(); i++) {
			String column = columns.get(i);
			int order = 1;
			for(String col: indexFields) {
				if(col.contains(column)) {
					if(col.toLowerCase().endsWith("desc")) {
						order = -1;
					}
				}
			}
			pairs.put(column, order);
		}
		
		System.out.println(pairs);

		String mongoQuery = "db."+table.getName()+".createIndex( \n\t"+pairs.toString().replace("=", ":")
				.replace("{", "{ ").replace("}", " }")+", \n\t{ name:\""+indexName+"\" } \n)";
		return mongoQuery;
	}
	
	/**
	 * @param str
	 * @param list
	 * @return
	 */
	public boolean containsCaseInsensitive(String str, List<String> list){
	     for (String string : list){
	        if (string.equalsIgnoreCase(str)){
	            return true;
	         }
	     }
	    return false;
	  }

	/*public static boolean isNumeric(String str) {
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}
	
	public static boolean isDouble(String str) {
		return str.matches("[-+]?[0-9]*\\.{1}[0-9]*");
	}*/

}
