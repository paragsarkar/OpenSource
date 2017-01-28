package com.OpenSource.engine.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.SystemService;

/**
 * Created by parag_sarkar on 27-01-2017.
 */
 @EApplication
public class AppInstance extends Application {



    static AppInstance appInstance;
    @SystemService
    ActivityManager activityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        //https://evernote.github.io/android-job/javadoc/
    }

    public static AppInstance getAppInstance() {
        return appInstance;
    }

    public void killYourself() {
        Intent intent = new Intent("BizBuzzz-Kill");
        intent.putExtra("message", "killYourself");
        this.sendBroadcast(intent);
        killOwnProcess();
    }

    @Background(delay = 1000)
    public void killOwnProcess() {
        activityManager.killBackgroundProcesses(this.getPackageName());
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public interface IAppEventsCommunicator {
        void onConfigurationChanged(Configuration newConfig);

        void onCreate();

        void onLowMemory();

        void onTerminate();
    }
}
