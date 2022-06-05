package com.tftechsz.im.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.business.session.helper.MessageListPanelHelper;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.imageview.AvatarVipFrameView;
import com.netease.nim.uikit.impl.cache.StickTopCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.StickTopSessionInfo;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.tftechsz.im.R;
import com.tftechsz.im.mvp.iview.IChatSettingView;
import com.tftechsz.im.mvp.presenter.ChatSettingPresenter;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.ChatMessageEvent;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.widget.CommonItemView;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.ReportPopWindow;

import androidx.appcompat.widget.SwitchCompat;

public class ChatSettingActivity extends BaseMvpActivity<IChatSettingView, ChatSettingPresenter> implements View.OnClickListener, IChatSettingView {
    private String sessionId;
    private SessionTypeEnum sessionType;
    private MsgService msgService;
    private SwitchCompat mSwTop;
    private AvatarVipFrameView mIvAvatar;
    private TextView mTvName;
    private CommonItemView mCIBlack;
    private TextView mTvAttention;

    @Override
    public ChatSettingPresenter initPresenter() {
        return new ChatSettingPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("聊天设置")
                .build();
        findViewById(R.id.tv_clear_message).setOnClickListener(this);   //清空消息
        findViewById(R.id.tv_bg_setting).setOnClickListener(this);   //清空消息
        mCIBlack = findViewById(R.id.item_black);
        mCIBlack.setOnClickListener(this);   //拉黑
        findViewById(R.id.tv_report).setOnClickListener(this);   //举报
        mSwTop = findViewById(R.id.switch_top);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvName = findViewById(R.id.tv_name);
        mTvAttention = findViewById(R.id.tv_attention);
        mTvAttention.setOnClickListener(this);
        findViewById(R.id.rl_avatar).setOnClickListener(this);


    }

    @Override
    protected int getLayout() {
        return R.layout.chat_activity_chat_setting;
    }


    @Override
    protected void initData() {
        super.initData();
        sessionId = getIntent().getStringExtra("sessionId");
        sessionType = (SessionTypeEnum) getIntent().getSerializableExtra("sessionType");
        msgService = NIMClient.getService(MsgService.class);
        mSwTop.setChecked(msgService.isStickTopSession(sessionId, sessionType));  //是否置顶
        mIvAvatar.setAvatar(sessionId);
        mTvName.setText(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
        NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(sessionId);
        if (userInfo != null) {
            CommonUtil.setVipInfo(userInfo, sessionId, mTvName, mIvAvatar);
        }
        mCIBlack.getTvLeft().setText(UserInfoHelper.isInBlackList(sessionId) ? "取消拉黑" : "拉黑");
        mCIBlack.getMySwitch().setChecked(UserInfoHelper.isInBlackList(sessionId));
        mSwTop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                msgService.addStickTopSession(sessionId, sessionType, "").setCallback(new RequestCallbackWrapper<StickTopSessionInfo>() {
                    @Override
                    public void onResult(int code, StickTopSessionInfo result, Throwable exception) {
                        if (ResponseCode.RES_SUCCESS == code) {
                            StickTopCache.recordStickTop(result, true);
                            RxBus.getDefault().post(new ChatMessageEvent(Constants.EVENT_CHAT_MESSAGE_TOP));
                        }
                    }
                });
            } else {
                msgService.removeStickTopSession(sessionId, sessionType, "").setCallback(new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int code, Void result, Throwable exception) {
                        if (ResponseCode.RES_SUCCESS == code) {
                            StickTopCache.recordStickTop(sessionId, sessionType, false);
                            RxBus.getDefault().post(new ChatMessageEvent(Constants.EVENT_CHAT_MESSAGE_TOP));
                        }
                    }
                });
            }
        });

        if (!TextUtils.isEmpty(sessionId))
            p.getIsAttention(Integer.parseInt(sessionId));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (TextUtils.isEmpty(sessionId))
            return;
        if (id == R.id.tv_clear_message) {   //清空消息记录
            CustomPopWindow pop = new CustomPopWindow(this);
            pop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
                @Override
                public void onCancel() {
                }

                @Override
                public void onSure() {
                    msgService.clearChattingHistory(sessionId, sessionType);
                    ToastUtil.showToast(ChatSettingActivity.this, "成功清空消息记录");
                    MessageListPanelHelper.getInstance().notifyClearMessages(sessionId);
                }
            });

            pop.setContent("确认删除和" + UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P) + "的聊天记录吗");
            pop.showPopupWindow();
        } else if (id == R.id.item_black) {   //拉黑用户
            if (!UserInfoHelper.isInBlackList(sessionId)) {
                getP().showBlackPop(this, Integer.parseInt(sessionId));
            } else {
                getP().cancelBlack(Integer.parseInt(sessionId));
            }
        } else if (id == R.id.tv_attention) {  //关注取消关注
            getP().attentionUser(Integer.parseInt(sessionId));
        } else if (id == R.id.rl_avatar) {   //跳转个人页面
            ARouterUtils.toMineDetailActivity(sessionId);
        } else if (id == R.id.tv_report) {   //举报
            ReportPopWindow pop = new ReportPopWindow(ChatSettingActivity.this, Integer.parseInt(sessionId), 1);
            pop.showPopupWindow();
        } else if (id == R.id.tv_bg_setting) { //个性化设置
            Intent intent = new Intent(mContext, BgSettingActivity.class);
            intent.putExtra(Interfaces.EXTRA_UID, sessionId);
            startActivity(intent);
        }

    }


    /**
     * 设置关注状态
     *
     * @param isAttention
     */
    private void setAttention(boolean isAttention) {
        if (isAttention) {
            mTvAttention.setText("取消关注");
            mTvAttention.setBackgroundResource(R.drawable.bg_gray_shape);
        } else {
            mTvAttention.setText("关注");
            mTvAttention.setBackgroundResource(R.drawable.bg_orange_radius100);
        }
    }


    /**
     * 查询是否关注成功
     */
    @Override
    public void getIsAttentionSuccess(boolean isAttention) {
        setAttention(isAttention);
    }

    @Override
    public void attentionSuccess(boolean isAttention) {
        setAttention(isAttention);
        if (isAttention)
            toastTip("关注成功");
    }

    @Override
    public void blackSuccess(boolean isBlack) {
        if (isBlack) {
            mCIBlack.getMySwitch().setChecked(true);
            mCIBlack.getTvLeft().setText("取消拉黑");
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_BLACK_SUCCESS));
            finish();
        }

    }

    @Override
    public void cancelBlackSuccess(boolean isBlack) {
        if (isBlack) {
            mCIBlack.getMySwitch().setChecked(false);
            mCIBlack.getTvLeft().setText("拉黑");
        }
    }
}
