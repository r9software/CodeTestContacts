package com.midevs.androidcodetestrodolfoabarca;

import android.os.Bundle;

import com.midevs.androidcodetestrodolfoabarca.data.Contact;

public class AddContactActivity extends BaseActivity {

    private Contact contact;


    @Override
    public void initialize() {
        initViews();
        initParam();
    }

    private void initViews() {

    }

    private void initParam() {
        if (getIntent() != null) {
            Bundle extra = getIntent().getExtras();
            String mid = extra.getString(CONTACT_ID);
            contact = Contact.$find(mid);
            if (contact != null) {
                updateViews();
            }
        }
    }

    private void updateViews() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_add_contact;
    }
}
