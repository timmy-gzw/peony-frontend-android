package com.tftechsz.common.widget.pop;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.ConvertUtils;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.PaymentTypeDto;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.adapter.ChargePayTypeAdapter;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.databinding.PopBasePayBinding;
import com.tftechsz.common.entity.AlipayResultInfo;
import com.tftechsz.common.entity.SXYWxPayResultInfo;
import com.tftechsz.common.entity.WxPayResultInfo;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.PayService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.RxUtil;
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

/**
 * 包 名 : com.tftechsz.mine.widget.pop
 * 描 述 : TODO
 */
public class BasePayPopWindow extends BaseBottomPop {

    private PopBasePayBinding mBind;
    private final CompositeDisposable mCompositeDisposable;
    UserProviderService userService;
    PublicService publicService;
    private final PayService service;
    private int typeId;
    private IWXAPI mApi;
    private final Activity mActivity;
    private ChargePayTypeAdapter adapter;
    private String rmb;
    private String coin;
    private int checkPosition = -1;


    public BasePayPopWindow(Activity context) {
        super(context);
        mActivity = context;
        mCompositeDisposable = new CompositeDisposable();
        userService = ARouter.getInstance().navigation(UserProviderService.class);
        service = ARouter.getInstance().navigation(PayService.class);
        publicService = RetrofitManager.getInstance().createConfigApi(PublicService.class);
        initUI();
        initRxBus();
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setPayInfo(String rmb, String coin) {
        this.rmb = rmb;
        this.coin = coin;
        if (mBind == null) return;
        if (!TextUtils.isEmpty(rmb) && !TextUtils.isEmpty(coin)) {
            mBind.tvPayInfo.setVisibility(View.VISIBLE);
            mBind.tvPayInfo.setText(Html.fromHtml(getContext().getString(R.string.recharge_coin_rmb_format, coin, rmb)));
            mBind.tvPay.setText(getContext().getString(R.string.recharge_now_format, rmb));
        } else {
            mBind.tvPayInfo.setVisibility(View.GONE);
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
    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_base_pay);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    private void initUI() {
        mBind.del.setOnClickListener(v -> dismiss());
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

        mBind.rvPayWay.setLayoutManager(new GridLayoutManager(mContext, 2));
        mBind.rvPayWay.addItemDecoration(new SpacingDecoration(ConvertUtils.dp2px(20f), ConvertUtils.dp2px(10f), false));
        ConfigInfo configInfo = userService.getConfigInfo();
        if (configInfo != null && configInfo.share_config != null && configInfo.share_config.payment_type != null) {
            adapter = new ChargePayTypeAdapter();
            mBind.rvPayWay.setAdapter(adapter);
            adapter.setList(configInfo.share_config.payment_type);
            adapter.setOnItemClickListener((adapter1, view, position) -> {
                if (typeId != 0) {
                    adapter.notifyDataPosition(position);
                } else {
                    Utils.toast("订单类型为空!");
                }
            });
        }
        getPaymentType();
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
            mBind.tvTerms.setText(builder);
            mBind.tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        }

        setPayInfo(rmb, coin);
    }



    public void getPaymentType() {
        mCompositeDisposable.add(publicService.getRechargeNewList()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<List<PaymentTypeDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<PaymentTypeDto>> response) {
                        if (response != null && response.getData() != null) {
                            adapter = new ChargePayTypeAdapter();
                            mBind.rvPayWay.setAdapter(adapter);
                            adapter.setList(response.getData());
                            if (adapter.getData().size() > checkPosition  && checkPosition != -1) {
                                adapter.notifyDataPosition(checkPosition);
                            }
                            adapter.setOnItemClickListener((adapter1, view, position) -> {
                                if (typeId != 0) {
                                    adapter.notifyDataPosition(position);
                                    checkPosition = position;
                                } else {
                                    Utils.toast("订单类型为空!");
                                }
                            });
                        }
                    }
                }));
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
            mApi.sendReq(CommonUtil.performWxReq(wx));
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
                }
            }
        });
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
                        LogUtil.e("==========","==" + payResult.toString());
                        ToastUtil.showToast(BaseApplication.getInstance(), "支付失败");
                    }

                });
        mCompositeDisposable.add(disposable);
    }
}
