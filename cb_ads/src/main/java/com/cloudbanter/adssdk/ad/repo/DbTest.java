package com.cloudbanter.adssdk.ad.repo;

import android.content.Context;

import com.cloudbanter.adssdk.ad.model.CbDevice;


public class DbTest {


  private Context context = null;

  public DbTest(Context context) {
    this.context = context;
  }

  public class DbObject {
    public String _id;
    public String key;
    public String type;
    public String obj;

    public DbObject(String id, String key, String type, String obj) {
      this._id = id;
      this.key = key;
      this.type = type;
      this.obj = obj;
    }
  }

  // handle escape
  DbObject d1 = new DbObject("001", "x001", CbDevice.class.getName(), "{\"phoneNumber\":\"333\"}");
  DbObject d2 = new DbObject("002", "x002", CbDevice.class.getName(),
          "{\"jwt\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                  ".eyJfaWQiOiI1NjBkN2Y3YWYwYTNkZDNiNTQzOGYyMjciLCJpYXQiOjE0NDM3MjUxNzg5Mzl9" +
                  ".OqUfxTdUnDyzKAiPMM-7ehHDUX-wOMsF2mGNkBuR4LI\",\"__v\":0," +
                  "\"createdAt\":\"2015-10-01T18:46:18.938Z\",\"updatedAt\":\"2015-10-01T18:46:18" +
                  ".938Z\",\"phoneNumber\":\"346\",\"_id\":\"560d7f7af0a3dd3b5438f227\"," +
                  "\"messages\":0,\"userInfo\":{\"preferences\":[]}}");

  public void createTest() {
    CbDatabase database = DatabaseFactory.getCbDatabase(context);
//        database.insertCBO(d1._id, d1.key, d1.type, d1.obj);
    database.insertCBO(d2._id, d2.key, d2.type, d2.obj);
  }

  CbDevice device = null;

  public void readTest() {
    CbDatabase database = DatabaseFactory.getCbDatabase(context);
    device = database.getDevice();
  }

  public void updateTest() {
    CbDatabase database = DatabaseFactory.getCbDatabase(context);
    device.phoneNumber = device.phoneNumber + "_mod";
    database.updateCBO(device._id, device.phoneNumber, device.getClass().getName(),
            device.toJson());
  }

  public void deleteTest() {
    CbDatabase database = DatabaseFactory.getCbDatabase(context);
    database.deleteCBObyMongoId(device._id);
  }


  public static void test(Context context) {
    DbTest t = new DbTest(context);
    t.createTest();
    t.readTest();
    t.updateTest();
    t.deleteTest();
  }

}
