package com.tftechsz.mine.mvp.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.LevelTitleAdapter;
import com.tftechsz.mine.adapter.LevelUpgradeAdapter;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.GradeLevelDto;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

public class WealthFragment extends BaseMvpFragment {

    private String type, userId;
    private ConstraintLayout mClBg;
    private ImageView mIvAvatar;
    private TextView mTvName, mTvTitle, mTvLevel, mTvNextLevelNum, mTvNextLevel;
    private ProgressBar mProgressbar;
    private RecyclerView mRvUpgrade, mRvLevelTitle;
    private ImageView mIvTitle,mIvCard;
    private UserProviderService service;
    private TextView mTitleWealth,mWealthDesc,mTvAge,title;
    private ImageView mAvaterBg;

    //type 0 财富 1 魅力
    //userId 空为自己，有值则是他人
    public static WealthFragment newInstance(String type, String userId, String sex) {
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("userId", userId);
        args.putString("sex", sex);
        WealthFragment fragment = new WealthFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public void getData() {
        mCompositeDisposable.add(RetrofitManager.getInstance().createUserApi(MineApiService.class).getAccostList(type=="0"?"rich":"charm").compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<GradeLevelDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<GradeLevelDto> response) {
                        GradeLevelDto dto = response.getData();
                        mTvTitle.setText(dto.title);
                        mTvLevel.setText("LV." + dto.level);
                        mTvNextLevelNum.setText("距离升级还差：" + dto.diff);
                        mTvNextLevel.setText("LV." + (dto.level + 1));
                        BigDecimal myl = new BigDecimal(dto.total);
                        BigDecimal next = new BigDecimal(dto.total + "").add(new BigDecimal(dto.diff + ""));
                        BigDecimal progress = myl.divide(next, 2, BigDecimal.ROUND_UP).multiply(new BigDecimal("100"));
                        mProgressbar.setProgress(progress.intValue());
                        if(null != service){
                            List<ConfigInfo.LevelLadder> levelDatas =  type == "0" ? service.getConfigInfo().share_config.wealth_level_ladder : service.getConfigInfo().share_config.charm_level_ladder;
                            ConfigInfo.LevelLadder temp = levelDatas.get(levelDatas.size()-1);
                            for (int i = 0; i < levelDatas.size(); i++) {
                                ConfigInfo.LevelLadder levelLadder = levelDatas.get(i);
                                if(dto.level>=levelLadder.min_level&&dto.level<=levelLadder.max_level){
                                    temp = levelLadder;
                                    break;
                                }
                            }
                            ConfigInfo config = getConfig(mContext);
                            Glide.with(getActivity()).load(config.api.oss.cdn_scheme + config.api.oss.cdn.pl+temp.icon).into(mIvTitle);
                        }
                    }
                }));

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_wealth;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        mClBg = getView(R.id.cl_userinfo);
        mIvAvatar = getView(R.id.iv_avatar);
        mTvAge = getView(R.id.tv_age);
        mIvTitle = getView(R.id.iv_title);
        mIvCard = getView(R.id.iv_card);
        mTvName = getView(R.id.tv_name);
        mTvTitle = getView(R.id.tv_title);
        mTvLevel = getView(R.id.tv_level);
        mTvNextLevelNum = getView(R.id.tv_level_to_next);
        mTvNextLevel = getView(R.id.tv_level_next);
        mProgressbar = getView(R.id.progress_bar);
        mRvUpgrade = getView(R.id.rv_upgrade);
        mRvLevelTitle = getView(R.id.rv_level_title);
        mTitleWealth = getView(R.id.tv_title_wealth);
        mWealthDesc = getView(R.id.tv_wealth_decs);
        mAvaterBg = getView(R.id.bg_image);
        title = getView(R.id.title);
        type = getArguments().getString("type");
        userId = getArguments().getString("userId");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvUpgrade.setLayoutManager(linearLayoutManager);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRvLevelTitle.setLayoutManager(llm);
    }

    @Override
    protected void initData() {
        getData();
        service = ARouter.getInstance().navigation(UserProviderService.class);
        if (!TextUtils.isEmpty(userId)) {//他人
            mClBg.setVisibility(View.GONE);
        } else {//自己
            mClBg.setVisibility(View.VISIBLE);
            if (type.equals("0")) {//财富
                mIvCard.setImageResource(R.mipmap.ic_card_wealth);
                mWealthDesc.setBackgroundResource(R.mipmap.bg_wealth);
                mClBg.setBackgroundResource(R.drawable.bg_card_wealth);
                mWealthDesc.setTextColor(Color.parseColor("#B2500A"));
                mTvName.setTextColor(Utils.getColor(R.color.c_edd398));
                mTvLevel.setTextColor(Utils.getColor(R.color.c_edd398));
                mTvNextLevelNum.setTextColor(Utils.getColor(R.color.white));
                mTvNextLevel.setTextColor(Utils.getColor(R.color.c_edd398));
                mProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_drawable_bg));
                mTitleWealth.setText("什么是财富等级？");
                mWealthDesc.setText("财富等级是您在平台中财富实力的象征，等级越高越容易获得异性的关注!");
                mAvaterBg.setImageResource(R.mipmap.bg_wealth_avater);
                title.setText("财富称号");
            } else {//魅力
                mIvCard.setImageResource(R.mipmap.ic_card_charm);
                mWealthDesc.setBackgroundResource(R.mipmap.bg_charm);
                mClBg.setBackgroundResource(R.drawable.bg_card_charm);
                mWealthDesc.setTextColor(Utils.getColor(R.color.white));
                mTvName.setTextColor(Utils.getColor(R.color.white));
                mTvLevel.setTextColor(Utils.getColor(R.color.white));
                mTvNextLevelNum.setTextColor(Utils.getColor(R.color.white));
                mTvNextLevel.setTextColor(Utils.getColor(R.color.white));
                mProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_drawable_bg));
                mTitleWealth.setText("什么是魅力等级？");
                mWealthDesc.setText("魅力等级是您在平台魅力的象征，等级越高表示您越受欢迎!");
                mAvaterBg.setImageResource(R.mipmap.bg_charm_avater);
                title.setText("魅力称号");
            }
            if (null != service) {
                mTvName.setText(service.getUserInfo().getNickname());
                Glide.with(getActivity()).load(service.getUserInfo().getIcon()).into(mIvAvatar);
            }
        }
        if(service.getUserInfo().isGirl()){
            mTvAge.setCompoundDrawablesWithIntrinsicBounds(getActivity().getResources().getDrawable(R.drawable.ic_girl),null,null,null);
            mTvAge.setBackgroundResource(R.drawable.bg_girl);
        }else{
            mTvAge.setCompoundDrawablesWithIntrinsicBounds(getActivity().getResources().getDrawable(R.drawable.ic_boy),null,null,null);
            mTvAge.setBackgroundResource(R.drawable.bg_boy);
        }
        mTvAge.setText(String.valueOf(service.getUserInfo().getAge()));
        LevelUpgradeAdapter upgradeAdapter = new LevelUpgradeAdapter(getActivity());
        mRvUpgrade.setAdapter(upgradeAdapter);
        upgradeAdapter.setList(type == "0" ? service.getConfigInfo().share_config.wealth_level_introduction : service.getConfigInfo().share_config.charm_level_introduction);
        LevelTitleAdapter titleAdapter = new LevelTitleAdapter(getActivity(), getArguments().getString("sex"));
        mRvLevelTitle.setAdapter(titleAdapter);
        titleAdapter.setList(type == "0" ? service.getConfigInfo().share_config.wealth_level_ladder : service.getConfigInfo().share_config.charm_level_ladder);
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    public ConfigInfo getConfig(Context context) {
        SharedPreferences sp = context.getSharedPreferences("tfpeony-pref",
                Context.MODE_PRIVATE);
        String config = sp.getString("configInfo", "");
        Gson gson = new Gson();
        ConfigInfo configInfo = gson.fromJson(config, (Type) ConfigInfo.class);
        return configInfo;
    }

}
