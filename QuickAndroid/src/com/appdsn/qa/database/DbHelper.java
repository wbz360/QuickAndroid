package com.appdsn.qa.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * db helper
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-10-21
 */
public class DbHelper extends SQLiteOpenHelper {

	private static DbHelper instance;
	
	public DbHelper(Context context) {
        super(context, DbConfig.DB_NAME, null, DbConfig.DB_VERSION);
    }

    public static DbHelper getInstance(Context pContext)
	{
		if (instance == null) {
			instance = new DbHelper(pContext);
			
		}
		return instance;
	}
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(DbConfig.CREATE_xxx_TABLE_SQL.toString());
            db.execSQL(DbConfig.CREATE_xxx_TABLE_SQL.toString());

          
            
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
