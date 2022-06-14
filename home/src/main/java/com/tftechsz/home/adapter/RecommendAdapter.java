package com.tftechsz.home.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieAnimationViewNew;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.NetworkUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.util.VipUtils;
import com.tftechsz.home.R;
import com.tftechsz.common.Constants;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nim.ChatSoundPlayer;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.StringUtils;
import com.tftechsz.common.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 推荐适配器
 */
public class RecommendAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {
    private int mType;
    private final UserProviderService service;
    private int isShow;


    public RecommendAdapter(int type) {
        super(R.layout.item_recommend);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mType = type;
    }


    public void setShow(int isShow) {
        this.isShow = isShow;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, UserInfo item, @NotNull List<?> payloads) {
        /*if (payloads.isEmpty()) {
            convert(holder, item);
        } else*/
        setData(holder, item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserInfo item) {
        setData(helper, item);
    }

    public void setData(BaseViewHolder helper, UserInfo item) {

        TextView tvName = helper.getView(R.id.tv_name);
        TextView tvOnline = helper.getView(R.id.tv_online);
        ImageView ivAvatar = helper.getView(R.id.iv_avatar);
        View bgFrame = helper.getView(R.id.bg_frame);

        LottieAnimationView lottieAnimationViewBtn = helper.getView(R.id.animation_view_btn);
        lottieAnimationViewBtn.setVisibility(View.GONE);
        lottieAnimationViewBtn.clearAnimation();

        helper.setVisible(R.id.iv_accost, item.getIs_accost() != 1);
        if (CommonUtil.isBtnTextHome(service)) {  //新的逻辑喜欢配置
            if (!item.isAccost()) {
                helper.setVisible(R.id.iv_accost, false);
                lottieAnimationViewBtn.setVisibility(View.VISIBLE);
                lottieAnimationViewBtn.setProgress(0);
            }
            //helper.setBackgroundResource(R.id.iv_accost, R.mipmap.img_0_mainnew2);
            helper.setBackgroundResource(R.id.iv_accost1, R.mipmap.peony_xxym_sx_icon_new);
            helper.setVisible(R.id.tv_distance, false);
        } else {
            helper.setImageResource(R.id.iv_accost, R.mipmap.home_icon_chat_self);
            helper.setBackgroundResource(R.id.iv_accost1, R.mipmap.home_ic_chat_up_selector);
        }
        if (mType == 1) {   //推荐
            tvOnline.setText(item.getOnline_message());
            helper.setVisible(R.id.tv_online, !TextUtils.isEmpty(item.getOnline_message()) && item.getIs_online() == 0);
            helper.setVisible(R.id.iv_online, item.getIs_online() == 1); //是否在线  1:在线
            helper.setVisible(R.id.tv_distance, false);
        } else {   //附近
            helper.setVisible(R.id.iv_online, false);
            helper.setVisible(R.id.tv_online, false);
            helper.setVisible(R.id.tv_distance, !TextUtils.isEmpty(item.tag_distance));
            helper.setText(R.id.tv_distance, item.tag_distance);
        }
        RecyclerView recycleView = helper.getView(R.id.recycleview);
        if (item.getPicture() != null && item.getPicture().size() > 0) { //有相册
            recycleView.setVisibility(View.VISIBLE);
            recycleView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            HomePictureAdapter adpater = new HomePictureAdapter(getContext());
            recycleView.setAdapter(adpater);
            adpater.setList(item.getPicture());
            adpater.setOnItemClickListener((adapter, view, position) -> {//点击图册
                if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
                if (!NetworkUtils.isConnected()) {
                    Utils.toast("暂无网络，请连接网络后重试！");
                    return;
                }
                ARouterUtils.toUserPicBrowserActivity(item.getUser_id(), position, null, item.isBoy());
            });
        } else {
            recycleView.setVisibility(View.GONE);
        }
        String name = StringUtils.handleText(item.getNickname(), Constants.MAX_NAME_LENGTH);
        //bgFrame.setBackgroundResource(VipUtils.getPictureFrameBackground(item.picture_frame, true, false));
        VipUtils.setPersonalise(bgFrame, item.picture_frame, true, true);
        CommonUtil.setUserName(tvName, name, item.isVip());
        helper.setText(R.id.tv_info, item.tags);
        helper.setText(R.id.tv_sign, item.getDesc());

        GlideUtils.loadRoundImage(getContext(), ivAvatar, item.getIcon());
//        GlideUtils.loadUserIcon(ivAvatar, item.getIcon());
        helper.setGone(R.id.iv_real_people, item.getIs_real() != 1);  //是否真人
//        helper.setGone(R.id.iv_auth, item.getIs_self() != 1);  //是否实名
        //helper.setVisible(R.id.animation_view, false);
        helper.setVisible(R.id.iv_accost1, item.getIs_accost() == 1);
        helper.setVisible(R.id.ll_accost, item.getIs_accost() != 1);

        helper.getView(R.id.ll_accost).clearAnimation();
        helper.getView(R.id.iv_accost1).clearAnimation();
        helper.getView(R.id.animation_view).clearAnimation();
        helper.getView(R.id.animation_view).setVisibility(View.GONE);
        if (isShow == 1) {   //只显示私信   图片显示认证图片
            helper.setImageResource(R.id.iv_real_people, R.mipmap.ic_attestation);
            helper.setVisible(R.id.iv_accost1, true);
            helper.setVisible(R.id.ll_accost, false);
        } else {
            helper.setImageResource(R.id.iv_real_people, R.mipmap.home_icon_real_people);
            helper.setVisible(R.id.iv_accost1, item.getIs_accost() == 1);
            helper.setVisible(R.id.ll_accost, item.getIs_accost() != 1);
        }

    }

    public void startAnimation(View rl_accost, View iv_accost1, LottieAnimationViewNew lottieAnimationView) { //搭讪按钮动画  头像特效   播放声音
        if (lottieAnimationView != null) {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.flagLAV = true;
            lottieAnimationView.playAnimation();
            lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (lottieAnimationView != null) {
                        lottieAnimationView.setVisibility(View.GONE);
                        if (rl_accost != null && iv_accost1 != null) {
                            Utils.playAccostAnimation(getContext(), rl_accost, iv_accost1);
                            lottieAnimationView.flagLAV = false;
                        }
                    }

                }
            });
        }
    }


    public void startAnimationNew(int position) {
        getData().get(position).setIs_accost(1);
        LottieAnimationView lottieAnimationView;
        if (CommonUtil.isBtnTextHome(service)) {
            lottieAnimationView = (LottieAnimationView) getViewByPosition(position, R.id.animation_view_btn);
        } else{
            lottieAnimationView = (LottieAnimationView) getViewByPosition(position, R.id.animation_view);
        }
        View rl_accost = getViewByPosition(position, R.id.ll_accost);
        View iv_accost1 = getViewByPosition(position, R.id.iv_accost1);
        View ivAccost = getViewByPosition(position, R.id.iv_accost);
        if (lottieAnimationView != null) {
            ChatSoundPlayer.instance().play(ChatSoundPlayer.RingerTypeEnum.ACCOST);
            lottieAnimationView.setVisibility(View.VISIBLE);
            if (ivAccost != null)
                ivAccost.setVisibility(View.INVISIBLE);
            lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lottieAnimationView.setVisibility(View.GONE);
                    if (rl_accost != null) {
                        rl_accost.setVisibility(View.GONE);
                    }
                    if (iv_accost1 != null) {
                        iv_accost1.setVisibility(View.VISIBLE);
                        ObjectAnimator oa3 = ObjectAnimator.ofFloat(iv_accost1, "scaleX", 0.0f, 1.0f);
                        ObjectAnimator oa4 = ObjectAnimator.ofFloat(iv_accost1, "scaleY", 0.0f, 1.0f);
                        AnimatorSet as2 = new AnimatorSet();
                        as2.play(oa3).with(oa4);
                        as2.setDuration(300);
                        as2.start();
                    }
                }
            });
            lottieAnimationView.playAnimation();
        }
    }


    public void notifyItemChangeSinge(int position) {
        getData().get(position).setIs_accost(1);
        getData().get(position).transitionAnima = true;
        notifyItemRangeChanged(position, 1, 1);
    }
}
