package com.tftechsz.im.uikit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.contact.ContactChangedObserver;
import com.netease.nim.uikit.api.model.main.OnlineStateChangeObserver;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.business.session.constant.Extras;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tftechsz.im.R;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.SoftHideKeyBoardUtil;
import com.tftechsz.common.utils.StatusBarUtil;

import java.util.List;
import java.util.Set;

import static com.tftechsz.common.constant.Interfaces.SCENE_NUMBER;


/**
 * 点对点聊天界面
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
@Route(path = ARouterApi.ACTIVITY_P2P_MESSAGE)
public class P2PMessageActivity extends BaseMessageActivity {

    private LottieAnimationView lottieAnimationView;

    /**
     * 接受到消息
     */
    private Observer<List<IMMessage>> messageReceiverObserver;

    /**
     * 好友资料变更（eg:关系）
     */
    private ContactChangedObserver friendDataChangedObserver;

    /**
     * 好友在线状态观察者
     */
    private OnlineStateChangeObserver onlineStateChangeObserver;

    public static void start(Context context, String contactId, SessionCustomization customization, IMMessage anchor, int height) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, contactId);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        intent.putExtra(Extras.EXTRA_TYPE_DIALOG_ACTIVITY_HEIGHT, height);
        if (anchor != null) {
            intent.putExtra(Extras.EXTRA_ANCHOR, anchor);
        }
        intent.setClass(context, P2PMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.fullScreen(this);
        StatusBarUtil.setLightStatusBar(this, false, true);
        if (!isDialogMenu()) {
            SoftHideKeyBoardUtil.assistActivity(this);
        }
        initReceiverObserver();

        // 单聊特例话数据，包括个人信息，
        requestBuddyInfo();
        displayOnlineState();
        registerObservers(true);
        lottieAnimationView = findView(R.id.animation_view);
        lottieAnimationView.setImageAssetsFolder(Constants.ACCOST_GIFT);//设置data.json引用的图片资源文件夹名称,如果没有可不写

    }

    public boolean isDialogMenu() {
        return false;
    }

    private void initReceiverObserver() {
        //接受到消息了
        messageReceiverObserver = new Observer<List<IMMessage>>() {
            @Override
            public void onEvent(List<IMMessage> imMessages) {
                if (imMessages != null) {
                    try {
                        for (IMMessage imMessage : imMessages) {
//                            IMMessage msg = imMessage;
                            ChatMsg chatMsg = ChatMsgUtil.parseMessage(imMessage);
                            if (chatMsg != null && TextUtils.equals(ChatMsg.ACCOST_TYPE, chatMsg.cmd_type) && TextUtils.equals(ChatMsg.REPLY_ACCOST_TYPE, chatMsg.cmd)) {
                                List<ChatMsg.AccostGift> accostGifts = JSONObject.parseArray(chatMsg.content, ChatMsg.AccostGift.class);
                                if (accostGifts != null && accostGifts.size() > 0) {
                                    for (ChatMsg.AccostGift accostGift : accostGifts) {
                                        CommonUtil.playJsonAnimation(lottieAnimationView, accostGift.animation, 1);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        /**
         * 好友资料变更（eg:关系）
         */
        friendDataChangedObserver = new ContactChangedObserver() {
            @Override
            public void onAddedOrUpdatedFriends(List<String> accounts) {
                setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
            }

            @Override
            public void onDeletedFriends(List<String> accounts) {
                setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
            }

            @Override
            public void onAddUserToBlackList(List<String> account) {
                setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
            }

            @Override
            public void onRemoveUserFromBlackList(List<String> account) {
                setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
            }
        };


        /**
         * 好友在线状态观察者
         */
        onlineStateChangeObserver = new OnlineStateChangeObserver() {
            @Override
            public void onlineStateChange(Set<String> accounts) {
                if (!accounts.contains(sessionId)) {
                    return;
                }
                // 按照交互来展示
                displayOnlineState();
            }
        };
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
            lottieAnimationView.destroyDrawingCache();
        }
        registerObservers(false);
        setOnHideKeyboardListener(null);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MMKVUtils.getInstance().encode(SCENE_NUMBER, 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MMKVUtils.getInstance().encode(SCENE_NUMBER, -1);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void requestBuddyInfo() {
        setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
    }

    private void displayOnlineState() {
        if (!NimUIKitImpl.enableOnlineState()) {
            return;
        }
        String detailContent = NimUIKitImpl.getOnlineStateContentProvider().getDetailDisplay(sessionId);
        setSubTitle(detailContent);
    }


//
//    /**
//     * 用户信息变更观察者
//     */
//    private UserInfoObserver userInfoObserver = new UserInfoObserver() {
//        @Override
//        public void onUserInfoChanged(List<String> accounts) {
//            if (!accounts.contains(sessionId)) {
//                return;
//            }
//            requestBuddyInfo();
//        }
//    };

    private void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(messageReceiverObserver, register);
//        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, register);
        NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register);
        if (NimUIKit.enableOnlineState()) {
            NimUIKit.getOnlineStateChangeObservable().registerOnlineStateChangeListeners(onlineStateChangeObserver, register);
        }

        if (!register) {
            onlineStateChangeObserver = null;
            friendDataChangedObserver = null;
            messageReceiverObserver = null;
        }
    }


    @Override
    protected MessageFragment fragment() {
        Bundle arguments = getIntent().getExtras();
        if (arguments == null) {
            arguments = new Bundle();
        }
        arguments.putSerializable(Extras.EXTRA_TYPE, SessionTypeEnum.P2P);
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(arguments);
        fragment.setContainerId(R.id.message_fragment_container);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.nim_message_activity;
    }

    @Override
    protected void initToolBar() {
//        ToolBarOptions options = new NimToolBarOptions();
//        setToolBar(R.id.toolbar, options);

    }

    @Override
    protected boolean enableSensor() {
        return true;
    }

    public interface OnHideKeyboardListener {
        boolean hideKeyboard(MotionEvent event);
    }

    public void setOnHideKeyboardListener(OnHideKeyboardListener onHideKeyboardListener) {
        this.onHideKeyboardListener = onHideKeyboardListener;
    }

    private OnHideKeyboardListener onHideKeyboardListener;

    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (onHideKeyboardListener != null) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                if (onHideKeyboardListener.hideKeyboard(ev)) {
                    return false;  //不在分发触控给子控件
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
