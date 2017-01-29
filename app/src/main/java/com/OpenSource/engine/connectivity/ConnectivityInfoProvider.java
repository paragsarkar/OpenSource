/*
 *  See the file "LICENSE" for the full license governing this code.
 */
package com.OpenSource.engine.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.net.ConnectivityManagerCompat;

import com.OpenSource.engine.application.AppInstance;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;
import org.androidannotations.annotations.SystemService;

import java.util.LinkedList;
import java.util.List;



/**
 * @author parag sarkar
 */

@EBean(scope = Scope.Singleton)
public class ConnectivityInfoProvider {


    protected List<IConnectivityListener> connectivityListenersList;

    @App
    AppInstance appInstance;

    @SystemService
    ConnectivityManager connectivityManager;

    @SystemService
    WifiManager wifiManager;

    ConnectivityChangeReceiver connectivityChangeReceiver;


    @AfterInject
    void afterInject() {
        connectivityListenersList = new LinkedList<IConnectivityListener>();
    }

    private class ConnectivityChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            debugIntent(intent);
            NetworkInfo networkInfo = ConnectivityManagerCompat.getNetworkInfoFromBroadcast(connectivityManager, intent);
            if (networkInfo != null) {
                notifyListeners(networkInfo);
            } else {
            }
        }
    }

    private void debugIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
            }
        } else {
        }
    }

    public boolean isAvailable() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo == null) ? false : networkInfo.isAvailable();
    }

    public boolean isRoaming() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo == null) ? false : networkInfo.isRoaming();
    }

    public boolean isConnected() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo == null) ? false : networkInfo.isConnected();
    }

    public boolean isConnected(int waitForSecondsIfConnecting) {
        // validate range for waitForSecondsIfConnecting
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }

        while (waitForSecondsIfConnecting > 0 && networkInfo.getState() == NetworkInfo.State.CONNECTING) {
            try {
                Thread.sleep(2 * 1000);
                waitForSecondsIfConnecting = waitForSecondsIfConnecting - 2;
            } catch (InterruptedException e) {
            }
        }
        return networkInfo.isConnected();
    }

    public boolean isWiFiConnected() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.getState() == NetworkInfo.State.CONNECTED && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }

    public void setWifiEnabled(boolean enabled) {
        wifiManager.setWifiEnabled(enabled);

    }

    public boolean isMobileConnected() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.getState() == NetworkInfo.State.CONNECTED && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public boolean isActiveNetworkMetered() {
        return ConnectivityManagerCompat.isActiveNetworkMetered(connectivityManager);
    }

    public int getActiveNetworkType() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return -1;
        }
        return networkInfo.getType();
    }

    public void registerConnectivityInfoListener(IConnectivityListener connectivityListener) {
        if (connectivityListenersList.isEmpty()) {
            connectivityChangeReceiver = new ConnectivityChangeReceiver();
            appInstance.registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        connectivityListenersList.add(connectivityListener);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            notifyListeners(networkInfo);
        }

    }

    public void unregisterConnectivityInfoListener(IConnectivityListener connectivityListener) {
        connectivityListenersList.remove(connectivityListener);
        if (connectivityListenersList.isEmpty()) {
            appInstance.unregisterReceiver(connectivityChangeReceiver);
            connectivityChangeReceiver = null;
        }
    }

    private void notifyListeners(NetworkInfo networkInfo) {
        if (connectivityListenersList != null) {
            for (IConnectivityListener connectivityListener : connectivityListenersList) {
                switch (networkInfo.getState()) {
                    case CONNECTED:
                        connectivityListener.onConnected(networkInfo.getType());
                        if (networkInfo.isRoaming()) {
                            connectivityListener.onRoaming();
                        }
                        break;
                    case CONNECTING:
                        connectivityListener.onConnecting(networkInfo.getType());
                        break;
                    case DISCONNECTED:
                        connectivityListener.onDisconnected(networkInfo.getType());
                        break;
                    case DISCONNECTING:
                        connectivityListener.onDisconnecting(networkInfo.getType());
                        break;
                    case UNKNOWN:
                        break;
                }
            }
        }
    }
}
