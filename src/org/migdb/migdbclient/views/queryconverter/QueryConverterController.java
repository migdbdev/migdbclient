package org.migdb.migdbclient.views.queryconverter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.AnyType;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
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
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;

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
				mongoQuery = convertInsertRecord(insertStatement);
			} else if(statement instanceof CreateTable){
				CreateTable createStatement = (CreateTable) statement;
				mongoQuery = convertCreateTable(createStatement);
			} else if(statement instanceof Alter) {
				Alter alterStatement = (Alter) statement;
				mongoQuery = convertAlterTable(alterStatement);
			} else if(statement instanceof CreateIndex) {
				CreateIndex indexStatement = (CreateIndex) statement;
				mongoQuery = convertCreateIndex(indexStatement,sqlQuery);
			} else if(statement instanceof Drop) {
				Drop dropStatement = (Drop) statement;
				mongoQuery = convertDropTable(dropStatement);
			} else if(statement instanceof Update) {
				Update updateStatement = (Update) statement;
				mongoQuery = convertUpdateRecord(updateStatement);
			}

			mongoQueryTxt.setText(mongoQuery);

		} catch (JSQLParserException e) {
			e.printStackTrace();
		} 
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
		.replace("{", "{\n\t").replace("}", "\n}").replace("},", "\t},")+")";
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

		String mongoQuery = "db."+table.getName()+".createIndex( \n\t"+pairs.toString().replace("=", ":")
				.replace("{", "{ ").replace("}", " }")+", \n\t{ name:\""+indexName+"\" } \n)";
		return mongoQuery;
	}
	
	/**
	 * @param dropStatement
	 * @return
	 */
	private String convertDropTable(Drop dropStatement) {
		
		Table table = dropStatement.getName();
		
		String mongoQuery = "db."+table.getName()+".drop()";
		return mongoQuery;
	}
	

	/**
	 * Converts a MySQL insert statement into MongoDB insert statement
	 * @param insertStatement
	 * @return
	 */
	private String convertInsertRecord(Insert insertStatement) {
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
	
	private String convertUpdateRecord(Update updateStatement) {
		String mongoQuery = "";
		
		Table table = updateStatement.getTables().get(0);
		List<Column> columnList = updateStatement.getColumns();
		List<Expression> expressionList =  updateStatement.getExpressions();
		Expression whereExpression = updateStatement.getWhere();
		
		LinkedHashMap<String, Object> pairs = new LinkedHashMap<String, Object>();
		
		for(int i=0; i<columnList.size(); i++) {
			Column column = columnList.get(i);
			Expression expression = expressionList.get(i);

			if(expression instanceof Addition) {
				Addition op = (Addition) expression;		
				Object obj = pairs.get("$inc");
				if(obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object,Object>();
					pair.put(op.getLeftExpression(), op.getRightExpression());
					pairs.put("$inc", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(op.getLeftExpression(), op.getRightExpression());
				}	
			} else if(expression instanceof Subtraction) {
				Subtraction op = (Subtraction) expression;
				boolean isDbl = isDouble(op.getRightExpression().toString());
				Object rightValue = "";
				if(isDbl) {
					rightValue = Double.parseDouble(op.getRightExpression().toString()) * -1;
				} else {
					rightValue = Long.parseLong(op.getRightExpression().toString()) * -1;
				}
				Object obj = pairs.get("$inc");
				if(obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object,Object>();
					pair.put(op.getLeftExpression(), "-"+op.getRightExpression());
					pairs.put("$inc", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(op.getLeftExpression(), "-"+op.getRightExpression());
				}
			} else if(expression instanceof Multiplication) {
				Multiplication op = (Multiplication) expression;
				Object obj = pairs.get("$mul");
				if(obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object,Object>();
					pair.put(op.getLeftExpression(), op.getRightExpression());
					pairs.put("$mul", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(op.getLeftExpression(), op.getRightExpression());
				}
			} else if(expression instanceof Division) {
				Division op = (Division) expression;
				Object obj = pairs.get("$mul");
				if(obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object,Object>();
					pair.put(op.getLeftExpression(), "1/"+op.getRightExpression());
					pairs.put("$inc", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(op.getLeftExpression(), "1/"+op.getRightExpression());
				}
			} else if(expression instanceof LongValue) {
				LongValue exp = (LongValue) expression;
				Object obj = pairs.get("$set");
				if(obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object,Object>();
					pair.put(column.getColumnName(), "NumberLong(\""+exp.getValue()+"\")");
					pairs.put("$set", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(column.getColumnName(), "NumberLong(\""+exp.getValue()+"\")");
				}
			} else if(expression instanceof DoubleValue) {
				DoubleValue exp = (DoubleValue) expression;
				Object obj = pairs.get("$set");
				if(obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object,Object>();
					pair.put(column.getColumnName(), exp.getValue());
					pairs.put("$set", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(column.getColumnName(), exp.getValue());
				}
			} else if(expression instanceof DateValue || expression instanceof TimestampValue) {
				DateValue exp = (DateValue) expression;
				Object obj = pairs.get("$set");
				if(obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object,Object>();
					pair.put(column.getColumnName(), "new Date(\""+exp.getValue()+"\")");
					pairs.put("$set", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(column.getColumnName(), "new Date(\""+exp.getValue()+"\")");
				}
			} else {
				Object obj = pairs.get("$set");
				if(obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object,Object>();
					pair.put(column.getColumnName(), expression);
					pairs.put("$set", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(column.getColumnName(), expression);
				}
			}
		}
		
		if(whereExpression instanceof AndExpression) {
			System.out.println("and");
		}
		else if(whereExpression instanceof OrExpression) {
			System.out.println("or");
		}
		else if(whereExpression instanceof NotEqualsTo) {
			System.out.println("not eq");
		}
		else if(whereExpression instanceof EqualsTo) {
			System.out.println("euqal");
		}
		else if(whereExpression instanceof GreaterThan) {
			System.out.println("gt");
		}
		else if(whereExpression instanceof MinorThan) {
			System.out.println("lt");
		}
		else if(whereExpression instanceof GreaterThanEquals) {
			System.out.println("gte");
		} 
		else if(whereExpression instanceof MinorThanEquals) {
			System.out.println("lte");
		}
		else if(whereExpression instanceof InExpression) {
			System.out.println("in");
		}
		
		
		
		
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
	}*/
	
	public static boolean isDouble(String str) {
		return str.matches("[-+]?[0-9]*\\.{1}[0-9]*");
	}

}
