package com.wonkglorg.database;

import com.wonkglorg.database.values.DbName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class GenericServerDatabase extends Database implements Connectable {

    //todo rework database class to correctly handle connection strings from different database
    // types currently quite limited
    protected ConnectionBuilder builder;
    private final BlockingQueue<Connection> connectionPool;
    private DbName databaseName;


    public GenericServerDatabase(ConnectionBuilder builder, String driver, String classLoader,
                                 int poolSize) {
        super(driver, classLoader);
        this.builder = builder;
        connectionPool = new ArrayBlockingQueue<>(poolSize);
        initializeConnectionPool(poolSize);
    }

    public GenericServerDatabase(ConnectionBuilder builder, DatabaseType databaseType,
                                 int poolSize) {
        this(builder, databaseType.driver(), databaseType.classLoader(), poolSize);
    }

    /**
     * Create a new GenericServerDatabase with a pool size of 3
     *
     * @param builder      the connection builder
     * @param databaseType the type of database
     */
    public GenericServerDatabase(ConnectionBuilder builder, DatabaseType databaseType) {
        this(builder, databaseType, 3);
    }

    public GenericServerDatabase(ConnectionBuilder builder, String driver, String classLoader) {
        this(builder, driver, classLoader, 3);
    }

    /**
     * @return a connection from the connection pool should be released after use manually
     */
    @Override
    public Connection getConnection() {
        try {
            return connectionPool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
     * Use a specific database for a connection
     *
     * @param connection   the connection to use the database on
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
     * Close all resources
     */
    @Override
    public void close() {
        disconnect();
    }
}
