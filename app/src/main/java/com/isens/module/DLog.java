package com.isens.module;

import android.content.Context;
import android.util.Log;

/**
 * Created by admin on 2016-06-30.
 */
public class DLog {
    /** Log Level Error **/
    private static final boolean LOGENABLE=true;
    public static final void e(Context context, String message) {
        if (LOGENABLE) Log.e(context.getClass().getSimpleName(), message);
    }

    /** Log Level Warning **/
    public static final void w(Context context, String message) {
        if (LOGENABLE) Log.w(context.getClass().getSimpleName(), message);
    }

    /** Log Level Information **/
    public static final void i(Context context, String message) {
        if (LOGENABLE) Log.i(context.getClass().getSimpleName(), message);
    }

    /** Log Level Debug **/
    public static final void d(Context context, String message) {
        if (LOGENABLE) Log.d(context.getClass().getSimpleName(), message);
    }

    /** Log Level Verbose **/
    public static final void v(Context context, String message) {
        if (LOGENABLE) Log.v(context.getClass().getSimpleName(), message);
    }

    /** Log Level Error **/
    public static final void e(String TAG, String message) {
        if (LOGENABLE) Log.e(TAG, message);
    }

    /** Log Level Warning **/
    public static final void w(String TAG, String message) {
        if (LOGENABLE) Log.w(TAG, message);
    }

    /** Log Level Information **/
    public static final void i(String TAG, String message) {
        if (LOGENABLE) Log.i(TAG, message);
    }

    /** Log Level Debug **/
    public static final void d(String TAG, String message) {
        if (LOGENABLE) Log.d(TAG, message);
    }

    /** Log Level Verbose **/
    public static final void v(String TAG, String message) {
        if (LOGENABLE) Log.v(TAG, message);
    }


}
