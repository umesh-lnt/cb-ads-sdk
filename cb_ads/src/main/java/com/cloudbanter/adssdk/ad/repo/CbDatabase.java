package com.cloudbanter.adssdk.ad.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cloudbanter.adssdk.ad.manager.DefaultSchedule;
import com.cloudbanter.adssdk.ad.model.AModel;
import com.cloudbanter.adssdk.ad.model.CbAdvert;
import com.cloudbanter.adssdk.ad.model.CbCentral;
import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;


public class CbDatabase extends Database {
  public static final String TAG = CbDatabase.class.getSimpleName();
  
  public static final String TABLE_NAME = "cb_json";
  public static final String KEY_ID = "k_id"; // object key -
  public static final String OBJ_TYPE = "o_t"; // object type
  public static final String JSON_DATA = "j_d"; // raw json object
  
  public static final String CREATE_TABLE =
          "CREATE TABLE " + TABLE_NAME + " (" +
                  DATABASE_COMMON_COLUMNS +
                  
                  KEY_ID + " TEXT" + ", " +
                  OBJ_TYPE + " TEXT" + ", " +
                  JSON_DATA + " TEXT " +
                  ");";
  
  public static final String[] CREATE_INDICES = {
          // add index for mongo ids
          "CREATE INDEX IF NOT EXISTS cb_key_index ON " + TABLE_NAME + " (" + MID + " );",
          "CREATE INDEX IF NOT EXISTS cb_key_index ON " + TABLE_NAME + " (" + KEY_ID + " );",
          "CREATE INDEX IF NOT EXISTS cb_objtype_index ON " + TABLE_NAME + " (" + OBJ_TYPE + " );"
  };
  
  private static final String[] CB_ALL_COLUMNS_VIEW = new String[]{
          ID, MID, KEY_ID, OBJ_TYPE, JSON_DATA
  };
  
  public CbDatabase(Context context, SQLiteOpenHelper openHelper) {
    super(context, openHelper);
  }
  
  private static SQLiteDatabase db;
  // CRUD queries
  
  // putObject(CbObject) // based on object type, putObject(id, k, type, j);
  // putObject(_id, key_id, type, json);
  
  // getObjectsByKey(type, key_id);
  // getObjectById(String _id)
  
  // updateObject(object)
  // updateObject(id, k, t, obj);
  
  // deleteObject(object)
  // deleteObject(id)
  
  public void insertCbDevice(CbDevice d) {
    insertCBO(d._id, d.phoneNumber, d.getClass().getName(), d.toJson());
  }
  
  public void insertCBO(String mongo_id, String k_id, String type, String json) {
    ContentValues values = new ContentValues(4);
    values.put(MID, mongo_id);
    values.put(KEY_ID, k_id);
    values.put(OBJ_TYPE, type);
    values.put(JSON_DATA, json);
    
    db = databaseHelper.getWritableDatabase();
    long objId = db.insert(TABLE_NAME, JSON_DATA, values);
    db.close();
  }
  
  public void updateCBO(String mongo_id, String k_id, String type, String json) {
    ContentValues values = new ContentValues(4);
    values.put(MID, mongo_id);
    values.put(KEY_ID, k_id);
    values.put(OBJ_TYPE, type);
    values.put(JSON_DATA, json);
    
    db = databaseHelper.getWritableDatabase();
    int n = db.update(TABLE_NAME, values, MID + " =?", new String[]{
            mongo_id
    });
    // n is updated row count
    db.close();
  }
  
  public void deleteCBObyMongoId(String mongo_id) {
    db = databaseHelper.getWritableDatabase();
    db.delete(TABLE_NAME, MID + " =?", new String[]{
            mongo_id
    });
    db.close();
  }
  
  public void deleteObject(String k_id) {
    db = databaseHelper.getWritableDatabase();
    db.delete(TABLE_NAME, OBJ_TYPE + " = ?", new String[]{k_id});
    db.close();
  }
  
  public static final String CB_DEVICE = CbDevice.class.getSimpleName();
  public static final String CB_ADVERT = CbAdvert.class.getSimpleName();
  public static final String CB_SCHEDULE = CbSchedule.class.getSimpleName();
  public static final String CB_DEFAULT_SCHEDULE = DefaultSchedule.class.getSimpleName();
  public static final String CB_SCHEDULE_ENTRY = CbScheduleEntry.class.getSimpleName();
  public static final String CB_CENTRAL = CbCentral.class.getSimpleName();
  
  public Cursor getCursorFor(String id) {
    String selection = OBJ_TYPE + " = ?";
    String[] args = new String[]{id};
    String groupBy = null;
    String having = null;
    String orderBy = null;
    db = databaseHelper.getReadableDatabase();
    return db.query(TABLE_NAME, CB_ALL_COLUMNS_VIEW, selection, args, groupBy, having, orderBy);
  }
  
  public Reader readerFor(Cursor cursor) {
    return new Reader(cursor);
  }
  
  public class Reader {
    private final Cursor cursor;
    
    public Reader(Cursor cursor) {
      this.cursor = cursor;
    }
    
    public AModel<?> getNext() {
      if (!db.isOpen() || null == cursor || cursor.isClosed() || !cursor.moveToNext()) {
        if (cursor != null && !cursor.isClosed()) {
          cursor.close();
        }
        if (db.isOpen()) {
          db.close();
        }
        return null;
      }
      return getObject();
    }
    
    public int getCount() {
      if (null == cursor) {
        return 0;
      } else {
        return cursor.getCount();
      }
    }
    
    public AModel<?> getObject() {
      
      long Id = cursor.getLong(cursor.getColumnIndexOrThrow(CbDatabase.ID));
      String mongoId = cursor.getString(cursor.getColumnIndexOrThrow(MID));
      String keyId = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID));
      String objType = cursor.getString(cursor.getColumnIndexOrThrow(OBJ_TYPE));
      String json = cursor.getString(cursor.getColumnIndexOrThrow(JSON_DATA));
      
      AModel<?> o = null;
      if (CB_ADVERT.equalsIgnoreCase(objType)) {
        o = CbAdvert.fromJson(json);
      } else if (CB_SCHEDULE_ENTRY.equalsIgnoreCase(objType)) {
        o = CbScheduleEntry.fromJson(json);
      } else if (CB_SCHEDULE.equalsIgnoreCase(objType)) {
        o = CbSchedule.fromJson(json);
      } else if (CB_DEVICE.equalsIgnoreCase(objType)) {
        o = CbDevice.fromJson(json);
      } else if (CB_CENTRAL.equalsIgnoreCase(objType)) {
        o = CbCentral.fromJson(json);
      }
      
      return o;
    }
  }
  
  public CbDevice getDevice() {
    Reader r = readerFor(getCursorFor(CB_DEVICE));
    CbDevice d = (CbDevice) r.getNext();
    if (!r.cursor.isClosed()) {
      r.cursor.close();
    }
    if (db.isOpen()) {
      db.close();
    }
    return d;
  }
  
  public void upsert(CbDevice device) {
    db = databaseHelper.getWritableDatabase();
    SQLiteDatabase tx = beginTransaction();
    // device should be unique...
    ContentValues values = new ContentValues();
    values.put(KEY_ID, device.phoneNumber);
    values.put(MID, device._id);
    values.put(OBJ_TYPE, CB_DEVICE);
    values.put(JSON_DATA, device.toJson());
    final String[] params = new String[]{
            CB_DEVICE
    };
    final String where = OBJ_TYPE + " = ?";
    db.updateWithOnConflict(TABLE_NAME, values, where, params, db.CONFLICT_IGNORE);
    db.insertWithOnConflict(TABLE_NAME, null, values, db.CONFLICT_IGNORE);
    endTransaction(tx);
    db.close();
  }
  
  public void upsert(CbSchedule sched) {
    final String obj_type = CB_SCHEDULE;
    db = databaseHelper.getWritableDatabase();
    SQLiteDatabase tx = beginTransaction();
    // device should be unique...
    ContentValues values = new ContentValues();
    values.put(KEY_ID, sched.name);
    values.put(MID, sched._id);
    values.put(OBJ_TYPE, obj_type);
    values.put(JSON_DATA, sched.toJson());
    final String[] params = new String[]{
            obj_type
    };
    final String where = OBJ_TYPE + " = ?";
    if (1 != db.updateWithOnConflict(TABLE_NAME, values, where, params, db.CONFLICT_IGNORE)) {
      db.insertWithOnConflict(TABLE_NAME, null, values, db.CONFLICT_IGNORE);
    }
    endTransaction(tx);
    db.close();
  }
  
  public CbSchedule getDefaultSchedule() {
    Reader r = readerFor(getCursorFor(CB_DEFAULT_SCHEDULE));
    CbSchedule s = (CbSchedule) r.getNext();
    
    if (!r.cursor.isClosed()) {
      r.cursor.close();
    }
    if (db.isOpen()) {
      db.close();
    }
    return s;
  }
  
  public void saveDefaultSchedule(CbSchedule sched) {
    final String obj_type = CB_DEFAULT_SCHEDULE;
    db = databaseHelper.getWritableDatabase();
    SQLiteDatabase tx = beginTransaction();
    // device should be unique...
    ContentValues values = new ContentValues();
    values.put(KEY_ID, sched.name);
    values.put(MID, sched._id);
    values.put(OBJ_TYPE, obj_type);
    values.put(JSON_DATA, sched.toJson());
    final String[] params = new String[]{
            obj_type
    };
    final String where = OBJ_TYPE + " = ?";
    if (1 != db.updateWithOnConflict(TABLE_NAME, values, where, params, db.CONFLICT_IGNORE)) {
      db.insertWithOnConflict(TABLE_NAME, null, values, db.CONFLICT_IGNORE);
    }
    endTransaction(tx);
    db.close();
  }
  
  public CbSchedule getCbSchedule() {
    Reader r = readerFor(getCursorFor(CB_SCHEDULE));
    CbSchedule s = (CbSchedule) r.getNext();
    if (!r.cursor.isClosed()) {
      r.cursor.close();
    }
    if (db.isOpen()) {
      db.close();
    }
    return s;
  }
  
  // remove from db where obj_type = schdule
  public void clearCbSchedules() {
    String where = OBJ_TYPE + " IN ( ?, ? );";
    db = databaseHelper.getWritableDatabase();
    db.delete(TABLE_NAME, where, new String[]{
            CB_SCHEDULE, CB_SCHEDULE_ENTRY
    });
    db.close();
  }
  
  public CbCentral getCbCentral() {
    Reader r = readerFor(getCursorFor(CB_CENTRAL));
    CbCentral cbc = (CbCentral) r.getNext();
    if (!r.cursor.isClosed()) {
      r.cursor.close();
    }
    if (db.isOpen()) {
      db.close();
    }
    return cbc;
  }
  
  public void upsert(CbCentral central) {
    final String obj_type = CB_CENTRAL;
    db = databaseHelper.getWritableDatabase();
    SQLiteDatabase tx = beginTransaction();
    // device should be unique...
    ContentValues values = new ContentValues();
    values.put(KEY_ID, central.name);
    values.put(MID, central._id);
    values.put(OBJ_TYPE, obj_type);
    values.put(JSON_DATA, central.toJson());
    final String[] params = new String[]{obj_type};
    final String where = OBJ_TYPE + " = ?";
    int rowsAffected =
            db.updateWithOnConflict(TABLE_NAME, values, where, params, db.CONFLICT_REPLACE);
    if (rowsAffected != 1) {
      Log.d(TAG, "Inserting cb central");
      db.insertWithOnConflict(TABLE_NAME, null, values, db.CONFLICT_IGNORE);
    } else {
      Log.d(TAG, "Successfully updated CbCentral");
    }
    endTransaction(tx);
    db.close();
  }
  
}
