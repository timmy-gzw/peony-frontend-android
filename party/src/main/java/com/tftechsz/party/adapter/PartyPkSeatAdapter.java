package com.tftechsz.party.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.util.VipUtils;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.CircleImageView;
import com.tftechsz.party.R;

import org.jetbrains.annotations.NotNull;

public class PartyPkSeatAdapter extends BaseQuickAdapter<VoiceRoomSeat, BaseViewHolder> {
    private int mLoveSwitch;
    private int seatTeamType;  //1 红队 2：蓝队
    private SVGAParser svgaParser;


    public PartyPkSeatAdapter() {
        super(R.layout.item_pk_party_seat);
    }


    public void setTeamType(int seatTeamType) {
        this.seatTeamType = seatTeamType;
    }


    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, VoiceRoomSeat item) {
        CircleImageView ivPic = baseViewHolder.findView(R.id.iv_user);
        SVGAImageView svgaImageView = baseViewHolder.findView(R.id.svga_head);
        ImageView iv_frame = baseViewHolder.findView(R.id.iv_frame);
        ImageView circle = baseViewHolder.findView(R.id.circle);
        ImageView circle1 = baseViewHolder.findView(R.id.circle1);
        if (!TextUtils.isEmpty(item.getIcon())) {
            if (item.getIcon().startsWith("http"))
                GlideUtils.loadImage(getContext(), ivPic, item.getIcon(), R.drawable.ic_default_avatar);
            else {
                GlideUtils.loadImage(getContext(), ivPic, CommonUtil.getHttpUrlHead() + item.getIcon(), R.drawable.party_ic_seat);
            }
        } else {
            if (item.getLock() == 1) {
                GlideUtils.loadImage(getContext(), ivPic, R.drawable.party_ic_lock_seat);
            } else {
                baseViewHolder.setImageResource(R.id.iv_user, R.drawable.party_ic_seat);
            }
            if (circle != null)
                circle.clearAnimation();
            if (circle1 != null)
                circle1.clearAnimation();
        }
        baseViewHolder.setText(R.id.tv_position, item.getIndex() + "号");
        baseViewHolder.setText(R.id.tv_chat_love, TextUtils.isEmpty(item.getCost()) ? "0" : item.getCost());
        VoiceRoomSeat seat = getItem(baseViewHolder.getLayoutPosition());
        baseViewHolder.setVisible(R.id.circle, false);
        baseViewHolder.setVisible(R.id.iv_mvp, item.getIs_mvp() == 1);
        //判断是否显示爱意值
        if (mLoveSwitch == 0) {
            baseViewHolder.setVisible(R.id.tv_chat_love, item.getUser_id() != 0);
        } else {
            baseViewHolder.setVisible(R.id.tv_chat_love, false);
        }
        if (seatTeamType == 1) {
            baseViewHolder.setBackgroundResource(R.id.circle, R.drawable.party_ic_red_round);
            baseViewHolder.setBackgroundResource(R.id.circle1, R.drawable.party_ic_red_round);
        } else {
            baseViewHolder.setBackgroundResource(R.id.circle, R.drawable.party_ic_blue_round);
            baseViewHolder.setBackgroundResource(R.id.circle1, R.drawable.party_ic_blue_round);
        }
        baseViewHolder.setVisible(R.id.iv_mute_voice, item.getSilence_switch() == 2);
        if (item.getUser_id() != 0) {
            if (svgaImageView != null && item.getAvatar_id() != 0) {
                svgaImageView.setVisibility(View.VISIBLE);
                playAirdrop(iv_frame, svgaImageView, item.getAvatar_id());
            }
        } else {
            if (iv_frame != null) {
                iv_frame.setBackground(null);
            }
            if (svgaImageView != null) {
                svgaImageView.setVisibility(View.INVISIBLE);
                svgaImageView.clearAnimation();
                svgaImageView.clear();
            }
        }
        if (seat == null || seat.getUser_id() == 0) {
            return;
        }
        baseViewHolder.setText(R.id.tv_position, item.getNickname());
        baseViewHolder.setVisible(R.id.circle, true);
    }


    /**
     * 播放svga动画
     */
    public void playAirdrop(ImageView iv_frame, SVGAImageView svgaAirDrop, int avatar_id) {
        if (svgaParser == null)
            svgaParser = new SVGAParser(BaseApplication.getInstance());
        String name = String.format("avatar/party_avatar_%s.svga", avatar_id);
        svgaParser.decodeFromAssets(name, new SVGAParser.ParseCompletion() {

            @Override
            public void onError() {
                VipUtils.setPersonalise(iv_frame, avatar_id, false, true);
            }

            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                if (svgaAirDrop != null) {
                    SVGADrawable drawable = new SVGADrawable(videoItem);
                    svgaAirDrop.setImageDrawable(drawable);
                    svgaAirDrop.startAnimation();
                }
            }
        }, null);
    }

    public void setLoveSwitch(int loveSwitch) {
        this.mLoveSwitch = loveSwitch;
        notifyDataSetChanged();
    }


    public void setLoveCost(String cost, int userId, int isMvp) {
        for (int i = 0; i < getData().size(); i++) {
            TextView tvLove = (TextView) getViewByPosition(i, R.id.tv_chat_love);
            ImageView ivMvp = (ImageView) getViewByPosition(i, R.id.iv_mvp);
            if (tvLove != null && getData().get(i).getUser_id() == userId)
                tvLove.setText(TextUtils.isEmpty(cost) ? "0" : cost);
            if (ivMvp != null && getData().get(i).getUser_id() == userId)
                ivMvp.setVisibility(isMvp == 1 ? View.VISIBLE : View.GONE);
        }
    }

    public void setLoveCost(int userId, int isMvp) {
        for (int i = 0; i < getData().size(); i++) {
            ImageView ivMvp = (ImageView) getViewByPosition(i, R.id.iv_mvp);
            if (ivMvp != null && getData().get(i).getUser_id() == userId)
                ivMvp.setVisibility(isMvp == 1 ? View.VISIBLE : View.GONE);
        }
    }


    public final void updateItem(int position, VoiceRoomSeat model) {
        if (model == null || position < 0 || position >= getData().size()) {
            return;
        }
        getData().set(position, model);
        notifyItemChanged(position);
    }


}
