package com.wonkglorg.database;

import com.wonkglorg.database.values.DbName;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class GenericServerDatabase<T extends DataSource> extends Database<T>
		implements Connectable {

	private final BlockingQueue<Connection> connectionPool;
	//todo rework database class to correctly handle connection strings from different database
	// types currently quite limited
	protected ConnectionBuilder builder;
	private DbName databaseName;


	public GenericServerDatabase(DatabaseType type, T datasource) {
		super(type, datasource);
	}

	/**
	 * Close all resources
	 */
	@Override
	public void close() {
		disconnect();
	}

	/**
	 * Helper Method to create a connection
	 *
	 * @return a new connection
	 */
	private Connection createConnection() {
		try {
			Class.forName(getClassLoader());
			return DriverManager.getConnection(builder.build());

		} catch (Exception e) {
			disconnect();
			throw new RuntimeException(e);
		}

	}

	/**
	 * Disconnect from the database and close all connections
	 */
	@Override
	public void disconnect() {
		for (Connection connection : connectionPool) {
			try {
				connection.close();
			} catch (SQLException e) {
				System.out.println("Error closing connection: " + e.getMessage());
			}
		}
	}

	/**
	 * @return a connection from the connection pool should be released after use manually
	 */
	@Override
	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Initialize the connection pool
	 *
	 * @param poolSize the size of the connection pool
	 */
	private void initializeConnectionPool(int poolSize) {
		for (int i = 0; i < poolSize; i++) {
			connectionPool.add(createConnection());
		}
	}

	/**
	 * Release a connection back to the connection pool
	 *
	 * @param connection the connection to release
	 */
	public void releaseConnection(Connection connection) {
		connectionPool.offer(connection);
	}

	/**
	 * Resize the connection pool
	 *
	 * @param newSize the new size of the connection pool
	 */
	public void resizePool(int newSize) throws InterruptedException {
		if (newSize < 1) {
			throw new IllegalArgumentException("Pool size must be at least 1");
		}
		synchronized (connectionPool) {
			int currentSize = connectionPool.size();
			if (newSize < currentSize) {
				for (int i = newSize; i < currentSize; i++) {
					try {
						connectionPool.take().close();
					} catch (SQLException e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			} else if (newSize > currentSize) {
				for (int i = currentSize; i < newSize; i++) {
					connectionPool.add(createConnection());
				}
			}
		}
	}

	/**
	 * Use a specific database for a connection
	 *
	 * @param connection the connection to use the database on
	 * @param databaseName the name of the database to use
	 */
	public void useDatabase(Connection connection, DbName databaseName) {
		String name = sanitize(databaseName.toString());
		try (Statement statement = connection.createStatement()) {
			statement.execute("USE " + name);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Use a specific database for all connections
	 *
	 * @param databaseName the name of the database to use
	 */
	public void useDatabaseForAllConnections(String databaseName) {
		this.databaseName = new DbName(databaseName);
		for (Connection connection : connectionPool) {
			useDatabase(connection, this.databaseName);
		}
	}
}
