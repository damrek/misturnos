package com.app.misturnos;


import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.misturnos.di.AppComponent;
import com.app.misturnos.di.AppModule;
import com.app.misturnos.di.DaggerAppComponent;
import com.app.misturnos.di.SharedPreferenceModule;

public class App extends Application {

    private AppComponent appComponent;
    public static SharedPreferences sharedPreferences = null;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .sharedPreferenceModule(new SharedPreferenceModule(this))
                .build();

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
