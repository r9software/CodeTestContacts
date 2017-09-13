package com.midevs.androidcodetestrodolfoabarca.data;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;

import java.util.ArrayList;

/**
 * Created by master on 13/09/17.
 */

public class Contact implements SortedListAdapter.ViewModel {
    private String mId;
    private String lastName;
    private int day;
    private int month;
    private int year;
    private ArrayList<String> addresses;
    private ArrayList<String> phone;
    private ArrayList<String> email;
    private String name;

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
            return contact.getID().equalsIgnoreCase(getmId());
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
            return getmId() != null ? getmId().equals(other.getID()) : other.getID() == null;
        }
        return false;
    }

    public String getID() {
        return getmId();
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
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
}
