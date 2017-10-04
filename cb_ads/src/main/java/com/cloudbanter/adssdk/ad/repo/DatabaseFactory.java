package com.cloudbanter.adssdk.ad.repo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseFactory {

  private static final int INITIAL_DATABASE_VERSION = 1;

  private static final int DATABASE_VERSION = INITIAL_DATABASE_VERSION;

  private static final String DATABASE_NAME = "cb.db";
  private static final Object lock = new Object();

  private static DatabaseFactory instance;

  private DatabaseHelper databaseHelper;

  private final CbDatabase cbDb;
  private final CbProtocolDatabase protocolDb;

  public DatabaseFactory(Context context) {
    this.databaseHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.cbDb = new CbDatabase(context, databaseHelper);
    this.protocolDb = new CbProtocolDatabase(context, databaseHelper);
  }

  public static DatabaseFactory getInstance(Context context) {
    synchronized (lock) {
      if (null == instance) {
        instance = new DatabaseFactory(context);
      }
      return instance;
    }
  }

  public static CbDatabase getCbDatabase(Context context) {
    return getInstance(context).cbDb;
  }

  public static CbProtocolDatabase getCbProtocolDatabase(Context context) {
    return getInstance(context).protocolDb;
  }

  public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(CbDatabase.CREATE_TABLE);

      executeStatements(db, CbDatabase.CREATE_INDICES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
//          if (oldV < MODIFIED_VERSION) executeStatements(db, new String[] { stmt, stmt };
    }

    private void executeStatements(SQLiteDatabase db, String[] statements) {
      for (String statement : statements) {
        db.execSQL(statement);
      }
    }

  }

}
