package com.wonkglorg.database.datatypes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class TypeHandlerTime implements DataTypeHandler<Time> {
    @Override
    public void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
        statement.setTime(index, (Time) value);
    }

    @Override
    public Time getParameter(ResultSet resultSet, int index) throws SQLException {
        return resultSet.getTime(index);
    }

    @Override
    public Time getParameter(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTime(columnName);
    }
}
