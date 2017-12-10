package com.goweii.swipedragtreerecyclerviewlibrary.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 *
 * @author cuizhen
 * @date 2017/11/21
 */

public class ToastUtil {
    private static Toast sToast = null;

    @SuppressLint("ShowToast")
    public static void show(Context context, String content){
        if (sToast == null){
            sToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(content);
        }
        sToast.show();
    }

    @SuppressLint("ShowToast")
    public static void show(Context context, int[] content){
        StringBuilder sb = new StringBuilder("(");
        for (int pos : content){
            sb.append(pos).append(",");
        }
        sb.deleteCharAt(sb.length() - 1).append(")");
        String s = sb.toString();
        if (sToast == null){
            sToast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(s);
        }
        sToast.show();
    }

    @SuppressLint("ShowToast")
    public static void show(Context context, int[] content, String prefix){
        StringBuilder sb = new StringBuilder("(");
        for (int pos : content){
            sb.append(pos).append(",");
        }
        sb.deleteCharAt(sb.length() - 1).append(")");
        String s = prefix + sb.toString();
        if (sToast == null){
            sToast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(s);
        }
        sToast.show();
    }
}
