package org.migdb.migdbclient.views.queryconverter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.json.simple.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
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
		System.out.println(sqlQuery);

		try {
			Statement statement = CCJSqlParserUtil.parse(sqlQuery);
			Insert insertStatement = (Insert) statement;

			String mongoQuery = convertInsert(insertStatement);
			mongoQueryTxt.setText(mongoQuery);

			

		} catch (JSQLParserException e) {
			e.printStackTrace();
		} 
	}
	
	private String convertInsert(Insert insertStatement) {
		Table table = insertStatement.getTable();
		List<Column> columnList = insertStatement.getColumns();
		String values = insertStatement.getItemsList().toString().replace("(", "").replace(")", "").replace(" ", "");
		
		System.out.println(table.getName());
		System.out.println(columnList);
		System.out.println(values);

		LinkedHashMap<String, Object> objs = new LinkedHashMap<String, Object>();

		StringTokenizer st = new StringTokenizer(values, ",");

		int count = 0;
		while (st.hasMoreElements()) {
			Object value = st.nextElement();
			boolean isNumber = isNumeric(value.toString());
			if(isNumber) {
				boolean isDouble = isDouble(value.toString());
				if(isDouble) {
					value = Double.parseDouble(value.toString());
				} else {
					value = Long.parseLong(value.toString());
				}
			}
			if(columnList == null) {
				objs.put("<Key"+(count+1)+">", value);
			} else {
				objs.put(columnList.get(count).getColumnName(), value);
			}
			System.out.println(objs);
			count++;
		}
		
		String mongoQuery = "db."+table.getName()+".insert("+objs.toString().replace("=", ":").replace(",", ",\n\t").replace("{", "{\n\t").replace("}", "\n}")+")";
		return mongoQuery;
	}

	public static boolean isNumeric(String str) {
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}
	
	public static boolean isDouble(String str) {
		return str.matches("[-+]?[0-9]*\\.{1}[0-9]*");
	}

}
