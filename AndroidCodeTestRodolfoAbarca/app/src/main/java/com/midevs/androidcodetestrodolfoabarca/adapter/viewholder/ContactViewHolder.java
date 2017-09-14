package com.midevs.androidcodetestrodolfoabarca.adapter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.midevs.androidcodetestrodolfoabarca.R;
import com.midevs.androidcodetestrodolfoabarca.adapter.ContactsAdapter;
import com.midevs.androidcodetestrodolfoabarca.data.Contact;

public class ContactViewHolder extends SortedListAdapter.ViewHolder<Contact> {
    private final TextView nameView, phoneView, emailView;
    private final View parentView;
    private Contact contact;


    public ContactViewHolder(View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.contact_view_holder_name);
        phoneView = itemView.findViewById(R.id.contact_view_holder_phone);
        emailView = itemView.findViewById(R.id.contact_view_holder_email);
        parentView = itemView.findViewById(R.id.contact_view_holder_parent);
    }

    @Override
    protected void performBind(@NonNull Contact contact) {
        this.contact = contact;
        nameView.setText(contact.getFullName());
        phoneView.setText(contact.getPhone().get(0));
        emailView.setText(contact.getEmail().get(0));
    }

    public void setListener(ContactsAdapter.Listener mListener) {
        parentView.setOnClickListener(view -> {
            mListener.onContactClicked(contact);
        });
    }

}
