package com.cloudbanter.adssdk.ad.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * Created by eric on 7/10/15.
 * <p>
 * TODO implement parcelable for the data models... ( ugh )
 */
public abstract class AModel<T> implements Serializable {

  public String _id;  // sqllite id - can be same as server side (mongo id)

  protected static GsonBuilder gsonBuilder = new GsonBuilder();
  protected static Gson gson;

  static {
    gson = gsonBuilder.create();
  }

  protected AModel() {
  }

}
