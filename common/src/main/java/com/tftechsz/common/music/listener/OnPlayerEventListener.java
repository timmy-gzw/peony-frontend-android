package com.tftechsz.common.music.listener;


import android.media.MediaPlayer;

import com.tftechsz.common.entity.AudioBean;

/**
 * 包 名 : com.tftechsz.common.music.listener
 * 描 述 : 播放进度监听器
 */
public interface OnPlayerEventListener {
    /**
     * 切换歌曲
     * 主要是切换歌曲的时候需要及时刷新界面信息
     */
    void onChange(int pos, AudioBean music);


    /**
     * 继续播放
     * 主要是切换歌曲的时候需要及时刷新界面信息，比如播放暂停按钮
     */
    void onPlayerStart();

    /**
     * 暂停播放
     * 主要是切换歌曲的时候需要及时刷新界面信息，比如播放暂停按钮
     */
    void onPlayerPause();

    /**
     * 更新进度
     * 主要是播放音乐或者拖动进度条时，需要更新进度
     */
    void onUpdateProgress(int progress);

    /**
     * 缓冲进度
     */
    void onBufferingUpdate(int percent);

    /**
     * 更新定时停止播放时间
     */
    void onTimer(long remain);

    /**
     * 播放错误的回调
     */
    void onError(MediaPlayer mp, int what, int extra);
}
