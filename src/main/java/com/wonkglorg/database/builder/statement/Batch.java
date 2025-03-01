package com.wonkglorg.database.builder.statement;

import com.wonkglorg.database.builder.Parameterized;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;

public class Batch<T> extends Parameterized<Batch<?>, Function<T, Object>, Void>{
	private final Collection<T> batch;
	
	public Batch(String sql, Collection<T> batch) {
		super(sql);
		this.batch = batch;
	}
	
	@Override
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
		
		return conn.prepareStatement(sqlWithPlaceholders.toString());
	}
	
	@Override
	public Void execute(Connection conn) throws SQLException {
		if(batch.isEmpty()){
			return null;
		}
		
		try(PreparedStatement stmt = build(conn)){
			List<String> paramOrder = getParameterOrder(sql);
			
			for(T item : batch){
				for(int i = 0; i < paramOrder.size(); i++){
					String paramName = paramOrder.get(i);
					Function<T, Object> extractor = params.get(paramName);
					if(extractor == null){
						throw new IllegalArgumentException("Missing extractor for parameter: " + paramName);
					}
					stmt.setObject(i + 1, extractor.apply(item));
				}
				stmt.addBatch();
			}
			stmt.executeBatch();
		}
		return null;
	}
	
}
