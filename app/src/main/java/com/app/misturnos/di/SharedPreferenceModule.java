package com.app.misturnos.di;


import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferenceModule {

    private Context context;

    public SharedPreferenceModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Context provideContext(){
        return context;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPrefs(Context context) {
        return context.getSharedPreferences("calendariosPref", context.MODE_PRIVATE);
    }

}
