package com.wonkglorg.database.builder;

import com.wonkglorg.database.Database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
public abstract class Parameterized<T extends Parameterized<?, ?, ?>, U, R>{
	
	protected static final Pattern NAMED_PARAM_PATTERN = Pattern.compile(":(\\w+)");
	
	protected final Map<String, U> params = new LinkedHashMap<>();
	
	protected final String sql;
	
	protected final boolean transaction;
	
	public Parameterized(String sql, boolean transaction) {
		this.sql = sql;
		this.transaction = transaction;
	}
	
	public Parameterized(String sql) {
		this(sql, true);
	}
	
	/**
	 * Add a parameter to the statement
	 *
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @return this
	 */
	public T param(String name, U value) {
		params.put(name, value);
		return (T) this;
	}
	
	public T param(String name1, U value1, String name2, U value2) {
		params.put(name1, value1);
		params.put(name2, value2);
		return (T) this;
	}
	
	public T param(String name1, U value1, String name2, U value2, String name3, U value3) {
		params.put(name1, value1);
		params.put(name2, value2);
		params.put(name3, value3);
		return (T) this;
	}
	
	//@formatter:off
	public T param(String name1, U value1, String name2, U value2, String name3, U value3, String name4, U value4) {
		params.put(name1, value1);
		params.put(name2, value2);
		params.put(name3, value3);
		params.put(name4, value4);
		return (T) this;
	}
	
	public T param(String name1, U value1, String name2, U value2, String name3, U value3, String name4, U value4, String name5, U value5) {
		params.put(name1, value1);
		params.put(name2, value2);
		params.put(name3, value3);
		params.put(name4, value4);
		params.put(name5, value5);
		return (T)this;
	}
	//@formatter:on
	
	/**
	 * Add multiple parameters to the statement
	 *
	 * @param values the values to add
	 * @return this
	 */
	public T param(Map<String, U> values) {
		params.putAll(values);
		return (T) this;
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
	 * Build the statement
	 * @param database the database to use
	 * @return the prepared statement
	 * @throws SQLException if the statement could not be prepared
	 */
	public PreparedStatement build(Database<? extends DataSource> database) throws SQLException {
		return build(database.getDataSource().getConnection());
	}
	
	public abstract R execute(Connection conn) throws SQLException;
	
	protected List<String> getParameterOrder(String sql) {
		Matcher matcher = NAMED_PARAM_PATTERN.matcher(sql);
		List<String> paramOrder = new ArrayList<>();
		while(matcher.find()){
			paramOrder.add(matcher.group(1));
		}
		return paramOrder;
	}
	
}
