package com.tftechsz.common.nertcvoiceroom.model.impl;

import android.graphics.Color;

import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.live.AddLiveTaskCallback;
import com.netease.lava.nertc.sdk.live.DeleteLiveTaskCallback;
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamLayout;
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamTaskInfo;
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamUserTranscoding;
import com.netease.lava.nertc.sdk.live.UpdateLiveTaskCallback;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tftechsz.common.nertcvoiceroom.model.Anchor;
import com.tftechsz.common.nertcvoiceroom.model.StreamTaskControl;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by luc on 1/18/21.
 */
public class StreamTaskControlImpl implements StreamTaskControl {
    private final NERtcEx engine;
    private final Anchor anchor;
    private NERtcLiveStreamTaskInfo streamTaskInfo;

    public StreamTaskControlImpl(Anchor anchor, NERtcEx engine) {
        this.engine = engine;
        this.anchor = anchor;
    }

    @Override
    public void addStreamTask(long uid, final String pushUrl) {
        NERtcLiveStreamTaskInfo streamTaskInfo = new NERtcLiveStreamTaskInfo();
        // 不进行直播录制，请注意与音视频服务端录制区分。
        streamTaskInfo.serverRecordEnabled = false;
        // 纯音频推流
        streamTaskInfo.liveMode = NERtcLiveStreamTaskInfo.NERtcLiveStreamMode.kNERtcLsModeAudio;
        // 推流地址
        streamTaskInfo.url = pushUrl;
        // 推流任务 id 可选字母、数字，下划线，不超过64位
        streamTaskInfo.taskId = pushUrl + System.currentTimeMillis();

        NERtcLiveStreamLayout layout = new NERtcLiveStreamLayout();
        layout.userTranscodingList = new ArrayList<>();
        layout.width = 720;//整体布局宽度
        layout.height = 1280;//整体布局高度
        layout.backgroundColor = Color.parseColor("#00000000"); // 整体背景色
        streamTaskInfo.layout = layout;
        this.streamTaskInfo = streamTaskInfo;

        addMixStreamUser(uid, false);

        engine.addLiveStreamTask(this.streamTaskInfo, new AddLiveTaskCallback() {
            @Override
            public void onAddLiveStreamTask(String taskId, int errorCode) {
                anchor.notifyStreamRestarted();
                LogUtil.e("StreamTaskControlImpl", uid + "onAddLiveStreamTask code is " + errorCode + ", pushUrl is " + pushUrl);
            }
        });
    }

    @Override
    public void removeStreamTask() {
        if (streamTaskInfo == null) {
            return;
        }
        final String url = streamTaskInfo.url;
        engine.removeLiveStreamTask(streamTaskInfo.taskId, new DeleteLiveTaskCallback() {
            @Override
            public void onDeleteLiveStreamTask(String taskId, int errorCode) {
                LogUtil.e("StreamTaskControlImpl", "onDeleteLiveStreamTask code is " + errorCode + ", task url is " + url);
            }
        });
        this.streamTaskInfo = null;
    }

    @Override
    public void addMixStreamUser(long uid) {
        addMixStreamUser(uid, true);
    }

    private void addMixStreamUser(final long uid, boolean update) {
        if (streamTaskInfo == null) {
            return;
        }
        NERtcLiveStreamUserTranscoding userTranscoding = new NERtcLiveStreamUserTranscoding();
        userTranscoding.videoPush = false;
        userTranscoding.audioPush = true;
        userTranscoding.uid = uid;
        streamTaskInfo.layout.userTranscodingList.add(userTranscoding);
        if (update) {
            engine.updateLiveStreamTask(streamTaskInfo, new UpdateLiveTaskCallback() {
                @Override
                public void onUpdateLiveStreamTask(String taskId, int errorCode) {
                    LogUtil.e("====>", "updateLiveStreamTask-add code is " + errorCode + ", uid is " + uid);
                }
            });
        }
    }

    @Override
    public void removeMixStreamUser(final long uid) {
        if (streamTaskInfo == null) {
            return;
        }
        boolean handle = false;
        Iterator<NERtcLiveStreamUserTranscoding> iterator = streamTaskInfo.layout.userTranscodingList.iterator();
        while (iterator.hasNext()) {
            NERtcLiveStreamUserTranscoding userTranscoding = iterator.next();
            if (userTranscoding == null) {
                continue;
            }
            if (userTranscoding.uid == uid) {
                handle = true;
                iterator.remove();
                break;
            }
        }
        if (handle) {
            engine.updateLiveStreamTask(streamTaskInfo, new UpdateLiveTaskCallback() {
                @Override
                public void onUpdateLiveStreamTask(String taskId, int errorCode) {
                    LogUtil.e("StreamTaskControlImpl", "updateLiveStreamTask-remove code is " + errorCode + ", uid is " + uid);
                }
            });
        }
    }
}
