/*
 *  See the file "LICENSE" for the full license governing this code.
 */
package com.OpenSource.engine.connectivity;

import android.net.ConnectivityManager;

import com.OpenSource.engine.application.AppInstance;
import com.OpenSource.engine.utils.AppPreference;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

/**
 * @author parag sarkar
 */
@EBean(scope = Scope.Default)
public class ConnectivityValidator {


    @App
    AppInstance appInstance;

    @Bean
    ConnectivityInfoProvider connectivityInfoProvider;

    @Bean
    AppPreference preferences;

    @AfterInject
    void afterInject() {
    }

    public void doValidation() throws ConnectivityException {
        if (connectivityInfoProvider.isConnected() == false) {
            throw new ConnectivityException(ConnectivityException.NETWORK_NOT_CONNECTED);
        }

        int networkType = connectivityInfoProvider.getActiveNetworkType();

        switch (networkType) {
            case ConnectivityManager.TYPE_WIFI:
                break;
            case ConnectivityManager.TYPE_MOBILE:
                if (connectivityInfoProvider.isRoaming()) {
                    doRoamingPolicyValidation();
                } else if (connectivityInfoProvider.isActiveNetworkMetered()) {
                    doMeteredNetworkPolicyValidation();
                }
                break;
            default:
                break;
        }
    }

    /**
     * @throws ConnectivityException
     */
    private void doMeteredNetworkPolicyValidation() throws ConnectivityException {
        String networkPolicyCode = preferences.getString("metered.network.policy.code",
                ConnectivityException.METERED_NETWORK_ASK_USER);
        if (networkPolicyCode.equalsIgnoreCase(ConnectivityException.METERED_NETWORK_ASK_USER)) {
            throw new ConnectivityException(ConnectivityException.METERED_NETWORK_ASK_USER);
        }
    }

    /**
     * @throws ConnectivityException
     */
    private void doRoamingPolicyValidation() throws ConnectivityException {
        String networkPolicyCode = preferences.getString("roaming.network.policy.code",
                ConnectivityException.ROAMING_NETWORK_ASK_USER);
        if (networkPolicyCode.equalsIgnoreCase(ConnectivityException.ROAMING_NETWORK_ASK_USER)) {
            throw new ConnectivityException(ConnectivityException.ROAMING_NETWORK_ASK_USER);
        }
    }
}
