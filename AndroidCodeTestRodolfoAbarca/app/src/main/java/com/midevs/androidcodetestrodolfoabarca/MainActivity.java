package com.midevs.androidcodetestrodolfoabarca;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.midevs.androidcodetestrodolfoabarca.adapter.ContactsAdapter;
import com.midevs.androidcodetestrodolfoabarca.data.Contact;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class MainActivity extends BaseActivity implements SortedListAdapter.Callback {

    private static final Comparator<Contact> COMPARATOR = new SortedListAdapter.ComparatorBuilder<Contact>()
            .setOrderForModel(Contact.class, new Comparator<Contact>() {
                @Override
                public int compare(Contact contact, Contact t1) {
                    return contact.getFullName().compareTo(contact.getFullName());
                }

            })
            .build();
    private List<Contact> contacts;
    private RecyclerView recyclerView;
    private ContactsAdapter mAdapter;
    private ProgressBar editProgressBar;
    private Animator mAnimator;

    private static List<Contact> filter(List<Contact> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Contact> filteredModelList = new ArrayList<>();
        for (Contact model : models) {
            final String text = model.getFullName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public void initialize() {
        setSupportActionBar((Toolbar) findViewById(R.id.tool_bar));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        editProgressBar = (ProgressBar) findViewById(R.id.edit_progress_bar);
        mAdapter = new ContactsAdapter(this, COMPARATOR, model -> {
            final String message = model.getFullName() + " Clicked";
            Snackbar.make(findViewById(R.id.recycler_view), message, Snackbar.LENGTH_SHORT).show();
        });

        mAdapter.addCallback(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        contacts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Contact a = new Contact();
            a.setmId(i + "");
            a.setName(UUID.randomUUID().toString());
            a.setLastName("Last Name " + i);
            contacts.add(a);
        }
        mAdapter.edit()
                .replaceAll(contacts)
                .commit();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                final List<Contact> filteredModelList = filter(contacts, query);
                mAdapter.edit()
                        .replaceAll(filteredModelList)
                        .commit();
                return true;
            }
        });
        return true;
    }

    @Override
    public void onEditStarted() {
        if (editProgressBar.getVisibility() != View.VISIBLE) {
            editProgressBar.setVisibility(View.VISIBLE);
            editProgressBar.setAlpha(0.0f);
        }

        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mAnimator = ObjectAnimator.ofFloat(editProgressBar, View.ALPHA, 1.0f);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.start();

        recyclerView.animate().alpha(0.5f);
    }

    @Override
    public void onEditFinished() {
        recyclerView.scrollToPosition(0);
        recyclerView.animate().alpha(1.0f);

        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mAnimator = ObjectAnimator.ofFloat(editProgressBar, View.ALPHA, 0.0f);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {

            private boolean mCanceled = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                mCanceled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!mCanceled) {
                    editProgressBar.setVisibility(View.GONE);
                }
            }
        });
        mAnimator.start();
    }
}
