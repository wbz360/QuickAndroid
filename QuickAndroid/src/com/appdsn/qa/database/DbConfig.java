package com.appdsn.qa.database;



public class DbConfig {
	
	public static final String DB_NAME = "common.db";
	public static final int DB_VERSION = 1;
	
	private DbConfig(){}
	public static final String CREATE_xxx_TABLE_SQL = "create table user"
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, name string, age int)";
	public static final String CREATE_xxx2_TABLE_SQL = "";
}
