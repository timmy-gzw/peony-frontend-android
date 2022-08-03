package com.tftechsz.im.uikit;

import static android.app.Activity.RESULT_OK;
import static com.tftechsz.common.constant.Interfaces.FIY_NUMBER;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.VoiceChatDto;
import com.tftechsz.im.service.RtcShareService;
import com.tftechsz.common.Constants;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.event.VoiceChatEvent;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomInfo;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;

import java.util.List;

import razerdp.util.KeyboardUtils;


/**
 * 群聊
 */
public class TeamMessageFragment extends MessageFragment {
    private CountBackUtils mCountWarm;
    private Team team;
    private boolean isLeave = false;
    private RtcShareServiceConnection mServiceConnection;
    private RtcShareService.RtcShareBinder binder;

    @Override
    public boolean isAllowSendMessage(IMMessage message) {
        if (team == null) {
            team = NimUIKit.getTeamProvider().getTeamById(sessionId);
        }

        if (team == null || !team.isMyTeam()) {
            ToastHelper.showToast(getActivity(), R.string.team_send_message_not_allow);
            return false;
        }
        return super.isAllowSendMessage(message);
    }

    public void setTeam(Team team) {
        this.team = team;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIvVoiceWarm = findView(R.id.iv_voice_close);
        mIvVoiceWarm.setOnClickListener(this);
        mLlVoiceWarm = findView(R.id.ll_voice_warm);
        mTvVoiceWarm = findView(R.id.tv_voice_warm);
        initRxBus();
    }

    @Override
    public void onResume() {
        super.onResume();
        setSessionListener();
        if (team == null) {
            team = NimUIKit.getTeamProvider().getTeamById(sessionId);
        }
        if (team != null) {
            MMKVUtils.getInstance().encode(FIY_NUMBER, team.getId());
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if (team == null) {
            team = NimUIKit.getTeamProvider().getTeamById(sessionId);
        }
        if (team != null) {
            MMKVUtils.getInstance().encode(FIY_NUMBER, "-1");
        }


    }

    @Override
    protected void commandObserverMessage(ChatMsg chatMsg) {
        super.commandObserverMessage(chatMsg);
        if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.VOICE_ROOM_ATTACH)) {

        } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_INTO_NOTICE)) {  //通知贵族进入有svga动画
            ChatMsg.JoinFamily dressUp = JSON.parseObject(chatMsg.content, ChatMsg.JoinFamily.class);
            if (dressUp != null && !TextUtils.isEmpty(dressUp.svg)) {
                GiftDto bean = new GiftDto();
                bean.name = Utils.getFileName(dressUp.svg).replace(".svga", "");
                bean.animation = dressUp.svg;
                myGiftList.offer(bean);
            }
        } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_JOIN_ROOM)) {  //开启了语音房
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.iv_voice_close) {
            mLlVoiceWarm.setVisibility(View.GONE);
        } else if (id == R.id.toolbar_team_back_all) {
            if (getActivity() != null)
                getActivity().finish();
            KeyboardUtils.close(getActivity());
        }

    }

    private void initRxBus() {

    }


    /**
     * 显示退出麦位弹窗
     */
    private void showBackPop() {
        new CustomPopWindow(getActivity()).setContent("退出后将离开麦位，确认要退出吗？").addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure() {
                if (getActivity() != null)
                    getActivity().finish();
                KeyboardUtils.close(getActivity());
            }
        }).showPopupWindow();
    }

    private Intent mShareData;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (data != null) {
                mShareData = data;
                startLoopback(data);
            }
        }
        if (aitManager != null) {
            aitManager.onActivityResult(requestCode, resultCode, data);
        }
        inputPanel.onActivityResult(requestCode, resultCode, data);
        int GROUP_COUPLE_MESSAGE = 10001;
        if (requestCode == GROUP_COUPLE_MESSAGE && RESULT_OK == resultCode) {
            int userId = data.getIntExtra("userId", 0);
            getGroupCouple(String.valueOf(userId));
        }
        messageListPanel.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
    }

    private void changeAudioLoopBack(boolean toOpen) {
        if (!toOpen) {
            stopLoopback();
        } else {
            if (RtcShareService.mediaProjectionIntent == null && getActivity() != null) {
                MediaProjectionManager mediaProjectionManager;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mediaProjectionManager = (MediaProjectionManager) getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                    Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
                    startActivityForResult(permissionIntent, 2);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startLoopback(mShareData);
                }
            }
        }
    }

    private void startLoopback(Intent intent) {
        if (intent == null) {
            Utils.toast("系统声音录制失败,请退出后重试");
            return;
        }
//        NERtcEx.getInstance().adjustLoopbackRecordingSignalVolume(35);
//        int startRes = NERtcEx.getInstance().startLoopBackAudio(intent, new MediaProjection.Callback() {
//            @Override
//            public void onStop() {
//                Utils.toast("录屏已停止");
//                super.onStop();
//            }
//        });
//        if (startRes == NERtcConstants.ErrorCode.OK) {
//            Utils.toast("系统声音录制开");
//        } else {
//            Utils.toast("系统声音录制失败,请退出后重试");
//        }
    }

    private void stopLoopback() {

    }

    private void bindScreenService() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), RtcShareService.class);
        mServiceConnection = new RtcShareServiceConnection();
        if (getActivity() != null)
            getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindScreenService() {
        if (getActivity() != null && mServiceConnection != null) {
            getActivity().unbindService(mServiceConnection);
        }
    }

    private class RtcShareServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            binder = (RtcShareService.RtcShareBinder) service;
            binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
        }
    }


    /**
     * 返回建监听
     */
    @Override
    public void onKeyBack() {
        super.onKeyBack();
        if (getActivity() != null)
            getActivity().finish();
        KeyboardUtils.close(getActivity());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindScreenService();
        if (mCountWarm != null)
            mCountWarm.destroy();
    }
}
