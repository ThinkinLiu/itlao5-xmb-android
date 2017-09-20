package com.e7yoo.e7.util;

import android.content.Context;
import android.widget.Toast;
import com.sdsmdg.tastytoast.TastyToast;

/**
 * Created by Administrator on 2017/9/4.
 */

public class TastyToastUtil {
    public static Toast toast(Context context, int stringId, Object... formatArgs) {
        return TastyToast.makeText(context, context.getString(stringId, formatArgs), TastyToast.LENGTH_SHORT, TastyToast.INFO);
    }


}
