package com.wonkglorg.database.databases;

import com.wonkglorg.database.Database;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;

import javax.sql.DataSource;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Wonkglorg
 */
@SuppressWarnings("unused")
public class JdbiDatabase<T extends DataSource> extends Database<T>{
	protected Jdbi jdbi;
	
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
	 */
	public JdbiDatabase(T dataSource) {
		super(SQLITE, dataSource);
		connect();
	}
	
	@Override
	public void close() {
		//nothing needs to be closed here
	}
	
	public void connect() {
		if(jdbi != null){
			return;
		}
		jdbi = Jdbi.create(dataSource);
	}
	
	/**
	 * Represents a query to the database that returns some result.
	 * @param sql The sql query to execute
	 * @param function The function to apply to the query
	 * @return
	 * @param <R>
	 */
	public <R> R query(@Language("SQL") String sql, Function<Query, R> function) {
		connect();
		try(Handle handle = jdbi.open(); Query query = handle.createQuery(sql)){
			return function.apply(query);
		} catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * Represents a query to the database that does not return a result.
	 * @param sql The sql query to execute
	 * @param function The function to apply to the query
	 */
	public void voidQuery(@Language("SQL") String sql, Consumer<Query> function) {
		connect();
		try(Handle handle = jdbi.open(); Query query = handle.createQuery(sql)){
			function.accept(query);
		} catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public Jdbi jdbi() {
		return jdbi;
	}
}

