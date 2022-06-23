package com.tftechsz.common.widget.pop;

import static com.tftechsz.common.base.BasePresenter.applySchedulers;
import static com.tftechsz.common.constant.Interfaces.FIY_NUMBER;
import static com.tftechsz.common.constant.Interfaces.SCENE_NUMBER;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.PaymentTypeDto;
import com.netease.nim.uikit.common.UserInfo;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.AlipayResultInfo;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.entity.RechargeDto;
import com.tftechsz.common.entity.RechargeQuickDto;
import com.tftechsz.common.entity.SXYWxPayResultInfo;
import com.tftechsz.common.entity.WxPayResultInfo;
import com.tftechsz.common.event.BuriedPointExtendDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.PayService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.PayTextClick;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RechargePopWindow extends BaseBottomPop implements View.OnClickListener {
    private CompositeDisposable mCompositeDisposable;
    private final Context mContext;
    private TextView mTvCoin;
    private ImageView mIvBanner;
    private TextView mTvContact;
    private RecyclerView mRvRecharge;
    private RechargeQuickDto mRechargeDto;
    private RechargeDto mRechargeData;  //选中支付金额
    private PaymentTypeDto mPayTypeData;  //选中支付方式
    private UserProviderService userService;
    private IWXAPI mApi;
    private PayService service;
    private PublicService payService;
    private TextView mTvPay;
    private int formType; //0默认   1家族  2派对

    /**
     * 2.from_type(1.发送消息2.送礼物3.音视频通话4.转盘抽奖)
     * <p>
     * 3.scene(触发场景1.私信2.任务、充值引导弹窗点击充值3.个人主页4.家族5.语音房)
     */
    public int from_type;
    public int scene;
    public int room_id;
    public int family_id;
    private String toUserId;

    public RechargePopWindow(Context context, int from_type, int scene) {
        super(context);
        mContext = context;
        this.scene = scene;
        this.from_type = from_type;
        newInit();
    }

    public RechargePopWindow(Context context, int from_type, int scene, int room_id) {
        super(context);
        mContext = context;
        this.scene = scene;
        this.from_type = from_type;
        this.room_id = room_id;
        newInit();
    }

    public RechargePopWindow(Context context, int from_type, int scene, int room_id, int family_id) {
        super(context);
        this.room_id = room_id;
        this.family_id = family_id;
        mContext = context;
        this.scene = scene;
        this.from_type = from_type;
        newInit();
    }

    public RechargePopWindow(Context context, int from_type, int scene, int room_id, int family_id, String userId) {
        super(context);
        this.room_id = room_id;
        this.toUserId = userId;
        this.family_id = family_id;
        mContext = context;
        this.scene = scene;
        this.from_type = from_type;
        newInit();
    }

    private void newInit() {
        userService = ARouter.getInstance().navigation(UserProviderService.class);
        payService = RetrofitManager.getInstance().createConfigApi(PublicService.class);
        service = ARouter.getInstance().navigation(PayService.class);
        mCompositeDisposable = new CompositeDisposable();
        mIvBanner = findViewById(R.id.iv_quick_recharge_banner);
        mTvCoin = findViewById(R.id.tv_coin);
        mRvRecharge = findViewById(R.id.rv_recharge);
        mTvPay = findViewById(R.id.tv_pay);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        mRvRecharge.setLayoutManager(layoutManager);
        findViewById(R.id.iv_close).setOnClickListener(v -> dismiss());
        findViewById(R.id.tv_pay).setOnClickListener(this);
        findViewById(R.id.tv_more).setOnClickListener(this);
        mTvContact = findViewById(R.id.tv_contact);
        setOutSideDismiss(false);
        initRxBus();
    }


    public void requestData() {
        if (mRechargeDto == null)
            getRechargeList();
        else
            initData();
    }

    private void initData() {
        if (mRechargeDto == null) return;
        if (userService.getUserInfo() == null) return;

        ConfigInfo configInfo = userService.getConfigInfo();
        if (configInfo != null && configInfo.api != null && configInfo.api.recharge_bottom != null && configInfo.api.recharge_bottom.size() > 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (ConfigInfo.MineInfo mineInfo : configInfo.api.recharge_bottom) {
                builder.append(mineInfo.title);
                if (!TextUtils.isEmpty(mineInfo.link)) {
                    int start = builder.toString().indexOf(mineInfo.title);
                    builder.setSpan(new PayTextClick(mContext, mineInfo), start, start + mineInfo.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new UnderlineSpan(), start, start + mineInfo.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            mTvContact.setText(builder);
            mTvContact.setMovementMethod(LinkMovementMethod.getInstance());
        }
        RechargeAdapter adapter = new RechargeAdapter(mRechargeDto.payment, mRechargeDto, userService.getUserInfo());
        mRvRecharge.setAdapter(adapter);
        View footerView = LayoutInflater.from(mContext).inflate(R.layout.pop_recharge_footer, null);
        RecyclerView mRvPayWay = footerView.findViewById(R.id.rv_pay_way);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext);
        mRvPayWay.setLayoutManager(layoutManager1);
        adapter.addFooterView(footerView);
        PayWayAdapter wayAdapter = new PayWayAdapter(mRechargeDto.payment_type, mRechargeDto, userService.getUserInfo());
        mRvPayWay.setAdapter(wayAdapter);
        //获取默认选中
        for (int i = 0; i < mRechargeDto.payment.size(); i++) {
            if (mRechargeDto.payment.get(i).is_active == 1) {
                adapter.setCheckPositions(i);
                mRechargeData = adapter.getItem(i);
            }
        }
        //获取默认选中
        for (int i = 0; i < mRechargeDto.payment_type.size(); i++) {
            if (mRechargeDto.payment_type.get(i).is_active == 1) {
                wayAdapter.setCheckPositions(i);
                mPayTypeData = wayAdapter.getItem(i);
            }
        }
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            adapter.setCheckPositions(position);
            mRechargeData = adapter.getItem(position);
        });
        wayAdapter.setOnItemClickListener((adapter12, view, position) -> {
            wayAdapter.setCheckPositions(position);
            mPayTypeData = wayAdapter.getItem(position);
        });

        if (!TextUtils.isEmpty(mRechargeDto.banner)) {
            GlideUtils.loadImage(mContext, mIvBanner, mRechargeDto.banner);
        } else {
            mIvBanner.setImageResource(R.mipmap.quick_recharge);
        }
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_recharge);
    }


    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                getCoin();
                                dismiss();
                            }
                        }
                ));
    }

    /**
     * 获取个人金币
     */
    public void getCoin() {
        userService.getField("property", "coin", new ResponseObserver<BaseResponse<IntegralDto>>() {
            @Override
            public void onSuccess(BaseResponse<IntegralDto> response) {
                if (response.getData() != null) {
                    UserInfo userInfo = userService.getUserInfo();
                    userInfo.setCoin(response.getData().coin);
                    userService.setUserInfo(userInfo);
                    setCoin(response.getData().coin);
                }
            }
        });
    }


    /**
     * 获取充值列表
     */
    private void getRechargeList() {
        mCompositeDisposable.add(payService.quickRecharge().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RechargeQuickDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RechargeQuickDto> response) {
                        if (null != response.getData()) {
                            mRechargeDto = response.getData();
                            initData();
                        }
                    }
                }));
    }


    /**
     * 设置金币
     *
     * @param coin
     */
    public void setCoin(String coin) {
        mTvCoin.setText(coin + "金币");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_pay) {
            if (!ClickUtil.canOperate()) {
                return;
            }
            if (mPayTypeData != null && mRechargeData != null) {
                switch (mPayTypeData.type) {
                    case Interfaces.WECHAT:
                        getWxOrderNum(mRechargeData.id);
                        break;

                    case Interfaces.ALIPAY:
                        getOrderNum(mRechargeData.id);
                        break;

                    case Interfaces.SHOUXINYI_WECHAT:
                        if (!AppUtils.isWeChatAppInstalled(mContext)) {
                            Utils.toast("手机未安装微信");
                            return;
                        }
                        service.SXYWeChatPay(mRechargeData.id, Utils.getPayTypeFrom(formType), new ResponseObserver<BaseResponse<SXYWxPayResultInfo>>() {
                            @Override
                            public void onSuccess(BaseResponse<SXYWxPayResultInfo> response) {
                                Utils.putPayType(formType);
                                mApi = WXAPIFactory.createWXAPI(mContext, response.getData().app_id);
                                WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                                req.userName = response.getData().mini_program_original_id; // 填小程序原始id
                                req.path = response.getData().url;
                                req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
                                mApi.sendReq(req);
                            }
                        });
                        break;

                    case Interfaces.SHOUXINYI_ALIPAY:
                        if (!AppUtils.isAliPayInstalled(mContext)) {
                            Utils.toast("手机未安装支付宝");
                            return;
                        }
                        service.SXYalipay(mRechargeData.id, Utils.getPayTypeFrom(formType), new ResponseObserver<BaseResponse<String>>() {
                            @Override
                            public void onSuccess(BaseResponse<String> response) {
                                CommonUtil.startIntentToAliPay(mContext, response.getData());
                            }
                        });
                        break;
                }
                if (Utils.isPayTypeInFamily(formType)) {
                    ARouter.getInstance()
                            .navigation(MineService.class)
                            .trackEvent("弹窗_余额不足点击充值", "click", "pop_to_recharge",
                                    JSON.toJSONString(new BuriedPointExtendDto(new BuriedPointExtendDto.RechargeExtendDto(mPayTypeData.type, mRechargeData.coin))), null);
                }
            }

        } else if (v.getId() == R.id.tv_more) {  //更多
            if (Utils.isPayTypeInFamily(formType)) {
                ARouter.getInstance()
                        .navigation(MineService.class)
                        .trackEvent("余额不足点击更多", "click", "pop_to_more", JSON.toJSONString(new BuriedPointExtendDto()), null);
            }
            ARouterUtils.toRechargeActivity("", formType);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    /**
     * 支付宝
     */
    public void wakeUpAliPay(final String orderInfo) {
        Disposable disposable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
                    PayTask alipay = new PayTask((Activity) mContext);
                    String result = alipay.pay(orderInfo, true);
                    emitter.onNext(result);
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                    AlipayResultInfo payResult = new AlipayResultInfo(s);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        MobclickAgent.onEvent(mContext, "pay_success");
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
                        ToastUtil.showToast(BaseApplication.getInstance(), "支付成功");
                        if (Utils.isPayTypeInFamily(formType)) {
                            ARouter.getInstance().navigation(MineService.class).trackEvent(Interfaces.POINT_SCENE_PAY_SUCCESS,
                                    Interfaces.POINT_EVENT_PAGE, Interfaces.POINT_INDEX_PAY_SUCCESS, JSON.toJSONString(new BuriedPointExtendDto()), null);
                        }
                    } else {
                        ToastUtil.showToast(BaseApplication.getInstance(), "支付失败");
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * 支付宝
     */
    public void getOrderNum(int typeId) {
        service.alipay(typeId, Utils.getPayTypeFrom(formType), new ResponseObserver<BaseResponse<String>>() {
            @Override
            public void onSuccess(BaseResponse<String> response) {
                if (mContext instanceof Activity) {
                    wakeUpAliPay(response.getData());
                } else {
                    if (listener != null) {
                        listener.alipay(response.getData());
                    }
                }
            }
        });

    }

    /**
     * 微信
     */
    public void getWxOrderNum(int typeId) {
        if (!AppUtils.isWeChatAppInstalled(mContext)) {
            ToastUtil.showToast(mContext, "手机未安装微信");
            return;
        }

        service.wechatPay(typeId, Utils.getPayTypeFrom(formType), new ResponseObserver<BaseResponse<WxPayResultInfo>>() {
            @Override
            public void onSuccess(BaseResponse<WxPayResultInfo> response) {
                Utils.putPayType(formType);
                wxPay(mContext, response.getData());
            }
        });
    }

    private void wxPay(Context context, WxPayResultInfo wx) {
        if (wx == null) return;
        new Thread(() -> {
            ConfigInfo configInfo = userService.getConfigInfo();
            String appId = CommonUtil.getWeChatAppId(configInfo);
            mApi = WXAPIFactory.createWXAPI(context, TextUtils.isEmpty(appId) ? Constants.WX_APP_ID : appId);
            mApi.registerApp(TextUtils.isEmpty(appId) ? Constants.WX_APP_ID : appId);
            mApi.sendReq(CommonUtil.performWxReq(wx));
        }).start();
    }

    public void setFormType(int in) {
        formType = in;
    }

    public void setFamilyId(int in) {
        family_id = in;
    }

    static class RechargeAdapter extends BaseQuickAdapter<RechargeDto, BaseViewHolder> {
        private int checkPosition;
        private final RechargeQuickDto rechargeQuickDto;
        private final UserInfo userInfo;

        public RechargeAdapter(@Nullable List<RechargeDto> data, RechargeQuickDto rechargeQuickDto, UserInfo userInfo) {
            super(R.layout.item_recharge, data);
            checkPosition = 0;
            this.rechargeQuickDto = rechargeQuickDto;
            this.userInfo = userInfo;
        }

        public void setCheckPositions(int p) {
            checkPosition = p;
            notifyDataSetChanged();
        }


        @Override
        protected void convert(@NonNull BaseViewHolder helper, RechargeDto item) {
            RelativeLayout rlRoot = helper.getView(R.id.rl_root);
            TextView tvDesc = helper.getView(R.id.tv_desc);
            helper.setText(R.id.tv_coin, item.coin.replace(getContext().getResources().getString(R.string.coin), ""))
                    .setText(R.id.tv_rmb, "¥" + item.rmb);
            helper.setGone(R.id.ll_desc, TextUtils.isEmpty(item.title));
            tvDesc.setText(item.title);
            if (checkPosition == helper.getLayoutPosition()) {
                rlRoot.setBackgroundResource(R.drawable.bg_quick_pay_choose);
            } else {
                rlRoot.setBackgroundResource(R.drawable.bg_recharge_normal);
            }
        }
    }


    static class PayWayAdapter extends BaseQuickAdapter<PaymentTypeDto, BaseViewHolder> {
        private int checkPosition;
        private final RechargeQuickDto rechargeQuickDto;
        private final UserInfo userInfo;


        public PayWayAdapter(@Nullable List<PaymentTypeDto> data, RechargeQuickDto rechargeQuickDto, UserInfo userInfo) {
            super(R.layout.item_payment_type, data);
            checkPosition = 0;
            this.rechargeQuickDto = rechargeQuickDto;
            this.userInfo = userInfo;
        }

        public void setCheckPositions(int p) {
            checkPosition = p;
            notifyDataSetChanged();
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, PaymentTypeDto item) {
            GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_icon), item.image);
            helper.setText(R.id.tv_pay_title, item.title);
            CheckBox checkBox = helper.getView(R.id.checkbox);
            checkBox.setChecked(checkPosition == helper.getLayoutPosition());
        }
    }


    public interface OnSelectListener {

        void alipay(String orderNum);

    }


    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        visitPartyList();
    }


    /**
     * 埋点
     */
    public void visitPartyList() {
        String number = MMKVUtils.getInstance().decodeString(FIY_NUMBER);
        int numberScene = MMKVUtils.getInstance().decodeInt(SCENE_NUMBER);
        if (userService != null) {
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent("金币充值拦截弹窗曝光", "coin_recharge_intercept_popup_visit", "", JSON.toJSONString(new NavigationLogEntity(userService.getUserId(), from_type, numberScene != -1 ? numberScene : scene, toUserId, room_id, !TextUtils.isEmpty(number) && !number.equals("-1") ? Long.parseLong(number) : family_id, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME)), null);
        }

    }


    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
