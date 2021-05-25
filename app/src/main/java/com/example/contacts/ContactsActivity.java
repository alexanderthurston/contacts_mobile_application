package com.example.contacts;

import androidx.annotation.Nullable;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.contacts.components.ContactCard;
import com.example.contacts.models.Contact;
import com.example.contacts.presenters.ContactsPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactsActivity extends BaseActivity implements ContactsPresenter.MVPView {
    ContactsPresenter presenter;
    LinearLayout contactsLayout;
    // request code
    private final int CREATE_NEW_CONTACT = 1;
    private final int MODIFY_CONTACT = 2;

    // result codes
    public static final int CONTACT_DELETED = 10;
    public static final int CONTACT_UPDATED = 11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ContactsPresenter(this);
        contactsLayout = new LinearLayout(this);
        contactsLayout.setOrientation(LinearLayout.VERTICAL);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(contactsLayout);

        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.addView(scrollView);
        FloatingActionButton newContactButton = new FloatingActionButton(this);
        newContactButton.setOnClickListener((view) -> {
            presenter.handleNewContactPress();
        });
        newContactButton.setImageResource(R.drawable.ic_baseline_add_24);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 48, 48);
        params.gravity = (Gravity.BOTTOM | Gravity.RIGHT);

        newContactButton.setLayoutParams(params);

        mainLayout.addView(newContactButton);
        setContentView(mainLayout);
    }

    @Override
    public void goToNewContactPage() {
        Intent intent = new Intent(this, CreateOrUpdateContactActivity.class);
        startActivityForResult(intent, CREATE_NEW_CONTACT);
    }

    @Override
    public void renderContact(Contact contact) {
        runOnUiThread(() -> {
            ContactCard contactCard = new ContactCard(this, contact);
            contactCard.setOnContactClickListener((view) -> {
                presenter.handleContactSelected(contact.id);
            });

            contactsLayout.addView(contactCard);
        });
    }

    @Override
    public void goToContactPage(long id) {
        Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, MODIFY_CONTACT);
    }

    @Override
    public void removeContact(Contact contact) {
        runOnUiThread(() -> {
            View view = contactsLayout.findViewWithTag(contact.id);
            contactsLayout.removeView(view);
        });
    }

    public void updateContactUI(Contact contact) {
        ContactCard card = contactsLayout.findViewWithTag(contact.id);
        card.setContact(contact);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_NEW_CONTACT && resultCode == Activity.RESULT_OK) {
            Contact contact = (Contact) data.getSerializableExtra("result");
            presenter.handleNewContactCreated(contact);
        }

        if (requestCode == MODIFY_CONTACT && resultCode == CONTACT_DELETED) {
            // do something to remove the contact from the screen
            Contact contact = (Contact) data.getSerializableExtra("contact");
            presenter.handleContactDeleted(contact);
        }

        if (requestCode == MODIFY_CONTACT && resultCode == CONTACT_UPDATED) {
            // do something to update the contact ui on the screen
            Contact contact = (Contact) data.getSerializableExtra("contact");
            presenter.handleContactUpdated(contact);
        }
    }
}