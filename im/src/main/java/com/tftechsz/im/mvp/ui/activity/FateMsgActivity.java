package com.tftechsz.im.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.makeramen.roundedimageview.RoundedImageView;
import com.netease.nim.uikit.common.ui.recyclerview.loadmore.MsgListFetchLoadMoreView;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.FateAdapter;
import com.tftechsz.im.model.FateInfo;
import com.tftechsz.im.model.dto.PullWiresDto;
import com.tftechsz.im.mvp.iview.FateMsgView;
import com.tftechsz.im.mvp.presenter.FateMsgPresenter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;


public class FateMsgActivity extends BaseMvpActivity <FateMsgView, FateMsgPresenter> implements  FateMsgView{


    private RoundedImageView mIvAvatar;

    private LinearLayoutManager linearLayoutManager;

    private RecyclerView messageListView;
    private List<FateInfo> items;
    private FateAdapter adapter;

    @Override
    protected int getLayout() {
        return  R.layout.fate_msg_activity_layout;
    }

    public RecyclerView getMessageListView(){
        return messageListView;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        PullWiresDto pullWiresDto = (PullWiresDto) getIntent().getSerializableExtra("PullWiresDto");
        if(pullWiresDto == null){
            finish();
            return;
        }
        mIvAvatar = findViewById(R.id.toolbar_iv_img);

        new ToolBarBuilder().showBack(true)
                .setTitle(pullWiresDto.to_user_nickname)
                .build();

        findViewById(R.id.toolbar_back_all).setOnClickListener((View view) ->{
            finish();
        });

        messageListView = findViewById(R.id.fate_msg_recyerview);
        linearLayoutManager = new LinearLayoutManager(this);
        ((SimpleItemAnimator) messageListView.getItemAnimator()).setSupportsChangeAnimations(false);
        messageListView.setItemAnimator(null);
        messageListView.setLayoutManager(linearLayoutManager);
        messageListView.requestDisallowInterceptTouchEvent(true);
        messageListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // adapter
        items = new ArrayList<>();
        adapter = new FateAdapter(messageListView,linearLayoutManager, items, null);
        adapter.setFetchMoreView(new MsgListFetchLoadMoreView());
        adapter.setLoadMoreView(new MsgListFetchLoadMoreView());
        messageListView.setAdapter(adapter);

        getP().getDetailFateMsg(pullWiresDto.to_user_id);

        GlideUtils.loadRouteImage(this, mIvAvatar, pullWiresDto.to_user_icon, R.mipmap.mine_ic_girl_default);
    }





    @Override
    public FateMsgPresenter initPresenter() {
        return new FateMsgPresenter();
    }

    @Override
    public void getFateMsgList(List<FateInfo> fateInfoList) {
        adapter.appendData(fateInfoList);
        adapter.updateShowTimeItem(fateInfoList, false, true);
    }
}
