package com.midevs.androidcodetestrodolfoabarca.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.midevs.androidcodetestrodolfoabarca.R;
import com.midevs.androidcodetestrodolfoabarca.adapter.viewholder.ContactViewHolder;
import com.midevs.androidcodetestrodolfoabarca.data.Contact;

import java.util.Comparator;

/**
 * Created with Android Studio
 */
public class ContactsAdapter extends SortedListAdapter<Contact> {


    private final Listener mListener;

    public ContactsAdapter(Context context, Comparator<Contact> comparator, Listener listener) {
        super(context, Contact.class, comparator);
        mListener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder<? extends Contact> onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        final View itemView = inflater.inflate(R.layout.contact_item, parent, false);
        ContactViewHolder contactViewHolder = new ContactViewHolder(itemView);
        contactViewHolder.setListener(mListener);
        return contactViewHolder;
    }

    public interface Listener {
        void onContactClicked(Contact model);
    }
}
