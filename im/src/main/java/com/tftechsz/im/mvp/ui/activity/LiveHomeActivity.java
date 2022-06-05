package com.tftechsz.im.mvp.ui.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.LiveHomeAdapter;
import com.tftechsz.im.model.dto.LiveHomeListDto;
import com.tftechsz.im.mvp.iview.ILiveHomeView;
import com.tftechsz.im.mvp.presenter.LiveHomePresenter;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.entity.LiveTokenDto;
import com.tftechsz.common.nim.model.NERTCVideoCall;
import com.tftechsz.common.nim.model.impl.NERTCVideoCallImpl;
import com.tftechsz.common.utils.MMKVUtils;

import java.util.List;
@Route(path = ARouterApi.ACTIVITY_LIVE_HOME)
public class LiveHomeActivity extends BaseMvpActivity<ILiveHomeView, LiveHomePresenter> implements ILiveHomeView{

    private RecyclerView mRvLiveHome;
    LiveHomeAdapter mAdapter;
    @Override
    public LiveHomePresenter initPresenter() {
        return new LiveHomePresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_live_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("直播")
                .build();
        mRvLiveHome = findViewById(R.id.rv_live_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvLiveHome.setLayoutManager(layoutManager);
        mAdapter = new LiveHomeAdapter();
        mRvLiveHome.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            getP().getLiveHomeToken(position,mAdapter.getData().get(position).channel_name);

        });
        getP().getLiveHome();

    }

    @Override
    public void getLiveHomeSuccess(List<LiveHomeListDto> data) {
        mAdapter.setList(data);
    }

    @Override
    public void getLiveHomeTokenSuccess(int position,String roomName, String token) {
        LiveTokenDto data = new LiveTokenDto();
        data.room_token = token;
        data.channel_name = roomName;
        MMKVUtils.getInstance().encode("room_token",token);
        MMKVUtils.getInstance().encode("room_name",roomName);
        NERTCVideoCall nertcVideoCall = NERTCVideoCallImpl.sharedInstance();
        nertcVideoCall.setTokenService((uid, callback) -> callback.onSuccess(data));
        getP().sendNotification(mAdapter.getData().get(position).anchor_info.user_id+"",mAdapter.getData().get(position).tid);
    }
}
