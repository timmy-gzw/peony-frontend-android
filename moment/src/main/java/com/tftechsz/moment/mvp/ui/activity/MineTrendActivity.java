package com.tftechsz.moment.mvp.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.moment.R;
import com.tftechsz.moment.mvp.ui.fragment.CustomTrendFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的动态
 */
@Route(path = ARouterApi.ACTIVITY_MINE_TREND)
public class MineTrendActivity extends BaseMvpActivity implements CustomTrendFragment.SrcollowInterface, View.OnClickListener {

    private TextView mTvTitle;
    private TextView mTvPublish;
    private CustomPopWindow mDelPop;
    @Autowired
    UserProviderService service;
    private CustomPopWindow mTipsPopWindow;
    private LinearLayoutManager mLinearLayoutManager;
    private CustomTrendFragment customTrendFragment;

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mTvPublish = findViewById(R.id.tv_publish);
        mTvPublish.setOnClickListener(this);
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
        } else if (id == R.id.tv_publish || id == R.id.tv_send) {
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
            showMediaSelector();
        }
    }

    /**
     * 打开图片/视频选择器
     */
    private void showMediaSelector() {
        mCompositeDisposable.add(new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        ChoosePicUtils.picMultiple(mActivity, Interfaces.PIC_SELCTED_NUM, PictureConfig.CHOOSE_REQUEST, null, true);
                    } else {
                        Utils.toast("请允许摄像头权限");
                    }
                })
        );
    }

    private void setData() {
        String userId = getIntent().getStringExtra("id");
        //用户名称
        String mUserName = getIntent().getStringExtra("name");
        LottieAnimationView lottie = findViewById(R.id.animation_view);
        lottie.setImageAssetsFolder(Constants.ACCOST_GIFT);//设置data.json引用的图片资源文件夹名称
        mTvPublish.setVisibility(TextUtils.isEmpty(userId) ? View.VISIBLE : View.GONE);

        if (TextUtils.isEmpty(userId)) {
            mTvPublish.setVisibility(View.VISIBLE);
            mTvTitle.setText("我的动态");
            customTrendFragment = CustomTrendFragment.newInstance(0, -1, this);
        } else {
            mTvTitle.setText(String.format("%s的动态", mUserName));
            mTvPublish.setVisibility(View.GONE);
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
