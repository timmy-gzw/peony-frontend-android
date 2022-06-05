package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseListActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.CheckExchangeDto;
import com.tftechsz.mine.entity.dto.ShopInfoDto;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * 积分商城
 */
public class IntegralShopActivity extends BaseListActivity<ShopInfoDto> {

    public String integral;
    public MineApiService service;

    @Override
    protected int getLayout() {
        return R.layout.activity_integral_shop;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        new ToolBarBuilder().setTitle("积分商城")
                .setRightText("兑换记录", v -> ARouterUtils.toIntegralDetailedActivity(2))
                .showBack(true)
                .build();
        TextView tvIntegralNum = findViewById(R.id.tv_integral_num);
        initRxBus();
        integral = getIntent().getStringExtra("integral");
        tvIntegralNum.setText(integral);
    }


    @Override
    protected void initData() {
        super.initData();

    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        new Consumer<CommonEvent>() {
                            @Override
                            public void accept(CommonEvent event) throws Exception {
                                if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                    finish();
                                }
                            }
                        }
                ));
    }


    @Override
    public void setData(List<ShopInfoDto> datas, int page) {
        super.setData(datas, page);
        if (adapter == null) return;
        adapter.setOnItemClickListener((adapter1, view, position) -> checkExChange(adapter.getData().get(position).id, position));
    }


    private void checkExChange(int type, int position) {
        if (service == null)
            service = RetrofitManager.getInstance().createExchApi(MineApiService.class);
        mCompositeDisposable.add(service.checkExchange(type).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<CheckExchangeDto>>() {
            @Override
            public void onSuccess(BaseResponse<CheckExchangeDto> response) {
                if (response.getData() == null) return;
                if (response.getData().is_lock == 0)
                    ExchangeDetailsActivity.startActivity(IntegralShopActivity.this, adapter.getData().get(position), integral);
                else
                    toastTip(response.getData().is_lock_msg);
            }
        }));
    }


    @Override
    public Flowable setNetObservable() {
        return new RetrofitManager().createConfigApi(MineApiService.class).getShop();
    }

    @Override
    public RecyclerView.LayoutManager setLayoutManager() {
        return new GridLayoutManager(this, 2);
    }

    @Override
    public int setItemLayoutRes() {
        return R.layout.item_integral_shop;
    }

    @Override
    public void bingViewHolder(BaseViewHolder helper, ShopInfoDto item, int position) {
        GlideUtils.loadRouteImage(this, helper.getView(R.id.iv_shop), item.image_small);
        helper.setText(R.id.tv_coin, item.cost)
                .setText(R.id.tv_exchange, item.number)
                .setText(R.id.tv_integral, item.integral);
    }

    @Override
    public String setEmptyContent() {
        return null;
    }


}
