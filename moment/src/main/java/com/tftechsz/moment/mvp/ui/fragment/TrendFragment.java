package com.tftechsz.moment.mvp.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.tablayout.SlidingScaleTabLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.config.PictureConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.AccostSuccessEvent;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.AccostChatPop;
import com.tftechsz.moment.R;
import com.tftechsz.moment.mvp.ui.activity.TrendNoticeActivity;
import com.tftechsz.moment.widget.PublishMomentGuidePop;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

/**
 * 动态主页
 */
@Route(path = ARouterApi.FRAGMENT_TREND)
public class TrendFragment extends BaseMvpFragment implements View.OnClickListener, CustomTrendFragment.SrcollowInterface {

    private TextView mTrendBadge;
    private View rootView;
    private ImageView mIvPublish;
    private boolean isFragmentVisible;
    private AccostChatPop mAccostPop;
    @Autowired
    UserProviderService service;
    private CustomTrendFragment customTrendFragment1, customTrendFragment2;
    private int mTagRefreshItemFragment;
    private boolean needShowGuide = true;//是否需要显示发布动态引导页

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        ImmersionBar.with(this).titleBarMarginTop(R.id.tabLayout).init();
        rootView = getView(R.id.cl_trend_root);
        mIvPublish = getView(R.id.iv_publish);
        ImageView ivSearch = getView(R.id.iv_search);
        mTrendBadge = getView(R.id.tv_blog_badge);
        mIvPublish.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        SlidingScaleTabLayout mTabLayout = getView(R.id.tabLayout);
        ViewPager mViewPager = getView(R.id.vp);
        ArrayList<Fragment> fragments = new ArrayList<>();
        customTrendFragment1 = CustomTrendFragment.newInstance(0, this);
        customTrendFragment2 = CustomTrendFragment.newInstance(2, this);
        //"type": 0 , // 动态列表列别 0-同城动态 1-附近的 2-关注
        fragments.add(customTrendFragment1);
//        fragments.add(CustomTrendFragment.newInstance(1));
        fragments.add(customTrendFragment2);
        List<String> titles = new ArrayList<>();
        titles.add("推荐");
//        titles.add("附近");
        titles.add("关注");
        mViewPager.setAdapter(new FragmentVpAdapter(getChildFragmentManager(), fragments, titles));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTagRefreshItemFragment = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
    }

    /**
     * 双击刷新
     */
    public void refreshRec() {
        if (mTagRefreshItemFragment == 0 && customTrendFragment1 != null && customTrendFragment1.mSmartRefreshLayout != null) {
            customTrendFragment1.mSmartRefreshLayout.autoRefresh();
//            customTrendFragment1.recyclerView.smoothScrollToPosition(0);
            if (customTrendFragment1.mLinearLayoutManager != null) {
                customTrendFragment1.mLinearLayoutManager.smoothScrollToPosition(customTrendFragment1.recyclerView, new RecyclerView.State(), 0);
            }
        } else if (mTagRefreshItemFragment == 1 && customTrendFragment2 != null && customTrendFragment2.mSmartRefreshLayout != null) {
            customTrendFragment2.mSmartRefreshLayout.autoRefresh();
            if (customTrendFragment2.mLinearLayoutManager != null) {
                customTrendFragment2.mLinearLayoutManager.smoothScrollToPosition(customTrendFragment2.recyclerView, new RecyclerView.State(), 0);
            }
//            customTrendFragment2.recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_trend;
    }

    @Override
    protected void initData() {
        initRxBus();
    }

    /**
     * 显示发布动态引导页，安装进入APP后只弹1次引导只显示一次(女生需要真人认证)
     */
    private void showMomentGuide() {
        if (needShowGuide) {
            long lastShowMomentTime = MMKVUtils.getInstance().decodeLong(Constants.LAST_SHOW_MOMENT_GUIDE_TIME);//上次显示动态引导页的时间戳ms
            if (lastShowMomentTime <= 0) {
                if (service != null && service.getUserInfo() != null && (service.getUserInfo().isReal() || service.getUserInfo().isBoy())) {
                    PublishMomentGuidePop pop = new PublishMomentGuidePop(getActivity());
                    pop.showPopupWindow();
                    MMKVUtils.getInstance().encode(Constants.LAST_SHOW_MOMENT_GUIDE_TIME, System.currentTimeMillis());
                    needShowGuide = false;
                }
            } else {
                needShowGuide = false;
            }
        }
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_BLOG_NOTICE) {  //有动态通知
                                showApply();
                            }
                        }
                ));
        mCompositeDisposable.add(RxBus.getDefault().toObservable(AccostSuccessEvent.class)
                .subscribe(event -> {
                            if (event.getEventFrom() == AccostSuccessEvent.ACCOUST_MOMENT || event.getEventFrom() == AccostSuccessEvent.ACCOUST_HOME) {
                                showAccostChatPop(event.getToUserId(), event.getToUsername(), event.getToUserAvatar());
                            }
                        }
                ));
    }


    @Override
    public void onResume() {
        super.onResume();
        showMomentGuide();
        if (!isFragmentVisible) {
            return;
        }
        showApply();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isFragmentVisible = isVisibleToUser;
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            showApply();
        }
    }


    /**
     * 显示通知数量
     */
    private void showApply() {
        Utils.runOnUiThread(() -> {
            if (CommonUtil.isShowTrendNum(mTrendBadge)) {
                mTrendBadge.setVisibility(View.VISIBLE);
            } else {
                mTrendBadge.setVisibility(View.INVISIBLE);
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
        int id = v.getId();
        if (id == R.id.iv_publish) {   // 发布
            showMediaSelector();
        } else if (id == R.id.iv_search) {//动态通知
            TrendNoticeActivity.startActivity(mContext);
        }
    }

    /**
     * 打开图片/视频选择器
     */
    private void showMediaSelector() {
        final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        PermissionUtil.beforeRequestPermission(mActivity, permissions, agreeToRequest -> {
            if (agreeToRequest) {
                mCompositeDisposable.add(new RxPermissions((FragmentActivity) mActivity)
                        .request(permissions)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                ChoosePicUtils.picMultiple(mActivity, Interfaces.PIC_SELCTED_NUM, PictureConfig.CHOOSE_REQUEST, null, true);
                            } else {
                                PermissionUtil.showPermissionPop(getActivity(), getString(R.string.chat_open_storage_camera_permission));
                            }
                        })
                );
            }
        });
    }

    @Override
    public void showImage() {
//        showFloatImage(moveDistance);
    }

    @Override
    public void hideImage() {
//        hideFloatImage(moveDistance);
    }

    private void showAccostChatPop(String toUserId, String toUsername, String toUserAvatar) {
        if (mAccostPop == null) {
            mAccostPop = new AccostChatPop(getContext(), toUserId, toUsername, toUserAvatar);
        } else {
            mAccostPop.setChatUserInfo(toUserId, toUsername, toUserAvatar);
        }
        mAccostPop.setPopupGravityMode(BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE);
        mAccostPop.showPopupWindow(rootView);
    }

    //隐藏动画
    private void hideFloatImage(int distance) {
        mIvPublish.setEnabled(false);
        //位移动画
        TranslateAnimation ta = new TranslateAnimation(0, distance, 0, 0);
        ta.setDuration(300);
        //渐变动画
        AlphaAnimation al = new AlphaAnimation(1f, 0.5f);
        al.setDuration(300);
        AnimationSet set = new AnimationSet(true);
        //动画完成后不回到原位
        set.setFillAfter(true);
        set.addAnimation(ta);
        set.addAnimation(al);
        mIvPublish.startAnimation(set);
    }

    //显示动画
    private void showFloatImage(int distance) {
        //位移动画
        TranslateAnimation ta = new TranslateAnimation(distance, 0, 0, 0);
        ta.setDuration(300);
        //渐变动画
        AlphaAnimation al = new AlphaAnimation(0.5f, 1f);
        al.setDuration(300);
        AnimationSet set = new AnimationSet(true);
        //动画完成后不回到原位
        set.setFillAfter(true);
        set.addAnimation(ta);
        set.addAnimation(al);
        mIvPublish.startAnimation(set);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvPublish.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
