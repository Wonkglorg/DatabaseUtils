package com.wonkglorg.database;

public record DatabaseType(String name, String driver, String classLoader){
	public static final DatabaseType MYSQL = new DatabaseType("Mysql", "jdbc:mysql:", "com.mysql.cj.jdbc.Driver");
	public static final DatabaseType SQLITE = new DatabaseType("Sqlite", "jdbc:sqlite:", "org.sqlite.JDBC");
	public static final DatabaseType JDBC = new DatabaseType("Sqlite", "jdbc:sqlite:", "org.sqlite.JDBC");
	public static final DatabaseType POSTGRESQL = new DatabaseType("PostgreSQL", "jdbc:postgresql:", "org.postgresql.Driver");
	public static final DatabaseType SQLSERVER = new DatabaseType("SQLServer", "jdbc:sqlserver:", "org.sqlserver.jdbc.SQLServerDriver");
	public static final DatabaseType MARIA_DB = new DatabaseType("MariaDB", "jdbc:mariadb:", "org.mariadb.jdbc.Driver");
}