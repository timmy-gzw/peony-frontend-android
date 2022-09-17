package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.BaseUserInfoAdapter;
import com.tftechsz.mine.adapter.GiftAdapter;
import com.tftechsz.mine.entity.dto.GiftDto;
import com.tftechsz.mine.mvp.IView.IMineAboutMeView;
import com.tftechsz.mine.mvp.presenter.MineAboutMePresenter;
import com.tftechsz.mine.mvp.ui.activity.MyWealthCharmLevelActivity;
import com.tftechsz.mine.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人主页 - 基础信息
 */
@Route(path = ARouterApi.FRAGMENT_USER_INFO)
public class MineAboutMeFragment extends BaseMvpFragment<IMineAboutMeView, MineAboutMePresenter> implements IMineAboutMeView, View.OnClickListener {

    @Autowired(name = "user_id")
    public String mUserId;
    @Autowired(name = "user_info")
    public UserInfo mUserInfo;
    @Autowired
    UserProviderService service;

    //等级相关
    private ConstraintLayout mClLevel;
    private TextView tvLevelTitle, tvLevelVip;
    private TextView mTvLocalTyrantTitle, mTvLocalTyrantLevel;    //土豪值相关
    private TextView mTvCharmTitle, mTvCharmLevel;   //魅力值相关
    private ImageView ivCharmBg, ivLocalTyrantBg;

    private LinearLayout mLlLevelCover, mLlGiftCover;

    //礼物相关
    private RecyclerView mRvGift;  //礼物
    private TextView mTvGift, mTvGiftVip, tvGiftTitle, tvGiftCount;
    private ConstraintLayout clGift;

    //标签
    private TextView mTvLabel;

    private BaseUserInfoAdapter userInfoAdapter;
    private GiftAdapter giftAdapter;
    private ArrayList<GiftDto> gifts = null;

    @Override
    protected int getLayout() {
        return R.layout.fragment_mine_about_me;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        //用户基本信息
        TextView tvUserInfo = getView(R.id.tv_user_info);
        RecyclerView rvUserInfo = getView(R.id.rv_user_info);
        rvUserInfo.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvUserInfo.addItemDecoration(new SpacingDecoration(ConvertUtils.dp2px(15f), ConvertUtils.dp2px(6), false));
        userInfoAdapter = new BaseUserInfoAdapter();
        userInfoAdapter.addChildClickViewIds(R.id.iv_copy);
        userInfoAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_copy) {
                UserInfo.BaseInfo item = userInfoAdapter.getItem(position);
                if (item == null) return;
                ClipboardUtils.copyText(item.value);
                toastTip("复制成功");
            }
        });
        rvUserInfo.setAdapter(userInfoAdapter);
        //等级
        mClLevel = getView(R.id.cl_level);
        tvLevelTitle = getView(R.id.tv_level_title);
        tvLevelVip = getView(R.id.tv_level_vip);

        getView(R.id.cl_local_tyrant).setOnClickListener(this);  //土豪值
        getView(R.id.cl_charm).setOnClickListener(this);  //亲密度
        mTvLabel = getView(R.id.tv_label);
        mTvLabel.setOnClickListener(this);
        //土豪值
        ivLocalTyrantBg = getView(R.id.iv_local_tyrant_bg);
        mTvLocalTyrantLevel = getView(R.id.tv_local_tyrant_level);
        mTvLocalTyrantTitle = getView(R.id.tv_local_tyrant_title);
        //魅力值
        ivCharmBg = getView(R.id.iv_charm_bg);
        mTvCharmLevel = getView(R.id.tv_charm_level);
        mTvCharmTitle = getView(R.id.tv_charm_title);
        mLlLevelCover = getView(R.id.ll_hidden);
        mLlGiftCover = getView(R.id.ll_hidden2);
        //礼物
        mTvGift = getView(R.id.tv_gift);
        mTvGiftVip = getView(R.id.tv_gift_vip);
        tvGiftTitle = getView(R.id.tv_gift_title);
        tvGiftCount = getView(R.id.tv_gift_count);
        clGift = getView(R.id.cl_mine_gift);
        mRvGift = getView(R.id.rv_gift);
        mRvGift.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        giftAdapter = new GiftAdapter();
        mRvGift.setAdapter(giftAdapter);
        giftAdapter.setOnItemClickListener((adapter, view, position) -> {
            Postcard postcard = ARouter.getInstance().build(ARouterApi.ACTIVITY_GIFT_WALL)
                    .withString("user_id", mUserId);
            if (gifts != null && gifts.size() > 0) {
                postcard.withParcelableArrayList("gifts", gifts);
            }
            postcard.navigation();
        });
        if (TextUtils.isEmpty(mUserId)) {   //自己
            tvUserInfo.setText(getString(R.string.personal_info));
            tvLevelTitle.setText(getString(R.string.level_mine));
            tvGiftTitle.setText(getString(R.string.receive_gift_my));
        } else {
            tvUserInfo.setText(getString(R.string.basic_info));
            tvLevelVip.setVisibility(View.GONE);
            mTvGiftVip.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        setUserInfo();
        initNet();
    }

    private void setUserInfo() {
        if (mUserInfo == null) return;
        if (userInfoAdapter != null) {
            List<UserInfo.BaseInfo> infoList = mUserInfo.info;
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(ConvertUtils.dp2px(12f));
            int max = 0;
            for (UserInfo.BaseInfo baseInfo : infoList) {
                float measuredWidth = textPaint.measureText(baseInfo.title);
                max = Math.max(max, Math.round(measuredWidth));
            }
            userInfoAdapter.setTextViewMaxWidth(max);
            userInfoAdapter.setList(infoList);
        }
        if (mUserInfo.levels != null && mUserInfo.levels.rich != null && mUserInfo.levels.charm != null) {
            mClLevel.setVisibility(View.VISIBLE);
            //土豪值
            GlideUtils.loadImage(getContext(), ivLocalTyrantBg, mUserInfo.levels.rich.icon);
            mTvLocalTyrantTitle.setText(mUserInfo.levels.rich.title);
            mTvLocalTyrantLevel.setText(getString(R.string.rich_lv_format, mUserInfo.levels.rich.level));
            //魅力值
            GlideUtils.loadImage(getContext(), ivCharmBg, mUserInfo.levels.charm.icon);
            mTvCharmTitle.setText(mUserInfo.levels.charm.title);
            mTvCharmLevel.setText(getString(R.string.charm_lv_format, mUserInfo.levels.charm.level));
        } else {
            mLlLevelCover.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(mUserId)) {   //自己
            if (mUserInfo.open_hidden_rank == 0) {
                tvLevelVip.setVisibility(View.GONE);
            } else {
                tvLevelVip.setVisibility(View.VISIBLE);
            }
            if (mUserInfo.open_hidden_gift == 0) {
                mTvGiftVip.setVisibility(View.GONE);
            } else {
                mTvGiftVip.setVisibility(View.VISIBLE);
            }
        } else {
            tvLevelTitle.setText(mUserInfo.isGirl() ? getString(R.string.level_her) : getString(R.string.level_his));
            tvGiftTitle.setText(mUserInfo.isGirl() ? getString(R.string.receive_gift_her) : getString(R.string.receive_gift_his));
            mTvLabel.setText(mUserInfo.isGirl() ? getString(R.string.label_her) : getString(R.string.label_his));
            mTvLabel.setCompoundDrawables(null, null, null, null);
        }
    }

    private void initNet() {
        String uid;
        if (TextUtils.isEmpty(mUserId)) {   //自己
            uid = UserManager.getInstance().getUserId() + "";
        } else {
            uid = mUserId;
        }
        p.getGiftList(uid);
    }

    @Override
    protected MineAboutMePresenter initPresenter() {
        return new MineAboutMePresenter();
    }

    @Override
    public void getGiftSuccess(ArrayList<GiftDto> data) {
        gifts = data;
        if (null == data || data.size() <= 0) {
            clGift.setVisibility(View.INVISIBLE);
            mTvGiftVip.setVisibility(View.GONE);
            mLlGiftCover.setVisibility(View.VISIBLE);
        } else {
            if (mUserInfo != null && mUserInfo.open_hidden_gift == 1 && !TextUtils.isEmpty(mUserId)) {
                clGift.setVisibility(View.INVISIBLE);
                mTvGiftVip.setVisibility(View.GONE);
                mLlGiftCover.setVisibility(View.VISIBLE);
            } else {
                List<GiftDto> giftList = data.subList(0, 3);
                giftAdapter.setList(giftList);
                clGift.setVisibility(View.VISIBLE);
                //fixme 礼物计数
                int count = 0;
                for (GiftDto dto : data) {
                    if (dto.number > 0) {
                        count++;
                    }
                }
                if (count <= 0) tvGiftTitle.setText(getString(R.string.receive_gift_empty));
                tvGiftCount.setText(count + "/" + data.size());
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cl_local_tyrant) {    //土豪值
//            if (!TextUtils.isEmpty(mUserId))
//                return;
            if (null != mUserInfo.levels && null != mUserInfo.levels.rich) {
                MyWealthCharmLevelActivity.startActivity(getActivity(), "0", mUserInfo.getSex() + "", mUserId); //用户性别：0.未知，1.男，2.女
            }
        } else if (id == R.id.cl_charm) {    //魅力值
//            if (!TextUtils.isEmpty(mUserId))
//                return;
            if (null != mUserInfo.levels && null != mUserInfo.levels.charm) {
                MyWealthCharmLevelActivity.startActivity(getActivity(), "1", mUserInfo.getSex() + "", mUserId); //用户性别：0.未知，1.男，2.女
            }
        } else if (id == R.id.tv_label) {
            if (!TextUtils.isEmpty(mUserId))
                return;
            ARouter.getInstance().build(ARouterApi.ACTIVITY_CHOOSE_TAG).withString("from", "info").navigation();
        }

    }
}
