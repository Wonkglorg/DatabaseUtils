package com.wonkglorg.database.datasources;

import com.wonkglorg.database.DatabaseType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PooledServerDataSource implements TypedDataSource{
	private static final Logger log = Logger.getLogger(ServerDataSource.class.getName());
	
	private final DatabaseType databaseType;
	private final String url;
	private final String user;
	private final String password;
	
	private final int maxPoolSize = 10;
	
	private final Queue<Connection> available = new ArrayDeque<>();
	private int createdConnections = 0;
	
	public PooledServerDataSource(DatabaseType type, String url, String user, String password) {
		this.databaseType = type;
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	private synchronized Connection createConnection() throws SQLException {
		try{
			Class.forName(databaseType.classLoader());
		} catch(ClassNotFoundException e){
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		createdConnections++;
		return DriverManager.getConnection(url, user, password);
	}
	
	@Override
	public synchronized Connection getConnection() {
		try{
			if(!available.isEmpty()){
				return new PooledConnection(available.poll(), this);
			}
			
			if(createdConnections < maxPoolSize){
				return new PooledConnection(createConnection(), this);
			}
			
			while(available.isEmpty()){
				try{
					wait();
				} catch(InterruptedException e){
					Thread.currentThread().interrupt();
					throw new RuntimeException(e);
				}
			}
			
			return new PooledConnection(available.poll(), this);
			
		} catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	
	synchronized void releaseConnection(Connection connection) {
		available.offer(connection);
		notifyAll();
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
