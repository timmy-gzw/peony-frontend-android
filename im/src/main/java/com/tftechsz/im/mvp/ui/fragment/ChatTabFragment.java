package com.tftechsz.im.mvp.ui.fragment;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.NetworkUtils;
import com.flyco.tablayout.SlidingScaleTabLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.im.R;
import com.tftechsz.im.mvp.ui.activity.VideoCallActivity;
import com.tftechsz.im.widget.pop.ChatMorePopWindow;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.NetStatusEvent;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.UserStatusEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nim.model.NERTCVideoCall;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.NetworkCallbackImpl;
import com.tftechsz.common.utils.NetworkUtil;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.DepthPageTransformer;
import com.tftechsz.common.widget.pop.MessageNotificationPopWindow;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

@Route(path = ARouterApi.FRAGMENT_CHAT_TAB)
public class ChatTabFragment extends BaseMvpFragment implements View.OnClickListener {

    private LinearLayout llDelete;  //删除 取消 按钮
    private ImageView ivMore;
    private RelativeLayout mRl_top;
    private boolean isClear = false;
    private MessageNotificationPopWindow mMessageNotificationPopWindow;
    private ViewPager mViewPager;
    private LinearLayout mLlOnlineStatus;
    private TextView mTvOnlineStatus;
    private ImageView mIvOnLineStatus;
    private String mOnlineStatus;
    @Autowired
    UserProviderService service;
    private ConstraintLayout mRootProgress;
    private ProgressBar mPb;
    private TextView mTv;
    private String mTaskLink;
    private TextView mTvNetError;
    private ConnectivityManager mConnMgr;
    private NetworkCallbackImpl mNetworkCallback;

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isClear) {
            checkPermission();
        }
    }

    private void checkPermission() {
        if (getActivity() != null) {
            NotificationManager manager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = manager.getNotificationChannel(Interfaces.CHANNELID);
                if (channel != null && channel.getImportance() < NotificationManager.IMPORTANCE_HIGH) {
                    mRl_top.setVisibility(View.VISIBLE);
                } else {
                    mRl_top.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        ImmersionBar.with(this).titleBarMarginTop(R.id.view).init();
        SlidingScaleTabLayout mTabLayout = getView(R.id.tabLayout);
        mTvNetError = getView(R.id.tv_net_error);
        mViewPager = getView(R.id.vp);
        mRootProgress = getView(R.id.root_chat_progress);
        mPb = getView(R.id.pb_progress);
        mTv = getView(R.id.tv_progress);
        mRl_top = getView(R.id.rl_top);
        mRl_top.setVisibility(View.GONE);
        mTvOnlineStatus = getView(R.id.tv_online_status);
        mIvOnLineStatus = getView(R.id.iv_online_status);
        mLlOnlineStatus = getView(R.id.ll_online_status);
        getView(R.id.iv_del).setOnClickListener(this);
        getView(R.id.rl_top).setOnClickListener(this);
        mTvNetError.setOnClickListener(this);
        mRootProgress.setOnClickListener(this);
        llDelete = getView(R.id.ll_delete);
        ivMore = getView(R.id.iv_more);
        ivMore.setOnClickListener(this);
        getView(R.id.tv_delete).setOnClickListener(this);  //删除选中聊天
        getView(R.id.tv_cancel).setOnClickListener(this);
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(ChatFragment.newInstance(0));
        fragments.add(ChatFragment.newInstance(1));
        fragments.add(new CallLogFragment());
        List<String> titles = new ArrayList<>();
        titles.add("消息");
        titles.add("密友");
        titles.add("通话");
        mViewPager.setAdapter(new FragmentVpAdapter(getChildFragmentManager(), fragments, titles));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setCurrentItem(0);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        initRxBus();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_DELETE_MESSAGE_CANCEL));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mNetworkCallback = new NetworkCallbackImpl();
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest request = builder.build();
        mConnMgr = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnMgr != null) {
            mConnMgr.registerNetworkCallback(request, mNetworkCallback);
        }

        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, true);
        mTvNetError.setVisibility(NetworkUtils.isConnected() ? View.GONE : View.VISIBLE);
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(UserStatusEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (service.getUserInfo() != null && service.getUserInfo().getSex() == 2) {
                                mOnlineStatus = event.status;
                                setStatus();
                            }
                        }
                ));
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_DELETE_CHOOSE_MESSAGE || event.type == Constants.NOTIFY_DELETE_MESSAGE_CANCEL) {
                                if (mViewPager.getCurrentItem() == 0) {
                                    ivMore.setVisibility(View.VISIBLE);
                                } else {
                                    ivMore.setVisibility(View.GONE);
                                }
                                llDelete.setVisibility(View.GONE);
                            } else if (event.type == Constants.NOTIFY_ON_LINE_STATUS) {
                                ChatMsg.UserTask eventType = JSON.parseObject(event.code, ChatMsg.UserTask.class);
                                mTaskLink = eventType.link;
                                if (eventType.type.equals("online_start")) {
                                    mRootProgress.setVisibility(View.VISIBLE);
                                    mPb.setProgress(eventType.value);
                                    mTv.setText(eventType.value + "%");
                                } else {
                                    mRootProgress.setVisibility(View.GONE);

                                }
                            }
                        }
                ));
        mCompositeDisposable.add(RxBus.getDefault().toObservable(NetStatusEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            mTvNetError.post(() -> {
                                mTvNetError.setVisibility(NetworkUtil.isNetworkAvailable(BaseApplication.getInstance()) ? View.GONE : View.VISIBLE);
                            });
                        }
                ));
    }


    private void setStatus() {
        Utils.runOnUiThread(() -> {
            if (TextUtils.isEmpty(mOnlineStatus)) {
                mLlOnlineStatus.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.equals("busy", mOnlineStatus)) {
                mTvOnlineStatus.setText("忙碌");
                mIvOnLineStatus.setImageResource(R.mipmap.chat_ic_status_busy);
            } else if (TextUtils.equals("offline", mOnlineStatus)) {
                mTvOnlineStatus.setText("离开");
                mIvOnLineStatus.setImageResource(R.mipmap.chat_ic_status_offline);
            } else if (TextUtils.equals("free", mOnlineStatus)) {
                mTvOnlineStatus.setText("空闲");
                mIvOnLineStatus.setImageResource(R.mipmap.chat_ic_status_free);
            }
            mLlOnlineStatus.setVisibility(View.VISIBLE);
        });
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_chat_tab;
    }

    @Override
    protected void initData() {

    }


    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode code) {
            if (service != null && service.getUserInfo() != null && service.getUserInfo().getSex() == 2) {
                if (code == StatusCode.NET_BROKEN || code == StatusCode.CONNECTING) {
                    mTvOnlineStatus.setText("离开");
                    mIvOnLineStatus.setImageResource(R.mipmap.chat_ic_status_offline);
                    mLlOnlineStatus.setVisibility(View.VISIBLE);
                } else {
                    setStatus();
                }
            }
            if (code == StatusCode.KICKOUT || code == StatusCode.KICK_BY_OTHER_CLIENT) {
                Activity activity = AppManager.getAppManager().getActivity(VideoCallActivity.class);
                if (activity != null) {
                    if (NERTCVideoCall.sharedInstance() != null) {
                        NERTCVideoCall.sharedInstance().releaseNERtc();
                    }
                    (activity).finish();
                }
                AppManager.getAppManager().finishAllActivity();
                SPUtils.put(Constants.IS_COMPLETE_INFO, 0);
                ARouterUtils.toLoginActivity(ARouterApi.MINE_LOGIN);
                NIMClient.getService(AuthService.class).logout();

            }
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_LOGIN_STATUS, code.getValue()));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, false);
        if (mConnMgr != null) {
            mConnMgr.unregisterNetworkCallback(mNetworkCallback);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
        if (id == R.id.iv_more) {   //更多功能弹窗
            ChatMorePopWindow pop = new ChatMorePopWindow(getActivity());
            pop.addOnClickListener(() -> {
                llDelete.setVisibility(View.VISIBLE);
                ivMore.setVisibility(View.INVISIBLE);
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_DELETE_MESSAGE));
            });
            pop.showPopupWindow(getView(R.id.iv_more));
        } else if (id == R.id.tv_delete) {   //删除选中聊天
            llDelete.setVisibility(View.GONE);
            ivMore.setVisibility(View.VISIBLE);
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_DELETE_CHOOSE_MESSAGE));
        } else if (id == R.id.tv_cancel) {  //取消
            llDelete.setVisibility(View.GONE);
            ivMore.setVisibility(View.VISIBLE);
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_DELETE_MESSAGE_CANCEL));
        } else if (id == R.id.rl_top) { //去设置消息通知权限
            if (mMessageNotificationPopWindow == null) {
                mMessageNotificationPopWindow = new MessageNotificationPopWindow(mContext);
            }
            mMessageNotificationPopWindow.showPopupWindow();
        } else if (id == R.id.iv_del) { //去掉弹框
            isClear = true;
            mRl_top.setVisibility(View.GONE);
        } else if (id == R.id.root_chat_progress) { //任务进度
            BaseWebViewActivity.start(mContext, "", mTaskLink, false, 0, 2);
        } else if (id == R.id.tv_net_error) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }
}
