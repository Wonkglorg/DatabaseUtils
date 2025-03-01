package com.wonkglorg.database.builder.statement;

import com.wonkglorg.database.builder.Parameterized;
import com.wonkglorg.database.builder.resultset.ClosingResultSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents an sql query, which can be parameterized, if parameters are given only 1 sql statement can be run at a time for processing multiple rows in 1 request use {@link Script}
 */
public class Query extends Parameterized<Query, Object, ClosingResultSet, PreparedStatement>{
	
	public Query(String sql) {
		super(sql);
	}
	
	@Override
	public ClosingResultSet execute(Connection conn) throws SQLException {
		PreparedStatement statement = build(conn);
		return new ClosingResultSet(statement.executeQuery(), statement);
	}
}
