package com.tftechsz.mine.mvp.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.util.VipUtils;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.BaseItemAdapter;
import com.tftechsz.mine.entity.BaseItemBean;
import com.tftechsz.mine.mvp.IView.IMineView;
import com.tftechsz.mine.mvp.presenter.MinePresenter;
import com.tftechsz.mine.mvp.ui.activity.MineFriendActivity;
import com.tftechsz.mine.mvp.ui.activity.SettingActivity;
import com.tftechsz.mine.mvp.ui.activity.VipActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的
 */
@Route(path = ARouterApi.FRAGMENT_MINE)
public class MineFragment extends BaseMvpFragment<IMineView, MinePresenter> implements IMineView, View.OnClickListener {

    private UserProviderService service;
    private TextView mTvName, mTvUserId, mTvSex;   //姓名,芍药号码,好友关注粉丝,性别
    private ImageView mIvAvatar, mIvAuth, mIvRealPeople, mVipIcon;
    private LinearLayout mLlFriend, mLlFans, mLlAttention;
    private TextView mTvFriend, mTvFans, mTvAttention,mVipCharge;
    private RecyclerView mRcMineMid, mRvMineBot;
    private UserInfo mUserInfo;
    private boolean isFragmentVisible;
    private ImageView mTvSetting;
    private TextView mTvJinbi;
    private TextView mTvJifen;
    private BaseItemAdapter mBotAdapter;
    private List<ConfigInfo.MineInfo> mBotConfigList;
    private ImageView mVipNor;
    private TextView mVipNorHint;
    private TextView mExpiredTime;
    private ConstraintLayout mClVip;
    private View mPicFrame, mTopBg;
    private TextView mVip_title;
    private NestedScrollView mNestedScrollView;
    private long mCurrentTime;

    @Override
    protected MinePresenter initPresenter() {
        return new MinePresenter();
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mNestedScrollView = getView(R.id.nestedScrollView);
        mTvName = getView(R.id.tv_name);
        mTvSetting = getView(R.id.tv_setting);
        mTvUserId = getView(R.id.tv_user_id);
        mLlFriend = getView(R.id.ll_friend);
        mTvFriend = getView(R.id.tv_friend);
        mLlFans = getView(R.id.ll_fans);
        mTvFans = getView(R.id.tv_fans);
        mLlAttention = getView(R.id.ll_attention);
        mTvAttention = getView(R.id.tv_attention);
        mIvAvatar = getView(R.id.iv_avatar);
        mIvAuth = getView(R.id.iv_auth);
        mIvRealPeople = getView(R.id.iv_real_people);
        mTvSex = getView(R.id.iv_sex);
        mRcMineMid = getView(R.id.rv_mine_mid);
        mRvMineBot = getView(R.id.rv_mine_bot);
        mTvJinbi = getView(R.id.tv_jinbi);
        mTvJifen = getView(R.id.tv_jifen);
        mVipNor = getView(R.id.vip_nor);
        mVipNorHint = getView(R.id.vip_nor_hint);
        mClVip = getView(R.id.cl_vip);
        mExpiredTime = getView(R.id.expired_time);
        mTopBg = getView(R.id.top_bg);
        mPicFrame = getView(R.id.pic_frame);
        mVip_title = getView(R.id.vip_title);
        mVipIcon = getView(R.id.vip_icon);
        mVipCharge = getView(R.id.vip_charge);
        initListener();

    }

    @Override
    public void onResume() {
        super.onResume();
        toNet();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isFragmentVisible = isVisibleToUser;
        if (isVisibleToUser) {
            initConfig();
            toNet();
        }
    }

    private void initListener() {
        mLlFriend.setOnClickListener(this);  //好友
        mLlAttention.setOnClickListener(this);  ///关注
        mLlFans.setOnClickListener(this);  //粉丝
        getView(R.id.tv_setting).setOnClickListener(this);  //设置
        getView(R.id.cl_vip).setOnClickListener(this);  //会员
        getView(R.id.cl_top).setOnClickListener(this);   //我的
        getView(R.id.ll_jifen).setOnClickListener(this);   //积分
        getView(R.id.ll_jinbi).setOnClickListener(this);   //金币
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initData() {
        mUserInfo = service.getUserInfo();

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mTvSetting.getLayoutParams();
        lp.topMargin = ImmersionBar.getStatusBarHeight(mActivity);
        mTvSetting.setLayoutParams(lp);
        mNestedScrollView.setPadding(0, ImmersionBar.getStatusBarHeight(mActivity), 0, 0);

        mRvMineBot.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBotAdapter = new BaseItemAdapter();
        mRvMineBot.setAdapter(mBotAdapter);
        mBotAdapter.setList(null);
        if (((SimpleItemAnimator) mRvMineBot.getItemAnimator()) != null)
            ((SimpleItemAnimator) mRvMineBot.getItemAnimator()).setSupportsChangeAnimations(false);
        mRvMineBot.setItemAnimator(null);
        mBotAdapter.onAttachedToRecyclerView(mRvMineBot);
        mBotAdapter.setOnItemClickListener((adapter1, view, position) -> {
            if (mBotConfigList != null && mBotConfigList.size() > 0) {
                if (mBotConfigList.get(position).link.startsWith(Interfaces.LINK_PEONY + Interfaces.LINK_PEONY_CREATE_LIVE)) {
                    p.getLiveToken("测试");
                } else {
                    CommonUtil.performLink(mActivity, mBotConfigList.get(position), position, 0);
                }
                if (mBotConfigList.get(position).title.contains("我的装扮")) {
                    ARouter.getInstance()
                            .navigation(MineService.class)
                            .trackEvent("我的装扮点击", "outfit_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), 1, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME)), null);

                } else if (mBotConfigList.get(position).title.contains("贵族")) {
                    ARouter.getInstance()
                            .navigation(MineService.class)
                            .trackEvent("我的页面_我的贵族点击", "mynoble_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), 1, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME)), null);

                }
            }
        });
        if (mUserInfo != null) {
            setUserInfo(mUserInfo);
        } else {
            initConfig();
        }
        initRxBus();

        toNet();
    }


    /**
     * 设置用户信息
     */
    @SuppressLint("SetTextI18n")
    private void setUserInfo(UserInfo userInfo) {
        initConfig();
        CommonUtil.setUserName(mTvName, userInfo.getNickname(), false, false);
        VipUtils.setPersonalise(mPicFrame, userInfo.picture_frame, false, true);
        //mPicFrame.setBackgroundResource(userInfo.picture_frame == 0 ? 0 : VipUtils.getPictureFrameBackground(userInfo.picture_frame));
        mTvUserId.setText(String.format("%s号: ", getString(R.string.app_name)) + userInfo.getUser_code());

        if (userInfo.is_show_icon == 1) {
            mTvUserId.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(R.drawable.min_name_img_left), null, null, null);
        } else {
            mTvUserId.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        mTvFriend.setText(String.valueOf(userInfo.getFriend_number()));
        mTvAttention.setText(String.valueOf(userInfo.getWatch_number()));
        mTvFans.setText(String.valueOf(userInfo.getFans_number()));
        mVip_title.setText(new SpannableStringUtils.Builder().append("VIP会员").setBold().append("·尊享特权").create());
        GlideUtils.loadRouteImage(getActivity(), mIvAvatar, userInfo.getIcon(), userInfo.getSex() == 1 ? R.mipmap.mine_ic_boy_default : R.mipmap.mine_ic_girl_default);
        mTvJifen.setText(userInfo.getIntegral());
        mTvJinbi.setText(userInfo.getCoin());

        //真人，实名，性别 不显示了
//        mIvRealPeople.setVisibility(userInfo.getIs_real() == 1 ? View.VISIBLE : View.GONE);  //是否真人
//        mIvAuth.setVisibility(userInfo.getIs_self() == 1 ? View.VISIBLE : View.GONE);   //是否实名
        //1.男，2.女
//        CommonUtil.setSexAndAge(getActivity(), userInfo.getSex(), String.valueOf(userInfo.getAge()), mTvSex);
        //蒙城头像
        //GlideUtils.loadImageGaussian(mIvAvatarGaussian, userInfo.getIcon(), R.mipmap.ic_default_avatar);


    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS || event.type == Constants.NOTIFY_FOLLOW) {  //刷新个人信息
                                toNet();
                            } else if (event.type == Constants.NOTIFY_MINE_USER_INFO) {
                                p.getUserInfo();
                            } else if (event.type == Constants.NOTIFY_TOP_CONFIG) {   //数显config
                                initConfig();
                            }
                        }
                ));
    }


    private String show_vip_desc;
    private long mTimeAssign = 0;//区间内刷新解决 闪烁问题

    private synchronized void initConfig() {
        Utils.runOnUiThread(() -> {
            ConfigInfo configInfo = service.getConfigInfo();
            BaseItemBean fkjl = null;
            if (null != configInfo && configInfo.share_config != null) {
                //渲染底部RecyclerView
                List<ConfigInfo.MineInfo> list = CommonUtil.addMineInfo(configInfo.share_config.my);

                if (list.size() > 0) {
                    List<BaseItemBean> beanList = new ArrayList<>();
                    for (ConfigInfo.MineInfo mineInfo : list) {
                        if ("红包福利".equals(mineInfo.title)) {
                            beanList.add(new BaseItemBean(mineInfo.icon, mineInfo.title, R.drawable.ic_red_packet));
                        } else if ("访客记录".equals(mineInfo.title)) {
                            fkjl = new BaseItemBean(mineInfo.icon, mineInfo.title, mUserInfo != null ? mUserInfo.visitor_count + "位访客" : "", "#999999");
                            beanList.add(fkjl);
                        } else if (mineInfo.link.equals(Interfaces.LINK_PEONY + Interfaces.LINK_PEONY_NOTE_VALUE)) { //音符值
                            beanList.add(new BaseItemBean(mineInfo.icon, mineInfo.title, mUserInfo != null ? mUserInfo.getNote_value() : "", "#999999"));
                        } else {
                            beanList.add(new BaseItemBean(mineInfo.icon, mineInfo.title, mineInfo.complete_msg, mineInfo.complete_msg_color));
                        }
                    }
                    mBotConfigList = list;
                    if (mBotAdapter != null) {//优化  列表晃动问题
                        List<BaseItemBean> data = mBotAdapter.getData();
                        if (data.size() == 0) {
                            mBotAdapter.setList(beanList);
                        } else {
                            int size2 = data.size();
                            if (beanList.size() != size2) {
                                mBotAdapter.setList(beanList);
                            } else {
                                if (mTimeAssign == 0)
                                    mBotAdapter.setList(beanList);
                                //更新访客记录
                                for (int j = 0; j < size2; j++) {
                                    BaseItemBean baseItemBean = data.get(j);
                                    if (baseItemBean.title.equals("访客记录")) {
                                        if (fkjl != null) {
                                            //加上时效 解决 只有访客记录item刷新闪烁问题
                                            if ((System.currentTimeMillis() - mTimeAssign) > 10 * 60 * 1000) {
                                                mBotAdapter.setData(j, fkjl);
                                            }
                                        }
                                    } else if (mBotConfigList.get(j).link.equals(Interfaces.LINK_PEONY + Interfaces.LINK_PEONY_NOTE_VALUE)) {
                                        TextView rightTxt = (TextView) mBotAdapter.getViewByPosition(j, R.id.right_txt);
                                        if (rightTxt != null) {
                                            rightTxt.setText(mUserInfo.getNote_value());
                                        }
                                    }
                                }
                                mTimeAssign = System.currentTimeMillis();
                            }
                        }
                    }
                }
            }

            if (null != configInfo && configInfo.share_config != null) {
                mClVip.setVisibility(configInfo.share_config.is_show_vip == 0 && !service.getUserInfo().isVip() ? View.GONE : View.VISIBLE);
                if (configInfo.share_config.is_show_vip == 0) {
                    show_vip_desc = configInfo.share_config.show_vip_desc;
                }
                if (service.getUserInfo() != null) {
                    UserInfo userInfo = service.getUserInfo();
                    mVipNor.setVisibility(View.VISIBLE);
                    GlideUtils.loadBackGround(mContext, userInfo.getVip_background_icon(), mVipNor);
                    if (userInfo.isVip()) {
                        mTopBg.setBackgroundResource(R.drawable.shape_vip_top_bg);
                        mVipIcon.setVisibility(View.VISIBLE);
                        mVipCharge.setVisibility(View.GONE);
                        mVip_title.setVisibility(View.VISIBLE);
                        mVipNorHint.setVisibility(View.INVISIBLE);
                        mExpiredTime.setText(userInfo.vip_expiration_time_desc_new);
                        mExpiredTime.setVisibility(View.VISIBLE);
                    } else {
//                        mTopBg.setBackgroundColor(Utils.getColor(R.color.EEEEEE));
                        mTopBg.setBackgroundResource(R.mipmap.mine_bg);
                        mVipIcon.setVisibility(View.GONE);
                        mVipCharge.setVisibility(View.VISIBLE);
                        mExpiredTime.setVisibility(View.VISIBLE);
                        mVip_title.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(userInfo.getVip_desc())) {
                            mVipNorHint.setVisibility(View.INVISIBLE);
                        } else {
                            mVipNorHint.setText(userInfo.getVip_desc());
                            mVipNorHint.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimeAssign = 0;
    }

    /**
     * 获取用户信息成功
     */
    @Override
    public void getUserInfoSuccess(UserInfo userInfo) {
        mCurrentTime = System.currentTimeMillis();
        mUserInfo = userInfo;
        service.setUserInfo(userInfo);
        setUserInfo(userInfo);
    }

    private void startMineFriend(int type) {
        Intent intent = new Intent(getContext(), MineFriendActivity.class);
        intent.putExtra(MineFriendActivity.TYPE_MINE_FRIEND, type);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_friend) {  // 个人朋友
            startMineFriend(MineFriendActivity.TYPE_FRIEND);
        } else if (id == R.id.ll_fans) {  // 个人粉丝
            startMineFriend(MineFriendActivity.TYPE_FANS);
        } else if (id == R.id.ll_attention) {  // 个人/关注
            startMineFriend(MineFriendActivity.TYPE_WATCH);
        } else if (id == R.id.cl_top) {   //我的资料
            ARouterUtils.toMineDetailActivity("");
        } else if (id == R.id.tv_setting) {  //设置
            startActivity(SettingActivity.class);
        } else if (id == R.id.ll_jifen) {  //收益
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_MINE_INTEGRAL_NEW);
        } else if (id == R.id.ll_jinbi) {  //金币充值
            if (null != mUserInfo) {
                ARouterUtils.toRechargeActivity(mUserInfo.getCoin());
            }
        } else if (id == R.id.cl_vip) { //会员
            if (service.getUserInfo().isVip() && !TextUtils.isEmpty(show_vip_desc)) {
                toastTip(show_vip_desc);
            } else {
                startActivity(VipActivity.class);
            }
        }
        /* else if (id == R.id.tv_family) {
            if (mUserInfo != null) {
                if (mUserInfo.family_id == 0)
                    SPUtils.put(BaseApplication.getInstance(), Constants.FAMILY_APPLY, "");
                ARouterUtils.toMineFamily(mUserInfo.family_id);
            }
        }*/
    }

    private void toNet() {
        if (System.currentTimeMillis() - mCurrentTime > 10000) {
            p.getUserInfo();
            mCurrentTime = System.currentTimeMillis();
        }

    }
}
