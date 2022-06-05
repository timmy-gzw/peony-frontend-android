package com.tftechsz.common.utils;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.NetStatusEvent;

/**
 * 包 名 : com.tftechsz.common.utils
 * 描 述 : TODO
 */
public class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
    /**
     * 网络已连接
     */
    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        RxBus.getDefault().post(new NetStatusEvent(true));
    }


    /**
     * 网络已断开
     */
    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        RxBus.getDefault().post(new NetStatusEvent(false));
    }

    /**
     * 网络改变
     */
    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            RxBus.getDefault().post(new NetStatusEvent(true));
        }
    }
}
