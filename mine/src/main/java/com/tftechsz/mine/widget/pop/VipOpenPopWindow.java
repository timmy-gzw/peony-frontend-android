package com.tftechsz.mine.widget.pop;

import static com.tftechsz.common.utils.RxUtil.applySchedulers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
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

import io.reactivex.disposables.CompositeDisposable;

/**
 * 开通VIP 弹窗
 */
public class VipOpenPopWindow extends BaseBottomPop implements View.OnClickListener {
    public MineApiService configService;
    private final CompositeDisposable mCompositeDisposable;
    private PopVipOpenBinding mBind;
    private VipPriceAdapter mPriceAdapter;
    private VipPayPopWindow mPop;
    private int mPayId;
    private int oldSel = -1;
    private final UserProviderService service;
    private int currentPosition = 0;

    public VipOpenPopWindow(Context context) {
        super(context);
        mCompositeDisposable = new CompositeDisposable();
        configService = RetrofitManager.getInstance().createConfigApi(MineApiService.class);
        service = ARouter.getInstance().navigation(UserProviderService.class);
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
        mBind.ivClose.setOnClickListener(this);
        mPriceAdapter = new VipPriceAdapter();
        mPriceAdapter.onAttachedToRecyclerView(mBind.priceRecy);
        mBind.priceRecy.setLayoutManager(new GridLayoutManager(mContext, 3));
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
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#5397FF")), start, start + mineInfo.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                                mPop.dismiss();
                            }
                        }
                ));
    }




    //vip装扮
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
                        }

                        if (data.privilege != null && data.privilege.size() > 0) {
                            OpenVipAdapter openVipAdapter = new OpenVipAdapter(mContext, data.privilege);
                            mBind.viewPager.setAdapter(openVipAdapter);
                            mBind.viewPager.setCurrentItem(currentPosition);
                            mBind.viewPager.setOffscreenPageLimit(data.privilege.size());
                            setPoint(mBind.llPoint,data.privilege.size());
                            mBind.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                @Override
                                public void onPageSelected(int position) {
                                    super.onPageSelected(position);
                                    setCurrentPoint(mBind.llPoint, position);
                                }
                            });
                        }
                        setPrice();
                    }
                }));
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
        mBind.xf.setText("¥" + mPriceAdapter.getData().get(oldSel).price + " 立即开通");
        mBind.tvDiscountPrice.setText(mPriceAdapter.getData().get(oldSel).reduce_price_title.replace("立","已"));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.xf) { //续费
            if (oldSel >= 0 && oldSel < mPriceAdapter.getItemCount()) {
                if (mPop == null)
                    mPop = new VipPayPopWindow((Activity) mContext);
                mPop.setTypeId(mPriceAdapter.getItem(oldSel).id);
                mPop.setPrice(" " + mPriceAdapter.getItem(oldSel).price + "元");
                mPop.setisRecharge(false);
                mPop.showPopupWindow();
            } else {
                Utils.toast("您还未选择套餐");
            }
        }else if(id == R.id.iv_close){
            dismiss();
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
}
