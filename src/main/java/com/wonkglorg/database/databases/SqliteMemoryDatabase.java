package com.wonkglorg.database.databases;

import com.wonkglorg.database.Connectable;
import com.wonkglorg.database.Database;
import static com.wonkglorg.database.DatabaseType.SQLITE_MEMORY;
import static com.wonkglorg.database.DatabaseType.SQLITE_MEMORY_SHARED;
import com.wonkglorg.database.datasources.MemoryDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Wonkglorg
 */
@SuppressWarnings("unused")
public class SqliteMemoryDatabase extends Database<MemoryDataSource> implements Connectable{
	
	/**
	 * Creates a shared sqlite memory database
	 *
	 * @param memoryName the database name
	 */
	public SqliteMemoryDatabase(String memoryName) {
		super(new MemoryDataSource(SQLITE_MEMORY_SHARED, SQLITE_MEMORY_SHARED + memoryName + "?mode=memory&cache=shared"));
	}
	
	/**
	 * Creates a private sqlite memory database
	 */
	public SqliteMemoryDatabase() {
		super(new MemoryDataSource(SQLITE_MEMORY));
	}
	
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
		return dataSource.getConnection();
	}
}

