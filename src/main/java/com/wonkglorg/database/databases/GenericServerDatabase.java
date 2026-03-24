package com.wonkglorg.database.databases;

import com.wonkglorg.database.Connectable;
import com.wonkglorg.database.Database;
import com.wonkglorg.database.datasources.TypedDataSource;

import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class GenericServerDatabase<T extends TypedDataSource> extends Database<T> implements Connectable{
	
	public GenericServerDatabase(T datasource) {
		super(datasource);
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
		try{
			dataSource.getConnection().close();
		} catch(SQLException e){
			throw new RuntimeException(e);
		}
		
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


