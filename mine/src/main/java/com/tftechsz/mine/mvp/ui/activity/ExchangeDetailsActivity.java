package com.tftechsz.mine.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.blankj.utilcode.util.AppUtils;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.ShopInfoDto;
import com.tftechsz.mine.mvp.IView.IExchangeDetailView;
import com.tftechsz.mine.mvp.presenter.ExchangeDetailPresenter;
import com.tftechsz.mine.widget.pop.RealNamePopWindow;

/**
 * 兑换详情
 */
public class ExchangeDetailsActivity extends BaseMvpActivity<IExchangeDetailView, ExchangeDetailPresenter> implements View.OnClickListener, IExchangeDetailView {
    public static final String EXTRA_SHOP = "shop";
    public static final String EXTRA_INTEGRAL = "extra_integral";
    private ShopInfoDto mShopInfo;
    private TextView mTvCoin, mTvExchange, mTvIntegral, mTvExchangeNum;    // 金币，兑换人数，积分, 兑换人数
    private ImageView mIvShop;
    private String integral;
    @Autowired
    UserProviderService service;
    private TextView mTips;

    public static void startActivity(Context context, ShopInfoDto dto, String integral) {
        Intent intent = new Intent(context, ExchangeDetailsActivity.class);
        intent.putExtra(EXTRA_SHOP, dto);
        intent.putExtra(EXTRA_INTEGRAL, integral);
        context.startActivity(intent);

    }


    @Override
    public ExchangeDetailPresenter initPresenter() {
        return new ExchangeDetailPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("兑换详情")
                .build();
        mTips = findViewById(R.id.tips);
        mIvShop = findViewById(R.id.iv_shop);
        mTvCoin = findViewById(R.id.tv_coin);
        mTvExchange = findViewById(R.id.tv_exchange);
        mTvIntegral = findViewById(R.id.tv_integral);
        mTvExchangeNum = findViewById(R.id.tv_exchange_num);
        initListener();

    }

    private void initListener() {
        mTvExchange.setOnClickListener(this);

    }


    @Override
    protected int getLayout() {
        return R.layout.activity_exchange_detail;
    }


    @Override
    protected void initData() {
        super.initData();
        mShopInfo = (ShopInfoDto) getIntent().getSerializableExtra(EXTRA_SHOP);
        integral = getIntent().getStringExtra(EXTRA_INTEGRAL);
        if (mShopInfo != null) {
            GlideUtils.loadRouteImage(this, mIvShop, mShopInfo.image_big);
            String number = mShopInfo.number;
            if (TextUtils.isEmpty(number)) {
                mTvExchangeNum.setVisibility(View.GONE);
            } else {
                mTvExchangeNum.setVisibility(View.VISIBLE);
                mTvExchangeNum.setText(number);
            }
            mTvCoin.setText(mShopInfo.cost);
            mTvIntegral.setText(getString(R.string.exchange_coin_reduce) + mShopInfo.integral);
            if (!TextUtils.equals("coin", mShopInfo.type)) {
                mTips.setVisibility(View.VISIBLE);
                if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && !TextUtils.isEmpty(service.getConfigInfo().sys.withdraw_tips)) {
                    mTips.setText(service.getConfigInfo().sys.withdraw_tips.replace(Constants.WEB_PARAMS_APP_NAME, AppUtils.getAppName()));
                }
            }else {
                mTvCoin.setTextColor(Utils.getColor(R.color.c_exchange_coin));
            }
        }
        initRxBus();
    }


    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                finish();
                            }
                        }
                ));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_exchange) {  //兑换
            if (service.getUserInfo().isReal()) {   //已经真人认证
                if (TextUtils.equals("coin", mShopInfo.type)) {  //兑换金币
                    p.exchange(mShopInfo.id);
                } else {
                    if (service.getUserInfo().isSelf()) { //已经实名认证


                        double money = TextUtils.isEmpty(integral) ? 0 : Double.parseDouble(integral);  //用户积分
                        double integral = TextUtils.isEmpty(mShopInfo.integral) ? 0 : Double.parseDouble(mShopInfo.integral.replace("积分", ""));
                        if (money > integral)
                            WithdrawActivity.startActivity(ExchangeDetailsActivity.this, mShopInfo);
                        else
                            toastTip("你的积分不够");
                    } else {
                        RealNamePopWindow popWindow = new RealNamePopWindow(this, 2);
                        popWindow.addOnClickListener(() -> p.getSelfInfo());
                        popWindow.showPopupWindow();
                    }
                }
            } else {  //不是真人认证
                RealNamePopWindow popWindow = new RealNamePopWindow(this, 1);
                popWindow.addOnClickListener(() -> p.getRealInfo());
                popWindow.showPopupWindow();
            }

        }
    }


    /**
     * 实名认证状态
     *
     * @param data // -1.未认证,0.等待审核，1.审核完成，2.审核驳回
     */
    @Override
    public void getRealInfoSuccess(RealStatusInfoDto data) {
        CommonUtil.performSelf(data);
    }

    /**
     * 真人认证状态
     *
     * @param data // -1.未认证,0.等待审核，1.审核完成，2.审核驳回
     */
    @Override
    public void getSelfInfoSuccess(RealStatusInfoDto data) {
        CommonUtil.performReal(data);
    }

    @Override
    public void exchange(Boolean data) {
        if (data) {
            toastTip("兑换成功");
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
            finish();
        } else {
            toastTip("兑换失败");
        }
    }

}
