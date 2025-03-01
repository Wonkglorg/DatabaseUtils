package com.wonkglorg.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class GenericServerDatabase<T extends DataSource> extends Database<T> implements Connectable{
	
	
	public GenericServerDatabase(DatabaseType type, T datasource) {
		super(type, datasource);
	}
	
	/**
	 * Close all resources
	 */
	@Override
	public void close() {
		disconnect();
	}
	
	@Override
	public void disconnect() {
	}
	
	@Override
	public Connection getConnection() {
		try{
			return dataSource.getConnection();
		} catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
}


