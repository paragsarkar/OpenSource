package com.OpenSource.app.appcontroller;

import android.content.res.Configuration;

import com.OpenSource.engine.application.AppInstance;

import org.androidannotations.annotations.EBean;




/**
 * Created by parag_sarkar on 28-01-2017.
 */
@EBean(scope = EBean.Scope.Singleton)
public class Application implements AppInstance.IAppEventsCommunicator {


    @Override
    public void onCreate() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }



    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTerminate() {

    }
}
