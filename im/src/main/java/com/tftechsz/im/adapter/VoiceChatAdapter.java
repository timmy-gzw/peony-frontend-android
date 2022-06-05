package com.tftechsz.im.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.util.VipUtils;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tftechsz.im.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.CircleImageView;

import org.jetbrains.annotations.NotNull;

public class VoiceChatAdapter extends BaseQuickAdapter<VoiceRoomSeat, BaseViewHolder> {

    private SVGAParser svgaParser;

    public VoiceChatAdapter() {
        super(R.layout.item_voice_chat);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, VoiceRoomSeat item) {
        SVGAImageView svgaImageView = baseViewHolder.findView(R.id.svga_head);
        ImageView iv_frame = baseViewHolder.findView(R.id.iv_frame);
        CircleImageView ivPic = baseViewHolder.findView(R.id.iv_user);
        if (!TextUtils.isEmpty(item.getIcon())) {
            GlideUtils.loadImage(getContext(), ivPic, CommonUtil.getHttpUrlHead() + item.getIcon(), R.mipmap.chat_ic_voice_chat_add);
        } else {
            baseViewHolder.setImageResource(R.id.iv_user, R.mipmap.chat_ic_voice_chat_add);
        }
        baseViewHolder.setText(R.id.tv_position, item.getIndex() + "号");
        baseViewHolder.setText(R.id.tv_chat_love, TextUtils.isEmpty(item.getCost()) ? "0" : item.getCost());
        VoiceRoomSeat seat = getItem(baseViewHolder.getLayoutPosition());
        baseViewHolder.setText(R.id.tv_user_position, item.getIndex() + "");
        baseViewHolder.setVisible(R.id.tv_user_position, false)
                .setVisible(R.id.circle, false)
                .setVisible(R.id.circle1, false);

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
        if (seat == null || seat.getStatus() == 0) {
            return;
        }
        int status = seat.getStatus();
        switch (status) {
            case VoiceRoomSeat.Status.INIT:   // 麦位初始化状态（没人）
                break;
            case VoiceRoomSeat.Status.APPLY:  // 正在申请（没人）
                break;

            case VoiceRoomSeat.Status.ON:  //麦位上有人，且能正常发言（有人）
                break;
            case VoiceRoomSeat.Status.CLOSED:  //麦位关闭（没人）
                break;
            case VoiceRoomSeat.Status.FORBID:  //麦位上没人，但是被主播屏蔽（没人）
                break;
            case VoiceRoomSeat.Status.AUDIO_MUTED: //麦位上有人，但是语音被屏蔽（有人）
            case VoiceRoomSeat.Status.AUDIO_CLOSED_AND_MUTED:  //麦位上有人，但是他关闭了自己的语音且主播屏蔽了他
                break;
            case VoiceRoomSeat.Status.AUDIO_CLOSED:  // 麦位上有人，但是他关闭了自己的语音（有人）(没有被屏蔽)
                break;
        }
        baseViewHolder.setText(R.id.tv_position, item.getNickname());
        baseViewHolder.setVisible(R.id.tv_user_position, true)
                .setVisible(R.id.circle, true)
                .setVisible(R.id.circle1, false);
    }

    public final void updateItem(int position, VoiceRoomSeat model) {
        if (model == null || position < 0 || position >= getData().size()) {
            return;
        }
        getData().set(position, model);
        notifyItemChanged(position);
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
}
