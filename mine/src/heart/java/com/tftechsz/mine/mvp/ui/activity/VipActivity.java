
package com.tftechsz.mine.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.PayTextClick;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.VipPriceAdapter;
import com.tftechsz.mine.adapter.VipPrivilegeAdapter;
import com.tftechsz.mine.databinding.ActVipBinding;
import com.tftechsz.mine.entity.DressUpBean;
import com.tftechsz.mine.entity.VipPriceBean;
import com.tftechsz.mine.entity.VipPrivilegeBean;
import com.tftechsz.mine.entity.dto.VipConfigBean;
import com.tftechsz.mine.mvp.IView.IVipView;
import com.tftechsz.mine.mvp.presenter.IVipPresenter;
import com.tftechsz.mine.widget.pop.VipOpenPopWindow;

import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 会员
 */
@Route(path = ARouterApi.ACTIVITY_VIP)
public class VipActivity extends BaseMvpActivity<IVipView, IVipPresenter> implements IVipView, View.OnClickListener {

    private ActVipBinding mBind;

    @Autowired
    UserProviderService service;
    private VipPriceAdapter mPriceAdapter;
    private VipPrivilegeAdapter mPrivilegeAdapter;
    private int oldSel = -1;
    private VipOpenPopWindow mPop;
    private int mPayId;
    private boolean inPayStatus;

    @Override
    public IVipPresenter initPresenter() {
        return new IVipPresenter();
    }

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_vip);
        return 0;
    }

    @Override
    protected boolean getImmersionBar() {
        return false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView(Bundle savedInstanceState) {
        mPayId = getIntent().getIntExtra(Interfaces.EXTRA_ID, 0);
        ImmersionBar.with(this).titleBar(mBind.toolbar).statusBarDarkFont(false).navigationBarColor(R.color.c_1d1d1d).init();
        mBind.tvTitle.setText("VIP会员");
        mBind.ivBack.setOnClickListener(v -> finish());
        mBind.tvVipUnlock.setOnClickListener(this);
        GlideUtils.loadRouteImage(this, mBind.icon, service.getUserInfo().getIcon(), service.getUserInfo().isBoy() ? R.mipmap.mine_ic_boy_default : R.mipmap.mine_ic_girl_default);
//        GlideUtils.loadRouteImage(this, mBind.userIcon, service.getUserInfo().getIcon(), service.getUserInfo().isBoy() ? R.mipmap.mine_ic_boy_default : R.mipmap.mine_ic_girl_default);

        mBind.name.setText(service.getUserInfo().getNickname());

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

        /*开通vip会员*/
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
            setPayButton(position);
            oldSel = position;
        });

        /*vip会员特权*/
        mPrivilegeAdapter = new VipPrivilegeAdapter();
        mBind.privilegeRecy.setLayoutManager(new GridLayoutManager(mContext, 4));
        mBind.privilegeRecy.setAdapter(mPrivilegeAdapter);

        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                mPop.dismiss();
                                mBind.hint.postDelayed(this::onResume, 300);
                            }
                        }
                ));


        if (service.getUserInfo().isVip()) {
            long timeSpanByNow = TimeUtils.getTimeSpanByNow(service.getUserInfo().getVip_expiration_time(), TimeConstants.DAY);
            if (timeSpanByNow <= 7) {
                mBind.hint.setTextColor(ContextCompat.getColor(this, R.color.c_d72537));
            } else {
                mBind.hint.setTextColor(ContextCompat.getColor(this, R.color.c_c89c5d));
            }
        } else {
            mBind.hint.setTextColor(ContextCompat.getColor(this, R.color.c_c89c5d));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Interfaces.PAY_REQUEST_CODE) {
            inPayStatus = true;
        }
    }

    @Override
    protected void initData() {
        mBind.getVip.setOnClickListener(this);
//        mBind.vipBubbleBg.setOnClickListener(this);
//        mBind.vipFrameBg.setOnClickListener(this);
//        mBind.loadMore.setOnClickListener(this);

//        if (service.getUserInfo().isVip()) {
//            p.getVipConfig();
//        } else {
        p.getVipPrice();
//        }
        p.getVipPrivilege();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBind.setIsVip(service.getUserInfo().isVip());
        if (service.getUserInfo().isVip()) {
            mBind.setVipExpirationTime(service.getUserInfo().vip_expiration_time_desc);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (inPayStatus) {
            inPayStatus = false;
            mPop.dismiss();
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.get_vip || id == R.id.tv_vip_unlock) {//获取vip
            if (oldSel >= 0 && oldSel < mPriceAdapter.getItemCount()) {
                if (mPop == null) {
                    mPop = new VipOpenPopWindow(mActivity);
                }
//                mPop.setTypeId(mPriceAdapter.getItem(oldSel).id);
//                mPop.setisRecharge(service.getUserInfo().isVip());
//                mPop.setisRecharge(false);
                mPop.showPopupWindow();
            } else {
                toastTip("您还未选择套餐");
            }
        }
//        else if (id == R.id.vip_frame_bg) {//头像框
//            ARouterUtils.toVipDressUp(1, false);
//        }
//        else if (id == R.id.vip_bubble_bg || id == R.id.load_more) { //聊天起泡 ||加载更多
//            ARouterUtils.toVipDressUp(2, false);
//        }
    }

    private void setPayButton(int position) {
        VipPriceBean item = mPriceAdapter.getItem(position);
        mBind.setItemPrice(item.price);
    }

    @Override
    public void getVipPriceSuccess(List<VipPriceBean> bean) {
        if (mPayId > 0) {
            for (VipPriceBean b : bean) {
                b.setSel(b.id == mPayId);
            }
        }
        mPriceAdapter.setList(bean);
        for (int i = 0; i < bean.size(); i++) {
            VipPriceBean vipPriceBean = bean.get(i);
            if (vipPriceBean.isSel()) {
                oldSel = i;
                setPayButton(i);
                break;
            }
        }
    }

    @Override
    public void getVipPrivilegeSuccess(List<VipPrivilegeBean> bean) {
        mPrivilegeAdapter.setList(bean);
        mBind.setVipPrivilegeCount(bean == null ? 0 : bean.size());
    }

    @Override
    public void getVipConfigSuccess(VipConfigBean data) {
//        VipUtils.setPersonalise(mBind.chatBubble, data.chat_bubble, true);
//        VipUtils.setPersonalise(mBind.picFrame, data.picture_frame, false, true);
    }

    @Override
    public void getDressUpSuccess(List<DressUpBean> data) {

    }

    @Override
    public void postVipConfigSuccess() {

    }


}
