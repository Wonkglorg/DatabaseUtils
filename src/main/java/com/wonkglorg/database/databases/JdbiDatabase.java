package com.wonkglorg.database.databases;


import com.wonkglorg.database.Database;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * @author Wonkglorg
 */
@SuppressWarnings("unused")
public class JdbiDatabase<T extends DataSource> extends Database<T> {
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
	 *
	 * @param sourcePath the original file to copy to a location
	 * @param destinationPath the location to copy to
	 */
	public JdbiDatabase(T dataSource) {
		super(SQLITE,dataSource);
		connect();
	}

	@Override
	public void close() {
		//nothing needs to be closed here
	}

	public void connect() {
		if (jdbi != null) {
			return;
		}
		jdbi = Jdbi.create(dataSource);
	}

	public Jdbi jdbi() {
		return jdbi;
	}
}

