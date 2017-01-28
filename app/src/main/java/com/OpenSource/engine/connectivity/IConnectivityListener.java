/*
 *  See the file "LICENSE" for the full license governing this code.
 */

package com.OpenSource.engine.connectivity;

/**
 * @author sanjivan_kumar
 */
public interface IConnectivityListener {
    void onConnected(int networkType);

    void onConnecting(int networkType);

    void onDisconnecting(int networkType);

    void onDisconnected(int networkType);

    void onRoaming();
}
