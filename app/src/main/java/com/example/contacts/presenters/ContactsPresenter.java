package com.example.contacts.presenters;

import com.example.contacts.database.AppDatabase;
import com.example.contacts.models.Contact;

import java.util.ArrayList;

public class ContactsPresenter {
    ArrayList<Contact> contacts = new ArrayList<>();

    public interface MVPView extends BaseMVPView {
        void goToNewContactPage();
        void renderContact(Contact contact);
        void goToContactPage(long id);
        void removeContact(Contact contact);
        void updateContactUI(Contact contact);
    }

    MVPView view;
    AppDatabase database;

    public ContactsPresenter(MVPView view) {
        this.view = view;
        database = view.getContextDatabase();
        new Thread(() -> {
            contacts = (ArrayList<Contact>) database.getContactDao().getContacts();
            contacts.forEach(contact -> {
                view.renderContact(contact);
            });
        }).start();
    }

    public void handleNewContactPress() {
        view.goToNewContactPage();
    }

    public void handleNewContactCreated(Contact contact) {
        contacts.add(contact);
        view.renderContact(contact);
    };

    public void handleContactSelected(long id) {
        view.goToContactPage(id);
    }



    public void handleContactDeleted(Contact contact) {
        for(int i =0; i < contacts.size(); i++) {
            if (contact.id == contacts.get(i).id) {
                contacts.remove(i);
                break;
            }
        }
        view.removeContact(contact);
    }

    public void handleContactUpdated(Contact contact) {
        for(int i =0; i < contacts.size(); i++) {
            if (contact.id == contacts.get(i).id) {
                contacts.set(i, contact);
                break;
            }
        }

        view.updateContactUI(contact);
    }
}
