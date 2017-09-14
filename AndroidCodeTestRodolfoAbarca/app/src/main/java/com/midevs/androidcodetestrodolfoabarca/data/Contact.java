package com.midevs.androidcodetestrodolfoabarca.data;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.middevs.local.android.sdk.json.JSONObject;
import com.middevs.local.android.sdk.task.handler.core.engine.ITaskEngine;
import com.middevs.local.android.sdk.task.handler.core.engine.TaskListener;
import com.middevs.local.android.sdk.task.handler.core.task.Task;
import com.middevs.local.db.core.MidDevLDB;
import com.middevs.local.db.model.IMigrationProtocol;
import com.middevs.local.db.model.MModel;
import com.midevs.androidcodetestrodolfoabarca.BaseApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by master on 13/09/17.
 */

public class Contact extends MModel implements SortedListAdapter.ViewModel {
    private String lastName;
    private int day;
    private int month;
    private int year;
    private ArrayList<String> addresses;
    private ArrayList<String> phone;
    private ArrayList<String> email;
    private String name;

    public Contact() {
        setMid(UUID.randomUUID().toString());
    }

    public static Contact $find(String mid) {
        JSONObject condition = new JSONObject();
        JSONObject subCondition = new JSONObject();
        MidDevLDB midDevLDB = BaseApplication.getMidDevLDB();
        subCondition.putOpt("$=", mid);
        condition.putOpt("mid", subCondition.clone());
        subCondition.clear();
        List<Contact> contacts = midDevLDB.find(condition, -1, -1, Contact.class);
        if (contacts.size() == 1) {
            return contacts.get(0);
        }
        return null;
    }

    public static List<Contact> $getContacts() {


        JSONObject condition = new JSONObject();
        JSONObject subCondition = new JSONObject();
        subCondition.putOpt("$=", false);
        condition.putOpt("deleted", subCondition.clone());
        MidDevLDB midDevLDB = BaseApplication.getMidDevLDB();
        List<Contact> contacts = midDevLDB.find(condition, -1, -1, Contact.class);
        return contacts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public <T> boolean isSameModelAs(@NonNull T item) {
        if (item instanceof Contact) {
            final Contact contact = (Contact) item;
            return contact.getMid().equalsIgnoreCase(getMid());
        }
        return false;
    }

    @Override
    public <T> boolean isContentTheSameAs(@NonNull T item) {
        if (item instanceof Contact) {
            final Contact other = (Contact) item;
            if (!getName().equalsIgnoreCase(other.getName())) {
                return false;
            }
            return getMid() != null ? getMid().equals(other.getMid()) : other.getMid() == null;
        }
        return false;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<String> addresses) {
        this.addresses = addresses;
    }

    public ArrayList<String> getPhone() {
        return phone;
    }

    public void setPhone(ArrayList<String> phone) {
        this.phone = phone;
    }

    public ArrayList<String> getEmail() {
        return email;
    }

    public void setEmail(ArrayList<String> email) {
        this.email = email;
    }

    public String getFullName() {
        return name + " " + lastName;
    }

    @Override
    public String modelName() {
        return "CONTACTS";
    }

    @Override
    protected IMigrationProtocol $getMigrationProtocol(int version) {
        return null;
    }

    @Override
    public void $saveDB() {
        BaseApplication.getTaskEngine().add(new Task() {

            @Override
            public void execute(ITaskEngine iTaskRunner,
                                TaskListener listener) {

                MModel.$saveDB(Contact.this, BaseApplication.getMidDevLDB());
                listener.finish();
            }
        });

    }

    @Override
    public void $delete(boolean keep) {
        MModel.$deleteDB(this, BaseApplication.getMidDevLDB());
    }
}
