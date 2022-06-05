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
        //语音闲聊
        mVoiceChat = findView(R.id.voice_chat);
        mIvVoiceWarm = findView(R.id.iv_voice_close);
        mIvVoiceWarm.setOnClickListener(this);
        mLlVoiceWarm = findView(R.id.ll_voice_warm);
        mTvVoiceWarm = findView(R.id.tv_voice_warm);
        mVoiceChat.addOnClickListener((userId, isAdmin, status) -> {
            showGiftPop(userId, mTeamType == 0 ? 3 : 4);   //聊天室点击的时候传4  群主点击传3
            if (giftPopWindow != null && isAdmin == 1)
                giftPopWindow.setVoiceChat(status);
        });
        initRxBus();
    }

    @Override
    public void onResume() {
        super.onResume();
        setSessionListener();
        if (mVoiceChat != null) {
            mVoiceChat.setVoiceMode(2);
        }
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
        if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.VOICE_ROOM_ATTACH)) {  //切换了语音聊天模式
            if (sessionType == SessionTypeEnum.Team && mTeamType == 0 && mVoiceChat != null) {
                switch (chatMsg.cmd) {
                    case "apply_num":  //用户申请上麦
                        postRunnable(() -> {
                            ChatMsg.ApplyMessage applyMessage = JSON.parseObject(chatMsg.content, ChatMsg.ApplyMessage.class);
                            if (applyMessage != null) {
                                mVoiceChat.setApplyNum(applyMessage.num);
                            }
                        });
                        break;
                    case "open":  //开启
                        mIsOpenRoom = 1;
                        postRunnable(() -> {
                            mVoiceChat.setVisibility(View.VISIBLE);
                            VoiceChatDto voiceChatDto = JSON.parseObject(chatMsg.content, VoiceChatDto.class);
                            mVoiceChat.setVoiceUser(voiceChatDto, false);
                            int size = moreAdapter.getData().size();
                            for (int i = 0; i < size; i++) {
                                if (TextUtils.equals(moreAdapter.getData().get(i).content, getString(R.string.chat_voice_chat))) {
                                    moreAdapter.getData().get(i).content = getString(R.string.chat_voice_chat_text);
                                    moreAdapter.getData().get(i).bg = R.mipmap.chat_ic_voice_chat_text;
                                    moreAdapter.setData(i, moreAdapter.getData().get(i));
                                    moreAdapter.notifyItemChanged(i);
                                }
                            }
                        });
                        if (getActivity() != null)
                            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        MMKVUtils.getInstance().encode(Constants.VOICE_IS_OPEN, 1);
                        break;
                    case "close":  //关闭
                        mIsOpenRoom = 0;
                        postRunnable(() -> {
                            mVoiceChat.setVisibility(View.GONE);
                            mVoiceChat.setApplyNum(0);
                            int size1 = moreAdapter.getData().size();
                            for (int i = 0; i < size1; i++) {
                                if (TextUtils.equals(moreAdapter.getData().get(i).content, getString(com.tftechsz.im.R.string.chat_voice_chat_text))) {
                                    moreAdapter.getData().get(i).content = getString(com.tftechsz.im.R.string.chat_voice_chat);
                                    moreAdapter.getData().get(i).bg = R.drawable.chat_ic_voice_chat;
                                    moreAdapter.setData(i, moreAdapter.getData().get(i));
                                    moreAdapter.notifyItemChanged(i);
                                }
                            }
                            mVoiceChat.openVoice();
                            mVoiceChat.resetAudioVoice();
                            mVoiceChat.releaseAudience();
                            mVoiceChat.leaveChannel(false);
                        });
                        if (getActivity() != null)
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        MMKVUtils.getInstance().encode(Constants.VOICE_IS_OPEN, 0);
                        break;
                    case "up":  //用户申请上麦
                        postRunnable(() -> {
                            VoiceRoomInfo voiceRoomInfo = JSON.parseObject(chatMsg.content, VoiceRoomInfo.class);
                            if (voiceRoomInfo.isIs_first()) {   // 第一个用户创建房间，其他用户加入房间
                                mVoiceChat.setVisibility(View.VISIBLE);
                                mVoiceChat.initData(voiceRoomInfo);
                            } else {
                                mVoiceChat.onSeat(voiceRoomInfo);
                            }
                            mVoiceChat.resetAudioVoice();
                            isLeave = false;
//                            if (null != service.getConfigInfo() && null != service.getConfigInfo().sys) { //其中有一个显示了
//                                if (service.getConfigInfo().sys.is_open_family_room_music == 1) {   //0-全关 1-主播开启 2-全员开启
//                                    if (voiceRoomInfo.index == 0) {
//                                        bindScreenService();
//                                        if (getActivity() != null) {
//                                            changeAudioLoopBack(true);
//                                        }
//                                    }
//                                } else if (service.getConfigInfo().sys.is_open_family_room_music == 2) {
//                                    bindScreenService();
//                                    if (getActivity() != null) {
//                                        changeAudioLoopBack(true);
//                                    }
//                                }
//                            }
//
                        });
                        break;
                    case "down":  //下麦
                        if (isLeave) return;
                        mVoiceChat.noticeDown();
                        mVoiceChat.resetAudioVoice();
                        break;
                    case "info":  //信息
                        postRunnable(() -> {
                            VoiceChatDto voiceChatDto1 = JSON.parseObject(chatMsg.content, VoiceChatDto.class);
                            mVoiceChat.setVoiceUser(voiceChatDto1, false);
                            if (NimUIKit.getTeamProvider().getTeamById(sessionId) != null)
                                mVoiceChat.setTid(NimUIKit.getTeamProvider().getTeamById(sessionId).getId());
                        });
                        break;
                    case "name":  //语音房名称
                        ChatMsg.RoomInfo voiceChatDto2 = JSON.parseObject(chatMsg.content, ChatMsg.RoomInfo.class);
                        mVoiceChat.setName(voiceChatDto2.name);
                        break;
                    case "microphone":  //声音控制
                        ChatMsg.RoomSeat status2 = JSON.parseObject(chatMsg.content, ChatMsg.RoomSeat.class);
                        if (status2 != null)
                            mVoiceChat.changeVoiceStatus(status2.user_id, status2.status);
                        break;
                    case "announcement":  //公告
                        Utils.runOnUiThreadDelayed(() -> {
                            ChatMsg.RoomInfo voiceChatDto3 = JSON.parseObject(chatMsg.content, ChatMsg.RoomInfo.class);
                            mVoiceChat.setAnnouncement(voiceChatDto3.announcement, voiceChatDto3);
                        }, 1000);
                        break;
                    case "cost":  //心动值更新
                        postRunnable(() -> {
                            List<ChatMsg.RoomSeat> status = JSON.parseArray(chatMsg.content, ChatMsg.RoomSeat.class);
                            if (status != null && status.size() > 0)
                                mVoiceChat.changeCost(status);
                        });
                        break;
                    case "warm":  //违规提示
                        ChatMsg.VideoStyle status1 = JSON.parseObject(chatMsg.content, ChatMsg.VideoStyle.class);
                        if (status1 != null) {
                            mTvVoiceWarm.setText(status1.msg);
                            mLlVoiceWarm.setVisibility(View.VISIBLE);
                            if (mCountWarm == null)
                                mCountWarm = new CountBackUtils();
                            mCountWarm.countBack(status1.second, new CountBackUtils.Callback() {
                                @Override
                                public void countBacking(long time) {
                                }

                                @Override
                                public void finish() {
                                    mLlVoiceWarm.setVisibility(View.GONE);
                                }
                            });
                        }
                        break;
                    case "warm_close":  //违规提示关闭
                        mLlVoiceWarm.setVisibility(View.GONE);
                        break;
                    case "role_change":  //角色改变
                        mVoiceChat.roleCheck(0, 0);
                        break;
                }
            }
        } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_INTO_NOTICE)) {  //通知贵族进入有svga动画
            ChatMsg.JoinFamily dressUp = JSON.parseObject(chatMsg.content, ChatMsg.JoinFamily.class);
            if (dressUp != null && !TextUtils.isEmpty(dressUp.svg)) {
                GiftDto bean = new GiftDto();
                bean.name = Utils.getFileName(dressUp.svg).replace(".svga", "");
                bean.animation = dressUp.svg;
                myGiftList.offer(bean);
            }
        } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_JOIN_ROOM)) {  //开启了语音房
            getRoomInfo();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.iv_voice_close) {
            mLlVoiceWarm.setVisibility(View.GONE);
        } else if (id == R.id.toolbar_team_back_all) {
            if (mVoiceChat != null && mIsOpenRoom == 1 && mVoiceChat.isOnSeat()) {
                showBackPop();
                return;
            }
            if (getActivity() != null)
                getActivity().finish();
            KeyboardUtils.close(getActivity());
            if (mVoiceChat != null)
                mVoiceChat.releaseAudience();
        }

    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(VoiceChatEvent.class)
                .subscribe(
                        type -> {
                            if (null != type && type.type == Constants.NOTIFY_EXIT_VOICE_ROOM && mVoiceChat != null) {
                                isLeave = true;
                                mVoiceChat.leaveChannel(mVoiceChat.isOnSeat());
                                mVoiceChat.releaseAudience();
                                mVoiceChat.setVoiceMode(1);
                                mVoiceChat.down(false);
                            }
                        }
                ));
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
                mVoiceChat.leaveChannel(true);
                mVoiceChat.down(false);
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
        if (mVoiceChat != null && mIsOpenRoom == 1 && mVoiceChat.isOnSeat()) {
            showBackPop();
            return;
        }
        if (getActivity() != null)
            getActivity().finish();
        KeyboardUtils.close(getActivity());
        if (mVoiceChat != null)
            mVoiceChat.releaseAudience();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVoiceChat != null) {
            mVoiceChat.releaseAudience();
            mVoiceChat.removeCallBack();
            mVoiceChat.onDestroy();
        }
        unbindScreenService();
        if (mCountWarm != null)
            mCountWarm.destroy();
        if (mVoiceChat != null) {
//            mVoiceChat.listen(false);
            mVoiceChat.removeAllViews();
            mVoiceChat = null;
        }
    }
}
