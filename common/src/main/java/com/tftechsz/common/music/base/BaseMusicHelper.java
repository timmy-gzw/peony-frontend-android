package com.tftechsz.common.music.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.tftechsz.common.entity.AudioBean;
import com.tftechsz.common.music.service.PlayService;
import com.tftechsz.common.service.PartyAudioService;

import java.util.ArrayList;
import java.util.List;

/**
 * 包 名 : com.tftechsz.common.music.base
 * 描 述 : TODO
 */
public class BaseMusicHelper {

    private Context mContext;
    /**
     * 播放音乐service
     */
    private PlayService mPlayService;

    private PartyAudioService partyService;
    /**
     * 本地歌曲列表
     */
    private final List<AudioBean> mMusicList = new ArrayList<>();

    private BaseMusicHelper() {
    }

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private final static BaseMusicHelper INSTANCE = new BaseMusicHelper();
    }

    public static BaseMusicHelper get() {
        return SingletonHolder.INSTANCE;
    }

    public void init(Application application) {
        mContext = application.getApplicationContext();
        //这里可以做一些初始化的逻辑
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 获取PlayService对象
     *
     * @return 返回PlayService对象
     */
    public PlayService getPlayService() {
        return mPlayService;
    }


    public PartyAudioService getPartyService() {
        return partyService;
    }
    /**
     * 设置PlayService服务
     */
    public void setPlayService(PlayService service) {
        mPlayService = service;
    }

    /**
     * 设置PlayService服务
     */
    public void setPartyService(PartyAudioService service) {
        partyService = service;
    }

    /**
     * 获取扫描到的音乐数据集合
     *
     * @return 返回list集合
     */
    public List<AudioBean> getMusicList() {
        return mMusicList;
    }


    /**
     * 设置音频结合
     *
     * @param list 音频集合
     */
    public void setMusicList(List<AudioBean> list) {
        mMusicList.clear();
        mMusicList.addAll(list);
        if (getPlayService() != null) {
            getPlayService().updateMusicList(list);
        }
    }
}
