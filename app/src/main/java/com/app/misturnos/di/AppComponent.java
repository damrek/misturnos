package com.app.misturnos.di;

import com.app.misturnos.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class, SharedPreferenceModule.class})
public interface AppComponent {
    void inject(MainActivity activity);
}
