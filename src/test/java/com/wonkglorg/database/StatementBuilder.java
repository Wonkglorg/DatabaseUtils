package com.wonkglorg.database;

import static com.wonkglorg.database.builder.StatementBuilder.query;
import static com.wonkglorg.database.builder.StatementBuilder.update;
import com.wonkglorg.database.databases.SqliteDatabase;
import com.wonkglorg.database.datasources.FileDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.nio.file.Path;
import java.sql.ResultSet;

class StatementBuilder{
	private static final Logger logger = LoggerFactory.getLogger(StatementBuilder.class);
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StatementBuilder.class);
	
	private static SqliteDatabase<FileDataSource> db;
	
	@BeforeAll
	static void setup() {
		log.info("Creating Database");
		db = new SqliteDatabase<>(new FileDataSource(DatabaseType.SQLITE, Path.of("test", "test.db")));
	}
	
	@Test
	void singleUpdateStatement() {
		try{
			update("CREATE TABLE IF NOT EXISTS test (id INTEGER PRIMARY KEY, name TEXT)").execute(db.getConnection());
			update("INSERT OR IGNORE INTO test (name) VALUES (:value)")
					.param("value", "test")
					.execute(db.getConnection());
			
			ResultSet resultSet = query("SELECT * FROM test").execute(db.getConnection());
			int resultCount = 0;
			while(resultSet.next()){
				resultCount++;
			}
			Assertions.assertEquals(1, resultCount);
		} catch(Exception e){
			Assertions.fail("Failed to create table", e);
		}
		
	}
}
