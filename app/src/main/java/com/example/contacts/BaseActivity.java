package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.contacts.database.AppDatabase;
import com.example.contacts.presenters.BaseMVPView;

public class BaseActivity extends AppCompatActivity implements BaseMVPView {
    @Override
    public AppDatabase getContextDatabase() {
        return Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "contacts").build();
    }
}
