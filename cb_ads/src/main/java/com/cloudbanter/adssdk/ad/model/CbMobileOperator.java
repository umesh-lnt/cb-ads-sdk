package com.cloudbanter.adssdk.ad.model;

import java.io.Serializable;

public class CbMobileOperator extends AModel<CbMobileOperator> implements Serializable {

  String email;

  public CbMobileOperator(String em) {
    email = em;
  }

  public CbMobileOperator(String id, String em) {
    _id = id;
    email = em;
  }

  ;
}

