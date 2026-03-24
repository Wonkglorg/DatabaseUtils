package com.wonkglorg.database.databases;

import com.wonkglorg.database.Connectable;
import com.wonkglorg.database.Database;
import static com.wonkglorg.database.DatabaseType.SQLITE_MEMORY;
import static com.wonkglorg.database.DatabaseType.SQLITE_MEMORY_SHARED;
import com.wonkglorg.database.datasources.FileDataSource;
import com.wonkglorg.database.datasources.MemoryDataSource;
import com.wonkglorg.database.datasources.TypedDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Wonkglorg
 */
@SuppressWarnings("unused")
public class SqliteDatabase<T extends TypedDataSource> extends Database<T> implements Connectable{
	
	/**
	 * * Creates a Sqlite database at the specified copyToPath.
	 * * The sourcePath indicates where in the project the database file can be found, it will
	 * then be
	 * copied to the destinationPath destination.
	 * * If there is no database file it will be created at the destinationPath location.
	 * <br>
	 * !!IMPORTANT!!
	 * <br>Use <br>
	 * <pre>
	 *     {@code
	 * <plugin>
	 * 	<groupId>org.apache.maven.plugins</groupId>
	 * 	<artifactId>maven-resources-plugin</artifactId>
	 * 	<version>3.3.1</version>
	 * 	<configuration>
	 * 		<nonFilteredFileExtensions>
	 * 			<nonFilteredFileExtension>db</nonFilteredFileExtension>
	 * 		</nonFilteredFileExtensions>
	 * 	</configuration>
	 * </plugin>
	 * }
	 * </pre>
	 * otherwise sqlite database files will be filtered and become corrupted.
	 *
	 * @param dataSource the datasource of the db
	 */
	private SqliteDatabase(T dataSource) {
		super(dataSource);
	}
	
	public static SqliteDatabase<MemoryDataSource> createSharedMemoryDb(String memoryName) {
		return new SqliteDatabase<>(new MemoryDataSource(SQLITE_MEMORY_SHARED, SQLITE_MEMORY_SHARED + memoryName + "?mode=memory&cache=shared"));
	}
	
	public static SqliteDatabase<MemoryDataSource> createMemoryDb() {
		return new SqliteDatabase<>(new MemoryDataSource(SQLITE_MEMORY));
	}
	
	public static SqliteDatabase<FileDataSource> createDb(FileDataSource source) {
		return new SqliteDatabase<>(source);
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
		try{
			return dataSource.getConnection();
		} catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
}

