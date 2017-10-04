package com.cloudbanter.adssdk.util;

import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Utility class that manages java reflection to create instances, call methods, etc.
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 1/3/17
 */
public final class ClassUtils {

  /** Tag for logs **/
  private static final String TAG = ClassUtils.class.getSimpleName();

  /** Private constructor to avoid instances **/
  private ClassUtils() {
  }

  /**
   * Creates a new instance of the class {@code clazz} with the given constructor arguments {@code
   * consArgs}
   *
   * @param clazz
   *         Target class
   * @param consArgs
   *         Constructor arguments
   * @param <T>
   *         Type of class to be created
   *
   * @return A new instance of the class or null if an error occurred while getting the instance
   */
  @Nullable
  public static <T> T newInstance(Class<T> clazz, Object... consArgs) {
    Class<?>[] consClasses = new Class[consArgs.length];
    for (int i = 0; i < consArgs.length; i++) {
      consClasses[i] = consArgs[i].getClass();
    }
    try {
      Constructor<T> constructor = clazz.getConstructor(consClasses);
      return constructor.newInstance(consArgs);
    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
            InvocationTargetException e) {
      Log.e(TAG, "An error occurred while creating a class instance", e);
      return null;
    }
  }

}
