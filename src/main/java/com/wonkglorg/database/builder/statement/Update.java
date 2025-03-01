package com.wonkglorg.database.builder.statement;

import com.wonkglorg.database.builder.Parameterized;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Update extends Parameterized<Update, Object, Integer>{
	
	public Update(String sql) {
		super(sql);
	}
	
	@Override
	public Integer execute(Connection conn) throws SQLException {
		if(transaction){
			conn.setAutoCommit(false);
		}
		try(PreparedStatement stmt = build(conn)){
			return stmt.executeUpdate();
		} finally{
			if(transaction){
				conn.commit();
				conn.setAutoCommit(true);
			}
		}
	}
}
