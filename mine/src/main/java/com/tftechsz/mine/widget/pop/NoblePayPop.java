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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.NoblePayAdapter;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.AlipayResultInfo;
import com.tftechsz.common.entity.WxPayResultInfo;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.iservice.PayService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.PayTextClick;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.NoblePriceAdapter;
import com.tftechsz.mine.databinding.PopNoblepaypopBinding;
import com.tftechsz.mine.entity.NobleBean;

import java.util.List;

import androidx.annotation.NonNull;
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
public class NoblePayPop extends BaseBottomPop implements OnItemClickListener {

    private PopNoblepaypopBinding mBind;
    private NoblePriceAdapter mPriceAdapter;
    private NoblePayAdapter mPayAdapter;
    private int oldSel = -1;
    private final UserProviderService userService;
    private int payId;
    private final PayService payService;
    private IWXAPI mApi;
    private final CompositeDisposable mCompositeDisposable;
    private final UserProviderService service;

    public NoblePayPop(Context context) {
        super(context);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        payService = ARouter.getInstance().navigation(PayService.class);
        userService = ARouter.getInstance().navigation(UserProviderService.class);
        mCompositeDisposable = new CompositeDisposable();
        initUI();
    }

    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_noblepaypop);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    private void initUI() {
        mBind.ivDel.setOnClickListener(v -> dismiss());
        mBind.recyPrice.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mPriceAdapter = new NoblePriceAdapter();
        mPriceAdapter.setOnItemClickListener(this);
        mBind.recyPrice.setAdapter(mPriceAdapter);

        mPayAdapter = new NoblePayAdapter();
        mPayAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!ClickUtil.canOperate()) {
                return;
            }
            if (payId != 0) {
                if (mPayAdapter.isWeChat(position)) {
                    getWxOrderNum(payId);
                    return;
                }
                if (mPayAdapter.isAliPay(position)) {
                    getOrderNum(payId);
                }
            } else {
                Utils.toast("您还未选择套餐");
            }
        });
        mBind.recyPay.setAdapter(mPayAdapter);


        ConfigInfo configInfo = service.getConfigInfo();
        if (configInfo != null && configInfo.api != null && configInfo.api.noble_recharge != null && configInfo.api.noble_recharge.size() > 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (ConfigInfo.MineInfo mineInfo : configInfo.api.noble_recharge) {
                builder.append(mineInfo.title);
                if (!TextUtils.isEmpty(mineInfo.link)) {
                    int start = builder.toString().indexOf(mineInfo.title);
                    builder.setSpan(new PayTextClick(mContext, mineInfo), start, start + mineInfo.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new UnderlineSpan(), start, start + mineInfo.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            mBind.agree.setText(builder);
            mBind.agree.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void getOrderNum(int payId) {
        payService.alipay(payId, "", new ResponseObserver<BaseResponse<String>>() {
            @Override
            public void onSuccess(BaseResponse<String> response) {
                wakeUpAliPay(response.getData());
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


    public void getWxOrderNum(int typeId) {
        if (!AppUtils.isWeChatAppInstalled(mContext)) {
            ToastUtil.showToast(mContext, "手机未安装微信");
            return;
        }
        payService.wechatPay(typeId, "", new ResponseObserver<BaseResponse<WxPayResultInfo>>() {
            @Override
            public void onSuccess(BaseResponse<WxPayResultInfo> response) {
                if (response.getData() != null) {
                    new Thread(() -> {
                        ConfigInfo configInfo = userService.getConfigInfo();
                        String appId = CommonUtil.getWeChatAppId(configInfo);
                        mApi = WXAPIFactory.createWXAPI(getContext(), TextUtils.isEmpty(appId) ? Constants.WX_APP_ID : appId);
                        mApi.registerApp(TextUtils.isEmpty(appId) ? Constants.WX_APP_ID : appId);
                        mApi.sendReq(CommonUtil.performWxReq(response.getData()));
                    }).start();
                }
            }
        });
    }


    public NoblePayPop setData(String icon, String name, List<NobleBean.PriceDTO> priceDTOS) {
        oldSel = -1;
        GlideUtils.loadRouteImage(getContext(), mBind.levelIcon, icon);
        mBind.title.setText(name);
        mPriceAdapter.setList(setListUsed(priceDTOS));
        ConfigInfo configInfo = userService.getConfigInfo();
        if (configInfo != null && configInfo.share_config != null && configInfo.share_config.payment_type != null && configInfo.share_config.payment_type.size() > 0) {
            if (configInfo.share_config.payment_type.size() != 2) {
                mBind.recyPay.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                mBind.recyPay.setLayoutManager(new GridLayoutManager(getContext(), 2));
            }
            mPayAdapter.showRightArrow(configInfo.share_config.payment_type.size() != 2);
            mPayAdapter.setList(configInfo.share_config.payment_type);
        }
        return this;
    }


    /**
     * @param priceDTOS 价格集合
     * @return 定位选中的index, 若没有默认第0个
     */
    private List<NobleBean.PriceDTO> setListUsed(List<NobleBean.PriceDTO> priceDTOS) {
        for (int i = 0, j = priceDTOS.size(); i < j; i++) {
            NobleBean.PriceDTO priceDTO = priceDTOS.get(i);
            if (priceDTO.isSel()) {
                oldSel = i;
                payId = priceDTO.pay_id;
            }
            priceDTO.setSelTemp(priceDTO.isSel());
        }
        if (oldSel != -1) {
            return priceDTOS;
        }
        priceDTOS.get(0).setSelTemp(true);
        oldSel = 0;
        payId = priceDTOS.get(0).pay_id;
        return priceDTOS;
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        if (oldSel == position) {
            return;
        }
        for (int i = 0, j = mPriceAdapter.getItemCount(); i < j; i++) {
            NobleBean.PriceDTO item = mPriceAdapter.getItem(i);
            if (position == i || oldSel == i) {
                item.setSelTemp(position == i);
                mPriceAdapter.setData(i, item);
            }
        }
        oldSel = position;
        payId = mPriceAdapter.getItem(position).pay_id;
    }
}
