package com.midevs.androidcodetestrodolfoabarca.utils;

import com.midevs.androidcodetestrodolfoabarca.BaseActivity;
import com.midevs.androidcodetestrodolfoabarca.R;
import com.midevs.androidcodetestrodolfoabarca.data.Contact;

public class ContactDetailActivity extends BaseActivity {


    private String mid;

    @Override
    public void initialize() {
        initParams();
        initViews();
    }

    private void initViews() {
        findViewById(R.id.contact_detail_edit).setOnClickListener(view -> navigator.navigateToUpdateContact(mid));
        findViewById(R.id.contact_detail_delete).setOnClickListener(view -> {
            Contact mContact = Contact.$find(mid);
            mContact.$delete(false);
            AlertMaker.showConfirmAlert(this, "Deleted", "Contact Deleted", "Ok", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                finish();
            });
        });
    }

    private void initParams() {
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                mid = getIntent().getStringExtra(CONTACT_ID);
            } else {
                finish();
            }
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_contact_detail;
    }
}
