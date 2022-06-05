package com.tftechsz.common.nim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;

import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;

/**
 * SoundPool
 */
public class ChatSoundPlayer {

    private static final String TAG = "ChatSoundPlayer";

    public enum RingerTypeEnum {
        MESSAGE,
        ACCOST,
        RADAR_RING,   //雷达
        PEER_REJECT,
        RING,
        RAIN_CLICK,
    }

    private SoundPool soundPool;
    private AudioManager audioManager;
    private int streamId;
    private int soundId;
    private boolean loop;
    private RingerTypeEnum ringerTypeEnum;
    private boolean isRingModeRegister = false;
    private int ringMode = -1;

    private static ChatSoundPlayer instance = null;
    private RingModeChangeReceiver ringModeChangeReceiver;

    public static ChatSoundPlayer instance() {
        if (instance == null) {
            synchronized (ChatSoundPlayer.class) {
                if (instance == null) {
                    instance = new ChatSoundPlayer();
                }
            }
        }
        return instance;
    }


    public synchronized void play(RingerTypeEnum type) {
        LogUtil.d(TAG, "play type->" + type.name());
        this.ringerTypeEnum = type;
        int ringId = 0;
        switch (type) {
            case ACCOST:
                ringId = R.raw.hit_up;
                loop = false;
                break;
            case MESSAGE:
                ringId = R.raw.msg;
                loop = false;
                break;
            case RADAR_RING:
                ringId = R.raw.radar_ring;
                loop = true;
                break;
            case PEER_REJECT:
                ringId = R.raw.avchat_peer_reject;
                loop = false;
                break;
            case RING:
                ringId = R.raw.avchat_ring;
                loop = true;
                break;
            case RAIN_CLICK:
                ringId = R.raw.coin_rain_click;
                loop = false;
                break;
        }

        if (ringId != 0) {
            play(ringId);
        }

    }


    public void stop() {
        if (soundPool != null) {
            if (streamId != 0) {
                soundPool.stop(streamId);
                streamId = 0;
            }
            if (soundId != 0) {
                soundPool.unload(soundId);
                soundId = 0;
            }
        }
        if (isRingModeRegister) {
            registerVolumeReceiver(false);
        }
    }

    private void play(int ringId) {
        initSoundPool();
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            soundId = soundPool.load(BaseApplication.getInstance(), ringId, 1);
        }
    }

    private void initSoundPool() {
        stop();
        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            soundPool.setOnLoadCompleteListener(onLoadCompleteListener);

            audioManager = (AudioManager) BaseApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
            ringMode = audioManager.getRingerMode();
        }
        registerVolumeReceiver(true);
    }

    SoundPool.OnLoadCompleteListener onLoadCompleteListener = new SoundPool.OnLoadCompleteListener() {
        @Override
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            if (soundId != 0 && status == 0) {
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    streamId = soundPool.play(soundId, curVolume, curVolume, 1, loop ? -1 : 0, 1f);
                }
            }
        }
    };

    private void registerVolumeReceiver(boolean register) {
        if (ringModeChangeReceiver == null) {
            ringModeChangeReceiver = new RingModeChangeReceiver();
        }

        if (register) {
            isRingModeRegister = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
            BaseApplication.getInstance().registerReceiver(ringModeChangeReceiver, filter);
        } else {
            BaseApplication.getInstance().unregisterReceiver(ringModeChangeReceiver);
            isRingModeRegister = false;
        }
    }

    private class RingModeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ringMode != -1 && ringMode != audioManager.getRingerMode()
                    && intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
                ringMode = audioManager.getRingerMode();
                play(ringerTypeEnum);
            }
        }
    }
}
