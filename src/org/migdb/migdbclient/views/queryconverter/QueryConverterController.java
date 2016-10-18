package org.migdb.migdbclient.views.queryconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
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
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
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

	/*
	 * List<String> textTypes =
	 * Arrays.asList("char","varchar","tinytext","text","blob","mediumtext"
	 * ,"mediumblob","longtext","longblob","enum","set");
	 */

	List<String> numberTypes = Arrays.asList("tinyint", "smallint", "mediumint", "int", "bigint", "float", "double",
			"decimal");

	List<String> dateTypes = Arrays.asList("date", "datetime", "timestamp", "time", "year");

	@FXML
	private void convert() {

		String sqlQuery = sqlQueryTxt.getText().replace("\n", " ").replace("\t", " ");
		System.out.println(sqlQuery);

		try {
			Statement statement = CCJSqlParserUtil.parse(sqlQuery);

			String mongoQuery = "";

			if (statement instanceof Insert) {
				Insert insertStatement = (Insert) statement;
				mongoQuery = convertInsertRecord(insertStatement);
			} else if (statement instanceof CreateTable) {
				CreateTable createStatement = (CreateTable) statement;
				mongoQuery = convertCreateTable(createStatement);
			} else if (statement instanceof Alter) {
				Alter alterStatement = (Alter) statement;
				mongoQuery = convertAlterTable(alterStatement);
			} else if (statement instanceof CreateIndex) {
				CreateIndex indexStatement = (CreateIndex) statement;
				mongoQuery = convertCreateIndex(indexStatement, sqlQuery);
			} else if (statement instanceof Drop) {
				Drop dropStatement = (Drop) statement;
				mongoQuery = convertDropTable(dropStatement);
			} else if (statement instanceof Update) {
				Update updateStatement = (Update) statement;
				mongoQuery = convertUpdateRecord(updateStatement);
			} else if (statement instanceof Delete) {
				Delete deleteStatement = (Delete) statement;
				mongoQuery = convertDeleteRecord(deleteStatement);
			} else if (statement instanceof Select) {
				Select selectStatement = (Select) statement;
				mongoQuery = convertSelectRecord(selectStatement);
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

		for (int i = 0; i < colDef.size(); i++) {
			ColumnDefinition def = colDef.get(i);
			String dataType = def.getColDataType().getDataType();
			if (containsCaseInsensitive(dataType, numberTypes)) {
				if (dataType.equalsIgnoreCase("int")) {
					pairs.put(def.getColumnName(), "NumberInt(\"<Value" + (i + 1) + ">\")");
				} else if (dataType.equalsIgnoreCase("bigint")) {
					pairs.put(def.getColumnName(), "NumberLong(\"<Value" + (i + 1) + ">\")");
				} else {
					pairs.put(def.getColumnName(), "<Value" + (i + 1) + ">");
				}
			} else if (containsCaseInsensitive(dataType, dateTypes)) {
				pairs.put(def.getColumnName(), "new Date(\"<Value" + (i + 1) + ">\")");
			} else {
				pairs.put(def.getColumnName(), "\"<Value" + (i + 1) + ">\"");
			}
		}

		for (int i = 0; i < indexes.size(); i++) {
			Index index = indexes.get(i);
			String keyType = index.getType();
			if (keyType.equalsIgnoreCase("foreign key")) {
				ForeignKeyIndex fk = (ForeignKeyIndex) index;
				LinkedHashMap<String, Object> fkPairs = new LinkedHashMap<String, Object>();
				fkPairs.put("$ref", fk.getTable().getName());
				fkPairs.put("$id", "ObjectId(\"<Id_Value>\")");
				pairs.put(fk.getColumnsNames().get(0), fkPairs);
			}
		}

		String mongoQuery = "db.createCollection(\"" + table.getName() + "\") \n\n OR \n\n" + "db." + table.getName()
				+ ".insert(" + pairs.toString().replace("$", "\t$").replace("=", ":").replace(",", ",\n\t")
						.replace("{", "{\n\t").replace("}", "\n}").replace("},", "\t},")
				+ ")";
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

		if (operation.equalsIgnoreCase("add")) {
			Pair<String, Object> pair;
			String dataType = alterStatement.getDataType().toString();
			if (containsCaseInsensitive(dataType, numberTypes)) {
				if (dataType.equalsIgnoreCase("int")) {
					pair = new Pair<String, Object>(alterStatement.getColumnName(), "NumberInt(\"<Value>\")");
				} else if (dataType.equalsIgnoreCase("bigint")) {
					pair = new Pair<String, Object>(alterStatement.getColumnName(), "NumberLong(\"<Value>\")");
				} else {
					pair = new Pair<String, Object>(alterStatement.getColumnName(), "<Value>");
				}
			} else if (containsCaseInsensitive(dataType, dateTypes)) {
				pair = new Pair<String, Object>(alterStatement.getColumnName(), "new ISODate()");
			} else {
				pair = new Pair<String, Object>(alterStatement.getColumnName(), "\"<Value>\"");
			}
			mongoQuery = "db." + table.getName() + ".update( \n\t{ }, \n\t{ $set: { "
					+ pair.toString().replace("=", ":") + " } }, \n\t{ multi: true } \n)";

		} else if (operation.equalsIgnoreCase("drop")) {
			mongoQuery = "db." + table.getName() + ".update( \n\t{ }, \n\t{ $unset: { " + alterStatement.getColumnName()
					+ ": \"\" } }, \n\t{ multi: true } \n)";
		}

		return mongoQuery;
	}

	/**
	 * @param indexStatement
	 * @return
	 */
	private String convertCreateIndex(CreateIndex indexStatement, String sqlQuery) {

		String[] indexFields = sqlQuery.substring(sqlQuery.indexOf("(") + 1, sqlQuery.indexOf(")")).split(",");

		Table table = indexStatement.getTable();
		Index index = indexStatement.getIndex();
		String indexName = index.getName();
		List<String> columns = index.getColumnsNames();

		LinkedHashMap<String, Object> pairs = new LinkedHashMap<String, Object>();

		for (int i = 0; i < columns.size(); i++) {
			String column = columns.get(i);
			int order = 1;
			for (String col : indexFields) {
				if (col.contains(column)) {
					if (col.toLowerCase().endsWith("desc")) {
						order = -1;
					}
				}
			}
			pairs.put(column, order);
		}

		String mongoQuery = "db." + table.getName() + ".createIndex( \n\t"
				+ pairs.toString().replace("=", ":").replace("{", "{ ").replace("}", " }") + ", \n\t{ name:\""
				+ indexName + "\" } \n)";
		return mongoQuery;
	}

	/**
	 * @param dropStatement
	 * @return
	 */
	private String convertDropTable(Drop dropStatement) {

		Table table = dropStatement.getName();

		String mongoQuery = "db." + table.getName() + ".drop()";
		return mongoQuery;
	}

	/**
	 * Converts a MySQL insert statement into MongoDB insert statement
	 * 
	 * @param insertStatement
	 * @return
	 */
	private String convertInsertRecord(Insert insertStatement) {
		Table table = insertStatement.getTable();
		List<Column> columnList = insertStatement.getColumns();
		String values = insertStatement.getItemsList().toString().replace("(", "").replace(")", "").replace(" ", "");

		LinkedHashMap<String, Object> pairs = new LinkedHashMap<String, Object>();

		StringTokenizer st = new StringTokenizer(values, ",");
		int count = 0;
		while (st.hasMoreElements()) {
			Object value = st.nextElement();

			if (columnList == null) {
				pairs.put("<Key" + (count + 1) + ">", value);
			} else {
				pairs.put(columnList.get(count).getColumnName(), value);
			}
			count++;
		}

		String mongoQuery = "db." + table.getName() + ".insert(" + pairs.toString().replace("=", ":")
				.replace(",", ",\n\t").replace("{", "{\n\t").replace("}", "\n}").replace(" ", "") + ")";
		return mongoQuery;
	}

	/**
	 * @param updateStatement
	 * @return
	 */
	private String convertUpdateRecord(Update updateStatement) {

		Table table = updateStatement.getTables().get(0);
		List<Column> columnList = updateStatement.getColumns();
		List<Expression> expressionList = updateStatement.getExpressions();
		Expression whereExpression = updateStatement.getWhere();

		LinkedHashMap<String, Object> updatePairs = new LinkedHashMap<String, Object>();

		for (int i = 0; i < columnList.size(); i++) {
			Column column = columnList.get(i);
			Expression expression = expressionList.get(i);

			if (expression instanceof Addition) {
				Addition op = (Addition) expression;
				Object obj = updatePairs.get("$inc");
				if (obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object, Object>();
					pair.put(op.getLeftExpression(), op.getRightExpression());
					updatePairs.put("$inc", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(op.getLeftExpression(), op.getRightExpression());
				}
			} else if (expression instanceof Subtraction) {
				Subtraction op = (Subtraction) expression;
				boolean isDbl = isDouble(op.getRightExpression().toString());
				Object rightValue = "";
				if (isDbl) {
					rightValue = Double.parseDouble(op.getRightExpression().toString()) * -1;
				} else {
					rightValue = Long.parseLong(op.getRightExpression().toString()) * -1;
				}
				Object obj = updatePairs.get("$inc");
				if (obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object, Object>();
					pair.put(op.getLeftExpression(), "-" + op.getRightExpression());
					updatePairs.put("$inc", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(op.getLeftExpression(), "-" + op.getRightExpression());
				}
			} else if (expression instanceof Multiplication) {
				Multiplication op = (Multiplication) expression;
				Object obj = updatePairs.get("$mul");
				if (obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object, Object>();
					pair.put(op.getLeftExpression(), op.getRightExpression());
					updatePairs.put("$mul", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(op.getLeftExpression(), op.getRightExpression());
				}
			} else if (expression instanceof Division) {
				Division op = (Division) expression;
				Object obj = updatePairs.get("$mul");
				if (obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object, Object>();
					pair.put(op.getLeftExpression(), "1/" + op.getRightExpression());
					updatePairs.put("$inc", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(op.getLeftExpression(), "1/" + op.getRightExpression());
				}
			} else if (expression instanceof LongValue) {
				LongValue exp = (LongValue) expression;
				Object obj = updatePairs.get("$set");
				if (obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object, Object>();
					pair.put(column.getColumnName(), "NumberLong(\"" + exp.getValue() + "\")");
					updatePairs.put("$set", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(column.getColumnName(), "NumberLong(\"" + exp.getValue() + "\")");
				}
			} else if (expression instanceof DoubleValue) {
				DoubleValue exp = (DoubleValue) expression;
				Object obj = updatePairs.get("$set");
				if (obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object, Object>();
					pair.put(column.getColumnName(), exp.getValue());
					updatePairs.put("$set", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(column.getColumnName(), exp.getValue());
				}
			} else if (expression instanceof DateValue || expression instanceof TimestampValue) {
				DateValue exp = (DateValue) expression;
				Object obj = updatePairs.get("$set");
				if (obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object, Object>();
					pair.put(column.getColumnName(), "new Date(\"" + exp.getValue() + "\")");
					updatePairs.put("$set", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(column.getColumnName(), "new Date(\"" + exp.getValue() + "\")");
				}
			} else {
				Object obj = updatePairs.get("$set");
				if (obj == null) {
					HashMap<Object, Object> pair = new HashMap<Object, Object>();
					pair.put(column.getColumnName(), expression);
					updatePairs.put("$set", pair);
				} else {
					HashMap<Object, Object> pList = (HashMap<Object, Object>) obj;
					pList.put(column.getColumnName(), expression);
				}
			}
		}

		HashMap<String, Object> conditionPair = convertWhere(whereExpression);

		String mongoQuery = "db." + table.getName() + ".update( \n\t"
				+ conditionPair.toString().replace("=", ": ").replace("{", "{ ").replace("}", " }") + ", \n\t"
				+ updatePairs.toString().replace("=", ": ").replace("{", "{ ").replace("}", " }")
				+ ", \n\t{ multi: true } \n)";

		return mongoQuery;
	}

	/**
	 * @param deleteStatement
	 * @return
	 */
	private String convertDeleteRecord(Delete deleteStatement) {

		Table table = deleteStatement.getTable();
		Expression whereExpression = deleteStatement.getWhere();
		String mongoQuery = "";

		if (whereExpression != null) {

			HashMap<String, Object> conditionPair = convertWhere(whereExpression);

			mongoQuery = "db." + table.getName() + ".remove( \n\t"
					+ conditionPair.toString().replace("=", ": ").replace("{", "{ ").replace("}", " }") + "\n)";

		} else {
			mongoQuery = "db." + table.getName() + ".remove({})";
		}

		return mongoQuery;
	}

	private String convertSelectRecord(Select selectStatement) {

		String mongoQuery = "";

		SelectBody select = selectStatement.getSelectBody();

		if (select instanceof PlainSelect) {
			PlainSelect plainSelect = (PlainSelect) select;

			List<Join> joins = plainSelect.getJoins();

			if (joins != null) {

				return convertJoin(joins, plainSelect);

			} else {
				List<SelectItem> selectItems = plainSelect.getSelectItems();
				Expression whereExpression = plainSelect.getWhere();
				Distinct distinct = plainSelect.getDistinct();
				Limit limit = plainSelect.getLimit();
				List<OrderByElement> orderByList = plainSelect.getOrderByElements();

				HashMap<String, Object> conditionPair = new HashMap<String, Object>();

				if (whereExpression != null) {
					conditionPair = convertWhere(whereExpression);
				}

				HashMap<String, Object> selectPair = new HashMap<String, Object>();
				String append = "";

				if ((selectItems.size()) == 1 && (selectItems.get(0) instanceof AllColumns)) {
					if (whereExpression == null) {
						mongoQuery = "db." + plainSelect.getFromItem() + ".find()";
					} else {
						mongoQuery = "db." + plainSelect.getFromItem() + ".find(\n\t"
								+ conditionPair.toString().replace("=", ": ").replace("{", "{ ").replace("}", " }")
								+ "\n)";
					}
				} else {
					for (int i = 0; i < selectItems.size(); i++) {
						SelectItem item = selectItems.get(i);
						if (item instanceof SelectExpressionItem) {
							SelectExpressionItem expressionItem = (SelectExpressionItem) item;
							Expression expression = expressionItem.getExpression();
							if (expression instanceof Function) {
								Function func = (Function) expression;
								if (func.getName().equalsIgnoreCase("count")) {
									if (func.getParameters() != null) {
										List<Expression> exList = func.getParameters().getExpressions();
										for (int k = 0; k < exList.size(); k++) {
											conditionPair.put(exList.get(k).toString(), "{ $exists: true }");
										}
									}
									append += ".count()";
								}
							} else if (expression instanceof Column) {
								Column col = (Column) expression;
								selectPair.put(col.getColumnName(), 1);
							}
						}

					}
				}

				if (orderByList != null) {
					HashMap<String, Object> orderPair = new HashMap<String, Object>();
					for (int i = 0; i < orderByList.size(); i++) {
						OrderByElement orderBy = orderByList.get(i);
						if (orderBy.isAsc()) {
							orderPair.put(orderBy.getExpression().toString(), 1);
						} else if (orderBy.isAscDescPresent()) {
							orderPair.put(orderBy.getExpression().toString(), -1);
						}
					}
					append += ".sort( " + orderPair.toString().replace("=", ": ").replace("{", "{ ").replace("}", " }")
							+ " )";
				}

				if (limit != null) {
					append += ".limit(" + limit.getOffset() + ")";
				}

				if (mongoQuery == "" && (!selectPair.isEmpty())) {
					mongoQuery = "db." + plainSelect.getFromItem() + ".find(\n\t"
							+ conditionPair.toString().replace("=", ": ").replace("{", "{ ").replace("}", " }")
							+ ",\n\t" + selectPair.toString().replace("=", ": ").replace("{", "{ ").replace("}", " }")
							+ "\n)";
				} else if (mongoQuery == "" && (selectPair.isEmpty())) {
					mongoQuery = "db." + plainSelect.getFromItem() + ".find(\n\t"
							+ conditionPair.toString().replace("=", ": ").replace("{", "{ ").replace("}", " }") + "\n)";
				}

				mongoQuery += append;
			}
		}
		return mongoQuery;
	}
	
	/**
	 * Method to convert join queries
	 * @param joins
	 * @param plainSelect
	 * @return
	 */
	private String convertJoin(List<Join> joins, PlainSelect plainSelect) {

		String mongoQuery = "";
		List<LinkedHashMap<String, Object>> lookupList = new ArrayList<LinkedHashMap<String, Object>>();

		for (int k = 0; k < joins.size(); k++) {
			Join join = joins.get(0);
			Table leftTable = (Table) plainSelect.getFromItem();
			Table rightTable = (Table) join.getRightItem();
			LinkedHashMap<String, Object> lookupObj = new LinkedHashMap<String, Object>();
			
			String statement = "";
			
			if (join.isLeft()) {
				
				lookupObj.put("from", "\"" + rightTable.getName() + "\"");
				Expression onExpression = join.getOnExpression();
				Column leftCol = null;
				Column rightCol = null;
				if (onExpression instanceof EqualsTo) {
					EqualsTo eq = (EqualsTo) onExpression;
					Expression leftExp = eq.getLeftExpression();
					if (leftExp instanceof Column) {
						leftCol = (Column) leftExp;
						if (leftTable.getAlias() == null && leftCol.getTable().getName().equals(leftTable.getName())) {
							lookupObj.put("localField", "\"" + leftCol.getColumnName() + "\"");
						} else if (leftTable.getAlias() != null
								&& leftTable.getAlias().getName().equals(leftCol.getTable().getName())) {
							lookupObj.put("localField", "\"" + leftCol.getColumnName() + "\"");
						} else if (rightTable.getAlias() == null
								&& leftCol.getTable().getName().equals(rightTable.getName())) {
							lookupObj.put("foreignField", "\"" + leftCol.getColumnName() + "\"");
						} else if (rightTable.getAlias() != null
								&& rightTable.getAlias().getName().equals(leftCol.getTable().getName())) {
							lookupObj.put("foreignField", "\"" + leftCol.getColumnName() + "\"");
						}
					}
					Expression rightExp = eq.getRightExpression();
					if (rightExp instanceof Column) {
						rightCol = (Column) rightExp;
						if (rightTable.getAlias() == null
								&& rightCol.getTable().getName().equals(rightTable.getName())) {
							lookupObj.put("foreignField", "\"" + rightCol.getColumnName() + "\"");
						} else if (rightTable.getAlias() != null
								&& rightTable.getAlias().getName().equals(rightCol.getTable().getName())) {
							lookupObj.put("foreignField", "\"" + rightCol.getColumnName() + "\"");
						} else if (leftTable.getAlias() == null
								&& leftCol.getTable().getName().equals(leftTable.getName())) {
							lookupObj.put("localField", "\"" + rightCol.getColumnName() + "\"");
						} else if (leftTable.getAlias() != null
								&& leftTable.getAlias().getName().equals(leftCol.getTable().getName())) {
							lookupObj.put("localField", "\"" + rightCol.getColumnName() + "\"");
						}
					}
				}
				lookupObj.put("as", "\"" + rightTable.getName() + "\"");
				
				
				lookupList.add(lookupObj);

				Expression whereExpression = plainSelect.getWhere();
				
				HashMap<String, Object> conditions = convertWhere(whereExpression);
				
				String matchConditions = conditions.toString();
				Pattern ptrn = Pattern.compile("(\\{|\\s)[a-zA-Z_\\.]+\\=");
				Matcher m = ptrn.matcher(matchConditions);
				while(m.find()) 
				{
					String subString = (String) (m.group().subSequence(1, m.group().length()-1));
					System.out.println(subString);
					matchConditions = matchConditions.replace(subString, "\""+subString+"\"");
				}
				
				if(leftTable.getAlias() == null) {
					String prefix = leftTable.getName()+".";
					matchConditions = matchConditions.replace(prefix, "");				
				} else {
					Alias a = leftTable.getAlias();
					matchConditions = matchConditions.replace(a+".", "");
				}
				
				List<SelectItem> selectItems = plainSelect.getSelectItems();
				HashMap<String, Object> cols = new HashMap<String, Object>();
				
				for(int i=0; i<selectItems.size(); i++) {
					SelectItem item = selectItems.get(i);
					if (item instanceof SelectExpressionItem) {
						SelectExpressionItem expressionItem = (SelectExpressionItem) item;
						Expression expression = expressionItem.getExpression();
						if(expression instanceof Column) {
							Column column = (Column) expression;
							cols.put(column.getColumnName(), expression);
						}
					}
				}
				
				statement = "db."+leftTable.getName()+".aggregate([";
				if(!conditions.isEmpty()) {
					statement+= "\n\t{\n\t\t$match:\n\t\t\t"+
				matchConditions.replace("=", ":").replace(",", ",\n\t\t\t")+"\n\t},";
				}
				statement+= "\n\t{\n\t\t$lookup:\n\t\t\t"+lookupObj.toString().replace("=", ":")
						.replace(",", ",\n\t\t\t")+"\n\t},";
				
				statement+= "\n\t{ $unwind: "+rightTable.getName()+" }";

				if(!cols.isEmpty()) {
					statement+= ",\n\t{\n\t\t$project:\n\t\t\t"+
							matchConditions.replace("=", ":").replace(",", ",\n\t\t\t")+"\n\t}";
				}

			}
			
			else if(join.isRight()) {
				
				
				
			}

			
			
			mongoQuery = statement+"\n])";

			
		}
		
		
		
		
		/*mongoQuery = "db." + plainSelect.getFromItem() + ".aggregate([\n\t{\n\t\t$lookup: \n\t\t\t{ \n\t\t\t\t"
				+ "from: \"" + join.getRightItem() + "\",\n\t\t\t\t" + "localField: \""
				+ leftCol.getColumnName() + "\",\n\t\t\t\t" + "foreignField: \"" + rightCol.getColumnName()
				+ "\",\n\t\t\t\t" + "as: \"" + join.getRightItem() + "_docs\"," + "\n\t\t\t} \n\t} \n])";
*/

		return mongoQuery;
	}

	/**
	 * @param str
	 * @param list
	 * @return
	 */
	public boolean containsCaseInsensitive(String str, List<String> list) {
		for (String string : list) {
			if (string.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * public static boolean isNumeric(String str) { return
	 * str.matches("[+-]?\\d*(\\.\\d+)?"); }
	 */

	public static boolean isDouble(String str) {
		return str.matches("[-+]?[0-9]*\\.{1}[0-9]*");
	}

	/**
	 * Method to where expressions in a SQL query
	 * 
	 * @param whereExpression
	 * @return
	 */
	public HashMap<String, Object> convertWhere(Expression whereExpression) {

		HashMap<String, Object> conditionPair = new HashMap<String, Object>();

		if (whereExpression instanceof AndExpression) {

			AndExpression whereExp = (AndExpression) whereExpression;
			Expression leftExp = whereExp.getLeftExpression();
			Expression rightExp = whereExp.getRightExpression();

			// List<Map<String, Object>> andList = new ArrayList<Map<String,
			// Object>>();

			if (leftExp instanceof EqualsTo) {
				EqualsTo exp = (EqualsTo) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$eq", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (leftExp instanceof NotEqualsTo) {
				NotEqualsTo exp = (NotEqualsTo) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$ne", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (leftExp instanceof GreaterThan) {
				GreaterThan exp = (GreaterThan) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$gt", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (leftExp instanceof MinorThan) {
				MinorThan exp = (MinorThan) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$lt", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (leftExp instanceof GreaterThanEquals) {
				GreaterThanEquals exp = (GreaterThanEquals) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$gte", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (leftExp instanceof MinorThanEquals) {
				MinorThanEquals exp = (MinorThanEquals) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$lte", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (leftExp instanceof InExpression) {
				InExpression exp = (InExpression) leftExp;
				ItemsList items = exp.getRightItemsList();

				List<Object> itemList = new ArrayList<Object>();
				String values = items.toString().replace("(", "").replace(")", "").replace(" ", "");
				StringTokenizer st = new StringTokenizer(values, ",");
				while (st.hasMoreElements()) {
					Object value = st.nextElement();
					itemList.add(value);
				}

				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$in", itemList);
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (leftExp instanceof LikeExpression) {
				LikeExpression exp = (LikeExpression) leftExp;
				// exp.g
			}

			if (rightExp instanceof EqualsTo) {
				EqualsTo exp = (EqualsTo) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$eq", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (rightExp instanceof NotEqualsTo) {
				NotEqualsTo exp = (NotEqualsTo) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$ne", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (rightExp instanceof GreaterThan) {
				GreaterThan exp = (GreaterThan) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$gt", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (rightExp instanceof MinorThan) {
				MinorThan exp = (MinorThan) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$lt", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (rightExp instanceof GreaterThanEquals) {
				GreaterThanEquals exp = (GreaterThanEquals) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$gte", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (rightExp instanceof MinorThanEquals) {
				MinorThanEquals exp = (MinorThanEquals) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$lte", exp.getRightExpression());
				// HashMap<String, Object> pair = new HashMap<String, Object>();
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
				// andList.add(pair);
			} else if (rightExp instanceof InExpression) {
				InExpression exp = (InExpression) rightExp;
				ItemsList items = exp.getRightItemsList();

				List<Object> itemList = new ArrayList<Object>();
				String values = items.toString().replace("(", "").replace(")", "").replace(" ", "");
				StringTokenizer st = new StringTokenizer(values, ",");
				while (st.hasMoreElements()) {
					Object value = st.nextElement();
					itemList.add(value);
				}

				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$in", itemList);
				conditionPair.put(exp.getLeftExpression().toString(), innerPair);
			}
		}

		else if (whereExpression instanceof OrExpression) {
			OrExpression whereExp = (OrExpression) whereExpression;

			Expression leftExp = whereExp.getLeftExpression();
			Expression rightExp = whereExp.getRightExpression();

			List<Map<String, Object>> andList = new ArrayList<Map<String, Object>>();

			if (leftExp instanceof EqualsTo) {
				EqualsTo exp = (EqualsTo) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$eq", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (leftExp instanceof NotEqualsTo) {
				NotEqualsTo exp = (NotEqualsTo) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$ne", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (leftExp instanceof GreaterThan) {
				GreaterThan exp = (GreaterThan) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$gt", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (leftExp instanceof MinorThan) {
				MinorThan exp = (MinorThan) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$lt", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (leftExp instanceof GreaterThanEquals) {
				GreaterThanEquals exp = (GreaterThanEquals) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$gte", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (leftExp instanceof MinorThanEquals) {
				MinorThanEquals exp = (MinorThanEquals) leftExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$lte", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (leftExp instanceof InExpression) {
				InExpression exp = (InExpression) leftExp;
				ItemsList items = exp.getRightItemsList();

				List<Object> itemList = new ArrayList<Object>();
				String values = items.toString().replace("(", "").replace(")", "").replace(" ", "");
				StringTokenizer st = new StringTokenizer(values, ",");
				while (st.hasMoreElements()) {
					Object value = st.nextElement();
					itemList.add(value);
				}

				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$in", itemList);
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			}

			if (rightExp instanceof EqualsTo) {
				EqualsTo exp = (EqualsTo) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$eq", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (rightExp instanceof NotEqualsTo) {
				NotEqualsTo exp = (NotEqualsTo) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$ne", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (rightExp instanceof GreaterThan) {
				GreaterThan exp = (GreaterThan) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$gt", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (rightExp instanceof MinorThan) {
				MinorThan exp = (MinorThan) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$lt", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (rightExp instanceof GreaterThanEquals) {
				GreaterThanEquals exp = (GreaterThanEquals) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$gte", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (rightExp instanceof MinorThanEquals) {
				MinorThanEquals exp = (MinorThanEquals) rightExp;
				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$lte", exp.getRightExpression());
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			} else if (rightExp instanceof InExpression) {
				InExpression exp = (InExpression) rightExp;
				ItemsList items = exp.getRightItemsList();

				List<Object> itemList = new ArrayList<Object>();
				String values = items.toString().replace("(", "").replace(")", "").replace(" ", "");
				StringTokenizer st = new StringTokenizer(values, ",");
				while (st.hasMoreElements()) {
					Object value = st.nextElement();
					itemList.add(value);
				}

				HashMap<String, Object> innerPair = new HashMap<String, Object>();
				innerPair.put("$in", itemList);
				HashMap<String, Object> pair = new HashMap<String, Object>();
				pair.put(exp.getLeftExpression().toString(), innerPair);
				andList.add(pair);
			}

			conditionPair.put("$or", andList);
		}

		else if (whereExpression instanceof NotEqualsTo) {
			NotEqualsTo whereExp = (NotEqualsTo) whereExpression;
			HashMap<String, Object> innerPair = new HashMap<String, Object>();
			innerPair.put("$ne", whereExp.getRightExpression());
			conditionPair.put(whereExp.getLeftExpression().toString(), innerPair);
		}

		else if (whereExpression instanceof EqualsTo) {
			EqualsTo whereExp = (EqualsTo) whereExpression;
			HashMap<String, Object> innerPair = new HashMap<String, Object>();
			innerPair.put("$eq", whereExp.getRightExpression());
			conditionPair.put(whereExp.getLeftExpression().toString(), innerPair);
		}

		else if (whereExpression instanceof GreaterThan) {
			GreaterThan whereExp = (GreaterThan) whereExpression;
			HashMap<String, Object> innerPair = new HashMap<String, Object>();
			innerPair.put("$gt", whereExp.getRightExpression());
			conditionPair.put(whereExp.getLeftExpression().toString(), innerPair);
		}

		else if (whereExpression instanceof MinorThan) {
			MinorThan whereExp = (MinorThan) whereExpression;
			HashMap<String, Object> innerPair = new HashMap<String, Object>();
			innerPair.put("$lt", whereExp.getRightExpression());
			conditionPair.put(whereExp.getLeftExpression().toString(), innerPair);
		}

		else if (whereExpression instanceof GreaterThanEquals) {
			GreaterThanEquals whereExp = (GreaterThanEquals) whereExpression;
			HashMap<String, Object> innerPair = new HashMap<String, Object>();
			innerPair.put("$gte", whereExp.getRightExpression());
			conditionPair.put(whereExp.getLeftExpression().toString(), innerPair);
		}

		else if (whereExpression instanceof MinorThanEquals) {
			MinorThanEquals whereExp = (MinorThanEquals) whereExpression;
			HashMap<String, Object> innerPair = new HashMap<String, Object>();
			innerPair.put("$lte", whereExp.getRightExpression());
			conditionPair.put(whereExp.getLeftExpression().toString(), innerPair);
		}

		else if (whereExpression instanceof InExpression) {
			InExpression whereExp = (InExpression) whereExpression;
			ItemsList items = whereExp.getRightItemsList();

			List<Object> itemList = new ArrayList<Object>();
			String values = items.toString().replace("(", "").replace(")", "").replace(" ", "");
			StringTokenizer st = new StringTokenizer(values, ",");
			while (st.hasMoreElements()) {
				Object value = st.nextElement();
				itemList.add(value);
			}

			HashMap<String, Object> innerPair = new HashMap<String, Object>();
			innerPair.put("$in", itemList);
			conditionPair.put(whereExp.getLeftExpression().toString(), innerPair);

		}

		else if (whereExpression instanceof LikeExpression) {
			LikeExpression exp = (LikeExpression) whereExpression;
			System.out.println(exp.getEscape());

		}

		return conditionPair;
	}



}
