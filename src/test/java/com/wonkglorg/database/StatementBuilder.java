package com.wonkglorg.database;

import static com.wonkglorg.database.builder.StatementBuilder.query;
import static com.wonkglorg.database.builder.StatementBuilder.script;
import static com.wonkglorg.database.builder.StatementBuilder.update;
import com.wonkglorg.database.builder.resultset.ClosingResultSet;
import com.wonkglorg.database.builder.statement.Query;
import com.wonkglorg.database.databases.SqliteDatabase;
import com.wonkglorg.database.datasources.FileDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.sql.SQLException;

class StatementBuilder{
	
	private static SqliteDatabase<FileDataSource> db;
	
	@BeforeAll
	static void setup() throws SQLException {
		System.out.println("Creating Database");
		db = new SqliteDatabase<>(new FileDataSource(DatabaseType.SQLITE, Path.of("test", "test.db")));
		update("CREATE TABLE IF NOT EXISTS test (id INTEGER PRIMARY KEY, name TEXT)").execute(db.getConnection());
	}
	
	@Test
	void singleUpdateStatement() {
		try{
			int rowCount = getRowCount();
			update("INSERT OR IGNORE INTO test (name) VALUES (:value)").param("value", "test").execute(db.getConnection());
			int newRowCount = getRowCount();
			
			Assertions.assertEquals(rowCount + 1, newRowCount);
		} catch(Exception e){
			Assertions.fail("Failed to create table", e);
		}
	}
	
	@Test
	void queryStatement() {
		try{
			query("SELECT * FROM test").execute(db.getConnection());
		} catch(Exception e){
			Assertions.fail("Failed to create query", e);
		}
	}
	
	@Test
	void multiQueryStatement() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> query("""
				INSERT OR IGNORE INTO test (name) VALUES (:valueOne);
				INSERT OR IGNORE INTO test (name) VALUES (:valueTwo);
				""").param("valueOne", "test").param("valueTwo", "test2").execute(db.getConnection()));
	}
	
	@Test
	void multiScriptStatement() {
		try{
			int rowCount = getRowCount();
			script("""
					INSERT OR IGNORE INTO test(name) VALUES (:value);
					INSERT OR IGNORE INTO test(name) VALUES (:value)
					""").param("value", "test").execute(db.getConnection());
			int newRowCount = getRowCount();
			
			Assertions.assertEquals(rowCount + 2, newRowCount);
		} catch(Exception e){
			Assertions.fail("Failed to parse script statemenmt", e);
		}
	}
	
	@Test
	void multiUpdateStatement() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> update("""
				INSERT OR IGNORE INTO test (name) VALUES (:valueOne);
				INSERT OR IGNORE INTO test (name) VALUES (:valueTwo);
				""").param("valueOne", "test").param("valueTwo", "test2").execute(db.getConnection()));
	}
	
	private int getRowCount() {
		Query query = query("SELECT COUNT(*) FROM test");
		try(ClosingResultSet resultSet = query.execute(db.getConnection())){
			if(resultSet.next()){
				return resultSet.getInt(1);
			}
		} catch(Exception e){
			System.err.println("Failed to get row count");
			return -1;
		}
		return 0;
	}
}
