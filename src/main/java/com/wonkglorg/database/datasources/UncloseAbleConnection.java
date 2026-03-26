package com.wonkglorg.database.datasources;

import java.sql.Connection;
import java.sql.SQLException;

public class UncloseAbleConnection extends ConnectionWrapper{
	public UncloseAbleConnection(Connection delegate) {
		super(delegate);
	}
	
	@Override
	public void close() throws SQLException {
		//do nothing
	}
}
