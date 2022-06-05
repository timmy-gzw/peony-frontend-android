package com.tftechsz.common.nertcvoiceroom.model.impl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.netease.neliveplayer.sdk.NELivePlayer;
import com.netease.neliveplayer.sdk.constant.NEBufferStrategy;
import com.netease.neliveplayer.sdk.model.NEAutoRetryConfig;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tftechsz.common.entity.VoicePlayBean;
import com.tftechsz.common.entity.VoicePlayRegionsBean;
import com.tftechsz.common.nertcvoiceroom.model.AudiencePlay;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by luc on 1/18/21.
 */
class AudiencePlayImpl implements AudiencePlay {
    /**
     * 播放器实例
     */
    private NELivePlayer player;

    private Gson gson;
    /**
     * 内部播放器回调代理
     */
    private final PlayerNotify innerNotify = new PlayerNotify() {
        @Override
        public void onPreparing() {
            if (isReleased()) {
                return;
            }
            if (notify != null) {
                notify.onPreparing();
            }
        }

        @Override
        public void onPlaying() {
            if (isReleased()) {
                return;
            }
            if (notify != null) {
                notify.onPlaying();
            }
        }

        @Override
        public void onError() {
            if (isReleased()) {
                return;
            }

            if (notify != null) {
                notify.onError();
            }
        }

        @Override
        public void getSeatVoice(List<VoicePlayRegionsBean> regions) {
            if (notify != null) {
                notify.getSeatVoice(regions);
            }
        }
    };

    /**
     * 播放器拉流准备完成回调
     */
    private final NELivePlayer.OnPreparedListener preparedListener = new NELivePlayer.OnPreparedListener() {
        @Override
        public void onPrepared(NELivePlayer mp) {
            if (isReleased()) {
                return;
            }
            // 视频开始播放
            if (player != null) {
                player.start();
            }
            LogUtil.e("AudiencePlayImpl", "player is playing. url is " + currentUrl);
            // 播放回调
            innerNotify.onPlaying();
        }
    };

    /**
     * 播放器拉流错误回调
     */
    private final NELivePlayer.OnErrorListener errorListener = new NELivePlayer.OnErrorListener() {
        @Override
        public boolean onError(NELivePlayer mp, int what, int extra) {
            player.release();
            LogUtil.e("AudiencePlayImpl", "errorCode is " + what + ", extra info is " + extra + ", url is " + currentUrl);
            innerNotify.onError();
            return true;
        }
    };
    /**
     * 视频播放回调
     */
    private PlayerNotify notify;
    /**
     * 当前播放的拉流Url
     */
    private String currentUrl;

    /**
     * 是否已经暂停
     */
    private boolean paused = false;

    /**
     * 当前播放音量
     */
    private float currentVolume = 1.0f;

    /**
     * 播放器构造函数
     *
     * @param notify 视频控制回调
     */
    public void registerNotify(PlayerNotify notify) {
        this.notify = notify;
    }

    /**
     * 执行预播放准备动作
     */
    private void doPreparePlayAction() {
        if (player != null) {
            player.stop();
            player.release();
        }
        gson = new Gson();
        player = NELivePlayer.create();
        setVolume(currentVolume);
        // 内部做重试动作；
        NEAutoRetryConfig config = new NEAutoRetryConfig();
        config.count = 1;
        config.delayDefault = 3000;
        config.retryListener = new NEAutoRetryConfig.OnRetryListener() {
            @Override
            public void onRetry(int what, int extra) {
                LogUtil.e("AudiencePlayImpl", "errorCode is " + what + ", extra info is " + extra);
            }
        };
        player.setAutoRetryConfig(config);
        // 回调准备中回调
        innerNotify.onPreparing();
        if (isReleased()) {
            return;
        }
        // 直播缓存策略，速度优先
        player.setBufferStrategy(NEBufferStrategy.NELPFLUENT); //防抖动与卡顿
//        player.setBufferSize(10*1024*1024);//缓冲空间设置
        // 设置相关回调
        player.setOnPreparedListener(preparedListener);
        player.setOnErrorListener(errorListener);
        // 设置拉流地址
        try {
            player.setDataSource(currentUrl);
        } catch (IOException e) {
            e.printStackTrace();
            // 拉流错误直接回到错误
            innerNotify.onError();
        }
        // prepare 阶段
        player.prepareAsync();
        player.setSyncOpen(true);
        player.setOnCurrentSyncContentListener(new NELivePlayer.OnCurrentSyncContentListener() {
            @Override
            public void onCurrentSyncContent(List<String> content) {
                if (content != null && content.size() > 0) {
                    VoicePlayBean voicePlayBean = gson.fromJson(content.get(0), (Type) VoicePlayBean.class);
                    if (voicePlayBean != null && voicePlayBean.regions != null && notify != null)
                        notify.getSeatVoice(voicePlayBean.regions);
                }
            }
        });
    }

    @Override
    public void play(String url) {
        this.currentUrl = url;
        doPreparePlayAction();
    }

    @Override
    public void pause() {
        if (player.isPlaying()) {
            player.pause();
            paused = true;
        }
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void resume() {
        player.start();
        paused = false;
    }

    @Override
    public void setVolume(float volume) {
        if (player == null) return;
        player.setVolume(volume);
        this.currentVolume = volume;
    }

    @Override
    public float getVolume() {
        return this.currentVolume;
    }

    @Override
    public void stop() {
        player.stop();
    }

    /**
     * 播放器资源释放避免内存占用过大
     */
    @Override
    public void release() {
        if (player != null) {
            player.release();
        }
        currentUrl = null;
        paused = false;
        currentVolume = 1.0f;
    }

    /**
     * 当前播放器是否已经资源释放
     */
    @Override
    public boolean isReleased() {
        return TextUtils.isEmpty(currentUrl) || player == null;
    }

}
