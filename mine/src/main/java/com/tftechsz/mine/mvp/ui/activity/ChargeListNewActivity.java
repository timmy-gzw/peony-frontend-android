package com.tftechsz.mine.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.common.ConfigInfo;
import com.robinhood.ticker.TickerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.entity.PaymentDto;
import com.tftechsz.common.event.BuriedPointExtendDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.RechargeAdapter;
import com.tftechsz.mine.api.MineApiService;

/**
 * 金币充值列表
 */
@Route(path = ARouterApi.ACTIVITY_CHARGE_LIST_NEW)
public class ChargeListNewActivity extends BaseMvpActivity {

    private TextView mTvFirstRecharge, mCoinHint;
    private TickerView mTvCoin;
    private boolean isFirst = false;//第一次支付首单
    private RecyclerView mRecy;
    private View mEmptyView;
    private RechargeAdapter mAdapter;
    private ImageView mVpBg, mCoinTip;
    private SmartRefreshLayout mRefresh;
    private int payId;
    private int form_type;
    private IntegralDto mIntegralDto;

    public static void startActivity(Context context, String coin) {
        Intent intent = new Intent(context, ChargeListNewActivity.class);
        intent.putExtra(Interfaces.EXTRA_COIN_VALUE, coin);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_charge_list_new;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("充值金币")
                .setRightText("金币清单", v -> ARouterUtils.toIntegralDetailedActivity(1))
                .build();
        mTvCoin = findViewById(R.id.tv_coin_num);
        mVpBg = findViewById(R.id.vp_bg);
        mRecy = findViewById(R.id.recycleview);
        mRefresh = findViewById(R.id.smartrefreshlayout);
        mCoinHint = findViewById(R.id.coin_hint);
        mCoinTip = findViewById(R.id.coin_tip);
        mEmptyView = findViewById(R.id.empty_view);
        mTvFirstRecharge = findViewById(R.id.tv_first_recharge);
        mCoinTip.setOnClickListener(v -> {
            if (mIntegralDto != null) {
                CommonUtil.performLink(mActivity, new ConfigInfo.MineInfo(mIntegralDto.free_coin_desc));
            }
        });
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        res.updateConfiguration(configuration, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void initData() {
        mVpBg.setOnClickListener(v -> {
            if (Utils.isPayTypeInFamily(form_type)) {
                ARouter.getInstance()
                        .navigation(MineService.class)
                        .trackEvent("充值界面点击vip", Interfaces.POINT_EVENT_CLICK, "recharge_to_vip", JSON.toJSONString(new BuriedPointExtendDto()), null);
            }

            Intent intent = new Intent(mContext, VipActivity.class);
            intent.putExtra(Interfaces.EXTRA_ID, payId);
            startActivity(intent);
        });
        mEmptyView.setVisibility(View.VISIBLE);
        mRecy.setVisibility(View.GONE);
        mRefresh.setEnableLoadMore(false);
        mRefresh.setOnRefreshListener(refreshLayout -> getRechargeNewList());
        mAdapter = new RechargeAdapter();
        mRecy.setLayoutManager(new LinearLayoutManager(this));
        mRecy.setAdapter(mAdapter);
        String coin = getIntent().getStringExtra(Interfaces.EXTRA_COIN_VALUE);
        form_type = getIntent().getIntExtra(Interfaces.EXTRA_RECHARGE_TYPE, 0);
        if (!TextUtils.isEmpty(coin)) {
            mTvCoin.setText(coin);
        }
        initRxBus();
        getCoin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRechargeNewList();
    }

    private void getRechargeNewList() {
        mCompositeDisposable.add(RetrofitManager.getInstance().createConfigApi(MineApiService.class)
                .getRechargeNewList()
                .compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<PaymentDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<PaymentDto> response) {
                        if (response.getData() == null) return;
                        mEmptyView.setVisibility(View.GONE);
                        mRecy.setVisibility(View.VISIBLE);
                        mRefresh.finishRefresh(true);
                        PaymentDto.Branch branch = response.getData().branch;
                        if (branch != null) {
                            payId = branch.id;
                            mVpBg.setVisibility(View.VISIBLE);
                            GlideUtils.loadRouteImage(mContext, mVpBg, branch.url);
                        } else {
                            mVpBg.setVisibility(View.GONE);
                        }

                        if (!TextUtils.isEmpty(response.getData().first_pay_desc)) {
                            mTvFirstRecharge.setText(response.getData().first_pay_desc);
                            mTvFirstRecharge.setVisibility(View.VISIBLE);
                        } else {
                            mTvFirstRecharge.setVisibility(View.GONE);
                        }
                        mAdapter.setList(response.getData().payment);
                        mAdapter.setOnItemClickListener((adapter, view, position) -> {
                            if (Utils.isPayTypeInFamily(form_type)) {
                                ARouter.getInstance()
                                        .navigation(MineService.class)
                                        .trackEvent("充值界面列表点击", "click", "charge_list_item",
                                                JSON.toJSONString(new BuriedPointExtendDto(new BuriedPointExtendDto.RechargeExtendDto("", mAdapter.getItem(position).coin))), null);
                            }
                            ARouterUtils.toChargePayActivity(mAdapter.getItem(position), form_type);
                        });
                        if (isFirst)
                            removeFirst();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mRefresh.finishRefresh(false);
                    }
                }));
    }

    private void getCoin() {
        mCompositeDisposable.add(RetrofitManager.getInstance().createUserApi(MineApiService.class)
                .getField("property", "coin")
                .compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<IntegralDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<IntegralDto> response) {
                        if (response == null) return;
                        mIntegralDto = response.getData();
                        mTvCoin.setAnimationDuration(Interfaces.TICKERVIEW_ANIMATION_MONEY);
                        mTvCoin.setText(response.getData().coin);
                        if (TextUtils.isEmpty(response.getData().free_coin)) {
                            mCoinHint.setVisibility(View.INVISIBLE);
                            mCoinTip.setVisibility(View.INVISIBLE);
                            return;
                        }
                        mCoinHint.setVisibility(View.VISIBLE);
                        mCoinHint.setText(new SpannableStringUtils.Builder()
                                .append("其中有 ")
                                .append(response.getData().free_coin).setForegroundColor(Utils.getColor(R.color.blue_7F89F3))
                                .append("金币 为绑定金币")
                                .create());
                        if (!TextUtils.isEmpty(response.getData().free_coin_desc)) {
                            mCoinTip.setVisibility(View.VISIBLE);
                        }

                    }
                }));
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                removeFirst();
                                getCoin();
                            }
                        }
                ));
    }

    /**
     * 移除掉首单
     */
    private void removeFirst() {
        int size = mAdapter.getItemCount();
        for (int i = size - 1; i >= 0; i--) {
            if (mAdapter.getItem(i).is_first == 1) {
                mAdapter.removeAt(i);
                isFirst = true;
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
