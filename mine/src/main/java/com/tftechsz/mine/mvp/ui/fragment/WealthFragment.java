package com.tftechsz.mine.mvp.ui.fragment;

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
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.LevelTitleAdapter;
import com.tftechsz.mine.adapter.LevelUpgradeAdapter;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.GradeLevelDto;

import java.math.BigDecimal;

public class WealthFragment extends BaseMvpFragment {

    private String type, userId;
    private ConstraintLayout mClBg;
    private ImageView mIvAvatar;
    private TextView mTvName, mTvTitle, mTvLevel, mTvNextLevelNum, mTvNextLevel;
    private ProgressBar mProgressbar;
    private RecyclerView mRvUpgrade, mRvLevelTitle;

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
                        mTvLevel.setText("Lv." + dto.level);
                        mTvNextLevelNum.setText("距离升级还差：" + dto.diff);
                        mTvNextLevel.setText("Lv." + (dto.level + 1));
                        BigDecimal myl = new BigDecimal(dto.total);
                        BigDecimal next = new BigDecimal(dto.total + "").add(new BigDecimal(dto.diff + ""));
                        BigDecimal progress = myl.divide(next, 2, BigDecimal.ROUND_UP).multiply(new BigDecimal("100"));
                        mProgressbar.setProgress(progress.intValue());
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
        mTvName = getView(R.id.tv_name);
        mTvTitle = getView(R.id.tv_title);
        mTvLevel = getView(R.id.tv_level);
        mTvNextLevelNum = getView(R.id.tv_level_to_next);
        mTvNextLevel = getView(R.id.tv_level_next);
        mProgressbar = getView(R.id.progress_bar);
        mRvUpgrade = getView(R.id.rv_upgrade);
        mRvLevelTitle = getView(R.id.rv_level_title);
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
        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        if (!TextUtils.isEmpty(userId)) {//他人
            mClBg.setVisibility(View.GONE);
        } else {//自己
            mClBg.setVisibility(View.VISIBLE);
            if (type.equals("0")) {//财富
                mClBg.setBackgroundResource(R.mipmap.bg_wealth_level);
                mTvName.setTextColor(Color.parseColor("#EDD398"));
                mTvLevel.setTextColor(Color.parseColor("#EDD398"));
                mTvNextLevelNum.setTextColor(Color.parseColor("#EDD398"));
                mTvNextLevel.setTextColor(Color.parseColor("#EDD398"));
                mProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_drawable_bg));
            } else {//魅力
                mClBg.setBackgroundResource(R.mipmap.bg_charm_level);
                mTvName.setTextColor(Color.parseColor("#4337FF"));
                mTvLevel.setTextColor(Color.parseColor("#4337FF"));
                mTvNextLevelNum.setTextColor(Color.parseColor("#4337FF"));
                mTvNextLevel.setTextColor(Color.parseColor("#4337FF"));
                mProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_charm_bg));
            }
            if (null != service) {
                mTvName.setText(service.getUserInfo().getNickname());
                Glide.with(getActivity()).load(service.getUserInfo().getIcon()).into(mIvAvatar);
//                mTvTitle.setText(type.equals("0")?service.getUserInfo().getLevels().rich.value:service.getUserInfo().getLevels().charm.value);
            }
        }
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
}
