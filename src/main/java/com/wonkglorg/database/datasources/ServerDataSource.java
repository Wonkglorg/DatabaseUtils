package com.wonkglorg.database.datasources;

import com.wonkglorg.database.DatabaseType;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class ServerDataSource implements DataSource{
	private BlockingQueue<Connection> connectionPool;
	private DatabaseType databaseType;
	
	public ServerDataSource(DatabaseType type) {
		//todo properly implement connections
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return null;
	}
	
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return null;
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}
	
	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}
	
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
	
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}
	
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	
	}
	
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	
	}
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}
}
