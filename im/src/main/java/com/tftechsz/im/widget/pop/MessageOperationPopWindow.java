package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.impl.cache.StickTopCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.StickTopSessionInfo;
import com.tftechsz.im.R;
import com.tftechsz.im.model.ContactInfo;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.widget.pop.BaseCenterPop;

/**
 *  消息操作弹窗
 */
public class MessageOperationPopWindow extends BaseCenterPop implements View.OnClickListener {

    private ContactInfo recent;
    private MsgService msgService;
    private String sessionId;
    private SessionTypeEnum sessionType;
    private boolean mFlagIsShowBtn;

    public MessageOperationPopWindow(Context context, ContactInfo recent) {
        super(context);
        this.recent = recent;
        initUI();
    }

    public MessageOperationPopWindow(Context context, ContactInfo recent, boolean flag) {
        super(context);
        mFlagIsShowBtn = flag;
        this.recent = recent;
        initUI();
    }


    private void initUI() {
        if (mFlagIsShowBtn) {
            findViewById(R.id.tv_title).setVisibility(View.GONE);
            findViewById(R.id.view).setVisibility(View.GONE);
        }
        findViewById(R.id.tv_del).setOnClickListener(this);
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setOnClickListener(this);
        msgService = NIMClient.getService(MsgService.class);
        sessionId = recent == null ? null : recent.getContactId();
        sessionType = recent == null ? null : recent.getSessionType();
        tvTitle.setText(StickTopCache.isStickTop(sessionId, sessionType) ? "取消置顶" : "置顶");
        tvName.setText(UserInfoHelper.getUserTitleName(recent.getContactId(), recent.getSessionType()));
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_message_operation);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_title) {
            if (StickTopCache.isStickTop(sessionId, sessionType)) {
                msgService.removeStickTopSession(sessionId, sessionType, "").setCallback(new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int code, Void result, Throwable exception) {
                        if (ResponseCode.RES_SUCCESS == code) {
                            if (listener != null) {
                                StickTopCache.recordStickTop(sessionId, sessionType, false);
                                listener.operationType(0);
                            }
                            dismiss();
                        }
                    }
                });
            } else {
                msgService.addStickTopSession(sessionId, sessionType, "").setCallback(new RequestCallbackWrapper<StickTopSessionInfo>() {
                    @Override
                    public void onResult(int code, StickTopSessionInfo result, Throwable exception) {
                        if (ResponseCode.RES_SUCCESS == code) {
                            if (listener != null) {
                                StickTopCache.recordStickTop(result, true);
                                listener.operationType(0);
                            }
                            dismiss();
                        } else if (419 == code) {
                            ToastUtil.showToast(BaseApplication.getInstance(), "置顶人数超过限制");
                            dismiss();
                        }
                    }
                });
            }
        } else if (id == R.id.tv_del) {   //删除
            // 删除会话，删除后，消息历史被一起删除
            msgService.deleteRecentContact(recent);
            msgService.clearChattingHistory(sessionId, sessionType);
            if (listener != null)
                listener.operationType(1);
            dismiss();
        }
    }

    public interface OnSelectListener {
        void operationType(int type);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
