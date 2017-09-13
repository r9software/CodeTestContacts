package com.midevs.androidcodetestrodolfoabarca.adapter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.midevs.androidcodetestrodolfoabarca.R;
import com.midevs.androidcodetestrodolfoabarca.adapter.ContactsAdapter;
import com.midevs.androidcodetestrodolfoabarca.data.Contact;

public class ContactViewHolder extends SortedListAdapter.ViewHolder<Contact> {
    private final TextView mValueView;
    private final View parentView;
    private Contact contact;


    public ContactViewHolder(View itemView) {
        super(itemView);
        mValueView = itemView.findViewById(R.id.contact_view_holder_name);
        parentView = itemView.findViewById(R.id.contact_view_holder_parent);
    }

    @Override
    protected void performBind(@NonNull Contact contact) {
        this.contact = contact;
        mValueView.setText(contact.getFullName());
    }

    public void setListener(ContactsAdapter.Listener mListener) {
        parentView.setOnClickListener(view -> {
            mListener.onContactClicked(contact);
        });
    }

}
