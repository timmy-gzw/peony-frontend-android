package com.tftechsz.family.mvp.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.flyco.tablayout.SlidingScaleTabLayout;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.family.R;
import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.family.mvp.IView.IFamilyDetailView;
import com.tftechsz.family.mvp.presenter.FamilyDetailPresenter;
import com.tftechsz.family.mvp.ui.fragment.FamilyRankFragment;
import com.tftechsz.family.widget.pop.FamilyCooperationPopWindow;
import com.tftechsz.family.widget.pop.FamilyInvitePopWindow;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.FamilyInviteBean;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.FamilyActivityDto;
import com.tftechsz.common.event.PiazzaConstDto;
import com.tftechsz.common.event.RecruitBaseDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.RectangleIndicator;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * 我的家族
 */
@Route(path = ARouterApi.ACTIVITY_MINE_FAMILY)
public class MineFamilyActivity extends BaseMvpActivity<IFamilyDetailView, FamilyDetailPresenter> implements View.OnClickListener, IFamilyDetailView, FamilyInvitePopWindow.FamilyInviteListener {
    private FamilyInfoDto mData;
    private ImageView mIvAvatar, mIvLevel;
    private TextView mTvName, mTvDesc;
    private TextView mTvLevel;
    private TextView mTvNumber, mTvPrestige;   //用户数量，威望
    private SlidingScaleTabLayout mTabLayout;
    private ViewPager mViewPager;
    private ConstraintLayout mClLeader;
    private RelativeLayout mRlBottom;
    private ImageView mIvWx, mIvRound;
    //聊天广场
    //创建家族
    @Autowired
    UserProviderService service;
    private ConstraintLayout mFamilyRed;
    private TextView mRedNum;
    private Banner<FamilyInfoDto.Banner, BannerImageAdapter<FamilyInfoDto.Banner>> mBanner;
    private TextView mCreateFamily, mEnterChat;
    private CoordinatorLayout mCoor;
    private PageStateManager mPageManager;
    private int mFamilyId;
    private FamilyCooperationPopWindow pop;

    @Override
    public FamilyDetailPresenter initPresenter() {
        return new FamilyDetailPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mCoor = findViewById(R.id.coor);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvName = findViewById(R.id.tv_name);
        mTvDesc = findViewById(R.id.tv_desc);
        mIvLevel = findViewById(R.id.iv_level);
        mTvLevel = findViewById(R.id.tv_level);
        mTvPrestige = findViewById(R.id.tv_prestige);
        mTvNumber = findViewById(R.id.tv_number);
        mClLeader = findViewById(R.id.cl_leader);
        mRlBottom = findViewById(R.id.rl_bottom);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.vp);
        mIvWx = findViewById(R.id.iv_wx);
        mIvRound = findViewById(R.id.iv_round);
        mFamilyRed = findViewById(R.id.cl_family_red);
        mRedNum = findViewById(R.id.red_num);
        mBanner = findViewById(R.id.banner);
        mCreateFamily = findViewById(R.id.create_family);
        mEnterChat = findViewById(R.id.enter_chat);
        initListener();
    }

    private void initListener() {
        mClLeader.setOnClickListener(this);
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        findViewById(R.id.center_view).setOnClickListener(this);
        findViewById(R.id.tv_enter_chat).setOnClickListener(this);
        findViewById(R.id.iv_search).setOnClickListener(this);
        mCreateFamily.setOnClickListener(this);
        mIvWx.setOnClickListener(this);
        mEnterChat.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        initBus();
        mFamilyId = getIntent().getIntExtra("familyId", 0);

        mPageManager = PageStateManager.initWhenUse(mCoor, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showLoading();
                initNet();
            }

            @Override
            public int customErrorLayoutId() {
                return R.layout.pager_error_exit;
            }

            @Override
            public void onExit() {
                finish();
            }
        });
        if (!NetworkUtils.isConnected()) {
            mPageManager.showError(null);
        } else {
            mPageManager.showContent();
            initNet();
        }
    }

    private void initNet() {
        setTopData(mFamilyId);
        p.getMineFamily();

        setTab(mFamilyId);
        p.getFamilyInvite();
        p.getRecruitBase();

    }

    private void setTopData(int familyId) {
        if (familyId == 0) {  //没有家族
            mClLeader.setVisibility(View.GONE);
            mCreateFamily.setVisibility(View.VISIBLE);
            mRlBottom.setVisibility(View.GONE);
        } else {
            mClLeader.setVisibility(View.VISIBLE);
            mCreateFamily.setVisibility(View.GONE);
            if (mData != null) {
                //mRlBottom.setVisibility(mData.status == 1 ? View.VISIBLE : View.GONE);
                //等级
                FamilyInfoDto.FamilyLevel level = mData.family_level;
                if (null != level) {
                    mTvLevel.setText(String.valueOf(level.level)); //等级
                    GlideUtils.loadRouteImage(this, mIvLevel, level.icon);
                }
                mTvPrestige.setText(String.format("总威望值%s", mData.prestige));
                mTvNumber.setText(String.format("%s人", mData.user_count));
                GlideUtils.loadRoundImage(this, mIvAvatar, mData.icon);
                mTvName.setText(mData.family_name.replace("\n", " "));
                mTvDesc.setText(mData.intro);
                mEnterChat.setVisibility(mData.status == 1 ? View.VISIBLE : View.GONE);
            }
        }

    }

    private void setTab(int familyId) {
        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.family != null) {
            if (service.getConfigInfo().sys.family.list_cate != null && service.getConfigInfo().sys.family.list_cate.size() > 0) {
                List<ConfigInfo.ListCast> listCasts = service.getConfigInfo().sys.family.list_cate;
                for (int i = 0; i < listCasts.size(); i++) {
                    fragments.add(FamilyRankFragment.newInstance(listCasts.get(i).cate, familyId));
                    titles.add(listCasts.get(i).title);
                }
            }
            if (service.getConfigInfo().sys.family.wechat_contact_new != null && service.getConfigInfo().sys.family.wechat_contact_new.size() > 0) {
                mIvWx.setVisibility(View.VISIBLE);
                mIvRound.setVisibility(View.VISIBLE);
            }
        }
        mViewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), fragments, titles));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setCurrentItem(0);
    }

    /**
     * 通知更新
     */
    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_EXIT_FAMILY) {  //成功退出家族
                                finish();
                            } else if (event.type == Constants.NOTIFY_UPDATE_DISSOLUTION_SUCCESS) {
                                finish();
                            } else if (event.type == Constants.NOTIFY_UPDATE_CREATE_FAMILY || event.type == Constants.NOTIFY_REFRESH_RECOMMEND) {
                                int familyId = event.familyId;
                                if (familyId != 0)
                                    setTab(familyId);
                                p.getMineFamily();
                            } else if (event.type == Constants.NOTIFY_FAMILY_RECRUIT) { //招募红包推送
                                RecruitBaseDto dto = JSON.parseObject(event.code, RecruitBaseDto.class);
                                setRedPacketData(dto);
                            } else if (event.type == Constants.NOTIFY_FAMILY_CONTENT) { //家族广场复制信息
                                PiazzaConstDto dtoCopyInfo = JSON.parseObject(event.code, PiazzaConstDto.class);
                                if (pop == null)
                                    pop = new FamilyCooperationPopWindow(MineFamilyActivity.this);
                                pop.initUI(dtoCopyInfo.wechat_contact);
                                pop.showPopupWindow();
                            } else if (event.type == Constants.NOTIFY_ENTER_FAMILY) { // 通知进入家族
                                if (!TextUtils.isEmpty(event.code)) {
                                    CommonUtil.startTeamChatActivity(this, event.code);
                                }
                            }
                        }
                ));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_mine_family;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.cl_leader) {   //家族详情
            if (mData != null) {
                if (mData.status == 2) {
                    toastTip("家族审核中...");
                    return;
                }
                if (mData.status == 9) {
                    toastTip("家族已被封禁!");
                    return;
                }
                Intent intent = new Intent(this, FamilyDetailActivity.class);
                intent.putExtra("familyId", mData.family_id);
                startActivity(intent);
            }
        } else if (id == R.id.tv_enter_chat) {
            if (null == mData) return;
            CommonUtil.startTeamChatActivity(this, mData.tid + "");
        } else if (id == R.id.iv_wx) {
            if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.family != null) {
                if (pop == null)
                    pop = new FamilyCooperationPopWindow(MineFamilyActivity.this);
                pop.initUI(service.getConfigInfo().sys.family.wechat_contact_new);
                pop.showPopupWindow();
            }
        } else if (id == R.id.center_view) { //家族招募红包
            startActivity(FamilyRecruitActivity.class);
        } else if (id == R.id.create_family) {  //创建家族
            p.getCondition();
        } else if (id == R.id.iv_search) {  //搜索家族
            Intent intent = new Intent(mContext, SearchFamilyActivity.class);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent);
            } else {
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, findViewById(R.id.iv_search), "family_search");
                startActivity(intent, optionsCompat.toBundle());
            }
        } else if (id == R.id.enter_chat) {  //进群聊天
            if (mData != null) {
                CommonUtil.startTeamChatActivity(this, mData.tid + "");
            }
        }
    }

    @Override
    public void getFamilyDetailSuccess(FamilyInfoDto data) {
        mData = data;
        setTopData(mData == null ? 0 : mData.family_id);
        setBanner(data);
        if (mPageManager != null) mPageManager.showContent();
    }

    private void setBanner(@Nullable FamilyInfoDto data) {
        if (data != null && data.banner != null && data.banner.size() > 0) {
            mBanner.setVisibility(View.VISIBLE);
            mBanner.setIndicator(new RectangleIndicator(mContext))
                    .setIndicatorMargins(new IndicatorConfig.Margins(0, 0, 0, ConvertUtils.dp2px(10)));
            mBanner.setAdapter(new BannerImageAdapter<FamilyInfoDto.Banner>(data.banner) {
                @Override
                public void onBindView(BannerImageHolder holder, FamilyInfoDto.Banner data, int position, int size) {
                    holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    GlideUtils.loadRouteImage(mActivity, holder.imageView, data.url);
                    holder.imageView.setOnClickListener(v -> { //banner 点击事件
                        ConfigInfo.Option option = new ConfigInfo.Option();
                        option.is_topbar = data.is_topbar;
                        option.topbar_color = data.topbar_color;
                        CommonUtil.performLink(mActivity, new ConfigInfo.MineInfo(data.link, option), position, 14);
                        ARouter.getInstance()
                                .navigation(MineService.class)
                                .trackEvent("家族大厅banner位点击", Interfaces.POINT_EVENT_CLICK, "family_square_banner",
                                        JSON.toJSONString(new FamilyActivityDto(data.link, service.getUserInfo().isBoy() ? "boy" : "girl")), null);
                        ARouter.getInstance()
                                .navigation(MineService.class)
                                .trackEvent("站内活动入口点击", "event_entrance_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), data.link, 24,
                                        position, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME, "新年活动banner点击", "-1", mFamilyId)), null
                                );

                    });
                }
            });
            if (data.banner.size() > 1) {
                mBanner.addBannerLifecycleObserver(this);
                mBanner.setLoopTime(5000);  //设置轮播间隔时间
                mBanner.setScrollTime(600); //设置轮播滑动过程的时间
                mBanner.start();
            }
        } else {
            mBanner.stop();
            mBanner.setVisibility(View.GONE);
        }
    }

    @Override
    public void getConditionSuccess(FamilyIdDto data) {
        if (data.is_create_family == 0) {  //是否拥有创建家族权限
            if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.family != null) {
                CustomPopWindow popWindow = new CustomPopWindow(this);
                popWindow.setContent(service.getConfigInfo().sys.family.create_message);
                popWindow.setRightGone();
                popWindow.setRightButton("知道了");
                popWindow.showPopupWindow();
            }
        } else {
            startActivity(CreateFamilyPrerequisitesActivity.class);
//            Intent intent = new Intent(this, CreateFamilyInfoActivity.class);
//            startActivity(intent);
           /* if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.family != null) {
                BaseWebViewActivity.start(MineFamilyActivity.this, "", service.getConfigInfo().sys.family.create_protocol_h5);
                LogUtil.e("=============", service.getConfigInfo().sys.family.create_protocol_h5);
            }*/
        }

    }

    @Override
    public void applySuccess(String data) {

    }

    @Override
    public void applyLeaveSuccess(Boolean data) {

    }

    @Override
    public void updateFamilyIconSuccess(Boolean data) {

    }

    @Override
    public void familySignSuccess(Boolean data) {

    }

    @Override
    public void getFamilyInviteSuccess(FamilyInviteBean data) {
        if (!isFinishing()) {
            new FamilyInvitePopWindow(mContext, data, this).showPopupWindow();
        }
    }

    @Override
    public void muteAllSuccess(Boolean data, int isMute) {

    }

    @Override
    public void getRecruitBaseSuccess(RecruitBaseDto data) {
        setRedPacketData(data);
    }

    private void setRedPacketData(RecruitBaseDto data) {
        if (data == null) {
            mFamilyRed.setVisibility(View.GONE);
        } else {
            mFamilyRed.setVisibility(View.VISIBLE);
            mRedNum.setText(String.format(data.desc, data.value));
        }
    }

    @Override
    public boolean isLoadingDialogShow() {
        return false;
    }

    @Override
    public void onFamilyInviteClick(int family_id) {
        apply(family_id);
    }

    /**
     * 申请加入群组
     */
    private void apply(int familyId) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(FamilyApiService.class).apply(familyId, "")
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        toastTip(response.getData());
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_REFRESH_FAMILY_BTN, familyId));
                    }
                }));
    }
}
