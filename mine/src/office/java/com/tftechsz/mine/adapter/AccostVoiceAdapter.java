package com.tftechsz.mine.adapter;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.AccostSettingListBean;

import java.util.List;

public class AccostVoiceAdapter extends BaseQuickAdapter<AccostSettingListBean, BaseViewHolder> {

    public int mPlayPosition = -1;

    public AccostVoiceAdapter(@Nullable List<AccostSettingListBean> data) {
        super(R.layout.item_accost_voice, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, AccostSettingListBean item) {
        ImageView ivVoice = helper.getView(R.id.iv_voice);
        View llVoice = helper.getView(R.id.ll_voice);
        helper.setText(R.id.content, item.title);
        helper.setVisible(R.id.tv_under_review, item.is_show == 0);
        helper.setText(R.id.tv_voice_time, item.time + "\"");
        ivVoice.setImageResource(R.drawable.nim_audio_animation_list_left_3);
        ivVoice.setColorFilter(Color.WHITE);
        int currentBubbleWidth = calculateBubbleWidth(item.time);
        ViewGroup.LayoutParams layoutParams = llVoice.getLayoutParams();
        layoutParams.width = currentBubbleWidth;
        llVoice.setLayoutParams(layoutParams);
        helper.setVisible(R.id.bot_line, getItemPosition(item) != getData().size() - 1);

    }


    public static int getAudioMaxEdge() {
        return (int) (0.5 * ScreenUtil.screenMin);
    }

    public static int getAudioMinEdge() {
        return (int) (0.18 * ScreenUtil.screenMin);
    }

    private int calculateBubbleWidth(long seconds) {
        int maxAudioBubbleWidth = getAudioMaxEdge();
        int minAudioBubbleWidth = getAudioMinEdge();
        int currentBubbleWidth;
        if (seconds <= 0) {
            currentBubbleWidth = minAudioBubbleWidth;
        } else if (seconds > 0 && seconds <= 8) {
            currentBubbleWidth = (int) ((maxAudioBubbleWidth - minAudioBubbleWidth)
                    * Math.atan(seconds / 10.0) + minAudioBubbleWidth);
        } else {
            currentBubbleWidth = maxAudioBubbleWidth;
        }
        if (currentBubbleWidth < minAudioBubbleWidth) {
            currentBubbleWidth = minAudioBubbleWidth;
        } else if (currentBubbleWidth > maxAudioBubbleWidth) {
            currentBubbleWidth = maxAudioBubbleWidth;
        }
        return currentBubbleWidth;
    }

    public void play(ImageView animationView) {
        animationView.setImageResource(R.drawable.nim_audio_animation_list_left);
        animationView.setColorFilter(Color.WHITE);
        if (animationView.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) animationView.getBackground();
            animation.start();
        }
    }

    private void stop(ImageView animationView) {
        if (animationView.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) animationView.getBackground();
            animation.stop();
            animationView.setImageResource(R.drawable.nim_audio_animation_list_left_3);
            animationView.setColorFilter(Color.WHITE);
        }
    }

    public void startCurrentPosition(int position) {
        ImageView stopIvVoice = (ImageView) getViewByPosition(mPlayPosition, R.id.iv_voice);
        if (stopIvVoice != null) {
            stopIvVoice.setColorFilter(Color.WHITE);
            stop(stopIvVoice);
        }
        mPlayPosition = position;
        ImageView PlayIvVoice = (ImageView) getViewByPosition(mPlayPosition, R.id.iv_voice);
        if (PlayIvVoice != null) {
            PlayIvVoice.setColorFilter(Color.WHITE);
            play(PlayIvVoice);
        }
    }

    public void stopCurrentPosition() {
        ImageView PlayIvVoice = (ImageView) getViewByPosition(mPlayPosition, R.id.iv_voice);
        if (PlayIvVoice != null) {
            PlayIvVoice.setColorFilter(Color.WHITE);
            stop(PlayIvVoice);
        }
    }


}
