package com.cloudbanter.adssdk.ad.ui.util;

import java.util.ArrayList;

public class ErrorUtil {

  public static ArrayList<String> errors = new ArrayList<String>();

  public static void newError(String s) {
    errors.add(s);
  }

  public static void clear() {
    errors.clear();
  }

  public static String getErrors() {
    StringBuffer sb = new StringBuffer();
    for (String s : errors) {
      sb.append(String.format("%s\n", s));
    }
    return sb.toString();
  }
}
