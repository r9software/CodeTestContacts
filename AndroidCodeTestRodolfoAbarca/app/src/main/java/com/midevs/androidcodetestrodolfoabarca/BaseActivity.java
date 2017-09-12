package com.midevs.androidcodetestrodolfoabarca;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.midevs.androidcodetestrodolfoabarca.utils.AlertMaker;
import com.midevs.androidcodetestrodolfoabarca.utils.Navigator;


/**
 * Created by asus on 06/08/2016.
 */
public abstract class BaseActivity extends Activity {

    protected Navigator navigator;
    protected AlertMaker alertMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alertMaker = new AlertMaker();
        navigator = new Navigator(this);
        Log.e("Activity Created", this.getClass().getCanonicalName() + " created");
        setContentView(getContentLayout());
        initialize();
    }

    public abstract void initialize();

    protected abstract int getContentLayout();
}