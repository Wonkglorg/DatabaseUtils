package com.wonkglorg.database;

import java.sql.Connection;

public interface Connectable {
	/**
	 * Fully disconnects the database connection
	 */
	void disconnect();

	/**
	 * @return A database connection
	 */
	Connection getConnection();
}
