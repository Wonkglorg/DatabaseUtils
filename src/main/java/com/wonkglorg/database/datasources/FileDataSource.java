package com.wonkglorg.database.datasources;

import com.wonkglorg.database.DatabaseType;
import com.wonkglorg.database.exception.DatabaseDriverNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class FileDataSource implements TypedDataSource{
	private static final Logger log = Logger.getLogger(FileDataSource.class.getName());
	protected final Path sourceDbFile;
	protected final Path dbFile;
	private final String connectionString;
	private final DatabaseType databaseType;
	protected Connection connection;
	
	/**
	 * IInstantiates a new Datasource
	 *
	 * @param type the type of database to connect to (has to be a file based one)
	 * @param file the file to connect to (or create if absent)
	 */
	public FileDataSource(DatabaseType type, Path file) {
		this(type, file, file, type.driver() + file.toString());
	}
	
	/**
	 * Instantiates a new Datasource
	 *
	 * @param type the type of database to connect to (has to be a file based one)
	 * @param file the file to connect to (or create if absent)
	 * @param connectionString the url to connect to the driver with if a custom one should be used
	 */
	public FileDataSource(DatabaseType type, Path file, String connectionString) {
		this(type, file, file, connectionString);
	}
	
	/**
	 * Instantiates a new Datasource
	 *
	 * @param type the type of database to connect to (has to be a file based one)
	 * @param sourceFile the location to copy it from if it exists
	 * @param file the file to connect to (or create if absent)
	 */
	public FileDataSource(DatabaseType type, Path sourceFile, Path file) {
		this(type, sourceFile, file, type.driver() + file.toString());
	}
	
	/**
	 * Instantiates a new Datasource
	 *
	 * @param type the type of database to connect to (has to be a file based one)
	 * @param file the file to connect to (or create if absent)
	 * @param connectionString the url to connect to the driver with if a custom one should be used
	 */
	public FileDataSource(DatabaseType type, Path sourceFile, Path file, String connectionString) {
		this.databaseType = type;
		this.sourceDbFile = sourceFile;
		this.dbFile = file;
		this.connectionString = connectionString;
	}
	
	/**
	 * Opens a new Connection to the database if non exists currently
	 */
	private synchronized void connect() {
		try{
			Class.forName(databaseType.classLoader());
			
			Path databaseFile = dbFile;
			if(!Files.exists(databaseFile)){
				copyDatabaseFile(databaseFile, sourceDbFile);
			}
			
			if(connection == null || connection.isClosed() || !connection.isValid(2)){
				connection = new UncloseAbleConnection(DriverManager.getConnection(connectionString));
			}
			
		} catch(ClassNotFoundException e){
			throw new DatabaseDriverNotFoundException("Database Driver does not exist for " + getType(), e);
		} catch(SQLException | IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Copies the database file from the sourcePath to the destinationPath or creates a new file
	 * if it
	 * does not exist.
	 *
	 * @param databaseFile the file to copy to
	 */
	private void copyDatabaseFile(Path databaseFile, Path sourceFile) throws IOException {
		if(databaseFile.getParent() != null){
			Files.createDirectories(databaseFile.getParent());
		}
		try(InputStream resourceStream = getResource(sourceFile.toString())){
			if(resourceStream != null){
				Files.createDirectories(databaseFile.getParent());
				Files.copy(resourceStream, databaseFile);
			} else {
				Files.createFile(databaseFile);
			}
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
	
	private InputStream getResource(String filename) {
		if(filename == null){
			throw new IllegalArgumentException("Filename cannot be null");
		}
		
		try{
			URL url = this.getClass().getClassLoader().getResource(filename.replace("\\\\", "/"));
			
			if(url == null){
				return null;
			}
			
			URLConnection urlConnection = url.openConnection();
			urlConnection.setUseCaches(false);
			return urlConnection.getInputStream();
		} catch(IOException ex){
			return null;
		}
	}
	
	@Override
	public DatabaseType getType() {
		return databaseType;
	}
}
