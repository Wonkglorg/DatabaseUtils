package com.wonkglorg.database.datasources;

import com.wonkglorg.database.DatabaseType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerDataSource implements TypedDataSource{
	private static final Logger log = Logger.getLogger(ServerDataSource.class.getName());
	
	private final DatabaseType databaseType;
	private final String url;
	private final String user;
	private final String password;
	
	protected Connection connection;
	
	public ServerDataSource(DatabaseType type, String url, String user, String password) {
		this.databaseType = type;
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	/**
	 * Opens a new Connection if none exists (same behavior as FileDataSource)
	 */
	private synchronized void connect() {
		try{
			Class.forName(databaseType.classLoader());
			
			if(connection == null || connection.isClosed() || !connection.isValid(2)){
				connection = new UncloseAbleConnection(DriverManager.getConnection(url, user, password));
			}
			
		} catch(ClassNotFoundException e){
			log.log(Level.SEVERE, e.getMessage(), e);
		} catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Connection getConnection() {
		connect();
		return connection;
	}
	
	@Override
	public Connection getConnection(String username, String password) {
		return getConnection();
	}
	
	@Override
	public DatabaseType getType() {
		return databaseType;
	}
}