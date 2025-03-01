package com.wonkglorg.database;

import javax.sql.DataSource;
import java.util.logging.Logger;

/**
 * @author Wonkglorg
 * <p>
 * Base class for databases
 */
@SuppressWarnings("unused")
public abstract class Database<T extends DataSource> implements AutoCloseable{
	protected final Logger logger = Logger.getLogger(Database.class.getName());
	protected final DatabaseType databaseType;
	protected final T dataSource;
	
	protected Database(DatabaseType databaseType, T dataSource) {
		this.databaseType = databaseType;
		this.dataSource = dataSource;
	}
	
	/**
	 * @return the classloader path
	 */
	public String getClassLoader() {
		return databaseType.classLoader();
	}
	
	/**
	 * @return The database driver
	 */
	public String getDriver() {
		return databaseType.driver();
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
		return databaseType;
	}
	
	public T getDataSource() {
		return dataSource;
	}
}
