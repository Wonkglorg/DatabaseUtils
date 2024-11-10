package com.wonkglorg.database;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * @author Wonkglorg
 */
@SuppressWarnings("unused")
public class SqliteDatabase extends Database {
    protected final Path sourcePath;
    protected Connection connection;
    protected final Path destinationPath;
    protected final String databaseName;

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
     * @param sourcePath      the original file to copy to a location
     * @param destinationPath the location to copy to
     */
    public SqliteDatabase(Path sourcePath, Path destinationPath) {
        super(DatabaseType.SQLITE);
        String name = destinationPath.getFileName().toString();
        databaseName = name.endsWith(".db") ? name : name + ".db";
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        connect();
    }

    public SqliteDatabase(Path openInPath) {
        this(openInPath, openInPath);
    }

    /**
     * Opens a new Connection to the database if non exists currently
     */

    public void connect() {
        if (connection != null) {
            return;
        }

        try {
            Class.forName(getClassLoader());

            File databaseFile = destinationPath.toAbsolutePath().toFile();
            if (!databaseFile.exists()) {
                copyDatabaseFile(databaseFile);
            }
            String connectionString = getDriver() + destinationPath;
            connection = DriverManager.getConnection(connectionString);

        } catch (ClassNotFoundException | SQLException | IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Copies the database file from the sourcePath to the destinationPath or creates a new file
     * if it
     * does not exist.
     *
     * @param databaseFile the file to copy to
     */
    private void copyDatabaseFile(File databaseFile) throws IOException {
        try (InputStream resourceStream = getResource(sourcePath.toString())) {
            if (resourceStream != null) {
                Files.createDirectories(destinationPath.getParent());
                Files.copy(resourceStream, databaseFile.toPath());
            } else {
                boolean ignore = databaseFile.createNewFile();
            }
        }

    }

    private InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = getClass().getClassLoader().getResource(filename.replace("\\\\", "/"));

            if (url == null) {
                return null;
            }

            URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            return urlConnection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }


    @Override
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }


    @Override
    public Connection getConnection() {
        connect();
        return connection;
    }

    @Override
    public void close() {
        disconnect();
    }
}

