package com.tftechsz.im.uikit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.tftechsz.im.R;

/**
 * 聊天室
 */
public class ChatRoomActivity extends UI {
    private static final String MODE= "mode";
    private static final String APP_KEY = "app_key";
    private static final  String ACCOUNT = "account";
    private static final  String PWD = "pwd";
    private static final  String ROOM_ID = "room_id";
    private static final String TAG = ChatRoomActivity.class.getSimpleName();

    /**
     * 聊天室基本信息
     */
    private String roomId;

    private ChatRoomInfo roomInfo;

    private boolean hasEnterSuccess = false; // 是否已经成功登录聊天室

    /**
     * 子页面
     */
    private ChatRoomMessageFragment messageFragment;

    private AbortableFuture<EnterChatRoomResultData> enterRequest;

    public static void start(Context context, String roomId) {
        Intent intent = new Intent();
        intent.setClass(context, ChatRoomActivity.class);
        intent.putExtra(ROOM_ID, roomId);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nim_activity_chat_room);
        boolean ok = getIntentData();
        if (!ok) {
            finish();
            return;
        }
        // 注册监听
        registerObservers(true);
        // 登录聊天室
        enterRoom();
    }
    private boolean getIntentData() {
        roomId = getIntent().getStringExtra(ROOM_ID);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    @Override
    public void onBackPressed() {
        if (messageFragment == null || !messageFragment.onBackPressed()) {
            super.onBackPressed();
        }
        logoutChatRoom();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (messageFragment != null) {
            messageFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void enterRoom() {
        DialogMaker.showProgressDialog(this, null, "", true, dialog -> {
            if (enterRequest != null) {
                enterRequest.abort();
                onLoginDone();
                finish();
            }
        }).setCanceledOnTouchOutside(false);
        hasEnterSuccess = false;
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoomEx(data, 1);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {

            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                onLoginDone();
                roomInfo = result.getRoomInfo();
                NimUIKit.enterChatRoomSuccess(result, false);
                initMessageFragment();
                hasEnterSuccess = true;
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    ToastHelper.showToast(ChatRoomActivity.this, "你已被拉入黑名单，不能再进入");
                } else if (code == ResponseCode.RES_ENONEXIST) {
                    ToastHelper.showToast(ChatRoomActivity.this, "聊天室不存在");
                } else {
                    ToastHelper.showToast(ChatRoomActivity.this,
                                          "enter chat room failed, code=" + code);
                }
                finish();
            }

            @Override
            public void onException(Throwable exception) {
                onLoginDone();
                ToastHelper.showToast(ChatRoomActivity.this,
                                      "enter chat room exception, e=" + exception.getMessage());
                finish();
            }
        });
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver,
                                                                                register);
    }

    private void logoutChatRoom() {
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        onExitedChatRoom();
    }

    public void onExitedChatRoom() {
        NimUIKit.exitedChatRoom(roomId);
        finish();
    }



    Observer<ChatRoomKickOutEvent> kickOutObserver = (Observer<ChatRoomKickOutEvent>) chatRoomKickOutEvent -> {
        ToastHelper.showToast(ChatRoomActivity.this,
                              "被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason());
        onExitedChatRoom();
    };


    private void initMessageFragment() {
        messageFragment = (ChatRoomMessageFragment) getSupportFragmentManager().findFragmentById(
                R.id.chat_rooms_fragment);
        if (messageFragment != null) {
            messageFragment.init(roomId);
        } else {
            // 如果Fragment还未Create完成，延迟初始化
            getHandler().postDelayed(() -> initMessageFragment(), 50);
        }
    }

    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    public ChatRoomInfo getRoomInfo() {
        return roomInfo;
    }
}
