package com.example.contacts.components;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.contacts.R;
import com.example.contacts.models.Contact;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

public class ContactCard extends MaterialCardView {
    MaterialButton callButton;
    MaterialButton textButton;
    MaterialButton emailButton;
    FloatingActionButton fab;
    Contact contact;
    AppCompatImageView imageView;
    MaterialTextView nameView;
    MaterialTextView phoneNumberView;
    MaterialTextView emailView;
    LinearLayout header;
    LinearLayout preview;
    LinearLayout body;
    LinearLayout footer;
    CircleDisplay profilePic;
    boolean showFullPost = false;

    public ContactCard(Context context, Contact post) {
        this(context, post, false);
    }

    public ContactCard(Context context, Contact contact, boolean showFullPost) {
        super(context);
        setTag(contact.id);
        this.contact = contact;
        this.showFullPost = showFullPost;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(48, 24,48,24);
        setLayoutParams(params);

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        header = new LinearLayout(context);
        header.setOrientation(LinearLayout.VERTICAL);
        preview = new LinearLayout(context);
        body = new LinearLayout(context);
        body.setPadding(72, 40, 72, 0);
        body.setOrientation(LinearLayout.VERTICAL);
        footer = new LinearLayout(context);
        mainLayout.addView(header);
        mainLayout.addView(body);
        mainLayout.addView(footer);

        addView(mainLayout);

        //Preview
        LinearLayout.LayoutParams previewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        previewParams.setMargins(20, 0, 20, 40);
        String[] nameList = contact.name.split("");
        profilePic = new CircleDisplay(context, nameList[0]);
        profilePic.setPadding(0, 0, 40, 20);
        preview.addView(profilePic);
        nameView = new MaterialTextView(context, null, R.attr.textAppearanceHeadline6);
        nameView.setText(contact.name);
        nameView.setPadding(40, 15, 20, 0);
        preview.setLayoutParams(previewParams);
        preview.addView(nameView);
        body.addView(preview);


        //Header
        imageView = new AppCompatImageView(context);


        //Body
        LinearLayout.LayoutParams contentsParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentsParams.setMargins(175, 12, 0, 0);

        phoneNumberView = new MaterialTextView(context);

        phoneNumberView.setText(contact.phoneNumber);
        phoneNumberView.setTextSize(18);
        phoneNumberView.setLayoutParams(contentsParams);

        emailView = new MaterialTextView(context);
        emailView.setText(contact.emailAddress);
        emailView.setLayoutParams(contentsParams);

        callButton = new MaterialButton(context, null, R.attr.borderlessButtonStyle);
        callButton.setIconResource(R.drawable.ic_baseline_call_24);
        textButton = new MaterialButton(context, null, R.attr.borderlessButtonStyle);
        textButton.setIconResource(R.drawable.ic_baseline_textsms_24);
        emailButton = new MaterialButton(context, null, R.attr.borderlessButtonStyle);
        emailButton.setIconResource(R.drawable.ic_baseline_email_24);


        if (showFullPost) {

            setupImage();
            header.addView(imageView);
            body.addView(phoneNumberView);
            if(!contact.emailAddress.isEmpty()) {
                emailView.setTextSize(18);
                body.addView(emailView);
            }
        } else {
            emailView.setEllipsize(TextUtils.TruncateAt.END);
        }


        // Footer
        if (showFullPost) {
            footer.addView(callButton);
            footer.addView(textButton);
            if(!contact.emailAddress.isEmpty()) {
                footer.addView(emailButton);
            }
            footer.setPadding(115, 0, 8, 0);
        }

        if (showFullPost) {
            fab = new FloatingActionButton(context);
            fab.setImageResource(R.drawable.ic_baseline_edit_24);
            MaterialCardView.LayoutParams fabParams = new MaterialCardView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fabParams.gravity = Gravity.RIGHT;
            fabParams.setMargins(0, 480, 48, 0);
            fab.setLayoutParams(fabParams);
            addView(fab);
        }
    }

    private void setupImage() {
        if (contact.pictureUri.equals("") && showFullPost) {
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 560);
            imageView.setLayoutParams(imageParams);
            imageView.setImageResource(R.drawable.ic_baseline_photo_size_select_actual_240);
            header.setBackgroundColor(getResources().getColor(R.color.colorDarkBackground, null));

        } else if (!contact.pictureUri.equals("")) {
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 560);
            imageView.setLayoutParams(imageParams);
            imageView.setImageURI(Uri.parse(contact.pictureUri));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        setupImage();
        nameView.setText(contact.name);
        phoneNumberView.setText(contact.phoneNumber);
        if(!contact.emailAddress.isEmpty() && showFullPost){
            emailView.setText(contact.emailAddress);
            footer.addView(emailButton);
            body.addView(emailView);
        } else if(contact.emailAddress.isEmpty()){
            footer.removeView(emailButton);
            body.removeView(emailView);
        }
    }


    public void setOnContactClickListener(@Nullable OnClickListener l) {
        body.setOnClickListener(l);

    }

    public void setOnCallClickListener(@Nullable OnClickListener l){
        callButton.setOnClickListener(l);
    }

    public void setOnTextClickListener(@Nullable OnClickListener l){
        textButton.setOnClickListener(l);
    }

    public void setOnEmailClickListener(@Nullable OnClickListener l){
        emailButton.setOnClickListener(l);
    }

    public void setFabOnClickListener(OnClickListener l) {
        fab.setOnClickListener(l);
    }

}
