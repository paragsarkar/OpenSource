package com.OpenSource.engine.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.OpenSource.engine.application.AppInstance;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Created by parag_sarkar on 27-01-2017.
 */


@EBean(scope = EBean.Scope.Singleton)

public class AppPreference {

    private final String APP_SHARED_PREFERENCE = "appSharedPreferences";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private PreferencesListener preferencesListener;

    protected List<SharedPreferences.OnSharedPreferenceChangeListener> OnSharedPreferenceChangeListenersList;

    @App
    public AppInstance appInstance;


    @AfterInject
    protected void afterInjectAfxPreferences() {

        sharedPreferences = appInstance.getSharedPreferences(APP_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        OnSharedPreferenceChangeListenersList = new LinkedList<SharedPreferences.OnSharedPreferenceChangeListener>();
        preferencesListener = new PreferencesListener();
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferencesListener);
    }

    class PreferencesListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
            for (SharedPreferences.OnSharedPreferenceChangeListener listener : OnSharedPreferenceChangeListenersList) {
                listener.onSharedPreferenceChanged(arg0, arg1);
            }
        }
    }

    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        editor = editor.putBoolean(key, value);
        editor.commit();
        return editor;
    }

    public SharedPreferences.Editor putFloat(String key, float value) {
        editor = editor.putFloat(key, value);
        editor.commit();
        return editor;
    }

    public SharedPreferences.Editor putInt(String key, int value) {
        editor = editor.putInt(key, value);
        editor.commit();
        return editor;
    }

    public SharedPreferences.Editor putLong(String key, long value) {
        editor = editor.putLong(key, value);
        editor.commit();
        return editor;
    }

    public SharedPreferences.Editor putString(String key, String value) {
        editor = editor.putString(key, value);
        editor.commit();
        return editor;
    }

    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        editor = editor.putStringSet(key, values);
        editor.commit();
        return editor;
    }

    public void remove(String key) {
        editor = editor.remove(key);
        editor.commit();
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        return sharedPreferences.getStringSet(key, defValues);
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        OnSharedPreferenceChangeListenersList.add(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        OnSharedPreferenceChangeListenersList.remove(listener);
    }

}
