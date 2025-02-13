package com.wonkglorg.database.databases;

import com.wonkglorg.database.ConnectionBuilder;
import com.wonkglorg.database.GenericServerDatabase;

/**
 * IMPORTANT! Please add the Microsoft SqlServer Connector to the project if you want to use SqlServer.
 */
public class MsSqlServerDatabase extends GenericServerDatabase {

    public MsSqlServerDatabase(ConnectionBuilder builder, int poolSize) {
        super(builder, SQLSERVER, poolSize);
    }
}
