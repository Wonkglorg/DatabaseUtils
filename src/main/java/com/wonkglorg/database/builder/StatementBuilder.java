package com.wonkglorg.database.builder;

import com.wonkglorg.database.objs.Tuple;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementBuilder{
	
	private static final Pattern NAMED_PARAM_PATTERN = Pattern.compile(":(\\w+)");
	
	private final String sql;
	private final Map<String, Object> params = new LinkedHashMap<>();
	
	public StatementBuilder(@Language("sql") String sql) {
		this.sql = sql;
	}
	
	public static StatementBuilder prepareNamedStatement(@Language("sql") String sql) {
		return new StatementBuilder(sql);
	}
	
	/**
	 * Add a parameter to the statement
	 *
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @return this
	 */
	public StatementBuilder param(String name, Object value) {
		params.put(name, value);
		return this;
	}
	
	public StatementBuilder param(String name1, Object value1, String name2, Object value2) {
		params.put(name1, value1);
		params.put(name2, value2);
		return this;
	}
	
	public StatementBuilder param(String name1, Object value1, String name2, Object value2, String name3, Object value3) {
		params.put(name1, value1);
		params.put(name2, value2);
		params.put(name3, value3);
		return this;
	}
	
	//@formatter:off
	public StatementBuilder param(String name1, Object value1, String name2, Object value2, String name3, Object value3, String name4, Object value4) {
		params.put(name1, value1);
		params.put(name2, value2);
		params.put(name3, value3);
		params.put(name4, value4);
		return this;
	}
	
	public StatementBuilder param(String name1, Object value1, String name2, Object value2, String name3, Object value3, String name4, Object value4, String name5, Object value5) {
		params.put(name1, value1);
		params.put(name2, value2);
		params.put(name3, value3);
		params.put(name4, value4);
		params.put(name5, value5);
		return this;
	}
	//@formatter:on
	
	public StatementBuilder param(Tuple<String, Object>... pair) {
		for(Tuple<String, Object> p : pair){
			params.put(p.first(), p.second());
		}
		return this;
	}
	
	/**
	 * Add multiple parameters to the statement
	 *
	 * @param values the values to add
	 * @return this
	 */
	public StatementBuilder param(Map<String, Object> values) {
		params.putAll(values);
		return this;
	}
	
	/**
	 * Build the statement
	 *
	 * @param conn the connection to use
	 * @return the prepared statement
	 * @throws SQLException if the statement could not be prepared
	 */
	public PreparedStatement build(Connection conn) throws SQLException {
		Matcher matcher = NAMED_PARAM_PATTERN.matcher(sql);
		
		List<String> paramOrder = new ArrayList<>();
		StringBuilder sqlWithPlaceholders = new StringBuilder();
		
		while(matcher.find()){
			String paramName = matcher.group(1);
			paramOrder.add(paramName);
			matcher.appendReplacement(sqlWithPlaceholders, "?");
		}
		matcher.appendTail(sqlWithPlaceholders);
		
		PreparedStatement stmt = conn.prepareStatement(sqlWithPlaceholders.toString());
		
		for(int i = 0; i < paramOrder.size(); i++){
			String paramName = paramOrder.get(i);
			if(!params.containsKey(paramName)){
				throw new IllegalArgumentException("Missing parameter: " + paramName);
			}
			stmt.setObject(i + 1, params.get(paramName));
		}
		
		return stmt;
	}
	
	/**
	 * Execute the statement
	 *
	 * @param conn the connection to use
	 * @return the number of rows affected
	 * @throws SQLException if the statement could not be executed
	 */
	public int executeUpdate(Connection conn) throws SQLException {
		try(PreparedStatement stmt = build(conn)){
			return stmt.executeUpdate();
		}
	}
	
	/**
	 * Execute the statement
	 *
	 * @param conn the connection to use
	 * @return the result set
	 * @throws SQLException if the statement could not be executed
	 */
	public ResultSet executeQuery(Connection conn) throws SQLException {
		try(PreparedStatement stmt = build(conn)){
			return stmt.executeQuery();
		}
	}
	
	/**
	 * Execute the statement
	 *
	 * @param conn the connection to use
	 * @throws SQLException if the statement could not be executed
	 */
	public boolean execute(Connection conn) throws SQLException {
		try(PreparedStatement stmt = build(conn)){
			return stmt.execute();
		}
	}
}

