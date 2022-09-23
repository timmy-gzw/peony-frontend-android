package com.tftechsz.home.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.like.LikeButton;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.bean.AccostDto;
import com.robinhood.ticker.TickerView;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.fragment.PicLoadFragment;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.home.R;
import com.tftechsz.home.entity.InfoPictureBean;
import com.tftechsz.home.mvp.iview.IPicBrowserView;
import com.tftechsz.home.mvp.presenter.PicBrowserPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 包 名 : com.tftechsz.home.mvp.ui.activity
 * 描 述 : 用户的图片查看
 */
@Route(path = ARouterApi.ACTIVITY_USER_PIC_BROWSER)
public class UserPicBrowserActivity extends BaseMvpActivity<IPicBrowserView, PicBrowserPresenter> implements IPicBrowserView, View.OnClickListener {

    private ViewPager mViewPager;
    private TextView mTvNum;
    private ImageView mIvAvatar;
    private LinearLayout mLlLike;
    private TextView mTvName;
    private ImageView mIs_real;
    private LikeButton mIv_like;
    private TickerView mTv_like;
    private LikeButton mIv_accost;
    private TextView mTv_accost;
    private UserProviderService service;
    private InfoPictureBean mPictureBean;
    private int mIndexPos;
    private int mUid;
    private String mFirstIcon;
    private LinearLayout mLlAccost;
    private boolean mFlagIsBoy;

    @Override
    protected int getLayout() {
        return R.layout.act_user_pic_browser;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mViewPager = findViewById(R.id.pic_viewpager);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvName = findViewById(R.id.tv_name);
        mTvNum = findViewById(R.id.tv_num);
        mIs_real = findViewById(R.id.is_real);
        mIs_real.setVisibility(View.GONE);
        mLlLike = findViewById(R.id.ll_like);
        mIv_like = findViewById(R.id.iv_like);
        mTv_like = findViewById(R.id.tv_like);
        mIv_accost = findViewById(R.id.iv_accost);
        mTv_accost = findViewById(R.id.tv_accost);
        mLlAccost = findViewById(R.id.ll_accost);

        mIv_accost.setOnClickListener(this);
        mIvAvatar.setOnClickListener(this);
        mLlLike.setOnClickListener(this);
        mIv_like.setOnClickListener(this);
        mLlAccost.setOnClickListener(this);

      /*  ConstraintLayout.LayoutParams topLp = (ConstraintLayout.LayoutParams) mIvAvatar.getLayoutParams();
        topLp.topMargin = ImmersionBar.getStatusBarHeight(this) + ConvertUtils.dp2px(10);
        mIvAvatar.setLayoutParams(topLp);
        ConstraintLayout.LayoutParams botLp = (ConstraintLayout.LayoutParams) mLlLike.getLayoutParams();
        botLp.bottomMargin = ConvertUtils.dp2px(20);
        mLlLike.setLayoutParams(botLp);*/
        mUid = getIntent().getIntExtra(Interfaces.EXTRA_UID, 0);
        mIndexPos = getIntent().getIntExtra(Interfaces.EXTRA_INDEX, 0);
        mFirstIcon = getIntent().getStringExtra(Interfaces.EXTRA_FIRST_ICON);
        mFlagIsBoy = getIntent().getBooleanExtra(Interfaces.EXTRA_ISBOY_ICON, false);
        p.getInfoPicture(mUid);

        mIv_accost.setUnlikeDrawableRes(
                CommonUtil.isBtnTextPhoto(service) ? R.mipmap.peony_xihuam_icon_new1 : R.mipmap.peony_home_xcds_icon
        );
    }

    @Override
    protected void initData() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .navigationBarColor(R.color.black)
                .statusBarColor(R.color.black)
                .statusBarDarkFont(false)
                .navigationBarDarkIcon(false)
                .init();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_avatar) { //头像
            if (mPictureBean != null && mPictureBean.getUser_id() != 0) {
                if (!TextUtils.isEmpty(mFirstIcon)) {
                    finish();
                } else {
                    ARouterUtils.toMineDetailActivity(String.valueOf(mPictureBean.getUser_id()));
                }
            }
        } else if (id == R.id.ll_like || id == R.id.iv_like) {//点赞
            if (mPictureBean != null) {
                if (mPictureBean.getIs_praise_picture() != 1) {
                    p.picLike(mPictureBean.getUser_id());
                } else {
                    toastTip("您已点过赞啦");
                }
            }
        } else if (id == R.id.ll_accost || id == R.id.iv_accost) {//搭讪
            if (mPictureBean == null) {
                return;
            }
            if (mPictureBean.isAccost()) {// 搭讪过 进入聊天
                if (service != null && service.getUserInfo() != null && service.getUserInfo().isGirl()) {   //判断女性
                    p.getMsgCheck(mPictureBean.getUser_id() + "");
                } else {
                    ARouterUtils.toChatP2PActivity(mPictureBean.getUser_id() + "", NimUIKit.getCommonP2PSessionCustomization(), null);
                }
            } else { //没有搭讪, 进行搭讪
                p.accostUser(String.valueOf(mPictureBean.getUser_id()), CommonUtil.isBtnTextHome(service) ? 1 : 2);
            }
        }
    }

    @Override
    public PicBrowserPresenter initPresenter() {
        return new PicBrowserPresenter();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void getInfoPictureSucess(InfoPictureBean bean) {
        mPictureBean = bean;
        if (mUid == service.getUserId()) { //如果是自己
            mIvAvatar.setEnabled(false);
            mIvAvatar.setVisibility(View.INVISIBLE);
            mTvName.setVisibility(View.GONE);
            mIs_real.setVisibility(View.GONE);
            mLlLike.setVisibility(View.VISIBLE);
        } else {
            mIvAvatar.setEnabled(true);
            GlideUtils.loadRoundImage(this, mIvAvatar, bean.getIcon(), 100);
            mIs_real.setVisibility(bean.getIs_real() == 0 ? View.GONE : View.VISIBLE);
            mLlLike.setVisibility(View.VISIBLE);
            mLlAccost.setVisibility(View.VISIBLE);
            CommonUtil.setUserName(mTvName, bean.getNickname(), bean.is_vip == 1, false);
            mTvName.setTextColor(Utils.getColor(R.color.colorWhite));
        }

        performLike(bean.getIs_praise_picture(), bean.getPraise_picture_count());
        performAccost(bean.getIs_accost());

        List<Fragment> list = new ArrayList<>();
        if (!TextUtils.isEmpty(mFirstIcon)) {
//            list.add(ImageMojitoFragment.Companion.newInstance(new FragmentConfig(mFirstIcon, mFirstIcon, null, 0, true, mIndexPos != 0)));
            list.add(PicLoadFragment.newInstance(0, mFirstIcon));
        }
        for (int i = 0; i < bean.getPicture().size(); i++) {
            String picUri = bean.getPicture().get(i);
            int pos = TextUtils.isEmpty(mFirstIcon) ? i : i + 1;
            //list.add(ImageMojitoFragment.Companion.newInstance(new FragmentConfig(picUri, picUri, null, pos, true, mIndexPos != pos)));
            list.add(PicLoadFragment.newInstance(pos, picUri));
        }
        mViewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), list));
        mViewPager.setOffscreenPageLimit(list.size());
        mTvNum.setText(String.format("1/%d", list.size()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTvNum.setText(String.format("%1$d/%2$d", position + 1, list.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (mIndexPos + 1 > list.size()) {
            mIndexPos = 0;
        }
        mViewPager.setCurrentItem(mIndexPos, false);
    }

    private void performLike(int is_praise_picture, int praise_picture_count) {
        mTv_like.setAnimationDuration(300);
        mTv_like.setText(String.valueOf(praise_picture_count));
        mTv_like.setTextColor(getResources().getColor(is_praise_picture == 1 ? R.color.c_like : R.color.color_light_font));
        mLlLike.setBackground(getResources().getDrawable(is_praise_picture == 1?R.drawable.shape_border_fd4683_radius_100:R.drawable.shape_border_979797_radius_100));
        mIv_like.setLiked(is_praise_picture != 0);
    }

    private void performAccost(int is_accost) {
        if (is_accost == 0) {//未搭讪
            mIv_accost.setLiked(false);
            mTv_accost.setText(CommonUtil.isBtnTextPhoto(service) ? "喜欢" : "搭讪");
        } else {
            setChatBtn();
        }
    }

    /**
     * 设置为私信
     */
    private void setChatBtn() {
        boolean flag = CommonUtil.isBtnTextPhoto(service);

        mIv_accost.setUnlikeDrawableRes(
                flag ? R.mipmap.peony_xxym_sx_icon_small : R.mipmap.peony_home_xcsx_icon
        );

        mIv_accost.setLiked(true);
        mTv_accost.setText("私聊");
    }


    @Override
    public void picLikeSucess(Boolean data) { //点赞成功
        mIv_like.onClick(null);
        mPictureBean.setIs_praise_picture(1);
        mPictureBean.setPraise_picture_count(mPictureBean.getPraise_picture_count() + 1);
        performLike(1, mPictureBean.getPraise_picture_count());
    }

    @Override
    public void accostUserSuccess(AccostDto data) {//搭讪成功
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            mIv_accost.onClick(null);
            performAccost(1);
            mPictureBean.setIs_accost(1);
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_PIC_ACCOST_SUCCESS, mPictureBean.getUser_id()));
            //首页搭讪 2  个人资料页搭讪 3  动态搭讪 4  相册搭讪 5
            CommonUtil.sendAccostGirlBoy(service, mPictureBean.getUser_id(), data, 5);
            if (data != null && data.gift != null) {
                Utils.playAccostAnimationAndSound(data.gift.name, data.gift.animation);
            }
        }
    }

    @Override
    public void getCheckMsgSuccess(String userId, MsgCheckDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            CommonUtil.checkMsg(service.getConfigInfo(), userId, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.finishAfterTransition(mActivity);
    }
}
