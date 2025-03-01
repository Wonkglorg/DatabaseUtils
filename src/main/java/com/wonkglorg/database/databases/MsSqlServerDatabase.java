package com.wonkglorg.database.databases;

import com.wonkglorg.database.GenericServerDatabase;

import javax.sql.DataSource;

/**
 * IMPORTANT! Please add the Microsoft SqlServer Connector to the project if you want to use SqlServer.
 */
public class MsSqlServerDatabase extends GenericServerDatabase{
	
	public MsSqlServerDatabase(DataSource datasource) {
		super(SQLSERVER, datasource);
	}
}
