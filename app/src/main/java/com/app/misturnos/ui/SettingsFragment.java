package com.app.misturnos.ui;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.app.misturnos.R;


public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference emailAddrPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey);
        emailAddrPref = findPreference("mailAddr");
        setupPreferenceListeners();
    }

    private void setupPreferenceListeners() {
        emailAddrPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!TextUtils.isEmpty(newValue.toString())) {
                    preference.setSummary(newValue.toString());
                } else {
                    preference.setSummary("Enter your email address to share");
                }
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EditTextPreference etpMail = (EditTextPreference) emailAddrPref;
        if (!TextUtils.isEmpty(etpMail.getText())) {
            emailAddrPref.setSummary(etpMail.getText());
        }
    }
}