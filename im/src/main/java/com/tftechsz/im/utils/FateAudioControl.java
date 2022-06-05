package com.tftechsz.im.utils;

import android.content.Context;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.audio.MessageAudioControl;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.media.audioplayer.BaseAudioControl;
import com.netease.nim.uikit.common.media.audioplayer.Playable;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.tftechsz.im.model.AudioFatePlayable;
import com.tftechsz.im.model.FateInfo;

import java.util.List;

public class FateAudioControl extends BaseAudioControl<FateInfo> {
    private static FateAudioControl mMessageAudioControl = null;

    private boolean mIsNeedPlayNext = false;

    private BaseMultiItemFetchLoadAdapter mAdapter;

    private FateInfo mItem = null;

    private FateAudioControl(Context context) {
        super(context, true);
    }

    public static FateAudioControl getInstance(Context context) {
        if (mMessageAudioControl == null) {
            synchronized (MessageAudioControl.class) {
                if (mMessageAudioControl == null) {
                    mMessageAudioControl = new FateAudioControl(NimUIKit.getContext());
                }
            }
        }

        return mMessageAudioControl;
    }

    @Override
    protected void setOnPlayListener(Playable playingPlayable, AudioControlListener audioControlListener) {
        this.audioControlListener = audioControlListener;

        BasePlayerListener basePlayerListener = new BasePlayerListener(currentAudioPlayer, playingPlayable) {

            @Override
            public void onInterrupt() {
                if (!checkAudioPlayerValid()) {
                    return;
                }

                super.onInterrupt();
                cancelPlayNext();
            }

            @Override
            public void onError(String error) {
                if (!checkAudioPlayerValid()) {
                    return;
                }

                super.onError(error);
                cancelPlayNext();
            }

            @Override
            public void onCompletion() {
                if (!checkAudioPlayerValid()) {
                    return;
                }

                resetAudioController(listenerPlayingPlayable);

                boolean isLoop = false;
                if (mIsNeedPlayNext) {
                    if (mAdapter != null && mItem != null) {
                        isLoop = playNextAudio(mAdapter, mItem);
                    }
                }

                if (!isLoop) {
                    if (audioControlListener != null) {
                        audioControlListener.onEndPlay(currentPlayable);
                    }

                    playSuffix();
                }
            }
        };

        basePlayerListener.setAudioControlListener(audioControlListener);
        currentAudioPlayer.setOnPlayListener(basePlayerListener);
    }

    @Override
    public FateInfo getPlayingAudio() {
        if (isPlayingAudio() && AudioFatePlayable.class.isInstance(currentPlayable)) {
            return ((AudioFatePlayable) currentPlayable).getMessage();
        } else {
            return null;
        }
    }


    //连续播放时不需要resetOrigAudioStreamType
    private void startPlayAudio(
            FateInfo message,
            AudioControlListener audioControlListener,
            int audioStreamType,
            boolean resetOrigAudioStreamType,
            long delayMillis) {
        if (StorageUtil.isExternalStorageExist()) {
            if (startAudio(new AudioFatePlayable(message), audioControlListener, audioStreamType, resetOrigAudioStreamType, delayMillis)) {

            }
        } else {
            ToastHelper.showToast(mContext, com.netease.nim.uikit.R.string.sdcard_not_exist_error);
        }
    }

    private boolean playNextAudio(BaseMultiItemFetchLoadAdapter tAdapter, FateInfo messageItem) {
        final List<?> list = tAdapter.getData();
        int index = 0;
        int nextIndex = -1;
        //找到当前已经播放的
        for (int i = 0; i < list.size(); ++i) {
            FateInfo item = (FateInfo) list.get(i);
            if (item.equals(messageItem)) {
                index = i;
                break;
            }
        }


        if (nextIndex == -1) {
            cancelPlayNext();
            return false;
        }
        FateInfo message = (FateInfo) list.get(nextIndex);
        if (mMessageAudioControl != null ) {
            //不是直接通过点击ViewHolder开始的播放，不设置AudioControlListener
            //notifyDataSetChanged会触发ViewHolder刷新，对应的ViewHolder会把AudioControlListener设置上去
            //连续播放 1.继续使用playingAudioStreamType 2.不需要resetOrigAudioStreamType
            mMessageAudioControl.startPlayAudio(message, null, getCurrentAudioStreamType(), false, 0);
            mItem = (FateInfo) list.get(nextIndex);
            tAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    private void cancelPlayNext() {
        setPlayNext(false, null, null);
    }

    public void setPlayNext(boolean isPlayNext, BaseMultiItemFetchLoadAdapter adapter, FateInfo item) {
        mIsNeedPlayNext = isPlayNext;
        mAdapter = adapter;
        mItem = item;
    }

    public void stopAudio() {
        super.stopAudio();
    }

    @Override
    public void startPlayAudioDelay(long delayMillis, FateInfo fateInfo, AudioControlListener audioControlListener, int audioStreamType) {
        // 如果不存在则下载
//        FateContentInfo audioAttachment = (FateContentInfo) fateInfo.getMsg_content();
//        File file = new File(audioAttachment.getUrl());
//        if (!file.exists()) {
//            NIMClient.getService(MsgService.class).downloadAttachment(message, false).setCallback(new RequestCallbackWrapper() {
//                @Override
//                public void onResult(int code, Object result, Throwable exception) {
                    startPlayAudio(fateInfo, audioControlListener, audioStreamType, true, delayMillis);
//                }
//            });
//            return;
//        }
        startPlayAudio(fateInfo, audioControlListener, audioStreamType, true, delayMillis);

    }
}
