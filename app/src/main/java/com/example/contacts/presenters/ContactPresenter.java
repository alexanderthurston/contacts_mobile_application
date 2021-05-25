package com.example.contacts.presenters;

import com.example.contacts.database.AppDatabase;
import com.example.contacts.models.Contact;

public class ContactPresenter {
    public interface MVPView extends BaseMVPView {
        void renderContact(Contact contact);
        void displayDeleteConfirmation();
        void goBackToContactsPage(Contact contact, boolean isDeleted, boolean isUpdated);
        void goToEditPage(Contact contact);
        void updateContactUI(Contact contact);
        void makePhoneCall(String phoneNumber);
        void sendText(String phoneNumber);
        void sendEmail(String emailAddress);
    }

    MVPView view;
    AppDatabase database;
    Contact contact;
    boolean didUpdate = false;

    public ContactPresenter(MVPView view, long id) {
        this.view = view;
        database = view.getContextDatabase();
        new Thread(() -> {
            contact = database.getContactDao().getContact(id);
            view.renderContact(contact);
        }).start();
    }

    public void handleCallPressed(String phoneNumber) {
        view.makePhoneCall(phoneNumber);
    }

    public void handleTextPressed(String phoneNumber){
        view.sendText(phoneNumber);
    }

    public void handleEmailPressed(String emailAddress){
        view.sendEmail(emailAddress);
    }

    public void handleDeletePressed() {
        view.displayDeleteConfirmation();
    }

    public void handleEditPressed() {
        view.goToEditPage(contact);
    }

    public void deleteContact() {
        new Thread(() -> {
            database.getContactDao().deleteContact(contact);
            view.goBackToContactsPage(contact, true, false);
        }).start();
    }

    public void handleContactUpdated(Contact contact) {
        this.contact = contact;
        didUpdate = true;
        view.updateContactUI(contact);
    }

    public void handleBackPressed() {
        view.goBackToContactsPage(contact, false, true);
    }
}