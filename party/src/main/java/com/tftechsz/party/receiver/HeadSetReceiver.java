package com.tftechsz.party.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadSetReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (BluetoothProfile.STATE_DISCONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                //Bluetooth headset is now disconnected
                if (headSetStatus != null)
                    headSetStatus.onHeadChange(0);
            } else if (BluetoothProfile.STATE_CONNECTING == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                if (headSetStatus != null)
                    headSetStatus.onHeadChange(1);
            } else if (BluetoothProfile.STATE_CONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                if (headSetStatus != null)
                    headSetStatus.onHeadChange(1);
            }
        } else if ("android.intent.action.HEADSET_PLUG".equals(action)) {
            if (BluetoothAdapter.getDefaultAdapter() != null){
                if (BluetoothProfile.STATE_CONNECTED == BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.HEADSET)) {
                    if (headSetStatus != null)
                        headSetStatus.onHeadChange(1);
                }
            }
            if (BluetoothAdapter.getDefaultAdapter() != null &&
                    BluetoothProfile.STATE_CONNECTED != BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.HEADSET)) {
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 2) == 0) {
                        if (headSetStatus != null)
                            headSetStatus.onHeadChange(0);
                    } else if (intent.getIntExtra("state", 2) == 1) {
                        if (headSetStatus != null)
                            headSetStatus.onHeadChange(1);
                    }
                }
            }
        }
    }

    HeadSetStatus headSetStatus;

    /**
     * 网络状态类型改变的监听接口
     */
    public interface HeadSetStatus {
        void onHeadChange(int headStatus);
    }

    /**
     * 设置网络状态监听接口
     */
    public void setHeadSetStatus(HeadSetStatus headSetStatus) {
        this.headSetStatus = headSetStatus;
    }

}
