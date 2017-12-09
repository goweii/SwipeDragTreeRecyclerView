package com.goweii.swipedragtreerecyclerviewlibrary.util;

import android.util.Log;

/**
 *
 * @author cuizhen
 * @date 2017/11/26
 */

public class LogUtil {
    public static void d(String tag, int msg, String prefix) {
        Log.d(tag, "" + prefix + msg);
    }
    public static void d(String tag, int msg) {
        Log.d(tag, "" + msg);
    }

    public static void d(String tag, int[] msg, String prefix) {
        StringBuffer s = new StringBuffer(",");
        for (int i : msg) {
            s = s.append(i);
        }
        Log.d(tag, prefix + s.substring(1));
    }
    public static void d(String tag, int[] msg) {
        StringBuffer s = new StringBuffer(",");
        for (int i : msg) {
            s = s.append(i);
        }
        Log.d(tag, s.substring(1));
    }

    public static void d(String tag, String msg, String prefix) {
        Log.d(tag, prefix + msg);
    }
    public static void d(String tag, String msg) {
        Log.d(tag,  msg);
    }
    public static void d(String tag, boolean msg, String prefix) {
        Log.d(tag, prefix + (msg ? "true" : "false"));
    }
    public static void d(String tag, boolean msg) {
        Log.d(tag, msg ? "true" : "false");
    }
}
