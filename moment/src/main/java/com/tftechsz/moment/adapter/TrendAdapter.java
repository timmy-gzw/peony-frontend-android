package com.tftechsz.moment.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieAnimationViewNew;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.like.LikeButton;
import com.netease.nim.uikit.common.ui.imageview.AvatarVipFrameView;
import com.robinhood.ticker.TickerView;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.CircleBean;
import com.tftechsz.common.entity.UserViewInfo;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nim.ChatSoundPlayer;
import com.tftechsz.common.player.interfaces.OnItemChildVideoClickListener;
import com.tftechsz.common.player.view.PrepareView;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.NumIndicator;
import com.tftechsz.moment.R;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.impl.DefaultPercentProgress;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * 动态适配器
 */
public class TrendAdapter extends BaseQuickAdapter<CircleBean, TrendAdapter.TendHolder> {
    @Autowired
    UserProviderService service;
    private final Activity context;
    private final OnItemChildVideoClickListener mOnItemChildClickListener;

    public TrendAdapter(Activity context, OnItemChildVideoClickListener mOnItemChildClickListener) {
        super(R.layout.dynamic_item_image);
        this.mOnItemChildClickListener = mOnItemChildClickListener;
        this.context = context;
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void convert(@NonNull TendHolder holder, CircleBean item) {
        if (item == null)
            return;
        AvatarVipFrameView ivAvatar = holder.getView(R.id.iv_avatar);
        GlideUtils.loadRoundImageRadius(getContext(), ivAvatar.getImageView(), item.getIcon());
        ivAvatar.setBgFrame(item.picture_frame);

        holder.tvTime.setText(item.getCreated_at());
//        helper.setText(R.id.tv_look_times, item.getProvince());  //浏览次数iv_accost
        holder.ivAccost.setVisibility(item.isAccost() ? View.GONE : View.VISIBLE);
        holder.ivAccost.setImageResource(CommonUtil.isBtnTextTrend(service, item.getSex() == 1) ? R.mipmap.img_0_mainnew2 : R.mipmap.home_ic_chat_up_normal);
        holder.ivChat.setVisibility(item.isAccost() ? View.VISIBLE : View.GONE);
        holder.ivChat.setImageResource(CommonUtil.isBtnTextTrend(service, item.getSex() == 1) ? R.mipmap.peony_xxym_sx_icon_new : R.mipmap.home_ic_chat_up_selector);
        holder.ivAccost.clearAnimation();
        holder.ivChat.clearAnimation();
        holder.getView(R.id.animation_view).clearAnimation();
        holder.getView(R.id.animation_view).setVisibility(View.GONE);
        holder.ivAccost.setVisibility(item.isAccost() ? View.GONE : View.VISIBLE);
        holder.ivChat.setVisibility(item.isAccost() ? View.VISIBLE : View.GONE);
        //设置文本内容
        String content = item.getContent().trim();
        if (!TextUtils.isEmpty(content)) {
            holder.tvContent.setText(content);
            holder.tvContent.setVisibility(View.VISIBLE);
        } else {
            holder.tvContent.setVisibility(View.GONE);
        }
        if (!item.isPraise()) {//为点过赞
            holder.btnLike.setLiked(false);
        } else {
            holder.btnLike.setLiked(true);
        }
        holder.mPosition = holder.getLayoutPosition();
        holder.tvLikeCount.setText(item.getPraises() == 0 ? "点赞" : String.valueOf(item.getPraises()));
        holder.tvDiscussCount.setText(item.getComments() == 0 ? "评论" : String.valueOf(item.getComments()));
        holder.setVisible(R.id.iv_real, item.isReal());  //是否真人
        holder.mPlayerContainer.setTransitionName(Utils.getString(R.string.video_transitions));
//        helper.setGone(R.id.iv_auth, item.getIs_self() != 1);  //是否实名
        //性别 年龄
        CommonUtil.setSexAndAge(getContext(), item.getSex(), item.age, holder.tvSex);

        //if (true) { //vip
        CommonUtil.setUserName(holder.tvName, item.getNickname(), false);
        holder.mImgVip.setVisibility(item.isVip() ? View.VISIBLE : View.GONE);
       /* new ConstraintUtil(holder.constraintLayout)
                .begin()
                .Left_toLeftOf(R.id.iv_sex, R.id.tv_name)
                .Top_toBottomOf(R.id.iv_sex, R.id.tv_name)
                .setMarginTop(R.id.iv_sex, ConvertUtils.dp2px(6))
                .commit();*/
        //}
        /*else {
            CommonUtil.setUserName(holder.tvName, item.getNickname(), false);
            new ConstraintUtil(holder.constraintLayout).begin()
                    .Left_toRightOf(R.id.iv_sex, R.id.tv_name)
                    .Top_toTopOf(R.id.iv_sex, R.id.tv_name)
                    .Bottom_toBottomOf(R.id.iv_sex, R.id.tv_name)
                    .setMarginTop(R.id.iv_sex, 0)
                    .commit();
        }*/

        holder.mPlayerContainer.setVisibility(View.GONE);
        holder.rvTrendImage.setVisibility(View.GONE);
        GridLayoutManager gridLayoutManager;
        if (null != item.getMedia()) {
            if (item.getMedia().size() == 1) {
                if (item.getTypeOfVideo()) {//视频
                    setVideoAdapter(item, holder);
                } else if (item.getTypeOfAudio()) { //音频
                    setAudioAdapter();
                } else if (item.getTypeOfPic()) { //图片
                    gridLayoutManager = new GridLayoutManager(getContext(), 2);
                    holder.rvTrendImage.setLayoutManager(gridLayoutManager);
                    setImgAdapter(holder.rvTrendImage, item, holder.constraintLayout);
                }
            } else {
                gridLayoutManager = new GridLayoutManager(getContext(), 3);
                holder.rvTrendImage.setLayoutManager(gridLayoutManager);
                setImgAdapter(holder.rvTrendImage, item, holder.constraintLayout);
            }

            holder.setVisible(R.id.tv_del, service.getUserId() == item.getUser_id());   //自己时可见
            holder.setVisible(R.id.rl_accost, service.getUserId() != item.getUser_id());   //自己时可见
            if (!TextUtils.isEmpty(item.getCity())) {
                holder.tvAddress.setText(item.getCity());  //城市
                holder.tvAddress.setVisibility(View.VISIBLE);
            } else {
                holder.tvAddress.setVisibility(View.GONE);
            }
        }
    }

    private void setAudioAdapter() {

    }

    private void setVideoAdapter(CircleBean item, TendHolder holder) {
        if (item.getVideo_size() == null || item.getVideo_size().size() < 2) {
            return;
        }
        holder.mPlayerContainer.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams lp = Utils.filterVideoWH(holder.mPlayerContainer.getLayoutParams(),
                (int) (ScreenUtils.getScreenWidth() / Interfaces.VideoW), (int) (ScreenUtils.getScreenHeight() / Interfaces.VideoH), item.getVideo_size().get(0), item.getVideo_size().get(1));
        holder.mPlayerContainer.setLayoutParams(lp);
        Glide.with(holder.mThumb.getContext())
                .load(item.getMedia_mini() != null ? item.getMedia_mini().get(0) : item.getMedia().get(0))
                .placeholder(android.R.color.darker_gray)
                .into(holder.mThumb);
    }

    public void praiseSuccess(int blog_id, int praises) {
        List<CircleBean> data = getData();
        for (int i = 0, j = data.size(); i < j; i++) {
            CircleBean circleBean = data.get(i);
            if (blog_id == circleBean.getBlog_id()) {
                LikeButton likeButton = (LikeButton) getViewByPosition(i, R.id.btn_like);
                if (likeButton != null)
                    likeButton.onClick(null);
                TickerView likeCount = (TickerView) getViewByPosition(i, R.id.tv_like_count);
                if (likeCount != null) {
                    likeCount.setAnimationDuration(praises == 1 ? 0 : Interfaces.TICKERVIEW_ANIMATION_LIKE);
                    likeCount.setText(String.valueOf(praises));
                }
                break;
            }
        }
    }

    public void startAnimation(int position) { //搭讪按钮动画  头像特效   播放声音
        View rl_accost = getViewByPosition(position, R.id.iv_accost);
        View iv_accost1 = getViewByPosition(position, R.id.iv_chat);
        LottieAnimationViewNew lottieAnimationView;
        if (CommonUtil.isBtnTextTrend(service, getData().get(position).getSex() == 1)) {
            lottieAnimationView = (LottieAnimationViewNew) getViewByPosition(position, R.id.animation_view_btn);
        } else {
            lottieAnimationView = (LottieAnimationViewNew) getViewByPosition(position, R.id.animation_view);
        }
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

    public void startAnimation(View rl_accost, View iv_accost1, LottieAnimationView lottieAnimationView) { //用下面都不能播放声音了！搭讪按钮动画  头像特效   播放声音
        if (rl_accost != null && iv_accost1 != null) {
            Utils.playAccostAnimation(getContext(), rl_accost, iv_accost1);
        }
        if (lottieAnimationView != null) {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
            lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (lottieAnimationView != null) {
                        lottieAnimationView.setVisibility(View.GONE);
                        lottieAnimationView.clearAnimation();
                    }

                }
            });
        }
    }

    public void notifyItemChangeSinge(int position) {
        getData().get(position).setIs_accost(1);
        getData().get(position).transitionAnima = true;
        notifyItemChanged(position);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setImgAdapter(RecyclerView rvTrendImage, CircleBean item, ConstraintLayout constraintLayout) {
        rvTrendImage.setVisibility(View.VISIBLE);
        if (item.getMedia().size() > 0) {
            ArrayList<UserViewInfo> userViewInfos = new ArrayList<>();
            List<String> urls = new ArrayList<>(item.getMedia());
            if (item.getMedia_mini() != null) {
                for (String url : item.getMedia_mini()) {
                    userViewInfos.add(new UserViewInfo(url));
                }
            } else {
                for (String url : item.getMedia()) {
                    userViewInfos.add(new UserViewInfo(url));
                }
            }
            TrendImageAdapter adapter = new TrendImageAdapter(userViewInfos);
            rvTrendImage.setAdapter(adapter);
            rvTrendImage.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    constraintLayout.performClick();
                }
                return false;
            });
            adapter.setOnItemClickListener((adapter1, view, position) -> {
                if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
                Mojito.with(context)
                        .urls(urls)
                        .position(position, 0, urls.size())
                        .views(rvTrendImage, urls.size() == 1 ? R.id.iv1_5 : R.id.iv1)
                        .autoLoadTarget(false)
                        .setIndicator(new NumIndicator(context))
                        .setProgressLoader(DefaultPercentProgress::new)
                        .start();
            });
        } else {
            TrendImageAdapter adapter = new TrendImageAdapter(null);
            rvTrendImage.setAdapter(adapter);
        }
    }

    public void updateData(int clickPosition, CircleBean data) {
        ImageView icon = (ImageView) getViewByPosition(clickPosition, R.id.iv_photo);
        if (!data.getIcon().equals(getData().get(clickPosition).getIcon())) {
            GlideUtils.loadRouteImage(getContext(), icon, data.getIcon());
        }

        TextView tvName = (TextView) getViewByPosition(clickPosition, R.id.tv_name);
        if (tvName != null)
            tvName.setText(data.getNickname());

        TextView tvTime = (TextView) getViewByPosition(clickPosition, R.id.tv_time);
        if (tvTime != null)
            tvTime.setText(data.getCreated_at());

        TickerView tvLike = (TickerView) getViewByPosition(clickPosition, R.id.tv_like_count);
        if (tvLike != null)
            tvLike.setText(data.getPraises() == 0 ? "点赞" : String.valueOf(data.getPraises()));

        TickerView tvLikeCount = (TickerView) getViewByPosition(clickPosition, R.id.tv_discuss_count);
        if (tvLikeCount != null)
            tvLikeCount.setText(data.getComments() == 0 ? "评论" : String.valueOf(data.getComments()));
    }

    public class TendHolder extends BaseViewHolder {
        public int mPosition;
        public ConstraintLayout constraintLayout;
        public ImageView mThumb, ivAccost, ivChat /*,videoBg*/;
        public TextView tvName, tvTime, tvSex, tvContent, tvAddress;
        public TickerView tvLikeCount, tvDiscussCount;
        public PrepareView mPrepareView;
        public FrameLayout mPlayerContainer;
        public LikeButton btnLike;//点赞按钮
        public RecyclerView rvTrendImage;
        private ImageView mImgVip;

        public TendHolder(View view) {
            super(view);
            mImgVip = view.findViewById(R.id.img_vip_dii);
            constraintLayout = view.findViewById(R.id.dynamic_item_image_root_view);
            mPrepareView = itemView.findViewById(R.id.prepare_view);
            mThumb = mPrepareView.findViewById(R.id.thumb);
            //videoBg = view.findViewById(R.id.video_bg);
            tvName = view.findViewById(R.id.tv_name);
            tvTime = view.findViewById(R.id.tv_time);
            tvSex = view.findViewById(R.id.iv_sex);
            mPlayerContainer = view.findViewById(R.id.player_container);
            btnLike = view.findViewById(R.id.btn_like);
            ivAccost = view.findViewById(R.id.iv_accost);
            ivChat = view.findViewById(R.id.iv_chat);//私信
            tvLikeCount = view.findViewById(R.id.tv_like_count); //点赞数
            tvDiscussCount = view.findViewById(R.id.tv_discuss_count);//评论数
            tvContent = view.findViewById(R.id.tv_content);
            tvAddress = view.findViewById(R.id.tv_address);
            rvTrendImage = view.findViewById(R.id.rv_trend_image);
            mPlayerContainer.setOnClickListener(v -> mOnItemChildClickListener.onItemChildClick(mPosition, mPlayerContainer));
            //通过tag将ViewHolder和itemView绑定
            view.setTag(this);
        }
    }
}
