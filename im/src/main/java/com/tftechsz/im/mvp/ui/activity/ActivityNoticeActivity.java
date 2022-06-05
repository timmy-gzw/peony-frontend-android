package com.tftechsz.im.mvp.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.NoticeAdapter;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;

import java.util.List;

/**
 * 活动通知
 */
@Route(path = ARouterApi.ACTIVITY_NOTICE)
public class ActivityNoticeActivity extends BaseMvpActivity {


    private RecyclerView mRecyclerview;
    private String mChatUser;
    private QueryDirectionEnum direction = null;
    private View emptyView;
    private IMMessage anchor;

    @Override
    protected int getLayout() {
        return R.layout.activity_notice;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        SmartRefreshLayout smartRefreshLayout = findViewById(R.id.smartrefreshlayout);
        mRecyclerview = findViewById(R.id.recycleview);
        emptyView = findViewById(R.id.empty_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(layoutManager);
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setEnableLoadMore(false);
    }


    @Override
    protected void initData() {
        super.initData();
        mChatUser = getIntent().getStringExtra("chat_user");
        new ToolBarBuilder().setTitle(TextUtils.equals(mChatUser, Constants.ACTIVITY_NOTICE) ? "活动通知" : "客服小秘书")
                .showBack(true)
                .build();
        anchor = MessageBuilder.createEmptyMessage(mChatUser, SessionTypeEnum.P2P, 0);
        loadFromLocal(QueryDirectionEnum.QUERY_OLD);
    }


    private void loadFromLocal(QueryDirectionEnum direction) {
        this.direction = direction;
        int loadMsgCount = 100;
        NIMClient.getService(MsgService.class)
                .queryMessageListEx(anchor, direction, loadMsgCount, true)
                .setCallback(callback);
    }


    private final RequestCallback<List<IMMessage>> callback = new RequestCallbackWrapper<List<IMMessage>>() {
        @Override
        public void onResult(int code, List<IMMessage> messages, Throwable exception) {
            if (code != ResponseCode.RES_SUCCESS || exception != null) {
                if (direction == QueryDirectionEnum.QUERY_OLD) {

                } else if (direction == QueryDirectionEnum.QUERY_NEW) {

                }
                return;
            }
            if (messages != null) {
                int size = messages.size();
                for (int i = size - 1; i >= 0; i--) {
                    if (TextUtils.isEmpty(messages.get(i).getContent())) {
                        messages.remove(i);
                    }
                }
                onMessageLoaded(messages);
            }
        }
    };


    private void onMessageLoaded(final List<IMMessage> messages) {
        if (messages == null) {
            return;
        }
        NoticeAdapter mAdapter = new NoticeAdapter(messages, mChatUser);
        mRecyclerview.setAdapter(mAdapter);
        if (messages.size() <= 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerview.scrollToPosition(mAdapter.getData().size() - 1);
        }
        NIMClient.getService(MsgService.class).clearUnreadCount(mChatUser, SessionTypeEnum.P2P);
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }
}
