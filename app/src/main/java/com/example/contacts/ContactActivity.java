package com.example.contacts;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.contacts.components.ContactCard;
import com.example.contacts.models.Contact;
import com.example.contacts.presenters.ContactPresenter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ContactActivity extends BaseActivity implements ContactPresenter.MVPView {
    ContactPresenter presenter;
    LinearLayout mainLayout;
    String phoneNumber;
    private final int UPDATE_CONTACT = 1;
    private final int REQUEST_PHONE_PERMISSIONS = 2;
    private final int REQUEST_SMS_PERMISSIONS = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ContactPresenter(this, getIntent().getLongExtra("id", -1));
        ScrollView scrollView = new ScrollView(this);
        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(mainLayout);
        setContentView(scrollView);
    }

    @Override
    public void renderContact(Contact contact) {
        runOnUiThread(() -> {
            ContactCard contactCard = new ContactCard(this, contact, true);
            phoneNumber = contact.phoneNumber;
            contactCard.setOnCallClickListener((view)-> {
                presenter.handleCallPressed(contact.phoneNumber);
            });
            contactCard.setOnTextClickListener((view)-> {
                presenter.handleTextPressed(contact.phoneNumber);
            });
            contactCard.setOnEmailClickListener((view)-> {
                presenter.handleEmailPressed(contact.emailAddress);
            });
            contactCard.setFabOnClickListener((fab) -> {
                PopupMenu popupMenu = new PopupMenu(this, fab);
                popupMenu.getMenu().add("Edit");
                popupMenu.getMenu().add("Delete");
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getTitle().toString().equals("Edit")) {
                        // handle editing
                        presenter.handleEditPressed();
                    } else {
                        // handle deleting
                        presenter.handleDeletePressed();
                    }
                    return true;
                });
                popupMenu.show();
            });
            mainLayout.addView(contactCard);
        });
    }

    @Override
    public void displayDeleteConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Are you sure you want to delete this contact?")
                .setPositiveButton("Delete", (view, i) -> {
                    presenter.deleteContact();
                })
                .setNeutralButton("Cancel", (view, i) -> {
                    view.dismiss();
                })
                .show();
    }

    @Override
    public void goBackToContactsPage(Contact contact, boolean isDeleted, boolean isUpdated) {
        Intent intent = new Intent();
        intent.putExtra("contact", contact);
        if (isDeleted) {
            setResult(ContactsActivity.CONTACT_DELETED ,intent);
        } else if (isUpdated) {
            setResult(ContactsActivity.CONTACT_UPDATED, intent);
        }
        finish();
    }

    @Override
    public void goToEditPage(Contact contact) {
        Intent intent = new Intent(this, CreateOrUpdateContactActivity.class);
        intent.putExtra("id", contact.id);
        startActivityForResult(intent, UPDATE_CONTACT);
    }

    @Override
    public void updateContactUI(Contact contact) {
        ContactCard card = mainLayout.findViewWithTag(contact.id);
        phoneNumber = contact.phoneNumber;
        card.setContact(contact);
    }

    @Override
    public void makePhoneCall(String phoneNumber) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } else {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_PERMISSIONS);

        }

    }

    @Override
    public void sendText(String phoneNumber) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setData(Uri.parse("sms:" + phoneNumber));
            startActivity(smsIntent);
        } else {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSIONS);
        }
    }

    @Override
    public void sendEmail(String emailAddress) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + emailAddress));
        startActivity(emailIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_CONTACT && resultCode == Activity.RESULT_OK) {
            // we know the contact was updated and we need to update the ui
            Contact contact = (Contact) data.getSerializableExtra("result");
            presenter.handleContactUpdated(contact);
        }
    }

    @Override
    public void onBackPressed() {
        presenter.handleBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PHONE_PERMISSIONS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                presenter.handleCallPressed(phoneNumber);
            } else {
                //display a message saying that they will need to allow permission

            }
        }
        if(requestCode == REQUEST_SMS_PERMISSIONS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                presenter.handleTextPressed(phoneNumber);
            } else {

            }
        }
    }
}