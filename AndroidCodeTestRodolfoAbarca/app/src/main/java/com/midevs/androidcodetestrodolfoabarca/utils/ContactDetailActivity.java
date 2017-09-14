package com.midevs.androidcodetestrodolfoabarca.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.midevs.androidcodetestrodolfoabarca.BaseActivity;
import com.midevs.androidcodetestrodolfoabarca.R;
import com.midevs.androidcodetestrodolfoabarca.data.Contact;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ContactDetailActivity extends BaseActivity {


    private String mid;
    private Contact contact;
    private LinearLayout parentPhonesLayout;
    private LinearLayout parentAddressLayout;
    private LinearLayout parentEmailLayout;

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
        TextView name = (TextView) findViewById(R.id.contact_detail_name);
        name.setText(contact.getName());
        TextView lastName = (TextView) findViewById(R.id.contact_detail_last_name);
        lastName.setText(contact.getLastName());
        TextView dateOfBirth = (TextView) findViewById(R.id.contact_detail_date_of_birth);

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, contact.getYear());
        cal.set(Calendar.MONTH, contact.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, contact.getDay());
        String birth = format.format(cal.getTime());
        dateOfBirth.setText(birth);
        parentPhonesLayout = (LinearLayout) findViewById(R.id.contact_detail_phone_number_parent);
        parentPhonesLayout.removeAllViews();
        parentAddressLayout = (LinearLayout) findViewById(R.id.contact_detail_address_parent);
        parentAddressLayout.removeAllViews();
        parentEmailLayout = (LinearLayout) findViewById(R.id.contact_detail_email_parent);
        parentEmailLayout.removeAllViews();

        for (String string : contact.getPhone()) {
            parentPhonesLayout.addView(createTextView(string, parentPhonesLayout, PHONE_TYPE));
        }
        for (String string : contact.getAddresses()) {
            parentAddressLayout.addView(createTextView(string, parentAddressLayout, ADDRESS_TYPE));
        }
        for (String string : contact.getEmail()) {
            parentEmailLayout.addView(createTextView(string, parentEmailLayout, EMAIL_TYPE));
        }
    }

    private View createTextView(String content, LinearLayout parentView, int viewType) {
        LinearLayout parent = (LinearLayout) parentView.inflate(this, R.layout.simple_text_view_form, null);
        TextView newText = (TextView) parent.findViewById(R.id.newText);
        newText.setVisibility(View.VISIBLE);
        newText.setText(content);
        return parent;
    }
    private void initParams() {
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                mid = getIntent().getStringExtra(CONTACT_ID);
                contact = Contact.$find(mid);
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        contact = Contact.$find(mid);
        initViews();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_contact_detail;
    }
}
