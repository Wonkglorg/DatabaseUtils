package com.wonkglorg.database.datatypes;

import com.wonkglorg.database.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An Interface for handling data types in a database queries used in {@link Database#recordIndexAdapter(Class)} and {@link com.wonkglorg.util.database.Database#recordAdapter(Class)}
 */
@SuppressWarnings("unused")
public interface DataTypeHandler<T> {
    /**
     * Set the parameter value in the prepared statement if it is set by index
     *
     * @param statement The prepared statement
     * @param index     The index of the parameter
     * @param value     The value to set
     * @throws SQLException If there is an error setting the parameter
     */
    void setParameter(PreparedStatement statement, int index, Object value) throws SQLException;


    /**
     * Get the parameter value from the prepared resultSet if it is set by index
     *
     * @param resultSet The prepared resultSet
     * @param index     The index of the parameter
     * @param value     The value to set
     * @throws SQLException If there is an error setting the parameter
     */
    T getParameter(ResultSet resultSet, int index) throws SQLException;

    /**
     * Get the parameter value from the prepared resultSet if it is set by columnName
     *
     * @param resultSet  The prepared resultSet
     * @param columnName The columnName of the parameter
     * @param value      The value to set
     * @throws SQLException If there is an error setting the parameter
     */
    T getParameter(ResultSet resultSet, String columnName) throws SQLException;
}
