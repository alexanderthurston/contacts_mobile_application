package com.example.contacts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.contacts.components.ImageSelector;
import com.example.contacts.components.MaterialInput;
import com.example.contacts.models.Contact;
import com.example.contacts.presenters.CreateOrUpdateContactPresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateOrUpdateContactActivity extends BaseActivity implements CreateOrUpdateContactPresenter.MVPView {
    CreateOrUpdateContactPresenter presenter;
    LinearLayout mainLayout;
    ImageSelector imageSelector;
    MaterialInput nameInput;
    MaterialInput phoneNumberInput;
    MaterialInput emailAddressInput;
    String currentPhotoPath = "";
    private final int SELECT_IMAGE = 1;
    private final int TAKE_PHOTO = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CreateOrUpdateContactPresenter(this, getIntent().getLongExtra("id", -1));
        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        imageSelector = new ImageSelector(
                this,
                ()-> {
                    new MaterialAlertDialogBuilder(this)
                    .setTitle("Choose Image")
                    .setItems(new CharSequence[]{"From Camera", "From Photos"}, (view, i)-> {
                                if(i == 0){
                                    presenter.handleTakePicturePress();
                                } else {
                                    presenter.handleSelectImagePress();
                                }
                    }).show();
                }
            );
        mainLayout.addView(imageSelector);

        nameInput = new MaterialInput(this, "Name");
        phoneNumberInput = new MaterialInput(this, "Phone Number", true);
        emailAddressInput = new MaterialInput(this, "Email Address");

        MaterialButton saveButton = new MaterialButton(this, null, R.attr.materialButtonStyle);
        saveButton.setText("Save");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.weight = 1;
        saveButton.setLayoutParams(params);
        MaterialButton cancelButton = new MaterialButton(this, null, R.attr.borderlessButtonStyle);
        cancelButton.setText("Cancel");
        cancelButton.setOnClickListener((view) -> {
            presenter.handleCancelPress();
        });

        LinearLayout buttons = new LinearLayout(this);
        buttons.setGravity(Gravity.RIGHT);
        buttons.setPadding(48, 0, 48, 0);
        buttons.addView(cancelButton);

        buttons.addView(saveButton);


        saveButton.setOnClickListener((view) -> {
            nameInput.setErrorEnabled(false);
            phoneNumberInput.setErrorEnabled(false);
            presenter.saveContact(
                    nameInput.getText().toString(),
                    phoneNumberInput.getText().toString(),
                    emailAddressInput.getText().toString(),
                    imageSelector.getImageUri()
            );
        });

        mainLayout.addView(nameInput);
        mainLayout.addView(phoneNumberInput);
        mainLayout.addView(emailAddressInput);
        mainLayout.addView(buttons);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(mainLayout);

        setContentView(scrollView);
    }


    public void goBackToContactsPage(Contact contact) {
        if (contact == null) {
            setResult(Activity.RESULT_CANCELED, null);
        } else {
            Intent intent = new Intent();
            intent.putExtra("result", contact);
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void goToPhotos() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);
    }

    @Override
    public void takePicture() {
        //generate unique file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String photoFileName = "JPEG_" + timeStamp + ".jpg";

        //create a file
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), photoFileName);
        currentPhotoPath = photoFile.getAbsolutePath();

        //get the location to send to the camera
        Uri photoUri = FileProvider.getUriForFile(this, "com.example.contacts.fileprovider", photoFile);

        //send location of the file to the camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    public void displayImage(String imageUri) {
        imageSelector.setImageUri(imageUri);
    }

    public void renderContact(Contact contact) {
        runOnUiThread(() -> {
            nameInput.setText(contact.name);
            phoneNumberInput.setText(contact.phoneNumber);
            emailAddressInput.setText(contact.emailAddress);
            imageSelector.setImageUri(contact.pictureUri);
        });
    }

    @Override
    public void displayNameError() {
        Snackbar.make(mainLayout, "Name cannot be blank.", Snackbar.LENGTH_SHORT).show();
        nameInput.setErrorEnabled(true);
        nameInput.setError("Name cannot be blank.");
    }

    @Override
    public void displayPhoneNumberError() {
        Snackbar.make(mainLayout, "Phone number cannot be blank.", Snackbar.LENGTH_SHORT).show();
        phoneNumberInput.setErrorEnabled(true);
        phoneNumberInput.setError("Phone number cannot be blank.");
    }

    @Override
    public void displayEmailError() {
        Snackbar.make(mainLayout, "Email address must be a valid email address.", Snackbar.LENGTH_SHORT).show();
        emailAddressInput.setErrorEnabled(true);
        emailAddressInput.setError("Email address must be a valid email address.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            presenter.handleImageSelected(imageUri.toString());
        }
        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_CANCELED) {
            presenter.handleImageSelected("");
        }
        if(requestCode == TAKE_PHOTO && resultCode == Activity.RESULT_OK){
            presenter.handleImageSelected(currentPhotoPath);
        }
    }
}
