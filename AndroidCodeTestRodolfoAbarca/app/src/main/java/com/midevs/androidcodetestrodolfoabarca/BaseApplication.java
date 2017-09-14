package com.midevs.androidcodetestrodolfoabarca;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.middevs.local.android.sdk.task.handler.core.engine.ITaskEngine;
import com.middevs.local.db.core.MidDevLDB;

/**
 * Created by rodol on 12/09/2017.
 */

public class BaseApplication extends Application {
    private static final String TAG = Application.class.getCanonicalName();
    private static MidDevLDB midDevLDB;
    private static ITaskEngine taskEngine;

    public static MidDevLDB getMidDevLDB() {
        return midDevLDB;
    }

    public static ITaskEngine getTaskEngine() {
        return taskEngine;
    }

    public void setTaskEngine(ITaskEngine taskEngine) {
        this.taskEngine = taskEngine;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        taskEngine = ITaskEngine.buildTaskRunner(-1);
        midDevLDB = MidDevLDB.getInstance(this, getTaskEngine());
        Context context = getBaseContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

}
