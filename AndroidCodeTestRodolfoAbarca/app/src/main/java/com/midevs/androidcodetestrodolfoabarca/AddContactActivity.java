package com.midevs.androidcodetestrodolfoabarca;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.midevs.androidcodetestrodolfoabarca.data.Contact;
import com.midevs.androidcodetestrodolfoabarca.utils.AlertMaker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddContactActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    private static final int EMAIL_TYPE = 0;
    private static final int PHONE_TYPE = 1;
    private static final int ADDRESS_TYPE = 2;
    private Contact contact;
    private ArrayList<EditText> alternativeEmails = new ArrayList<>(), alternativePhones = new ArrayList<>(), alternativeAddresses = new ArrayList<>();
    private EditText firstName;
    private EditText lastName;
    private EditText phone;
    private EditText address;
    private EditText email;
    private int month, day, year;
    private TextView dateBirth;
    private LinearLayout parentPhonesLayout;
    private LinearLayout parentEmailLayout;
    private LinearLayout parentAddressLayout;
    private boolean isUpdate;


    @Override
    public void initialize() {
        initViews();
        initParam();
    }

    private void initViews() {
        firstName = (EditText) findViewById(R.id.add_contact_name_edit);
        lastName = (EditText) findViewById(R.id.add_contact_last_name_edit);
        phone = (EditText) findViewById(R.id.add_contact_phone);
        address = (EditText) findViewById(R.id.add_contact_address_edit);
        dateBirth = (TextView) findViewById(R.id.add_contact_date_birth);
        email = (EditText) findViewById(R.id.add_email_edit);

        parentPhonesLayout = (LinearLayout) findViewById(R.id.add_contact_add_phones_parent);
        findViewById(R.id.add_alternative_add_number).setOnClickListener(view -> {
            parentPhonesLayout.addView(createTextView("", parentPhonesLayout, PHONE_TYPE));
        });
        parentEmailLayout = (LinearLayout) findViewById(R.id.add_contact_add_email_parent);
        findViewById(R.id.add_alternative_email).setOnClickListener(view -> {
            parentEmailLayout.addView(createTextView("", parentEmailLayout, EMAIL_TYPE));
        });
        parentAddressLayout = (LinearLayout) findViewById(R.id.add_contact_add_address_parent);
        findViewById(R.id.add_contact_add_address).setOnClickListener(view -> {
            parentAddressLayout.addView(createTextView("", parentAddressLayout, ADDRESS_TYPE));
        });
        findViewById(R.id.add_contact_add_date_birth).setOnClickListener(view -> {
            showCalendarView();
        });
        findViewById(R.id.add_contact_create).setOnClickListener(view -> {
            validateParams();
        });
        if (isUpdate)
            ((Button) findViewById(R.id.add_contact_create)).setText("Update");
    }

    private void validateParams() {
        if (!TextUtils.isEmpty(firstName.getText()) && !TextUtils.isEmpty(lastName.getText()) && !TextUtils.isEmpty(phone.getText()) && !TextUtils.isEmpty(email.getText())) {
            //save contact
            Contact mContact;
            if (contact == null)
                mContact = new Contact();
            else
                mContact = contact;
            mContact.setName(firstName.getText().toString());
            mContact.setLastName(lastName.getText().toString());
            ArrayList<String> emails = new ArrayList<>();
            emails.add(email.getText().toString());
            for (EditText mEdit : alternativeEmails) {
                emails.add(mEdit.getText().toString());
            }
            ArrayList<String> addresses = new ArrayList<>();
            addresses.add(address.getText().toString());
            for (EditText mEdit : alternativeAddresses) {
                addresses.add(mEdit.getText().toString());
            }
            ArrayList<String> phones = new ArrayList<>();
            phones.add(phone.getText().toString());
            for (EditText mEdit : alternativePhones) {
                phones.add(mEdit.getText().toString());
            }
            mContact.setDay(day);
            mContact.setYear(year);
            mContact.setMonth(month);
            mContact.setEmail(emails);
            mContact.setPhone(phones);
            mContact.setAddresses(addresses);
            mContact.$saveDB();
            String message = "Contact Saved";
            if (isUpdate)
                message = "Contact updated";
            AlertMaker.showConfirmAlert(this, "Succeed", message, "Ok", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                finish();
            });
        } else {
            AlertMaker.showConfirmAlert(this, "Error", "You need to fill all the needed information", "Ok", (dialogInterface, i) -> dialogInterface.dismiss());
        }
    }

    private void showCalendarView() {
        DatePickerDialog dialog = DatePickerDialog.newInstance(this, 1985, 4, 4);
        dialog.show(getFragmentManager(), "DatePicker");
    }

    private void initParam() {
        if (getIntent() != null) {
            Bundle extra = getIntent().getExtras();
            if (extra != null) {
                String mid = extra.getString(CONTACT_ID);
                contact = Contact.$find(mid);
                if (contact != null) {
                    updateViews();
                    isUpdate = true;
                }
            }
        }
    }

    private View createTextView(String content, LinearLayout parentView, int viewType) {
        LinearLayout parent = (LinearLayout) parentView.inflate(this, R.layout.simple_edit_text_form, null);
        EditText newText = (EditText) parent.findViewById(R.id.newEdit);
        newText.setVisibility(View.VISIBLE);
        newText.setText(content);
        switch (viewType) {
            case EMAIL_TYPE: {
                newText.setInputType(InputType.TYPE_CLASS_TEXT);
                alternativeEmails.add(newText);
                break;
            }
            case PHONE_TYPE: {
                newText.setInputType(InputType.TYPE_CLASS_PHONE);
                alternativePhones.add(newText);
                break;
            }
            case ADDRESS_TYPE: {
                newText.setInputType(InputType.TYPE_CLASS_TEXT);
                alternativeAddresses.add(newText);
                break;
            }
        }
        return parent;
    }

    private void updateViews() {
        firstName.setText(contact.getName());
        lastName.setText(contact.getLastName());
        day = contact.getDay();
        month = contact.getMonth();
        year = contact.getYear();
        if (day != 0 && month != 0 && year != 0) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            String birth = format.format(cal.getTime());
            dateBirth.setText(birth);
        }
        if (contact.getPhone().size() > 0) {
            phone.setText(contact.getPhone().get(0));
            contact.getPhone().remove(0);
            for (String string : contact.getPhone()) {
                parentPhonesLayout.addView(createTextView(string, parentPhonesLayout, PHONE_TYPE));
            }
        }
        if (contact.getAddresses().size() > 0) {
            address.setText(contact.getAddresses().get(0));
            contact.getAddresses().remove(0);
            for (String string : contact.getAddresses()) {
                parentAddressLayout.addView(createTextView(string, parentAddressLayout, ADDRESS_TYPE));
            }
        }
        if (contact.getEmail().size() > 0) {
            email.setText(contact.getEmail().get(0));
            contact.getEmail().remove(0);
            for (String string : contact.getEmail()) {
                parentEmailLayout.addView(createTextView(string, parentEmailLayout, EMAIL_TYPE));
            }
        }

    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_add_contact;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
        runOnUiThread(() -> {

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String birth = format.format(cal.getTime());
            dateBirth.setText(birth);
        });
    }
}
