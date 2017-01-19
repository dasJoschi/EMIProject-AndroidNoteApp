package com.example.emiproject_androidnoteapp;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import io.realm.Realm;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
