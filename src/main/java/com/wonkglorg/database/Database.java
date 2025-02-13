package com.wonkglorg.database;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Wonkglorg
 * <p>
 * Base class for databases
 */
@SuppressWarnings("unused")
public abstract class Database implements AutoCloseable {
	public record DatabaseType(String name, String driver, String classLoader) {
	}

	public static final DatabaseType MYSQL =
			new DatabaseType("Mysql", "jdbc:mysql:", "com.mysql.cj.jdbc.Driver");
	public static final DatabaseType SQLITE =
			new DatabaseType("Sqlite", "jdbc:sqlite:", "org.sqlite.JDBC");
	public static final DatabaseType POSTGRESQL =
			new DatabaseType("PostgreSQL", "jdbc:postgresql:", "org.postgresql.Driver");
	public static final DatabaseType SQLSERVER =
			new DatabaseType("SQLServer", "jdbc:sqlserver:", "org.sqlserver.jdbc.SQLServerDriver");
	public static final DatabaseType MARIA_DB =
			new DatabaseType("MariaDB", "jdbc:mariadb:", "org.mariadb.jdbc.Driver");
	protected final String driver;
	protected final String classloader;
	protected final Logger logger = Logger.getLogger(Database.class.getName());

	protected Database(DatabaseType databaseType) {
		this.driver = databaseType.driver();
		this.classloader = databaseType.classLoader();
	}

	protected Database(final String driver, final String classLoader) {
		this.driver = driver;
		this.classloader = classLoader;
	}


	public static byte[] convertToByteArray(BufferedImage image, String formatType)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, formatType, baos);
		return baos.toByteArray();
	}

	/**
	 * Checks the current database the connection is connected to
	 *
	 * @return Gets the name of the database currently connected to
	 */
	public String checkCurrentDatabase(Connection connection) {

		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT DB_NAME() AS CurrentDB")) {
			if (rs.next()) {
				return rs.getString("CurrentDB");
			}
		} catch (Exception e) {
			throw new RuntimeException("Error logging action: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Close the result set and the statement
	 *
	 * @param resultSet the result set to close
	 */
	protected void closeResources(ResultSet resultSet) {
		Statement statement = null;
		if (resultSet != null) {
			try {
				statement = resultSet.getStatement();
				resultSet.close();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	/**
	 * @return the classloader path
	 */
	public String getClassLoader() {
		return classloader;
	}

	/**
	 * @return The database driver
	 */
	public String getDriver() {
		return driver;
	}


	/**
	 * Small helper method to sanitize input for sql only does not other sanitizations like xss or
	 * html based
	 *
	 * @param input The input to sanitize
	 * @return The sanitized output
	 */
	public String sanitize(String input) {
		return input.replaceAll("[^a-zA-Z0-9]", "");
	}

}
