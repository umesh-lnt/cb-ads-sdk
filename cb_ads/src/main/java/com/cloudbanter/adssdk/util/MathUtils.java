package com.cloudbanter.adssdk.util;

/**
 * Created by Antonio on 5/12/17.
 */
public final class MathUtils {

  private MathUtils() {
  }

  public static int gcd(int a, int b) {
    return b == 0 ? a : gcd(b, a % b);
  }

}
