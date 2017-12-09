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
    public static void showToast(Context context, String content){
        if (sToast == null){
            sToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(content);
        }
        sToast.show();
    }
}
