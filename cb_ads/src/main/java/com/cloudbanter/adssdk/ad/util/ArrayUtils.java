package com.cloudbanter.adssdk.ad.util;

import java.lang.reflect.Array;

/**
 * Created by 10603675 on 03-10-2017.
 */

public class ArrayUtils {

    public static <T> T[] removeElement(Class<T> kind, T[] array, T element) {
        if(array != null) {
            int length = array.length;

            for(int i = 0; i < length; ++i) {
                if(array[i] == element) {
                    if(length == 1) {
                        return null;
                    }

                    T[] result = (T[])((T[]) Array.newInstance(kind, length - 1));
                    System.arraycopy(array, 0, result, 0, i);
                    System.arraycopy(array, i + 1, result, i, length - i - 1);
                    return result;
                }
            }
        }

        return array;
    }
}
