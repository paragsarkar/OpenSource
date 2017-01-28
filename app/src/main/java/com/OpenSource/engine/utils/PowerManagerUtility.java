package com.OpenSource.engine.utils;

import android.os.PowerManager;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

/**
 * Created by parag_sarkar on 27-01-2017.
 */


@EBean
public class PowerManagerUtility {

    PowerManager.WakeLock wakeLock;
    @SystemService
    PowerManager powerManager;

    @AfterInject
    void afterInject() {
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PowerManagerUtility");
        wakeLock.setReferenceCounted(true);
    }

    public void acquirePartialWakeLock() {
        if (wakeLock.isHeld() == false) {
            wakeLock.acquire();
        }
    }

    public void releasePartialWakeLock() {
        if (wakeLock.isHeld() == true) {
            wakeLock.release();
        }
    }

}
