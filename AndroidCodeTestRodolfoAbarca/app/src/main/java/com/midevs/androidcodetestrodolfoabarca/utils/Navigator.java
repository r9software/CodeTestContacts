package com.midevs.androidcodetestrodolfoabarca.utils;

import android.content.Context;
import android.content.Intent;

import com.midevs.androidcodetestrodolfoabarca.AddContactActivity;


/**
 * Created by master on 05/08/17.
 */

public class Navigator {
    private final Context mContext;

    public Navigator(Context context) {
        mContext = context;
    }


    public void navigateToDetailContact(String mid) {
        Intent mIntent = new Intent(mContext, ContactDetailActivity.class);
        mIntent.putExtra(ContactDetailActivity.CONTACT_ID, mid);
        mContext.startActivity(mIntent);
    }

    public void navigateToAddContact() {
        Intent mIntent = new Intent(mContext, AddContactActivity.class);
        mContext.startActivity(mIntent);
    }

    public void navigateToUpdateContact(String mid) {
        Intent mIntent = new Intent(mContext, AddContactActivity.class);
        mIntent.putExtra(AddContactActivity.CONTACT_ID, mid);
        mContext.startActivity(mIntent);
    }
}
