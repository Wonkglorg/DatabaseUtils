package com.wonkglorg.database;


import com.wonkglorg.database.datatypes.*;
import com.wonkglorg.database.exceptions.IncorrectTypeConversionException;
import com.wonkglorg.interfaces.functional.checked.CheckedFunction;
import com.wonkglorg.ip.IPv4;
import com.wonkglorg.ip.IPv6;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.RecordComponent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Wonkglorg
 * <p>
 * Base class for databases
 */
@SuppressWarnings("unused")
public abstract class Database implements AutoCloseable {
    protected final String driver;
    protected final String classloader;
    protected final Logger logger = Logger.getLogger(Database.class.getName());
    /**
     * A Data Mapper used to map values for {@link #recordAdapter(Class)} for all databases
     */
    private static final Map<Class<?>, DataTypeHandler<?>> globalDataTypeMapper = new ConcurrentHashMap<>();
    /**
     * A Data Mapper used to map values for {@link #recordAdapter(Class)} for this database only
     */
    private final Map<Class<?>, DataTypeHandler<?>> localDataTypeMapper = new ConcurrentHashMap<>();

    static {
        globalDataTypeMapper.put(Blob.class, new TypeHandlerBlob());
        globalDataTypeMapper.put(Boolean.class, new TypeHandlerBoolean());
        globalDataTypeMapper.put(boolean.class, new TypeHandlerBoolean());
        globalDataTypeMapper.put(Byte.class, new TypeHandlerByte());
        globalDataTypeMapper.put(byte.class, new TypeHandlerByte());
        globalDataTypeMapper.put(byte[].class, new TypeHandlerByteArray());
        globalDataTypeMapper.put(Character.class, new TypeHandlerChar());
        globalDataTypeMapper.put(char.class, new TypeHandlerChar());
        globalDataTypeMapper.put(Date.class, new TypeHandlerDate());
        globalDataTypeMapper.put(Double.class, new TypeHandlerDouble());
        globalDataTypeMapper.put(double.class, new TypeHandlerDouble());
        globalDataTypeMapper.put(Float.class, new TypeHandlerFloat());
        globalDataTypeMapper.put(float.class, new TypeHandlerFloat());
        globalDataTypeMapper.put(Image.class, new TypeHandlerImage());
        globalDataTypeMapper.put(Integer.class, new TypeHandlerInteger());
        globalDataTypeMapper.put(int.class, new TypeHandlerInteger());
        globalDataTypeMapper.put(Long.class, new TypeHandlerLong());
        globalDataTypeMapper.put(long.class, new TypeHandlerLong());
        globalDataTypeMapper.put(Short.class, new TypeHandlerShort());
        globalDataTypeMapper.put(short.class, new TypeHandlerShort());
        globalDataTypeMapper.put(String.class, new TypeHandlerString());
        globalDataTypeMapper.put(Time.class, new TypeHandlerTime());
        globalDataTypeMapper.put(Timestamp.class, new TypeHandlerTimeStamp());
        globalDataTypeMapper.put(IPv4.class, new TypeHandlerIpv4());
        globalDataTypeMapper.put(IPv6.class, new TypeHandlerIpv6());
    }


    protected Database(DatabaseType databaseType) {
        this.driver = databaseType.getDriver();
        this.classloader = databaseType.getClassLoader();
    }

    protected Database(final String driver, final String classLoader) {
        this.driver = driver;
        this.classloader = classLoader;
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

    /**
     * @return A database connection
     */

    public abstract Connection getConnection();

    /**
     * Fully disconnects the database connection
     */
    public abstract void disconnect();

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

    public CheckedFunction<ResultSet, Integer> singleIntAdapter() {
        return resultSet -> resultSet.getInt(1);
    }

    public CheckedFunction<ResultSet, String> singleStringAdapter() {
        return resultSet -> resultSet.getString(1);
    }

    public CheckedFunction<ResultSet, Boolean> singleBooleanAdapter() {
        return resultSet -> resultSet.getBoolean(1);
    }

    public CheckedFunction<ResultSet, Long> singleLongAdapter() {
        return resultSet -> resultSet.getLong(1);
    }

    public CheckedFunction<ResultSet, Double> singleDoubleAdapter() {
        return resultSet -> resultSet.getDouble(1);
    }

    public CheckedFunction<ResultSet, Float> singleFloatAdapter() {
        return resultSet -> resultSet.getFloat(1);
    }

    public CheckedFunction<ResultSet, Short> singleShortAdapter() {
        return resultSet -> resultSet.getShort(1);
    }

    public CheckedFunction<ResultSet, Byte> singleByteAdapter() {
        return resultSet -> resultSet.getByte(1);
    }


    /**
     * Naps each row to its matching record class
     *
     * @param resultSet the result set to map
     * @param adapter   the adapter to map the result set to a record
     * @param <T>       the type of the record
     * @return the list of records or null if an error occurred
     */
    public <T extends Record> List<T> mapRecords(ResultSet resultSet, CheckedFunction<ResultSet, T> adapter) {
        try {
            List<T> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(adapter.apply(resultSet));
            }
            return list;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }


    /**
     * Maps each row to a placeholder in the sql prepared statement
     *
     * @param record    the record to map
     * @param statement the statement to map the record to
     * @param offset    the offset to start (default:0)  starts at index 1
     */
    public void recordToDatabase(Record record, PreparedStatement statement, int offset) {
        try {
            RecordComponent[] components = record.getClass().getRecordComponents();
            for (int i = 0; i < components.length; i++) {
                Object value = components[i].getAccessor().invoke(record);
                globalDataTypeMapper.get(value.getClass()).setParameter(statement, i + 1 + offset, value);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }


    /**
     * Maps a record constructor to its matching sql columns (names MUST match, or it will not work)
     * <p/>
     * If any of the record columns do not have an adapter mapped a custom can be added / overwritten
     * with {@link #addDataMapper(Class, DataTypeHandler)}
     *
     * @param recordClass the record class to map
     * @param <T>         the type of the record
     * @return the adapter to convert the result set to a record
     */
    private  <T extends Record> CheckedFunction<ResultSet, T> genericRecordAdapter(Class<T> recordClass, boolean useIndex, int offset) {
        return resultSet -> {
            Class<?> type = null;
            String columnName = null;
            try {
                RecordComponent[] components = recordClass.getRecordComponents();
                Object[] args = new Object[components.length];

                if (resultSet == null) {
                    throw new SQLException("Result set is null");
                }

                for (int i = 0; i < components.length; i++) {
                    RecordComponent component = components[i];
                    columnName = component.getName();
                    type = component.getType();
                    //check local first then global
                    var mappingFunction = localDataTypeMapper.get(type);
                    if (mappingFunction == null) {
                        mappingFunction = globalDataTypeMapper.get(type);
                        if (mappingFunction == null) {
                            throw new NullPointerException("Data type %s does not have a valid mapping function".formatted(type));
                        }
                    }

                    if (useIndex) {
                        args[i] = mappingFunction.getParameter(resultSet, i + 1 + offset);
                    } else {
                        args[i] = mappingFunction.getParameter(resultSet, columnName);
                    }
                }
                try {
                    return recordClass.getDeclaredConstructor(Arrays.stream(components)//
                            .map(RecordComponent::getType)//
                            .toArray(Class<?>[]::new)).newInstance(args);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Failed to create record: " + recordClass.getName() + " with args: " + Arrays.toString(args), e);
                }

            } catch (SQLException e) {
                throw new IncorrectTypeConversionException("Failed to map record components: type(" + type + ") referenceName(" + columnName + ")", columnName, type, e);
            }
        };
    }

    //todo rework this to not need some weird stupid extras to work

    /**
     * Maps a record constructor to its matching sql columns (names MUST match, or it will not work)
     * <p/>
     * If any of the record columns do not have an adapter mapped a custom can be added / overwritten
     * with {@link #addDataMapper(Class, DataTypeHandler)}
     *
     * @param recordClass the record class to map
     * @param <T>         the type of the record
     * @return the adapter to convert the result set to a record
     */
    public <T extends Record> T recordAdapter(Class<T> recordClass, ResultSet resultSet) {
        return genericRecordAdapter(recordClass, false, 0).apply(resultSet);
    }

    /**
     * Maps a record constructor to its matching sql columns (in index order constructor must match
     * the order)
     * <p/>
     * If any of the record columns do not have an adapter mapped a custom can be added / overwritten
     * with {@link #addDataMapper(Class, DataTypeHandler)}
     *
     * @param recordClass the record class to map
     * @param <T>         the type of the record
     * @param offset      the offset to start (default:0)  starts at index 1
     * @return the adapter to convert the result set to a record
     */
    public <T extends Record> T recordIndexAdapter(Class<T> recordClass, ResultSet resultSet, int offset) {
        return genericRecordAdapter(recordClass, true, offset).apply(resultSet);
    }

    /**
     * Central method to create a blob from a byte array
     *
     * @param bytes the byte array to convert
     * @return the blob
     */
    public Blob createBlob(byte[] bytes) {
        try {
            Blob blob = getConnection().createBlob();
            blob.setBytes(1, bytes);
            return blob;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    //todo how do I ensure the connection is released after use? Make my own connection object that auto closes after x happens?
    public PreparedStatement prepareStatement(String sql) {
        try {
            return getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Checks the current database the connection is connected to
     *
     * @return Gets the name of the database currently connected to
     */
    public String checkCurrentDatabase(Connection connection) {

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery("SELECT DB_NAME() AS CurrentDB")) {
            if (rs.next()) {
                return rs.getString("CurrentDB");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error logging action: " + e.getMessage());
        }
        return null;
    }

    public static byte[] convertToByteArray(BufferedImage image, String formatType) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, formatType, baos);
        return baos.toByteArray();
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
     * Adds a data mapper function used in {@link #recordAdapter(Class)}
     * and{@link #recordIndexAdapter(Class, int)} to map records to the correct type for all databases created
     *
     * @param type    the type to map
     * @param handler mapper function
     * @param <T>     the type of the handler
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> DataTypeHandler<T> addGlobalDataMapper(Class<T> type, DataTypeHandler<T> handler) {
        return (DataTypeHandler<T>) globalDataTypeMapper.put(type, handler);
    }

    /**
     * Adds a data mapper function used in {@link #recordAdapter(Class)}
     * and{@link #recordIndexAdapter(Class, int)} to map records to the correct type, <br>
     * To globally set a data mapper use {@link #addGlobalDataMapper(Class, DataTypeHandler)}
     *
     * @param type    the type to map
     * @param handler mapper function
     * @param <T>     the type of the handler
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> DataTypeHandler<T> addDataMapper(Class<T> type, DataTypeHandler<T> handler) {
        return (DataTypeHandler<T>) localDataTypeMapper.put(type, handler);
    }

    /**
     * Removes a data mapper used in  {@link #recordAdapter(Class)} and
     * {@link #recordIndexAdapter(Class, int)} to map records to the correct type
     *
     * @param type the type to remove
     * @param <T>  the type of the handler
     * @return the removed handler
     */
    @SuppressWarnings("unchecked")
    public static <T> DataTypeHandler<T> removeDataMapper(Class<T> type) {
        return (DataTypeHandler<T>) globalDataTypeMapper.remove(type);
    }

    public enum DatabaseType {
        MYSQL("Mysql", "jdbc:mysql:", "com.mysql.cj.jdbc.Driver"), SQLITE("Sqlite", "jdbc:sqlite:", "org.sqlite.JDBC"), POSTGRESQL("Postgresql", "jdbc:postgresql:", "org.postgresql.Driver"), SQLSERVER("SqlServer", "jdbc:sqlserver:", "com.microsoft.sqlserver.jdbc.SQLServerDriver"), MARIA("MariaDB", "jdbc:mariadb:", "org.mariadb.jdbc.Driver");
        private final String driver;
        private final String classLoader;
        private final String name;

        DatabaseType(String name, String driver, String classLoader) {
            this.driver = driver;
            this.classLoader = classLoader;
            this.name = name;
        }

        public String getDriver() {
            return driver;
        }

        public String getClassLoader() {
            return classLoader;
        }

        public String getName() {
            return name;
        }
    }

}
