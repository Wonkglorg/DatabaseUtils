package com.wonkglorg.database.datasources;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class MemorySqliteDataSource implements DataSource{
	
	private static final String JDBC_URL = "jdbc:sqlite:file:memdb1?mode=memory&cache=shared";
	private PrintWriter logWriter = new PrintWriter(System.out);
	private int loginTimeout = 30; // seconds
	
	static {
		try{
			Class.forName("org.sqlite.JDBC"); // Ensure driver is loaded
		} catch(ClassNotFoundException e){
			throw new RuntimeException("SQLite JDBC driver not found!", e);
		}
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(JDBC_URL);
	}
	
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		// SQLite ignores username/password, but method needs to be implemented
		return getConnection();
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return logWriter;
	}
	
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.logWriter = out;
	}
	
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		this.loginTimeout = seconds;
		DriverManager.setLoginTimeout(seconds);
	}
	
	@Override
	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}
	
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("ParentLogger not supported by SQLite");
	}
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(this)){
			return iface.cast(this);
		}
		throw new SQLException("Not a wrapper for " + iface.getName());
	}
	
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface.isInstance(this);
	}
}
