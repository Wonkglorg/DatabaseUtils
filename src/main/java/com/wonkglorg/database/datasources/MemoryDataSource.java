package com.wonkglorg.database.datasources;

import com.wonkglorg.database.DatabaseType;
import com.wonkglorg.database.exception.DatabaseDriverNotFoundException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemoryDataSource implements TypedDataSource{
	private static final Logger log = Logger.getLogger(MemoryDataSource.class.getName());
	private final String connectionString;
	private final DatabaseType databaseType;
	protected Connection connection;
	
	/**
	 * IInstantiates a new Datasource
	 *
	 * @param type the type of database to connect to (has to be a file based one)
	 */
	public MemoryDataSource(DatabaseType type) {
		this(type, type.driver());
	}
	
	/**
	 * Instantiates a new Datasource
	 *
	 * @param type the type of database to connect to (has to be a file based one)
	 * @param connectionString the url to connect to the driver with if a custom one should be used
	 */
	public MemoryDataSource(DatabaseType type, String connectionString) {
		this.databaseType = type;
		this.connectionString = connectionString;
	}
	
	/**
	 * Opens a new Connection to the database if non exists currently
	 */
	private void connect() {
		if(connection != null){
			return;
		}
		
		try{
			Class.forName(databaseType.classLoader());
			connection = DriverManager.getConnection(databaseType.driver() + connectionString);
			
		} catch(ClassNotFoundException e){
			throw new DatabaseDriverNotFoundException("Database Driver does not exist for " + getType(), e);
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
