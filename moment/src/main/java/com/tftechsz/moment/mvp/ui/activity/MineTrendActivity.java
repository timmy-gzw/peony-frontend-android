package com.tftechsz.moment.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.moment.R;
import com.tftechsz.moment.mvp.ui.fragment.CustomTrendFragment;
import com.tftechsz.moment.widget.SendTrendPop;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的动态
 */
@Route(path = ARouterApi.ACTIVITY_MINE_TREND)
public class MineTrendActivity extends BaseMvpActivity implements CustomTrendFragment.SrcollowInterface, View.OnClickListener {

    private TextView mTvTitle;
    private ImageView mIvPublish;
    private CustomPopWindow mDelPop;
    @Autowired
    UserProviderService service;
    private CustomPopWindow mTipsPopWindow;
    private LinearLayoutManager mLinearLayoutManager;
    private CustomTrendFragment customTrendFragment;
    private SendTrendPop mPop;

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mIvPublish = findViewById(R.id.iv_publish);
        mIvPublish.setOnClickListener(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mTvTitle = findViewById(R.id.toolbar_title);
        setData();
      /*  TextView tvSend = mNotDataView.findViewById(R.id.tv_send);
        TextView tvEmpty = mNotDataView.findViewById(R.id.tv_empty);
        tvEmpty.setText("快发布动态吧 别人都等急了");
        tvSend.setVisibility(View.VISIBLE);
        tvSend.setOnClickListener(this);*/
        /*mRvTrend.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { //滚动停止
                    autoPlayVideo(recyclerView);
                }
            }

            private void autoPlayVideo(RecyclerView view) {
                nextForTag = 0;
                if (view == null) return;
                int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                for (int i = firstVisibleItemPosition; i < lastVisibleItemPosition + 1; i++) {
                    View itemView = view.getChildAt(nextForTag);
                    if (itemView == null) return;
                    Rect rect = new Rect();
                    RecyclerView tvContent = itemView.findViewById(R.id.rv_trend_image);
                    tvContent.getLocalVisibleRect(rect);
                    Utils.logE("页面可见:" + firstVisibleItemPosition
                            + " - " + lastVisibleItemPosition
                            + " , 当前pos:" + i
                            + " -->{" + rect.top
                            + " ," + rect.bottom
                            + " ," + rect.left
                            + " ," + rect.right + "}"
                    );
                    int height = tvContent.getHeight();
                    if (mAdapter.getItem(i).getType() == 1 && rect.top == 0 && rect.bottom == height) {
                        Utils.logE("自动第" + i + "个播放:" + Objects.requireNonNull(mAdapter.getItem(i)).getContent());
                        break;
                    } else {
                        if (nextForTag < lastVisibleItemPosition) {
                            nextForTag++;
                        }
                    }
                }
            }
        });*/
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_mine_trend;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.iv_publish || id == R.id.tv_send) {
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
            if (mPop == null) {
                mPop = new SendTrendPop(mActivity, true);
            }
            mPop.showPopupWindow(mIvPublish);
            //ChoosePicUtils.picMultiple(this, Interfaces.PIC_SELCTED_NUM, PictureConfig.CHOOSE_REQUEST, null, true);
        }
    }

    private void setData() {
        String userId = getIntent().getStringExtra("id");
        //用户名称
        String mUserName = getIntent().getStringExtra("name");
        LottieAnimationView lottie = findViewById(R.id.animation_view);
        lottie.setImageAssetsFolder(Constants.ACCOST_GIFT);//设置data.json引用的图片资源文件夹名称
        mIvPublish.setVisibility(TextUtils.isEmpty(userId) ? View.VISIBLE : View.GONE);

        if (TextUtils.isEmpty(userId)) {
            mIvPublish.setVisibility(View.VISIBLE);
            mTvTitle.setText("我的动态");
            customTrendFragment = CustomTrendFragment.newInstance(0, -1, this);
        } else {
            mTvTitle.setText(String.format("%s的动态", mUserName));
            mIvPublish.setVisibility(View.GONE);
            customTrendFragment = CustomTrendFragment.newInstance(0, Integer.parseInt(userId), this);
        }
        getSupportFragmentManager()    //
                .beginTransaction()
                .add(R.id.fragment_container, customTrendFragment)   // 此处的R.id.fragment_container是要盛放fragment的父容器
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList.size() > 0) {
                ArrayList<LocalMedia> list = new ArrayList<>(selectList);
                Intent intent = new Intent(mContext, SendTrendActivity.class);
                intent.putParcelableArrayListExtra(Interfaces.EXTRA_TREND, list);
                startActivity(intent);
            }
        }
    }

    @Override
    public void showImage() {

    }

    @Override
    public void hideImage() {

    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }
}
