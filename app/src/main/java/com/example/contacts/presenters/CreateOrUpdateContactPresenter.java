package com.example.contacts.presenters;

import com.example.contacts.database.AppDatabase;
import com.example.contacts.models.Contact;

public class CreateOrUpdateContactPresenter {
    public interface MVPView extends BaseMVPView {
        void goBackToContactsPage(Contact contact);
        void goToPhotos();
        void takePicture();
        void displayImage(String imageUri);
        void renderContact(Contact contact);
        void displayNameError();
        void displayPhoneNumberError();
        void displayEmailError();
    }

    MVPView view;
    AppDatabase database;
    Contact contact;
    public CreateOrUpdateContactPresenter(MVPView view, long id) {
        this.view = view;
        database = view.getContextDatabase();
        if (id != -1) {
            new Thread(() -> {
                contact = database.getContactDao().getContact(id);
                view.renderContact(contact);
            }).start();
        }
    }

    public void saveContact(String name, String phoneNumber, String emailAddress, String pictureUri) {
        if (name.length() == 0) {
            // dont save
            view.displayNameError();
            return;
        }
        if(phoneNumber.length() == 0){
            view.displayPhoneNumberError();
            return;
        }
        if(!emailAddress.isEmpty() && !emailAddress.contains("@")){
            view.displayEmailError();
            return;
        }

        new Thread(() -> {
            if (contact == null) {
                Contact contact = new Contact();
                contact.name = name;
                contact.phoneNumber = phoneNumber;
                contact.emailAddress = emailAddress;
                contact.pictureUri = pictureUri;
                contact.id = database.getContactDao().createContact(contact);
                view.goBackToContactsPage(contact);
            } else {
                // update the existing contact
                contact.name = name;
                contact.phoneNumber = phoneNumber;
                contact.emailAddress = emailAddress;
                contact.pictureUri = pictureUri;
                database.getContactDao().updateContact(contact);
                view.goBackToContactsPage(contact);
            }

        }).start();
    }

    public void handleCancelPress() {
        view.goBackToContactsPage(null);
    }

    public void handleSelectImagePress() {
        view.goToPhotos();
    }

    public void handleTakePicturePress() {view.takePicture();}

    public void handleImageSelected(String imageUri) {
        view.displayImage(imageUri);
    }
}