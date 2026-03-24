package com.wonkglorg.database.datasources;

import com.wonkglorg.database.DatabaseType;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Datasource containing a specific Database Type definition
 */
public interface TypedDataSource extends DataSource{
	
	DatabaseType getType();
	
	@Override
	default PrintWriter getLogWriter() throws SQLException {
		return null;
	}
	
	@Override
	default void setLogWriter(PrintWriter out) throws SQLException {
	
	}
	
	@Override
	default void setLoginTimeout(int seconds) throws SQLException {
	
	}
	
	@Override
	default int getLoginTimeout() throws SQLException {
		return 0;
	}
	
	@Override
	default Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
	
	@Override
	default <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}
	
	@Override
	default boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}
}
