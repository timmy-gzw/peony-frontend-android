package com.tftechsz.family.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyConfigDto;
import com.tftechsz.family.entity.req.JoinRuleReq;
import com.tftechsz.family.mvp.IView.IChangeJoinConditionView;
import com.tftechsz.family.mvp.presenter.ChangeJoinConditionPresenter;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.utils.Utils;

/**
 * 更改加入条件
 */
public class ChangeJoinConditionActivity extends BaseMvpActivity<IChangeJoinConditionView, ChangeJoinConditionPresenter> implements View.OnClickListener, IChangeJoinConditionView {

    private TextView mTvEveryUser, mTvAllUser, mTvCustom, mTvCustomUser;
    private LinearLayout mLlCustom;  //自定义条件
    private RelativeLayout mRlRoot;
    private JoinRuleReq mData;
    private JoinRuleReq.JoinRule joinRule;
    private TextView mTvBoyLevel, mTvGirlLevel;
    private FamilyConfigDto mFamilyConfig;

    @Override
    public ChangeJoinConditionPresenter initPresenter() {
        return new ChangeJoinConditionPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mTvEveryUser = findViewById(R.id.tv_every_user);
        mTvAllUser = findViewById(R.id.tv_all_user);
        mTvCustom = findViewById(R.id.tv_custom);
        mTvCustomUser = findViewById(R.id.tv_custom_user);
        mLlCustom = findViewById(R.id.ll_custom);
        mRlRoot = findViewById(R.id.root);
        mTvBoyLevel = findViewById(R.id.tv_boy_level);
        mTvGirlLevel = findViewById(R.id.tv_girl_level);
        setListener();
    }


    private void setListener() {
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
        findViewById(R.id.rl_boy).setOnClickListener(this);
        findViewById(R.id.rl_girl).setOnClickListener(this);
        mTvEveryUser.setOnClickListener(this);
        mTvCustomUser.setOnClickListener(this);
        mTvAllUser.setOnClickListener(this);
        mTvCustom.setOnClickListener(this);
        mLlCustom.setOnClickListener(this);
    }


    @Override
    protected void initData() {
        super.initData();
        mData = new JoinRuleReq();
        joinRule = new JoinRuleReq.JoinRule();
        p.getJoinRule();
        p.getFamilyConfig();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_change_join_condition;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.submit) {
            if (mData != null) {
                if (mData.type == 0) {
                    mData.rules = null;
                } else {
                    joinRule.boy_rich_level = mTvBoyLevel.getText().toString();
                    joinRule.girl_charm_level = mTvGirlLevel.getText().toString();
                    mData.rules = joinRule;
                }
                p.changeJoinRule(mData);
            }
        } else if (id == R.id.tv_every_user) {  //每个人
            mData.type = 0;
            p.setDrawable(this, mTvEveryUser, mTvAllUser, mTvCustom);
            mLlCustom.setVisibility(View.GONE);
        } else if (id == R.id.tv_all_user) {  //自动通过
            mData.type = 2;
            p.setDrawable(this, mTvAllUser, mTvEveryUser, mTvCustom);
            mLlCustom.setVisibility(View.GONE);
        } else if (id == R.id.tv_custom) { //自定义
            mData.type = 1;
            p.setDrawable(this, mTvCustom, mTvAllUser, mTvEveryUser);
            mLlCustom.setVisibility(View.VISIBLE);
        } else if (id == R.id.rl_boy) {  //男性魅力值
            String level = "0";
            if (joinRule != null)
                level = joinRule.boy_rich_level;
            if (mFamilyConfig != null && mFamilyConfig.boy_rich_level != null) {
                p.chooseLevel(this, 1, mFamilyConfig.boy_rich_level, mRlRoot, level);
            }
        } else if (id == R.id.rl_girl) {  //女性魅力值
            String level = "0";
            if (joinRule != null)
                level = joinRule.girl_charm_level;
            if (mFamilyConfig != null && mFamilyConfig.girl_charm_level != null) {
                p.chooseLevel(this, 2, mFamilyConfig.girl_charm_level, mRlRoot, level);
            }
        } else if (id == R.id.tv_custom_user) { //满足条件后自动加入
            joinRule.is_auto = joinRule.is_auto == 1 ? 0 : 1;
            mTvCustomUser.setCompoundDrawablesWithIntrinsicBounds(joinRule.is_auto == 1 ? Utils.getDrawable(R.mipmap.family_ic_check_selector)
                    : Utils.getDrawable(R.mipmap.family_ic_check_normal), null, null, null);
        }

    }

    @Override
    public void changeJoinRuleSuccess(Boolean data) {
        if (data) {
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_REFRESH_RECOMMEND));
            toastTip("更改成功");
            setResult(RESULT_OK);
            finish();
        }
    }

    /**
     * 获取条件成功
     */
    @Override
    public void getChangeJoinRule(JoinRuleReq data) {
        mData = data;
        if (data.type == 0) {
            p.setDrawable(this, mTvEveryUser, mTvAllUser, mTvCustom);
            mLlCustom.setVisibility(View.GONE);
        } else if (data.type == 2) {
            p.setDrawable(this, mTvAllUser, mTvEveryUser, mTvCustom);
            mLlCustom.setVisibility(View.GONE);
        } else {
            p.setDrawable(this, mTvCustom, mTvAllUser, mTvEveryUser);
            mLlCustom.setVisibility(View.VISIBLE);
            if (data.rules != null) {
                joinRule = data.rules;
                mTvBoyLevel.setText(data.rules.boy_rich_level);
                mTvGirlLevel.setText(data.rules.girl_charm_level);
                mTvCustomUser.setCompoundDrawablesWithIntrinsicBounds(mData.rules.is_auto == 1 ? Utils.getDrawable(R.mipmap.family_ic_check_selector)
                        : Utils.getDrawable(R.mipmap.family_ic_check_normal), null, null, null);
            } else {
                mTvCustomUser.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(R.mipmap.family_ic_check_normal), null, null, null);
            }
        }
    }

    @Override
    public void getChooseLevel(int type, String data) {
        if (type == 1) {
            mTvBoyLevel.setText(data);
            joinRule.boy_rich_level = mTvBoyLevel.getText().toString();
        } else {
            mTvGirlLevel.setText(data);
            joinRule.girl_charm_level = mTvGirlLevel.getText().toString();
        }
        mData.rules = joinRule;
    }


    @Override
    public void getFamilyConfigSuccess(FamilyConfigDto data) {
        mFamilyConfig = data;
    }

}
