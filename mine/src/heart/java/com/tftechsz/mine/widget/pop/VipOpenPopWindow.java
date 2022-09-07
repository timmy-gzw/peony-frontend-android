package com.tftechsz.mine.widget.pop;

import static com.tftechsz.common.base.BasePresenter.applySchedulers;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.ConvertUtils;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.ChargePayTypeAdapter;
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
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.PayTextClick;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.OpenVipAdapter;
import com.tftechsz.mine.adapter.VipPriceAdapter;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.databinding.PopVipOpenBinding;
import com.tftechsz.mine.entity.VipPriceBean;
import com.tftechsz.mine.entity.dto.VipConfigBean;
import com.umeng.analytics.MobclickAgent;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 开通VIP 弹窗
 */
public class VipOpenPopWindow extends BaseBottomPop implements View.OnClickListener {
    public MineApiService configService;
    private final CompositeDisposable mCompositeDisposable;
    private PopVipOpenBinding mBind;
    private VipPriceAdapter mPriceAdapter;
    private ChargePayTypeAdapter adapter;
    private final PayService payService;
    private int mPayId;
    private int oldSel = -1;
    private final UserProviderService service;
    private int currentPosition = 0;
    private IWXAPI mApi;

    public VipOpenPopWindow(Context context) {
        super(context);
        mCompositeDisposable = new CompositeDisposable();
        configService = RetrofitManager.getInstance().createConfigApi(MineApiService.class);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        payService = ARouter.getInstance().navigation(PayService.class);
        mContext = context;
        initUI();
    }


    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_vip_open);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    private void initUI() {
        mBind.xf.setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(this);
        mPriceAdapter = new VipPriceAdapter();
        mPriceAdapter.onAttachedToRecyclerView(mBind.priceRecy);
        mBind.priceRecy.setLayoutManager(new GridLayoutManager(mContext, 3));
        mBind.payRecy.setLayoutManager(new GridLayoutManager(mContext, 2));
        mBind.payRecy.addItemDecoration(new SpacingDecoration(ConvertUtils.dp2px(20f), ConvertUtils.dp2px(10f), false));
        mBind.priceRecy.setAdapter(mPriceAdapter);
        mPriceAdapter.addChildClickViewIds(R.id.root);
        mPriceAdapter.setOnItemChildClickListener((ad, view, position) -> {
            if (oldSel == position) {
                return;
            }
            for (int i = 0, j = mPriceAdapter.getItemCount(); i < j; i++) {
                VipPriceBean item = mPriceAdapter.getItem(i);
                if (position == i || oldSel == i) {
                    item.setSel(position == i);
                    mPriceAdapter.setData(i, item);
                }
            }
            oldSel = position;
            setPrice();
        });
        ConfigInfo configInfo = service.getConfigInfo();
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
        getVipInfo();
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                            }
                        }
                ));
    }


    public void getVipInfo() {
        mCompositeDisposable.add(configService.getVipInfo().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<VipConfigBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<VipConfigBean> response) {
                        if (response.getData() == null) return;
                        VipConfigBean data = response.getData();
                        if (data.shopping != null && data.shopping.size() > 0) {
                            if (mPayId > 0) {
                                for (VipPriceBean b : data.shopping) {
                                    b.setSel(b.id == mPayId);
                                }
                            }
                            mPriceAdapter.setList(data.shopping);
                            for (int i = 0; i < data.shopping.size(); i++) {
                                VipPriceBean vipPriceBean = data.shopping.get(i);
                                if (vipPriceBean.isSel()) {
                                    oldSel = i;
                                    break;
                                }
                            }
                            setTypeId(mPriceAdapter.getItem(oldSel).id);
                        }
                        if (data.privilege != null && data.privilege.size() > 0) {
                            OpenVipAdapter openVipAdapter = new OpenVipAdapter(mContext, data.privilege);
                            mBind.viewPager.setAdapter(openVipAdapter);
                            mBind.viewPager.setCurrentItem(currentPosition);
                            mBind.viewPager.setOffscreenPageLimit(data.privilege.size());
                            setPoint(mBind.llPoint, data.privilege.size());
                            mBind.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                @Override
                                public void onPageSelected(int position) {
                                    super.onPageSelected(position);
                                    setCurrentPoint(mBind.llPoint, position);
                                }
                            });
                        }
                        if (data.payment_show != null && data.payment_show.size() > 0) {
                            adapter = new ChargePayTypeAdapter();
                            mBind.payRecy.setAdapter(adapter);
                            adapter.setList(data.payment_show);
                            adapter.setOnItemClickListener((adapter1, view, position) -> {
                                if (mPayId != 0) {
                                    adapter.notifyDataPosition(position);
                                } else {
                                    Utils.toast("订单类型为空!");
                                }
                            });
                        }
                        setPrice();
                    }
                }));
    }

    public void setTypeId(int typeId) {
        this.mPayId = typeId;
    }

    /**
     * 设置底部的点
     */
    private void setPoint(LinearLayout llPoint, int count) {
        llPoint.removeAllViews();
        if (count == 0) return;
        for (int i = 0; i < count; i++) {
            ImageView point = new ImageView(BaseApplication.getInstance());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = ConvertUtils.dp2px(3);
            params.leftMargin = ConvertUtils.dp2px(3);
            point.setLayoutParams(params);
            point.setBackgroundResource(R.drawable.selector_vip_point_bg);
            llPoint.addView(point);
        }
    }


    private void setCurrentPoint(LinearLayout llPoint, int position) {
        if (llPoint.getChildCount() <= position) {
            return;
        }
        for (int i = 0, j = llPoint.getChildCount(); i < j; i++) {
            llPoint.getChildAt(i).setEnabled(i != position);
        }
    }

    private void setPrice() {
        mBind.xf.setText("立即获取");
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.xf) { //续费
            if (oldSel >= 0 && oldSel < mPriceAdapter.getItemCount()) {
                if (adapter == null || adapter.getData().size() <= 0)
                    return;
                setTypeId(mPriceAdapter.getItem(oldSel).id);
                if (adapter.isWeChat(adapter.getDataPosition())) {
                    getWxOrderNum(mPayId);
                    return;
                }
                if (adapter.isAliPay(adapter.getDataPosition())) {
                    getOrderNum(mPayId);
                    return;
                }
                if (adapter.isSXYWeChat(adapter.getDataPosition())) {
                    payService.SXYWeChatPay(mPayId, "", new ResponseObserver<BaseResponse<SXYWxPayResultInfo>>() {
                        @Override
                        public void onSuccess(BaseResponse<SXYWxPayResultInfo> response) {
                            mApi = WXAPIFactory.createWXAPI(mContext, response.getData().app_id);
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
                    payService.SXYalipay(mPayId, "", new ResponseObserver<BaseResponse<String>>() {
                        @Override
                        public void onSuccess(BaseResponse<String> response) {
                            CommonUtil.startIntentToAliPay(mContext, response.getData());
                        }
                    });
                }
            } else {
                Utils.toast("您还未选择套餐");
            }
        } else if (id == R.id.iv_close) {
            dismiss();
        }
    }


    public void getWxOrderNum(int typeId) {
        if (!AppUtils.isWeChatAppInstalled(mContext)) {
            ToastUtil.showToast(mContext, "手机未安装微信");
            return;
        }
        payService.wechatPay(typeId, "", new ResponseObserver<BaseResponse<WxPayResultInfo>>() {
            @Override
            public void onSuccess(BaseResponse<WxPayResultInfo> response) {
                wxPay(mContext, response.getData());
            }
        });
    }

    private void wxPay(Context context, WxPayResultInfo wx) {
        if (wx == null) return;
        new Thread(() -> {
            ConfigInfo configInfo = service.getConfigInfo();
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
        payService.alipay(typeId, "", new ResponseObserver<BaseResponse<String>>() {
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
                        ToastUtil.showToast(BaseApplication.getInstance(), "支付失败");
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }
}
