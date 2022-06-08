package com.tftechsz.mine.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.netease.nim.uikit.common.ConfigInfo;
import com.robinhood.ticker.TickerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.ChargePayAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.entity.PaymentDto;
import com.tftechsz.common.entity.RechargeDto;
import com.tftechsz.common.event.BuriedPointExtendDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.PayTextClick;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.RechargeAdapter;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.mvp.IView.IChargePayView;
import com.tftechsz.mine.mvp.presenter.ChargePayPresenter;

/**
 * 金币充值列表
 */
@Route(path = ARouterApi.ACTIVITY_CHARGE_LIST_NEW)
public class ChargeListNewActivity extends BaseMvpActivity<IChargePayView, ChargePayPresenter> implements View.OnClickListener, IChargePayView {

    private TextView mTvFirstRecharge;
    private TickerView mTvCoin;
    private boolean isFirst = false;//第一次支付首单
    private RecyclerView mRecy;
    private View mEmptyView;
    private RechargeAdapter mAdapter;
    private ImageView mVpBg;
    private SmartRefreshLayout mRefresh;
    private int payId;
    private int form_type;
    private IntegralDto mIntegralDto;
    @Autowired
    UserProviderService service;
    private TextView mTvContact;
    private CheckBox checkBox1, checkBox2;
    private ChargePayAdapter adapter;
    private RelativeLayout mRlZFB, mRlWX;
    private RecyclerView mRvPayWay;
    private RechargeDto mData;
    private TextView tvToPay;

    public static void startActivity(Context context, String coin) {
        Intent intent = new Intent(context, ChargeListNewActivity.class);
        intent.putExtra(Interfaces.EXTRA_COIN_VALUE, coin);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public ChargePayPresenter initPresenter() {
        return new ChargePayPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_charge_list_new;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(mActivity).transparentStatusBar().navigationBarDarkIcon(false).navigationBarColor(R.color.black).statusBarDarkFont(false, 0.2f).init();
        new ToolBarBuilder().showBack(true)
                .setTitle(getString(R.string.coin_recharge))
                .setTitleColor(R.color.white)
                .setRightText(getString(R.string.income_and_expenditude_records), v -> ARouterUtils.toIntegralDetailedActivity(1))
                .setRightTextColor(R.color.white)
                .setBackgroundColor(0)
                .setBackImg(R.mipmap.mine_ic_back_white)
                .build();
        baseTitle.setBackgroundResource(0);
        mTvCoin = findViewById(R.id.tv_coin_num);
        mVpBg = findViewById(R.id.vp_bg);
        mRecy = findViewById(R.id.recycleview);
        mRecy.setLayoutManager(new GridLayoutManager(this, 3));
        mRecy.addItemDecoration(new GridSpacingItemDecoration(3, ConvertUtils.dp2px(10f), false));
        mRecy.setPadding(ConvertUtils.dp2px(17f), 0, ConvertUtils.dp2px(17f), 0);
        mRecy.setVerticalScrollBarEnabled(false);
        mRefresh = findViewById(R.id.smartrefreshlayout);
        mRefresh.setBackgroundResource(R.color.white);
        mEmptyView = findViewById(R.id.empty_view);
        mTvFirstRecharge = findViewById(R.id.tv_first_recharge);

        checkBox1 = findViewById(R.id.checkbox);
        checkBox2 = findViewById(R.id.checkbox2);
        mRlZFB = findViewById(R.id.rl_zfb);
        mRlWX = findViewById(R.id.rl_wx);
        mTvContact = findViewById(R.id.tv_contact);
        mRvPayWay = findViewById(R.id.rv_pay_way);
        tvToPay = findViewById(R.id.tv_pay);
        mRvPayWay.setLayoutManager(new LinearLayoutManager(this));
        initListener();
    }

    private void initListener() {
        mRlZFB.setOnClickListener(this);
        mRlWX.setOnClickListener(this);
        tvToPay.setOnClickListener(this);
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
        mRecy.setAdapter(mAdapter);
        String coin = getIntent().getStringExtra(Interfaces.EXTRA_COIN_VALUE);
        form_type = getIntent().getIntExtra(Interfaces.EXTRA_RECHARGE_TYPE, 0);
        if (!TextUtils.isEmpty(coin)) {
            mTvCoin.setText(coin);
        }

        ConfigInfo configInfo = service.getConfigInfo();
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
        if (configInfo != null && configInfo.share_config != null && configInfo.share_config.payment_type != null) {
            adapter = new ChargePayAdapter(configInfo.share_config.payment_type);
            mRvPayWay.setAdapter(adapter);
            adapter.setOnItemClickListener((adapter1, view, position) -> {
                adapter.notifyDataPosition(position);
            });
            mRlZFB.setVisibility(View.GONE);
            mRlWX.setVisibility(View.GONE);
        } else {
            mRlZFB.setVisibility(View.VISIBLE);
            mRlWX.setVisibility(View.VISIBLE);
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

//                        if (!TextUtils.isEmpty(response.getData().first_pay_desc)) {
//                            mTvFirstRecharge.setText(response.getData().first_pay_desc);
//                            mTvFirstRecharge.setVisibility(View.VISIBLE);
//                        } else {
//                            mTvFirstRecharge.setVisibility(View.GONE);
//                        }
                        mAdapter.setList(response.getData().payment);
                        mAdapter.setOnItemClickListener((adapter, view, position) -> {
                            int tempIndex = mAdapter.selectedIndex;
                            mAdapter.selectedIndex = position;
                            mAdapter.notifyItemChanged(position);
                            if (tempIndex >= 0) {
                                mAdapter.notifyItemChanged(tempIndex);
                            }
                            if (Utils.isPayTypeInFamily(form_type)) {
                                ARouter.getInstance()
                                        .navigation(MineService.class)
                                        .trackEvent("充值界面列表点击", "click", "charge_list_item",
                                                JSON.toJSONString(new BuriedPointExtendDto(new BuriedPointExtendDto.RechargeExtendDto("", mAdapter.getItem(position).coin))), null);
                            }
                            mData = mAdapter.getItem(position);
                            if (mData != null) {
                                tvToPay.setEnabled(true);
                                tvToPay.setText(getString(R.string.pay_now_format, mData.rmb));
                            }
                            form_type = 2;
//                            ARouterUtils.toChargePayActivity(mAdapter.getItem(position), form_type);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (null == mData)
            return;
        if (id == R.id.rl_zfb) {  //支付宝
            checkBox1.setChecked(true);
            checkBox2.setChecked(false);
        } else if (id == R.id.rl_wx) {   //微信
            checkBox1.setChecked(false);
            checkBox2.setChecked(true);
        } else if (id == R.id.tv_pay) {
            if (!ClickUtil.canOperate()) {
                return;
            }
            if (mRlZFB.getVisibility() == View.VISIBLE) {// 两个选择方式
                if (checkBox1.isChecked()) {
                    p.getOrderNum(this, mData.id, Utils.getPayTypeFrom(form_type));
                } else if (checkBox2.isChecked()) {
                    p.getWxOrderNum(this, mData.id, Utils.getPayTypeFrom(form_type));
                }
            } else {
                if (adapter == null) {
                    return;
                }
                if (Utils.isPayTypeInFamily(form_type)) {
                    ARouter.getInstance()
                            .navigation(MineService.class)
                            .trackEvent("界面_余额不足点击充值", "click", "page_to_recharge",
                                    JSON.toJSONString(new BuriedPointExtendDto(new BuriedPointExtendDto.RechargeExtendDto(adapter.getItem(adapter.getDataPosition()).type, mData.coin))), null);
                }

                if (adapter.isWeChat(adapter.getDataPosition())) {
                    Utils.putPayType(form_type);
                    p.getWxOrderNum(this, mData.id, Utils.getPayTypeFrom(form_type));
                    return;
                }
                if (adapter.isAliPay(adapter.getDataPosition())) {
                    p.getOrderNum(this, mData.id, Utils.getPayTypeFrom(form_type));
                    return;
                }
                if (adapter.isSXYWeChat(adapter.getDataPosition())) {
                    Utils.putPayType(form_type);
                    p.getSXYWeChatPay(mActivity, mData.id, Utils.getPayTypeFrom(form_type));
                }
                if (adapter.isSXYAliPay(adapter.getDataPosition())) {
                    p.getSXYAliOrderNum(mActivity, mData.id, Utils.getPayTypeFrom(form_type));
                }
            }
        }
    }

    @Override
    public void paySuccess() {
        if (Utils.isPayTypeInFamily(form_type)) {
            ARouter.getInstance().navigation(MineService.class).trackEvent(Interfaces.POINT_SCENE_PAY_SUCCESS,
                    Interfaces.POINT_EVENT_PAGE, Interfaces.POINT_INDEX_PAY_SUCCESS, JSON.toJSONString(new BuriedPointExtendDto()), null);
        }
        toastTip("支付成功");
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
        finish();
    }

    @Override
    public void payFail() {
        toastTip("支付失败");
    }
}
