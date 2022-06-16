package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.NetworkUtils;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.base.BaseListFragment;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.CheckExchangeDto;
import com.tftechsz.mine.entity.dto.ShopInfoDto;
import com.tftechsz.mine.mvp.ui.activity.ExchangeDetailsActivity;
import com.tftechsz.mine.mvp.ui.activity.IntegralShopActivity;

import java.util.List;

import com.tftechsz.mine.mvp.ui.activity.MineIntegralActivity;
import com.tftechsz.mine.mvp.ui.activity.MineIntegralNewActivity;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class IntegralListFragment extends BaseListFragment<ShopInfoDto> {

    public static final String TYPE = "type";
    public static final String TYPE_INTEGRAL_TO_COIN = "coin";   //积分换金币
    public static final String TYPE_INTEGRAL_TO_RMB = "rmb";   //积分换现金
    public String mType;
    private PageStateManager mPageManager;
    public MineApiService service;

    public static IntegralListFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        IntegralListFragment fragment = new IntegralListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Flowable setNetObservable() {
        mType = getArguments().getString(TYPE);
        return new RetrofitManager().createConfigApi(MineApiService.class).getShop();
    }

    @Override
    public RecyclerView.LayoutManager setLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void setData(List<ShopInfoDto> datas, int page) {
        mPageManager.showContent();
        Disposable disposable = Observable.fromIterable(datas)
            .filter(shopInfoDto -> {
                if (TYPE_INTEGRAL_TO_RMB.equals(mType)) {
                    return "rmb".equals(shopInfoDto.type);
                } else if (TYPE_INTEGRAL_TO_COIN.equals(mType)) {
                    return "coin".equals(shopInfoDto.type);
                }
                return false;
            }).toList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(lists -> {
                super.setData(lists, page);
            });
        mCompositeDisposable.add(disposable);
    }


    @Override
    public int setItemLayoutRes() {
        return R.layout.item_integral_exchange;
    }

    @Override
    public void bingViewHolder(BaseViewHolder helper, ShopInfoDto item, int position) {
        GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_shop), item.image_small);
        helper.setText(R.id.tv_coin, item.cost)
            .setText(R.id.tv_integral, item.integral);
    }

    @Override
    public String setEmptyContent() {
        return null;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        mType = getArguments().getString(TYPE);
        LinearLayout root = getView(R.id.root);

        mPageManager = PageStateManager.initWhenUse(root, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showLoading();
                onRefresh();
            }

            @Override
            public int customErrorLayoutId() {
                return R.layout.pager_error_exit;
            }

            @Override
            public void onExit() {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        if (!NetworkUtils.isConnected()) {
            root.postDelayed(() -> mPageManager.showError(null), 300);
        }
        adapter.setOnItemClickListener((adapter1, view, position) ->
            checkExChange(position));
    }

    private void checkExChange(int position) {
        ShopInfoDto item = adapter.getItem(position);
        int type = item.id;
        FragmentActivity activity = getActivity();
        String integral = activity instanceof IntegralShopActivity ? ((IntegralShopActivity) activity).integral :
            activity instanceof MineIntegralNewActivity ? ((MineIntegralNewActivity) activity).integral :
            activity instanceof MineIntegralActivity ? ((MineIntegralActivity) activity).integral : "";
        if (service == null)
            service = RetrofitManager.getInstance().createExchApi(MineApiService.class);
        mCompositeDisposable.add(service.checkExchange(type).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<CheckExchangeDto>>() {
            @Override
            public void onSuccess(BaseResponse<CheckExchangeDto> response) {
                if (response.getData() == null) return;
                if (response.getData().is_lock == 0)
                    ExchangeDetailsActivity.startActivity(getContext(), item, integral);
                else
                    toastTip(response.getData().is_lock_msg);
            }
        }));
    }

    @Override
    protected void onRefresh() {
        if (!NetworkUtils.isConnected()) {
            mPageManager.showError(null);
            return;
        }
        super.onRefresh();

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_integral_list;
    }

    @Override
    protected void initData() {

    }
}
