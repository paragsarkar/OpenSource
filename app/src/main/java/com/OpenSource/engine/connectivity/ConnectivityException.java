/*
 *  See the file "LICENSE" for the full license governing this code.
 */

package com.OpenSource.engine.connectivity;

/**
 * @author  parag sarkar
 */
@SuppressWarnings("serial")
public class ConnectivityException extends Exception {

    public static final String NETWORK_NOT_CONNECTED = "NETWORK_NOT_CONNECTED";

    public static final String WIFI_NETWORK_ALWAYS_ALLOW = "WIFI_NETWORK_ALWAYS_ALLOW";

    public static final String METERED_NETWORK_ASK_USER = "METERED_NETWORK_ASK_USER";
    public static final String METERED_NETWORK_USER_PERMITTED = "METERED_NETWORK_USER_PERMITTED";


    public static final String ROAMING_NETWORK_ASK_USER = "ROAMING_NETWORK_ASK_USER";
    public static final String ROAMING_NETWORK_USER_PERMITTED = "ROAMING_NETWORK_USER_PERMITTED";


    /**
     * @param errorMessage
     */
    public ConnectivityException(String errorMessage) {
        super(errorMessage);
    }
}
