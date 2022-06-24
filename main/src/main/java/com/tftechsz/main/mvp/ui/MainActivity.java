package com.tftechsz.main.mvp.ui;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.*;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieListener;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.netease.neliveplayer.sdk.NELivePlayer;
import com.netease.neliveplayer.sdk.model.NESDKConfig;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.netease.nim.uikit.common.util.MD5Util;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.im.mvp.ui.fragment.ChatTabFragment;
import com.tftechsz.home.entity.SignInBean;
import com.tftechsz.home.mvp.ui.fragment.HomeFragment;
import com.tftechsz.home.widget.SignInPopWindow;
import com.tftechsz.home.widget.SignSucessPopWindow;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter2;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.entity.UpdateInfo;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.MessageEvent;
import com.tftechsz.common.event.UnReadMessageEvent;
import com.tftechsz.common.event.UpdateEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.PartyService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.location.BDLocationManager;
import com.tftechsz.common.location.LocationListener;
import com.tftechsz.common.music.base.BaseMusicHelper;
import com.tftechsz.common.music.logger.AppLogUtils;
import com.tftechsz.common.music.service.PlayService;
import com.tftechsz.common.nim.service.CallService;
import com.tftechsz.common.nim.service.DownGiftWork;
import com.tftechsz.common.service.PartyAudioService;
import com.tftechsz.common.utils.*;
import com.tftechsz.common.widget.UpdateDialog;
import com.tftechsz.common.widget.pop.MessageNotificationPopWindow;
import com.tftechsz.common.widget.pop.OneKeyAccostPopWindow;
import com.tftechsz.common.widget.pop.UserLevelUpPop;
import com.tftechsz.main.R;
import com.tftechsz.main.entity.UpdateLocationReq;
import com.tftechsz.main.mvp.IView.IMainView;
import com.tftechsz.main.mvp.presenter.MainPresenter;
import com.tftechsz.mine.mvp.ui.fragment.MineFragment;
import com.tftechsz.party.mvp.ui.fragment.PartyFragment;
import com.tftechsz.moment.mvp.ui.activity.SendTrendActivity;
import com.tftechsz.moment.mvp.ui.fragment.TrendFragment;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import razerdp.basepopup.BasePopupWindow;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

@Route(path = ARouterApi.MAIN_MAIN)
public class MainActivity extends BaseMvpActivity<IMainView, MainPresenter> implements IMainView, View.OnClickListener, LocationListener, SignInPopWindow.SignInPopClickListener {
    private List<View> views;
    private List<TextView> mTextViews;
    private TextView tvBadge;
    private ViewPager2 vp;
    private final List<Fragment> fragmentList = new ArrayList<>();
    private int prevPosition = 0;
    private long mExitTime;
    private UpdateDialog dialog;
    private UpdateEvent mEvent;  //更新信息
    private boolean isFirstSign = true;
    private String signCost;
    private OneKeyAccostPopWindow mOneKeyAccostPopWindow;
    private PlayServiceConnection mPlayServiceConnection;
    private PartyServiceConnection mPartyServiceConnection;
    private PartyService partyService;
    @Autowired
    UserProviderService service;
    private SignInPopWindow mSignInPopWindow;
    private ConfigInfo.Icon mIcon;
    /**
     * 数据加载不放入onCreate,加快Activity挂载
     */
    private ServiceConnection mServiceConnection;
    private ConstraintLayout mBotChatView;
    private List<ChatMsg.AccostPopup> mAccostPopList;
    private UserLevelUpPop mRichLevelUpPop;
    private UserLevelUpPop mCharmLevelUpPop;

    private MessageEvent mMessageEvent;  //消息
    private HomeFragment mHomeFragment;
    private TrendFragment trendFragment;
    private boolean mIsShowParty = false;  //是否显示party功能
    private PartyFragment partyFragment;
    private ComponentName componentName1, componentName2;
    private PackageManager mPackageManager;

    public MainActivity() {
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

   /* @Override
    protected boolean getImmersionBar() {
        return false;
    }*/


    @Override
    protected void initView(Bundle savedInstanceState) {
        //StatusBarUtil.fullScreen(this);
        NESDKConfig config = new NESDKConfig();
        config.dataUploadListener = new NELivePlayer.OnDataUploadListener() {
            @Override
            public boolean onDataUpload(String url, String data) {
                return true;
            }

            @Override
            public boolean onDocumentUpload(String url, Map<String, String> params, Map<String, String> filepaths) {
                return false;
            }
        };
        NELivePlayer.init(getApplicationContext(), config);
        mIsShowParty = MMKVUtils.getInstance().decodeBoolean(Constants.SHOW_PARTY_ICON);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        partyService = ARouter.getInstance().navigation(PartyService.class);
        final ConfigInfo configInfo = service.getConfigInfo();
        vp = findViewById(R.id.vp);
        final LottieAnimationView viewHome = findViewById(R.id.view_home);
        setConfigLottie(viewHome, "main_tab_home.zip", configInfo);
        final LottieAnimationView viewTrend = findViewById(R.id.view_trend);
        setConfigLottie(viewTrend, "main_tab_trend.zip", configInfo);
        final LottieAnimationView viewParty = findViewById(R.id.view_party);
        setConfigLottie(viewParty, "main_tab_party.zip", configInfo);
        final LottieAnimationView viewMessage = findViewById(R.id.view_message);
        setConfigLottie(viewMessage, "main_tab_message.zip", configInfo);
        final LottieAnimationView viewMine = findViewById(R.id.view_mine);
        setConfigLottie(viewMine, "main_tab_mine.zip", configInfo);


        LinearLayout llParty = findViewById(R.id.btn_party);
        if (mIsShowParty) {
            llParty.setVisibility(View.VISIBLE);
            startCheckPartyService();
        } else {
            llParty.setVisibility(View.GONE);
        }
        TextView tvHome = findViewById(R.id.tv_home);
        setConfigTextColor(tvHome, true, configInfo);
        TextView tvMessage = findViewById(R.id.tv_message);
        setConfigTextColor(tvMessage, true, configInfo);
        TextView tvTrend = findViewById(R.id.tv_trend);
        setConfigTextColor(tvTrend, true, configInfo);
        TextView tvParty = findViewById(R.id.tv_party);
        setConfigTextColor(tvParty, true, configInfo);
        TextView tvMine = findViewById(R.id.tv_mine);
        setConfigTextColor(tvMine, true, configInfo);
        tvBadge = findViewById(R.id.tv_badge);
        mBotChatView = findViewById(R.id.bot_chat_view);
        if (findViewById(R.id.btn_home) != null) {
            findViewById(R.id.btn_home).setOnClickListener(this);
            findViewById(R.id.btn_trend).setOnClickListener(this);
            llParty.setOnClickListener(this);
            findViewById(R.id.btn_message).setOnClickListener(this);
            findViewById(R.id.btn_mine).setOnClickListener(this);
            mBotChatView.setOnClickListener(this);
            findViewById(R.id.bot_all_sel).setOnClickListener(this);
            findViewById(R.id.bot_del).setOnClickListener(this);
        }
        mHomeFragment = new HomeFragment();
        fragmentList.add(mHomeFragment);
        final FragmentVpAdapter2 fragmentPagerAdapter = new FragmentVpAdapter2(this, fragmentList);
        if (vp != null) {
            vp.setAdapter(fragmentPagerAdapter);
            vp.setUserInputEnabled(false);
            vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    performAccost();
                    views.get(prevPosition).setEnabled(true);
                    views.get(position).setEnabled(false);
                    // GlideUtils.loadRouteImage(mContext, viewHome, mIcon == null ? "" : mIcon.ic_tab_home_normal, R.mipmap.ic_tab_home_normal);
//                    GlideUtils.loadRouteImage(mContext, viewTrend, mIcon == null ? "" : mIcon.ic_tab_trend_normal, R.mipmap.ic_tab_trend_normal);
//                    GlideUtils.loadRouteImage(mContext, viewMessage, mIcon == null ? "" : mIcon.ic_tab_message_normal, R.mipmap.ic_tab_message_normal);
//                    GlideUtils.loadRouteImage(mContext, viewMine, mIcon == null ? "" : mIcon.ic_tab_mine_normal, R.mipmap.ic_tab_mine_normal);
                    if (viewHome.isAnimating()) viewHome.cancelAnimation();
                    if (viewTrend.isAnimating()) viewTrend.cancelAnimation();
                    if (viewParty.isAnimating()) viewParty.cancelAnimation();
                    if (viewMessage.isAnimating()) viewMessage.cancelAnimation();
                    if (viewMine.isAnimating()) viewMine.cancelAnimation();

                    viewHome.setFrame(1);
                    viewTrend.setFrame(1);
                    viewParty.setFrame(1);
                    viewMessage.setFrame(1);
                    viewMine.setFrame(1);
                    switch (position) {
                        case 0:
                            viewHome.playAnimation();
                            //GlideUtils.loadRouteImage(mContext, viewHome, mIcon == null ? "" : mIcon.ic_tab_home_selector, R.mipmap.ic_tab_home_selector);
                            break;

                        case 1:
                            viewTrend.playAnimation();
                            //GlideUtils.loadRouteImage(mContext, viewTrend, mIcon == null ? "" : mIcon.ic_tab_trend_selector, R.mipmap.ic_tab_trend_selector);
                            break;
                        case 2:
                            if (mIsShowParty) {
                                viewParty.playAnimation();
                            } else {
                                viewMessage.playAnimation();
                            }
                            //GlideUtils.loadRouteImage(mContext, viewTrend, mIcon == null ? "" : mIcon.ic_tab_trend_selector, R.mipmap.ic_tab_trend_selector);
                            break;
                        case 3:
                            if (mIsShowParty) {
                                viewMessage.playAnimation();
                            } else {
                                viewMine.playAnimation();
                            }
                            //GlideUtils.loadRouteImage(mContext, viewMessage, mIcon == null ? "" : mIcon.ic_tab_message_selector, R.mipmap.ic_tab_message_selector);
                            break;

                        case 4:
                            viewMine.playAnimation();
                            //GlideUtils.loadRouteImage(mContext, viewMine, mIcon == null ? "" : mIcon.ic_tab_mine_selector, R.mipmap.ic_tab_mine_selector);
                            break;
                    }

                    mTextViews.get(prevPosition).setEnabled(true);
                    setConfigTextColor(mTextViews.get(prevPosition), true, configInfo);
                    setConfigTextColor(mTextViews.get(position), false, configInfo);
                    mTextViews.get(prevPosition).setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    mTextViews.get(position).setEnabled(false);
                    mTextViews.get(position).setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    prevPosition = position;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && TextUtils.equals(ChatTabFragment.class.getName(), fragmentList.get(position).getClass().getName())) {
                        if (com.blankj.utilcode.util.SPUtils.getInstance().getBoolean(Interfaces.SP_MESSAGE_TAB_FIRST, true)) {
                            com.blankj.utilcode.util.SPUtils.getInstance().put(Interfaces.SP_MESSAGE_TAB_FIRST, false);
                            new MessageNotificationPopWindow(mContext).showPopupWindow();
                        }
                    }
                }
            });
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                trendFragment = new TrendFragment();
                fragmentList.add(trendFragment);
                if (mIsShowParty) {
                    partyFragment = new PartyFragment();
                    fragmentList.add(partyFragment);
                }
                fragmentList.add(new ChatTabFragment());
                fragmentList.add(new MineFragment());
                vp.setOffscreenPageLimit(5);
                fragmentPagerAdapter.notifyDataSetChanged();
                initBus();
            }
        }, 1600);

        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null) {
            mIcon = service.getConfigInfo().sys.icon;
        }
        //GlideUtils.loadRouteImage(mContext, viewHome, mIcon == null ? "" : mIcon.ic_tab_home_selector, R.mipmap.ic_tab_home_selector);
//        GlideUtils.loadRouteImage(mContext, viewTrend, mIcon == null ? "" : mIcon.ic_tab_trend_normal, R.mipmap.ic_tab_trend_normal);
//        GlideUtils.loadRouteImage(mContext, viewMessage, mIcon == null ? "" : mIcon.ic_tab_message_normal, R.mipmap.ic_tab_message_normal);
//        GlideUtils.loadRouteImage(mContext, viewMine, mIcon == null ? "" : mIcon.ic_tab_mine_normal, R.mipmap.ic_tab_mine_normal);

        views = new ArrayList<>();
        mTextViews = new ArrayList<>();
        if (viewHome != null) {
            viewHome.setEnabled(false);
            views.add(viewHome);
            views.add(viewTrend);
            views.add(viewParty);
            views.add(viewMessage);
            views.add(viewMine);
            mTextViews.add(tvHome);
            mTextViews.add(tvTrend);
            if (mIsShowParty) mTextViews.add(tvParty);
            mTextViews.add(tvMessage);
            mTextViews.add(tvMine);
            tvHome.setEnabled(false);
            tvHome.setTypeface(mTextViews.get(prevPosition).getTypeface(), Typeface.BOLD);
        }
    }

    private int txt_color_enable;
    private int txt_color_unenable;

    /**
     * 设置配置的文本颜色 读取  /storage/emulated/0/Android/data/com.tftechsz.peony/cache/tfpeony/main_tab_lottie/config.txt
     *
     * @param textView  TextView
     * @param isEnabled 是否可点击
     */
    private void setConfigTextColor(TextView textView, boolean isEnabled, ConfigInfo configInfo) {
        if (txt_color_enable != 0 && txt_color_unenable != 0) { //如果有值直接设置
            textView.setTextColor(isEnabled ? txt_color_enable : txt_color_unenable);
            return;
        }
        txt_color_enable = Utils.getColor(R.color.color_black_ff666666);
        txt_color_unenable = Utils.getColor(R.color.colorPrimary);
        if (configInfo == null || configInfo.sys == null || configInfo.sys.is_main_tab_lottie_config == 0) {
            return;
        }
        final File file = new File(DownloadHelper.FILE_PATH + File.separator + Interfaces.MAIN_TAB_LOTTIE_FOLDER + File.separator + "config.txt");
        if (FileUtils.isFileExists(file)) { //配置文件存在
            Map<String, String> map = new HashMap<>();
            try {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String mimeTypeLine;
                while ((mimeTypeLine = br.readLine()) != null) {
                    if (mimeTypeLine.contains("=")) {
                        String[] split = mimeTypeLine.trim().split("=");
                        if (split.length == 2) {
                            map.put(split[0], split[1]);
                        }
                    }
                }
                String txtColorEnable = map.get("txt_color_enable");
                String txtColorUnEnable = map.get("txt_color_unenable");
                txt_color_enable = Color.parseColor(txtColorEnable);
                txt_color_unenable = Color.parseColor(txtColorUnEnable);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 设置配置的lottie动画
     * /storage/emulated/0/Android/data/com.tftechsz.peony/cache/tfpeony/main_tab_lottie/{fileName}
     *
     * @param mLottieAnimationView LottieAnimationView
     * @param fileName             文件名
     */
    private void setConfigLottie(final LottieAnimationView mLottieAnimationView, final String fileName, ConfigInfo configInfo) {
        mLottieAnimationView.setAnimation(fileName);
        if (configInfo == null || configInfo.sys == null || configInfo.sys.is_main_tab_lottie_config == 0) {
            return;
        }
        final File file = new File(DownloadHelper.FILE_PATH + File.separator + Interfaces.MAIN_TAB_LOTTIE_FOLDER + File.separator + fileName);
        if (FileUtils.isFileExists(file)) {
            try {
                LottieCompositionFactory.fromZipStream(new ZipInputStream(new FileInputStream(file.getAbsolutePath())), null)
                    .addListener(new LottieListener<LottieComposition>() {
                        @Override
                        public void onResult(final LottieComposition lottieComposition) {
                            if (lottieComposition != null) {
                                mLottieAnimationView.setComposition(lottieComposition);
                            }
                        }
                    });
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        parseIntent();
        int pos = intent.getIntExtra("position", 0);
        if (prevPosition == pos) {
            return;
        }
        vp.setCurrentItem(pos, false);
    }


    private void parseIntent() {
        Intent intent = getIntent();
        IMMessage message = (IMMessage) intent.getSerializableExtra(
            NimIntent.EXTRA_NOTIFY_CONTENT);
        if (null != message) {
            intent.removeExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            switch (message.getSessionType()) {
                case P2P:
                    if (TextUtils.equals(message.getSessionId(), Constants.CUSTOMER_SERVICE) || TextUtils.equals(message.getSessionId(), Constants.ACTIVITY_NOTICE)) {
                        IMMessage lastMessage = NIMClient.getService(MsgService.class).queryLastMessage(message.getSessionId(), SessionTypeEnum.P2P);
                        if (lastMessage != null) {
                            if (TextUtils.isEmpty(lastMessage.getAttachStr()) && TextUtils.equals(message.getSessionId(), Constants.CUSTOMER_SERVICE)) {
                                ARouterUtils.toNoticeActivity(Constants.CUSTOMER_SERVICE);
                            } else {
                                ConfigInfo.MineInfo pushMessageDto = JSON.parseObject(lastMessage.getAttachStr(), ConfigInfo.MineInfo.class);
                                if (pushMessageDto != null) {
                                    if (pushMessageDto.is_remove == 1) {
                                        NIMClient.getService(MsgService.class).deleteChattingHistory(lastMessage);
                                    }
                                    CommonUtil.performLink(MainActivity.this, pushMessageDto, 0, 0);
                                }
                            }
                        } else {
                            if (TextUtils.equals(message.getSessionId(), Constants.CUSTOMER_SERVICE))
                                ARouterUtils.toNoticeActivity(Constants.CUSTOMER_SERVICE);
                        }
                    } else {
                        ARouterUtils.toChatP2PActivity(message.getSessionId(), NimUIKit.getCommonP2PSessionCustomization(), null);
                    }
                    break;
                case Team:
                    ARouterUtils.toChatTeamActivity(message.getSessionId(), NimUIKit.getCommonTeamSessionCustomization(), null);
                    break;
            }
        }
    }


    @Override
    protected void initData() {
        super.initData();
        MMKVUtils.getInstance().encode(Constants.PARTY_IS_ON_SEAT, false);
        initService();
        startWork();
    }

    BDLocationManager bdLocationManager;

    private void initService() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // 获取服务的操作对象
                CallService.MyBinder binder = (CallService.MyBinder) service;
                binder.getService();

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        bdLocationManager = new BDLocationManager();
        bdLocationManager.addListener(MainActivity.this);
        requestPermissions();
        Intent intent = new Intent(BaseApplication.getInstance(), CallService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void startWork() {
        ConfigInfo configInfo = service.getConfigInfo();
        if (configInfo != null && configInfo.sys != null) {
            Utils.logE("是否压缩: " + configInfo.sys.is_video_compress + "  压缩率: " + configInfo.sys.video_compress_bitrate);
            com.blankj.utilcode.util.SPUtils.getInstance().put(Interfaces.SP_IS_VIDEO_COMPRESS, configInfo.sys.is_video_compress == 1);
            com.blankj.utilcode.util.SPUtils.getInstance().put(Interfaces.SP_VIDEO_COMPRESS_FILTER, configInfo.sys.video_compress_filter);
            com.blankj.utilcode.util.SPUtils.getInstance().put(Interfaces.SP_VIDEO_COMPRESS_BITRATE, configInfo.sys.video_compress_bitrate);
            com.blankj.utilcode.util.SPUtils.getInstance().put("sp_vip_pic_host", configInfo.sys.vip_pic_host);
            if (configInfo.sys.loading_h5 != null && configInfo.sys.loading_h5.onhot != null && configInfo.sys.loading_h5.onhot.zip_source != null) {
                final File file = new File(DownloadHelper.FILE_PATH + File.separator + MD5Util.toMD516(configInfo.sys.loading_h5.onhot.zip_source) + ".zip");
                if (!file.exists()) {
                    DownloadHelper.downloadZip(configInfo.sys.loading_h5.onhot.zip_source, new DownloadHelper.DownloadListener() {
                        @Override
                        public void completed() {
                            Utils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ZipUtils.unzipFile(file.getPath(), DownloadHelper.FILE_PATH + File.separator);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_DOWN_HOT_SUCCESS));
                                }
                            });
                        }

                        @Override
                        public void failed() {
                        }

                        @Override
                        public void onProgress(int progress) {
                        }
                    });
                }
            }

            if (service.getConfigInfo() != null && service.getConfigInfo().sys != null) {
                if (service.getConfigInfo().sys.noble_zip != null && !TextUtils.isEmpty(service.getConfigInfo().sys.noble_zip.zip_source)) {
                    Data data = new Data.Builder().putString(Interfaces.WORKER_NOBLE_SVGA, service.getConfigInfo().sys.noble_zip.zip_source).build();//创建需要传入的数据,注意不支持序列化数据传入
                    OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DownGiftWork.class)//一次性Work请求
                        .setInitialDelay(2, TimeUnit.SECONDS)
                        .setInputData(data)
                        .build();
                    WorkManager.getInstance(MainActivity.this).enqueue(request);//添加到WorkManager队列中
                }
                if (service.getConfigInfo().sys.bubble_zip != null && !TextUtils.isEmpty(service.getConfigInfo().sys.bubble_zip.zip_source)) {
                    Data data = new Data.Builder().putString(Interfaces.WORKER_BUBBLE_PNG, service.getConfigInfo().sys.bubble_zip.zip_source).build();//创建需要传入的数据,注意不支持序列化数据传入
                    OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DownGiftWork.class)//一次性Work请求
                        .setInitialDelay(2, TimeUnit.SECONDS)
                        .setInputData(data)
                        .build();
                    WorkManager.getInstance(MainActivity.this).enqueue(request);//添加到WorkManager队列中
                }
            }
        }
    }


    /**
     * 显示更新弹窗
     */
    private void showUpdateDialog(final UpdateInfo updateInfo) {
        if (null == dialog)
            dialog = new UpdateDialog(MainActivity.this, updateInfo);

        dialog.show();
        dialog.setOnSureClick(new UpdateDialog.OnSureClick() {
            @Override
            public void onCancel() {
                if (isFirstSign) {
                    isFirstSign = false;
                    getP().signList();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        ConfigInfo configInfo = service.getConfigInfo();
        if (configInfo != null && configInfo.share_config != null && configInfo.share_config.is_show_pop_notice == 0) {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        ConfigInfo configInfo = service.getConfigInfo();
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_DELETE_MESSAGE_CANCEL));
        if (configInfo != null && configInfo.share_config != null && configInfo.share_config.is_show_pop_notice == 0) {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        }
    }


    /**
     * 通知更新
     */
    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
//                .compose(MainActivity.this.<CommonEvent>bindToLifecycle())
            .subscribe(
                new Consumer<CommonEvent>() {
                    @Override
                    public void accept(CommonEvent event) {
                        if (event.type == Constants.NOTIFY_APP_STOPPED) {
                            getP().updateLocationReq("close");
                            switchLogo();
                        } else if (event.type == Constants.NOTIFY_APP_STARTED) {
                            getP().updateLocationReq("open");
                            InvitedEvent invitedEvent = (InvitedEvent) SPUtils.readObject(BaseApplication.getInstance(), Constants.INVITED_EVENT);
                            if (invitedEvent != null) {
                                ARouterUtils.toCallActivity(invitedEvent, service.getCallType(), service.getMatchType(), service.getCallId(), service.getCallIsMatch());
                            }
                        } else if (event.type == Constants.NOTIFY_ALIPAY) {
                            getP().showRechargePop();
                        } else if (event.type == Constants.NOTIFY_DELETE_MESSAGE) {   //删除聊天
                            mBotChatView.setVisibility(View.VISIBLE);
                        } else if (event.type == Constants.NOTIFY_DELETE_MESSAGE_CANCEL) {  //取消删除
                            mBotChatView.setVisibility(View.GONE);
                        } else if (event.type == Constants.NOTIFY_TOP_CONFIG) {   //数显config
                            ConfigInfo configInfo = service.getConfigInfo();
                            if (configInfo != null && configInfo.share_config != null && configInfo.share_config.is_show_pop_notice == 1) {
                                NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
                            }
                        } else if (event.type == Constants.NOTIFY_ACCOST_POPUP) {  //搭讪pop
                            mAccostPopList = JSON.parseArray(event.code, ChatMsg.AccostPopup.class);
                            performAccost();
                        } else if (event.type == Constants.NOTIFY_USER_LEVEL_UP) {  //个人等级提升
                            ChatMsg.UserLevelUp levelUp = JSON.parseObject(event.code, ChatMsg.UserLevelUp.class);
                            if (levelUp != null) {
                                if (levelUp.user_type.equals("rich")) {//土豪值升级
                                    if (mRichLevelUpPop == null) {
                                        mRichLevelUpPop = new UserLevelUpPop(mContext);
                                    }
                                    if (mCharmLevelUpPop == null || !mCharmLevelUpPop.isShowing()) {
                                        mRichLevelUpPop.setRotate();
                                    }
                                    mRichLevelUpPop.setData(levelUp);
                                    mRichLevelUpPop.showPopupWindow();

                                } else if (levelUp.user_type.equals("charm")) {//魅力值升级
                                    if (mCharmLevelUpPop == null) {
                                        mCharmLevelUpPop = new UserLevelUpPop(mContext);
                                    }
                                    if (mRichLevelUpPop == null || !mRichLevelUpPop.isShowing()) {
                                        mCharmLevelUpPop.setRotate();
                                    }
                                    mCharmLevelUpPop.setData(levelUp);
                                    mCharmLevelUpPop.showPopupWindow();
                                }
                            }
                        } else if (event.type == Constants.NOTIFY_ENTER_PARTY_ROOM) {
                            startCheckService();
                            startCheckPartyService();
                        } else if (event.type == Constants.NOTIFY_START_LOC) {  //通知了开启定位
                            bdLocationManager.removeListener();
                            bdLocationManager = new BDLocationManager();
                            bdLocationManager.addListener(MainActivity.this);
                            bdLocationManager.initGPS();
                            bdLocationManager.startLoc();
                        } else if (event.type == Constants.NOTIFY_REQUEST_LOC) {  //询问授权
                            requestPermissions();
                        }
                    }
                }
            ));

        mCompositeDisposable.add(RxBus.getDefault().toObservable(UnReadMessageEvent.class)
//                .compose(MainActivity.this.<UnReadMessageEvent>bindToLifecycle())
            .subscribe(
                new Consumer<UnReadMessageEvent>() {
                    @Override
                    public void accept(final UnReadMessageEvent event) {
                        Utils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (tvBadge == null) return;
                                tvBadge.setVisibility(event.num > 0 ? View.VISIBLE : View.GONE);
                                tvBadge.setText(event.num > 99 ? "99+" : String.valueOf(event.num));
                                if (partyFragment != null) {
                                    partyFragment.setMessageNum(event.p2pNumber);
                                }
                            }
                        });

                    }
                }
            ));
        mCompositeDisposable.add(RxBus.getDefault().toObservable(UpdateEvent.class)
//                .compose(MainActivity.this.<UpdateEvent>bindToLifecycle())
            .subscribe(
                new Consumer<UpdateEvent>() {
                    @Override
                    public void accept(UpdateEvent event) {
                        if (event.alert_accost != null) {
                            mEvent = event;
                        }
                        if (event.updateInfo != null) {
                            showUpdateDialog(event.updateInfo);
                        } else {
                            if (dialog != null && dialog.isShowing())
                                return;
                            if (isFirstSign) {
                                isFirstSign = false;
                                p.signList();
                            }
                        }
                    }
                }
            ));
        mCompositeDisposable.add(RxBus.getDefault().toObservable(MessageEvent.class)
            .subscribe(new Consumer<MessageEvent>() {
                @Override
                public void accept(MessageEvent messageEvent) throws Exception {
                    mMessageEvent = messageEvent;
                }
            }));
        getP().register(true);

    }


    /**
     * 请求权限
     */
    private void requestPermissions() {
        if (mCompositeDisposable == null)
            mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(new RxPermissions(MainActivity.this)
            .requestEach(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE)
            .subscribe(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) {
                    if (permission.granted) {
                        if (permission.name.equals(Manifest.permission.ACCESS_FINE_LOCATION) || permission.name.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            bdLocationManager.initGPS();
                            bdLocationManager.startLoc();
                        }
                    } else {
                        MMKVUtils.getInstance().removeKey(service.getUserId() + Constants.LOCATION_LATITUDE);
                        MMKVUtils.getInstance().removeKey(service.getUserId() + Constants.LOCATION_LONGITUDE);
                        MMKVUtils.getInstance().removeKey(service.getUserId() + Constants.LOCATION_CITY);
                        MMKVUtils.getInstance().removeKey(service.getUserId() + Constants.LOCATION_PROVINCE);
                        getP().updateLocationReq("open");
                    }
                }
            }));
    }


    private void performAccost() {
        //FIXME 暂无一键搭讪
//        if (vp.getCurrentItem() != 3 && mAccostPopList != null && mAccostPopList.size() > 0) {
//            if (mOneKeyAccostPopWindow == null)
//                mOneKeyAccostPopWindow = new OneKeyAccostPopWindow(mContext);
//            mOneKeyAccostPopWindow.setUserList(mAccostPopList);
//            mOneKeyAccostPopWindow.showPopupWindow();
//            mAccostPopList = null;
//        }
    }

    @Override
    public MainPresenter initPresenter() {
        return new MainPresenter();
    }


    private int prevViewId;

    /**
     * 上传日志统计
     *
     * @param type 1.首页2.动态3.派对4.消息5.我的
     */
    private void trackNavbarClick(int type) {
        ARouter.getInstance()
            .navigation(MineService.class)
            .trackEvent("底部导航栏点击", "navigation_bar_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), type, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME)), null);
//语音房间列表页曝光
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_home) {
            trackNavbarClick(1);
            if (ClickUtil.canOperate()) {
                if (prevPosition == 0) {
                    return;
                }
                prevViewId = 0;
            } else {
                if (prevViewId == id) {
                    //第二次刷新
                    if (mHomeFragment != null) {
                        mHomeFragment.autoRefresh();
                    }
                    prevViewId = 0;
                }
            }
            vp.setCurrentItem(0, false);

        } else if (id == R.id.btn_trend) {
            trackNavbarClick(2);
            if (ClickUtil.canOperate()) {
                if (prevPosition == 1) {
                    return;
                }
                prevViewId = 0;
            } else {
                if (prevViewId == id) {
                    //第二次刷新
                    if (trendFragment != null) {
                        trendFragment.refreshRec();
                    }
                    prevViewId = 0;
                }
            }
            vp.setCurrentItem(1, false);
        } else if (id == R.id.btn_party) {
            trackNavbarClick(3);
            if (partyFragment != null) {
                partyFragment.visitPartyList();
            }
            vp.setCurrentItem(fragmentList.size() - 3, false);
        } else if (id == R.id.btn_message) {
            trackNavbarClick(4);
            long now = System.currentTimeMillis();
            if ((now - mExitTime) > 500) {
                mExitTime = System.currentTimeMillis();
            } else {
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_MESSAGE_DOUBLE_CLICK));
            }

            if (prevPosition == fragmentList.size() - 2) {
                return;
            }
            vp.setCurrentItem(fragmentList.size() - 2, false);
        } else if (id == R.id.btn_mine) {
            trackNavbarClick(5);
            if (prevPosition == fragmentList.size() - 1) {
                return;
            }
            vp.setCurrentItem(fragmentList.size() - 1, false);
        } else if (id == R.id.bot_all_sel) { //全选
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_DELETE_CHOOSE_MESSAGE_ALL));
        } else if (id == R.id.bot_del) { //删除
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_DELETE_CHOOSE_MESSAGE));
            mBotChatView.setVisibility(View.GONE);
        }
        prevViewId = id;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bdLocationManager.stopLoc();
        bdLocationManager.removeListener();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
            mServiceConnection = null;
        }
        if (partyService.isRunFloatService()) {
            partyService.stopFloatService();
        }
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPlayService() != null && BaseMusicHelper.get().getPlayService().isPlaying()) {
            BaseMusicHelper.get().getPlayService().onDestroy();
        }
        if (mPlayServiceConnection != null) {
            unbindService(mPlayServiceConnection);
        }
        if (mPartyServiceConnection != null)
            unbindService(mPartyServiceConnection);
    }


    @Override
    public void onLocationChanged(BDLocation location) {
        LogUtil.e("================", "执行定位代码");
        if (location != null) {
            UpdateLocationReq addressReq = new UpdateLocationReq();
            addressReq.fromTpe = "open";
            if (location.getLatitude() <= 4.9E-324 && !TextUtils.isEmpty(location.getAddress().address)) {
                addressReq.latitude = location.getLatitude();
                addressReq.longitude = location.getLongitude();
                addressReq.province = location.getProvince();
                addressReq.city = location.getCity();
                String lat = MMKVUtils.getInstance().decodeString(service.getUserId() + Constants.LOCATION_LATITUDE);
                //存储经纬度
                MMKVUtils.getInstance().encode(service.getUserId() + Constants.LOCATION_LATITUDE, location.getLatitude());
                MMKVUtils.getInstance().encode(service.getUserId() + Constants.LOCATION_LONGITUDE, location.getLongitude());
                MMKVUtils.getInstance().encode(service.getUserId() + Constants.LOCATION_CITY, location.getCity());
                MMKVUtils.getInstance().encode(service.getUserId() + Constants.LOCATION_PROVINCE, location.getProvince());
                if (TextUtils.isEmpty(lat)) {
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_REFRESH_NEAR));
                }
            }
            getP().updateLocation(addressReq);
        }
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
        if (dialog != null) {
            dialog.onActivityResult(requestCode, resultCode, data);
        }
    }


    //双击退出app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBotChatView != null && mBotChatView.getVisibility() == View.VISIBLE) {
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_DELETE_MESSAGE_CANCEL));
                return true;
            }
            if (mMessageEvent == null || mMessageEvent.messageInfoList == null || mMessageEvent.messageInfoList.size() <= 0) {
                getP().showRetainNoMessagePop(this);
            } else {
                getP().showRetainPop(this, mMessageEvent.messageInfoList.get(0));
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void signListSucceeded(SignInBean data) {
        List<SignInBean.SignInListBean> list = data.list;
        for (SignInBean.SignInListBean bean : list) {
            if (data.start_day == bean.day_number) {
                signCost = "+" + bean.cost;
                break;
            }
        }

        if (mSignInPopWindow == null) {
            mSignInPopWindow = new SignInPopWindow(mContext, data, this);
            mSignInPopWindow.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //if (!signTag) {
                    showAccostDialog();
                    // }
                }
            });
        }
        if (!mSignInPopWindow.isShowing()) {
            mSignInPopWindow.showPopupWindow();
        }
    }

    //获取签到列表失败
    @Override
    public void signListFail() {
        showAccostDialog();
    }

    @Override
    public void signInSucceeded() {
        if (mSignInPopWindow != null) {
            mSignInPopWindow.dismiss();
        }
        new SignSucessPopWindow(mContext).showPop(signCost).setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                showAccostDialog();
            }
        });
    }

    private void showAccostDialog() {
        if (mEvent != null && null != mEvent.alert_accost
            && null != mEvent.alert_accost.alert_accost_list
            && mEvent.alert_accost.alert_accost_list.size() > 0
            && null != mEvent.alert_accost.gift_info) {
            p.showAccostDialog(MainActivity.this, mEvent.alert_accost);
        }
    }

    //点击签到
    @Override
    public void signInPopClick() {
        p.sign();
    }

    /**
     * 检测服务
     */
    private void startCheckService() {
        if (BaseMusicHelper.get().getPlayService() == null) {
            bindService();
        }
    }

    /**
     * 开启服务
     */
    private void startService() {
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        mPlayServiceConnection = new PlayServiceConnection();
        bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private static class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AppLogUtils.e("onServiceConnected" + name);
            final PlayService playService = ((PlayService.PlayBinder) service).getService();
            BaseMusicHelper.get().setPlayService(playService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            AppLogUtils.e("onServiceDisconnected" + name);
        }
    }


    /**
     * 检测服务
     */
    private void startCheckPartyService() {
        if (BaseMusicHelper.get().getPartyService() == null) {
            startPartyService();
        }
    }

    /**
     * 开启服务
     */
    private void startPartyService() {
        Intent intent = new Intent();
        intent.setClass(this, PartyAudioService.class);
        mPartyServiceConnection = new PartyServiceConnection();
        bindService(intent, mPartyServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private static class PartyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AppLogUtils.e("onServiceConnected11111" + name);
            final PartyAudioService playService = ((PartyAudioService.MyBinder) service).getService();
            BaseMusicHelper.get().setPartyService(playService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            AppLogUtils.e("onServiceDisconnected" + name);
        }
    }

    /**
     * 动态切换logo
     */
    private void switchLogo() {
        mPackageManager = getPackageManager();
        componentName2 = new ComponentName(getBaseContext(), "com.tftechsz.peony.SplashActivity1");
        componentName1 = new ComponentName(getBaseContext(), "com.tftechsz.peony.SplashActivity");
        if (service != null) {
            if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.is_main_tab_lottie_config == 1) {
                enableComponent(componentName2);
                disableComponent(componentName1);
            } else {
                enableComponent(componentName1);
                disableComponent(componentName2);
            }
        }
    }


    /**
     * 启动组件 - 替换logo
     *
     * @param componentName 组件名
     */
    private void enableComponent(ComponentName componentName) {
        //此方法用以启用和禁用组件，会覆盖Androidmanifest文件下定义的属性
        mPackageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * 禁用组件- 替换logo
     *
     * @param componentName 组件名
     */
    private void disableComponent(ComponentName componentName) {
        mPackageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }


}
