package com.cloudbanter.adssdk.ad.repo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {

  protected SQLiteOpenHelper databaseHelper;
  protected final Context context;

  public Database(Context context, SQLiteOpenHelper openHelper) {
    this.context = context;
    this.databaseHelper = openHelper;
  }

  public SQLiteDatabase beginTransaction() {
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    return db;
  }

  public void endTransaction(SQLiteDatabase db) {
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  // common database columns
  public static final String ID = "_id"; // sqlite id - auto assigned
  public static final String MID = "m_id"; // sync id from Mongo on server

  protected static final String DATABASE_COMMON_COLUMNS =
          ID + " INTEGER PRIMARY KEY, " +
                  MID + " TEXT, ";

}
