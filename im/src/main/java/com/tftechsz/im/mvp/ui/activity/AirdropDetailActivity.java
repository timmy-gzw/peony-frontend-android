package com.tftechsz.im.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.AirdropDetailAdapter;
import com.tftechsz.im.model.dto.AirdropDetailDto;
import com.tftechsz.im.mvp.iview.IAirdropDetailView;
import com.tftechsz.im.mvp.presenter.AirdropDetailPresenter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

public class AirdropDetailActivity extends BaseMvpActivity<IAirdropDetailView, AirdropDetailPresenter> implements IAirdropDetailView {
    public static final String AIRDROP_ID = "airdrop_id";
    private SVGAImageView svgaImageView;
    private SVGAParser svgaParser;
    private AirdropDetailAdapter mAdapter;
    private ImageView mIvAvatar;
    private TextView mTvName, mTvContent, mTvReceiveDetail;
    @Autowired
    UserProviderService service;


    public static void startActivity(Context context, int airdropId) {
        Intent intent = new Intent(context, AirdropDetailActivity.class);
        intent.putExtra(AIRDROP_ID, airdropId);
        context.startActivity(intent);
    }

    @Override
    public AirdropDetailPresenter initPresenter() {
        return new AirdropDetailPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_airdrop_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("空投详情")
                .build();
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvName = findViewById(R.id.tv_name);
        mTvContent = findViewById(R.id.tv_content);
        mTvReceiveDetail = findViewById(R.id.tv_receive_detail);
        svgaImageView = findViewById(R.id.svg_image);
        RecyclerView mRvAirdropDetail = findViewById(R.id.rv_airdrop_detail);
        mRvAirdropDetail.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AirdropDetailAdapter();
        mRvAirdropDetail.setAdapter(mAdapter);
        View emptyView = View.inflate(mContext, R.layout.base_empty_view, null);
        TextView tvEmpty = emptyView.findViewById(R.id.tv_empty);
        tvEmpty.setText("空投已过期");

        mAdapter.setEmptyView(emptyView);
        mAdapter.setList(null);
        mAdapter.addChildClickViewIds(R.id.iv_avatar);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_avatar) {
                if (service.getUserId() == mAdapter.getData().get(position).user_id) {
                    ARouterUtils.toMineDetailActivity("");
                } else {
                    ARouterUtils.toMineDetailActivity("" + mAdapter.getData().get(position).user_id);
                }
            }
        });
        playAirdrop();
    }

    @Override
    protected void initData() {
        super.initData();
        int mAirdropId = getIntent().getIntExtra(AIRDROP_ID, 0);
        getP().getAirdropDetail(mAirdropId);
    }

    private void playAirdrop() {
        if (null == svgaParser)
            svgaParser = new SVGAParser(this);
        svgaParser.decodeFromAssets("airdrops_receive.svga", new SVGAParser.ParseCompletion() {

            @Override
            public void onError() {

            }

            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                if (svgaImageView != null) {
                    SVGADrawable drawable = new SVGADrawable(videoItem);
                    svgaImageView.setImageDrawable(drawable);
                    svgaImageView.startAnimation();
                }
            }
        },null);
    }

    @Override
    public void getAirdropDetailSuccess(AirdropDetailDto data) {
        mTvName.setText(data.nickname);
        mTvReceiveDetail.setText(data.num_desc + "   " + data.coin_desc);
        GlideUtils.loadRoundImageRadius(this, mIvAvatar, data.icon);
        mAdapter.setList(data.member_list);
        mTvContent.setText(data.desc);


    }
}
