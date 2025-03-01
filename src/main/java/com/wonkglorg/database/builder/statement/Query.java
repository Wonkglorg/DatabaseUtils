package com.wonkglorg.database.builder.statement;

import com.wonkglorg.database.builder.Parameterized;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Query extends Parameterized<Query,Object, ResultSet>{
	
	public Query(String sql) {
		super(sql);
	}
	
	@Override
	public ClosingResultSet execute(Connection conn) throws SQLException {
		if(transaction){
			conn.setAutoCommit(false);
		}
		try(PreparedStatement stmt = build(conn)){
			return stmt.executeQuery();
		} finally{
			if(transaction){
				conn.commit();
				conn.setAutoCommit(true);
			}
		}
	}
}
