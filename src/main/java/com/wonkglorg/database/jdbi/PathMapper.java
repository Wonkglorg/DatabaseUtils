package com.wonkglorg.database.jdbi;

import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a String to a Path
 */
public class PathMapper implements ColumnMapper<Path>{
	@Override
	public Path map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
		return Path.of(r.getString(columnNumber));
	}
}
