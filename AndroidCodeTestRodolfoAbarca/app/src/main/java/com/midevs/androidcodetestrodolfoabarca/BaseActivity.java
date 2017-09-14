package com.midevs.androidcodetestrodolfoabarca;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.midevs.androidcodetestrodolfoabarca.utils.Navigator;


/**
 * Created by asus on 06/08/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final String CONTACT_ID = "CONTACT_ID";
    protected static final int EMAIL_TYPE = 0;
    protected static final int PHONE_TYPE = 1;
    protected static final int ADDRESS_TYPE = 2;
    protected Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = new Navigator(this);
        Log.e("Activity Created", this.getClass().getCanonicalName() + " created");
        setContentView(getContentLayout());
        initialize();
    }

    public abstract void initialize();

    protected abstract int getContentLayout();
}
