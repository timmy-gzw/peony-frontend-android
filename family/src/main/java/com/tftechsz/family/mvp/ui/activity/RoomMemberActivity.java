package com.tftechsz.family.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.family.R;
import com.tftechsz.family.adapter.RoomMemberAdapter;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.mvp.IView.IRoomMemberView;
import com.tftechsz.family.mvp.presenter.RoomMemberPresenter;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.FamilyMemberEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 聊天室成员
 */
@Route(path = ARouterApi.ACTIVITY_ROOM_MEMBER)
public class RoomMemberActivity extends BaseMvpActivity<IRoomMemberView, RoomMemberPresenter> implements IRoomMemberView {
    private RoomMemberAdapter mAdapter;
    private RecyclerView mRvMember;
    protected SmartRefreshLayout smartRefreshLayout;
    @Autowired
    UserProviderService service;

    @Override
    public RoomMemberPresenter initPresenter() {
        return new RoomMemberPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("聊天室成员")
                .build();
        mRvMember = findViewById(R.id.recycleview);
        mRvMember.setLayoutManager(new LinearLayoutManager(this));
        smartRefreshLayout = findViewById(R.id.smartrefreshlayout);
        mLlEmpty = findViewById(R.id.ll_empty);
        mTvEmpty = findViewById(R.id.tv_empty);
    }


    @Override
    protected void initData() {
        super.initData();
        String mTid = getIntent().getStringExtra("rid");
        int mFrom = getIntent().getIntExtra("from", 0);
        mAdapter = new RoomMemberAdapter(null);
        mRvMember.setAdapter(mAdapter);
        p.getRoomMember(mPage, mTid, true);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mPage = 1;
            p.getRoomMember(mPage, mTid, false);
        });
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mPage += 1;
            p.getRoomMember(mPage, mTid, false);
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                FamilyMemberDto data = mAdapter.getData().get(position);
                if (mFrom == 0) {  //点击更多进入
                    ARouterUtils.toMineDetailActivity(data.user_id == service.getUserId() ? "" : ("" + data.user_id));
                } else if (mFrom == 1) {  //@ 进入
                    RxBus.getDefault().post(new FamilyMemberEvent(data.user_id, data.nickname, data.icon));
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("userId", data.user_id);
                    intent.putExtra("nickName", data.nickname);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_family_member;
    }

    /**
     * 获取成员列表
     *
     * @param data
     */
    @Override
    public void getMemberSuccess(List<FamilyMemberDto> data) {

        final int size = data == null ? 0 : data.size();
        if (mPage == 1) {
            mAdapter.setList(data);
            if (size == 0) {
                mLlEmpty.setVisibility(View.VISIBLE);
            }
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }
}
