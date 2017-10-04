package com.cloudbanter.adssdk.ad.repo;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class CbProtocolDatabase extends Database {

  public static final String TABLE_NAME = "protocol";
  public static final String FIELD_1 = "f_1";

  public static final String CREATE_TABLE =
          "CREATE TABLE " + TABLE_NAME + " (" +
                  ID + " STRING PRIMARY KEY" + ", " +
                  FIELD_1 + " TEXT " +
                  ");";

  public static final String[] CREATE_INDICES = {
  };

  private static final String[] PROTOCOL_VIEW = new String[]{
          ID, FIELD_1
  };

  public CbProtocolDatabase(Context context, SQLiteOpenHelper openHelper) {
    super(context, openHelper);
  }

  // queries...
  // save protocol queue object
  // get protocol data
}
