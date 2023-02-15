package com.tftechsz.mine.widget.pop;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alipay.sdk.app.PayTask;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.PaymentTypeDto;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.widget.pop.RechargePopWindow;
import com.umeng.analytics.MobclickAgent;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.AlipayResultInfo;
import com.tftechsz.common.entity.SXYWxPayResultInfo;
import com.tftechsz.common.entity.WxPayResultInfo;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.PayService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.PayTextClick;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.mine.R;
import com.tftechsz.common.adapter.ChargePayAdapter;
import com.tftechsz.mine.adapter.VipPriceAdapter;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.databinding.PopVipPayBinding;
import com.tftechsz.mine.entity.VipPriceBean;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 包 名 : com.tftechsz.mine.widget.pop
 * 描 述 : TODO
 */
public class VipPayPopWindow extends BaseBottomPop {

    private PopVipPayBinding mBind;
    private final CompositeDisposable mCompositeDisposable;
    UserProviderService userService;
    PublicService publicService;
    private final PayService service;
    private int typeId;
    private IWXAPI mApi;
    private VipPriceAdapter mPriceAdapter;
    private int oldSel = -1;
    private final Activity mActivity;
    private ChargePayAdapter adapter;
    private String mPrice;
    private int checkPosition = -1;

    public VipPayPopWindow(Activity context) {
        super(context);
        mActivity = context;
        mCompositeDisposable = new CompositeDisposable();
        userService = ARouter.getInstance().navigation(UserProviderService.class);
        service = ARouter.getInstance().navigation(PayService.class);
        publicService = RetrofitManager.getInstance().createConfigApi(PublicService.class);
        initUI();
        initRxBus();
    }

    public void setisRecharge(boolean isRecharge) {//是否续费
        mBind.setShowPrice(isRecharge);
        if (isRecharge) {
            mCompositeDisposable.add(RetrofitManager.getInstance().createConfigApi(MineApiService.class)
                    .getVipPrice().compose(RxUtil.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<List<VipPriceBean>>>() {
                        @Override
                        public void onSuccess(BaseResponse<List<VipPriceBean>> response) {
                            mPriceAdapter.setList(response.getData());
                            for (int i = 0; i < response.getData().size(); i++) {
                                VipPriceBean vipPriceBean = response.getData().get(i);
                                if (vipPriceBean.isSel()) {
                                    oldSel = i;
                                    setTypeId(vipPriceBean.id);
                                    break;
                                }
                            }
                        }
                    }));
        }
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_PAY_FAIL) {
                                getPaymentType();
                            }
                        }
                ));
    }

    @Override
    public void onShowing() {
        super.onShowing();
        getPaymentType();
    }

    public void setPrice(String price) {
        this.mPrice = price;
        mBind.tvPay.setText("立即充值" + price);
    }


    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_vip_pay);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    private void initUI() {
        mBind.del.setOnClickListener(v -> dismiss());
        mPriceAdapter = new VipPriceAdapter();
        mPriceAdapter.addChildClickViewIds(R.id.root);
        mBind.tvPay.setOnClickListener((View.OnClickListener) v -> {
            if (adapter.isWeChat(adapter.getDataPosition())) {
                getWxOrderNum(typeId);
                return;
            }
            if (adapter.isAliPay(adapter.getDataPosition())) {
                getOrderNum(typeId);
                return;
            }
            if (adapter.isSXYWeChat(adapter.getDataPosition())) {
                service.SXYWeChatPay(typeId, "", new ResponseObserver<BaseResponse<SXYWxPayResultInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<SXYWxPayResultInfo> response) {
                        mApi = WXAPIFactory.createWXAPI(mActivity, response.getData().app_id);
                        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                        req.userName = response.getData().mini_program_original_id; // 填小程序原始id
                        req.path = response.getData().url;
                        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
                        mApi.sendReq(req);
                    }
                });
                return;
            }

            if (adapter.isSXYAliPay(adapter.getDataPosition())) {
                service.SXYalipay(typeId, "", new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        CommonUtil.startIntentToAliPay(mActivity, response.getData());
                    }
                });
            }

        });
        mPriceAdapter.setOnItemChildClickListener((ad, view, position) -> {
            if (oldSel == position) {
                return;
            }
            for (int i = 0, j = mPriceAdapter.getItemCount(); i < j; i++) {
                VipPriceBean item = mPriceAdapter.getItem(i);
                if (position == i || oldSel == i) {
                    item.setSel(position == i);
                    mPriceAdapter.setData(i, item);
                    if (position == i) {
                        setTypeId(item.id);
                    }
                }
            }
            oldSel = position;
        });
        mBind.priceRecy.setLayoutManager(new GridLayoutManager(mContext, 3));
        mBind.priceRecy.setAdapter(mPriceAdapter);

        mBind.payRecy.setLayoutManager(new LinearLayoutManager(mContext));

        ConfigInfo configInfo = userService.getConfigInfo();
        if (configInfo != null && configInfo.api != null && configInfo.api.vip_recharge != null && configInfo.api.vip_recharge.size() > 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (ConfigInfo.MineInfo mineInfo : configInfo.api.vip_recharge) {
                builder.append(mineInfo.title);
                if (!TextUtils.isEmpty(mineInfo.link)) {
                    int start = builder.toString().indexOf(mineInfo.title);
                    builder.setSpan(new PayTextClick(mContext, mineInfo), start, start + mineInfo.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new UnderlineSpan(), start, start + mineInfo.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            mBind.vipAgree.setText(builder);
            mBind.vipAgree.setMovementMethod(LinkMovementMethod.getInstance());
        }
       /* mBind.vipAgree.setText(new SpannableStringUtils.Builder().append("续费即表示同意")
                .setForegroundColor(Utils.getColor(R.color.color_999999))
                .append("芍药增值服务")
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        BaseWebViewActivity.start(mContext, "", Interfaces.VIP_AGREE);
                    }
                })
                .setForegroundColor(Utils.getColor(R.color.color_999999))
                .create());
        mBind.vipAgree.setMovementMethod(LinkMovementMethod.getInstance());*/

        getPaymentType();
    }

    public void getWxOrderNum(int typeId) {
        if (!AppUtils.isWeChatAppInstalled(mContext)) {
            ToastUtil.showToast(mContext, "手机未安装微信");
            return;
        }
        service.wechatPay(typeId, "", new ResponseObserver<BaseResponse<WxPayResultInfo>>() {
            @Override
            public void onSuccess(BaseResponse<WxPayResultInfo> response) {
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
            if (CommonUtil.performWxReq((Activity) context, wx) != null)
                mApi.sendReq(CommonUtil.performWxReq((Activity) context, wx));
        }).start();
    }

    /**
     * 支付宝
     */
    public void getOrderNum(int typeId) {
        service.alipay(typeId, "", new ResponseObserver<BaseResponse<String>>() {
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


    public void getPaymentType() {
        mCompositeDisposable.add(publicService.getRechargeNewList()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<List<PaymentTypeDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<PaymentTypeDto>> response) {
                        if (response != null && response.getData() != null) {
                            adapter = new ChargePayAdapter(response.getData());
                            mBind.payRecy.setAdapter(adapter);
                            if (adapter.getData().size() > checkPosition  && checkPosition != -1) {
                                adapter.notifyDataPosition(checkPosition);
                            }
                            adapter.setOnItemClickListener((adapter1, view, position) -> {
                                if (typeId != 0) {
                                    adapter.notifyDataPosition(position);
                                    checkPosition = position;
                                } else {
                                    Utils.toast("您还未选择套餐");
                                }
                            });
                        }
                    }
                }));
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
                    } else {
                        ToastUtil.showToast(BaseApplication.getInstance(), "支付失败");
                        getPaymentType();
                    }
                });
        mCompositeDisposable.add(disposable);
    }


    public interface OnSelectListener {
        void alipay(String orderNum);
    }

    public RechargePopWindow.OnSelectListener listener;

    public void addOnClickListener(RechargePopWindow.OnSelectListener listener) {
        this.listener = listener;
    }
}
