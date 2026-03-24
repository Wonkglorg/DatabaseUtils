package com.wonkglorg.database;

import com.wonkglorg.database.datasources.TypedDataSource;

import java.util.logging.Logger;

/**
 * @author Wonkglorg
 * <p>
 * Base class for databases
 */
@SuppressWarnings("unused")
public abstract class Database<T extends TypedDataSource> implements AutoCloseable{
	protected final Logger logger = Logger.getLogger(Database.class.getName());
	protected final T dataSource;
	
	protected Database(T dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * @return the classloader path
	 */
	public String getClassLoader() {
		return dataSource.getType().classLoader();
	}
	
	/**
	 * @return The database driver
	 */
	public String getDriver() {
		return dataSource.getType().driver();
	}
	
	/**
	 * Small helper method to sanitize input for sql only does not other sanitizations like xss or
	 * html based
	 *
	 * @param input The input to sanitize
	 * @return The sanitized output
	 */
	public String sanitize(String input) {
		return input.replaceAll("[^a-zA-Z0-9]", "");
	}
	
	public DatabaseType getDatabaseType() {
		return dataSource.getType();
	}
	
	public T getDataSource() {
		return dataSource;
	}
}
