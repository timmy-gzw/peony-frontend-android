package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseActivity;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.IView.IMineIntegralView;
import com.tftechsz.mine.mvp.presenter.MineIntegralPresenter;

/**
 * 我的积分
 */
@Route(path = ARouterApi.ACTIVITY_MINE_INTEGRAL)
public class MineIntegralActivity extends BaseMvpActivity<IMineIntegralView, MineIntegralPresenter> implements View.OnClickListener, IMineIntegralView {

    private TextView mTvIntegralNum;   //积分
    private TextView mTvWithdrawNum;  //金币
    private TextView mTvFriend, mTvIntegralQuestion;   //交友密集，积分问题
    private ConfigInfo configInfo;
    @Autowired
    UserProviderService service;
    private boolean mIsNoteValue;
    private IntegralDto mData;

    @Override
    public MineIntegralPresenter initPresenter() {
        return new MineIntegralPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mIsNoteValue = getIntent().getIntExtra(Interfaces.EXTRA_TYPE, 0) == 1;
        new BaseActivity.ToolBarBuilder().showBack(true)
                .setTitle(mIsNoteValue ? "我的音符" : "我的积分")
                .setRightText(mIsNoteValue ? "音符清单" : "积分清单", this)
                .build();
        mTvIntegralNum = findViewById(R.id.tv_integral_num);   //积分余额
        mTvWithdrawNum = findViewById(R.id.tv_withdraw_num);
        mTvFriend = findViewById(R.id.tv_friend);
        mTvIntegralQuestion = findViewById(R.id.tv_integral_question);
        TextView tv_intefral_shop = findViewById(R.id.tv_intefral_shop);
        tv_intefral_shop.setOnClickListener(this);
        mTvFriend.setOnClickListener(this);
        mTvIntegralQuestion.setOnClickListener(this);

        if (mIsNoteValue) { //如果是音符类型
            TextView tv_integral = findViewById(R.id.tv_integral);
            tv_integral.setText("音符余额 (个)");
            tv_integral.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.peony_me_jfye_icon, 0, 0, 0);
            TextView tv_withdraw = findViewById(R.id.tv_withdraw);
            tv_withdraw.setText("最多可兑换金币 (个)");
            mTvIntegralQuestion.setText("音符问题");
            tv_intefral_shop.setText("音符兑换");
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_mine_integral;
    }


    @Override
    protected void initData() {
        super.initData();
        configInfo = service.getConfigInfo();
        mTvFriend.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mTvIntegralQuestion.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mTvFriend.getPaint().setAntiAlias(true);//抗锯齿；
        mTvIntegralQuestion.getPaint().setAntiAlias(true);//抗锯齿
        if (mIsNoteValue) {
            p.getNoteValue();
        } else {
            p.getIntegral();
        }
        initRxBus();
        if (null != configInfo && null != configInfo.api && null != configInfo.api.my_integral && configInfo.api.my_integral.size() >= 1) {
            mTvIntegralQuestion.setVisibility(View.VISIBLE);
        } else {
            mTvIntegralQuestion.setVisibility(View.GONE);
        }

    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                if (mIsNoteValue) {
                                    p.getNoteValue();
                                } else {
                                    p.getIntegral();
                                }
                            }
                        }
                ));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_tv_menu) {//顶部右边文本
            if (mIsNoteValue) {
                ARouterUtils.toIntegralDetailedActivity(4);
            } else {
                ARouterUtils.toIntegralDetailedActivity(0);
            }
        } else if (id == R.id.tv_intefral_shop) { // btn
            if (mIsNoteValue) {
                Intent intent = new Intent(this, NoteValueShopActivity.class);
                intent.putExtra(Interfaces.EXTRA_DATA, mData);
                startActivity(intent);
            } else {
                startActivity(IntegralShopActivity.class, "integral", integral);
            }
        } else if (id == R.id.tv_friend) {  //交友
//            startWeb(0);
        } else if (id == R.id.tv_integral_question) {  //积分问题
            startWeb();
        }

    }


    private void startWeb() {
        if (mIsNoteValue) {
            CommonUtil.performLink(mActivity, new ConfigInfo.MineInfo(mData.note_problem), 0, 19);
        } else {
            if (null != configInfo && null != configInfo.api && null != configInfo.api.my_integral && configInfo.api.my_integral.size() >= 1) {
                if (null != configInfo.api.my_integral.get(0))
                    CommonUtil.performLink(mActivity, configInfo.api.my_integral.get(0), 0, 19);
            }
        }
    }


    public String integral;

    @Override
    public void getIntegralSuccess(IntegralDto data) {
        integral = data.integral;
        this.mData = data;
        if (mIsNoteValue) {
            mTvIntegralNum.setText(data.note_value);
            mTvWithdrawNum.setText(data.coin);
            mTvIntegralQuestion.setVisibility(TextUtils.isEmpty(data.note_problem) ? View.GONE : View.VISIBLE);
        } else {
            mTvIntegralNum.setText(data.integral);
            mTvWithdrawNum.setText(data.rmb);
        }
    }
}
