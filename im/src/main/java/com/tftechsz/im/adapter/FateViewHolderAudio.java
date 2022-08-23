package com.tftechsz.im.adapter;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.business.session.audio.MessageAudioControl;
import com.netease.nim.uikit.common.media.audioplayer.Playable;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.tftechsz.im.model.FateContentInfo;
import com.tftechsz.im.model.FateInfo;
import com.tftechsz.im.utils.FateAudioControl;

public class FateViewHolderAudio extends FateViewHolderBase {

    public FateViewHolderAudio(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    public static final int CLICK_TO_PLAY_AUDIO_DELAY = 500;

    private TextView durationLabel;
    private View containerView;
    private View unreadIndicator;
    private ImageView animationView;

    private FateAudioControl audioControl;

    @Override
    public int getContentResId() {
        return com.netease.nim.uikit.R.layout.nim_message_item_audio;
    }

    @Override
    public void inflateContentView() {
        durationLabel = findViewById(com.netease.nim.uikit.R.id.message_item_audio_duration);
        containerView = findViewById(com.netease.nim.uikit.R.id.message_item_audio_container);
        unreadIndicator = findViewById(com.netease.nim.uikit.R.id.message_item_audio_unread_indicator);
        unreadIndicator.setVisibility(View.GONE);
        animationView = findViewById(com.netease.nim.uikit.R.id.message_item_audio_playing_animation);
        animationView.setBackgroundResource(0);
        audioControl = FateAudioControl.getInstance(context);
    }

    @Override
    public void bindContentView() {
        layoutByDirection();

        refreshStatus();

        controlPlaying();
    }

    @Override
    public void onItemClick() {
        if (audioControl != null) {

            initPlayAnim(); // 设置语音播放动画

            audioControl.startPlayAudioDelay(CLICK_TO_PLAY_AUDIO_DELAY, mFateInfo, onPlayListener);

            audioControl.setPlayNext(!NimUIKitImpl.getOptions().disableAutoPlayNextAudio, adapter, mFateInfo);
        }
    }

    private void layoutByDirection() {
//        if (isReceivedMessage()) {
//            setGravity(animationView, Gravity.LEFT | Gravity.CENTER_VERTICAL);
//            setGravity(durationLabel, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//
//            containerView.setBackgroundResource(NimUIKitImpl.getOptions().messageLeftBackground);
//            containerView.setPadding(ScreenUtil.dip2px(12), ScreenUtil.dip2px(12), ScreenUtil.dip2px(12), ScreenUtil.dip2px(12));
//            animationView.setBackgroundResource(com.netease.nim.uikit.R.drawable.nim_audio_animation_list_left);
//            durationLabel.setTextColor(Color.BLACK);
//
//        } else {
            setGravity(animationView, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            setGravity(durationLabel, Gravity.LEFT | Gravity.CENTER_VERTICAL);
//            unreadIndicator.setVisibility(View.GONE);

            containerView.setBackgroundResource(NimUIKitImpl.getOptions().messageRightBackground);
            containerView.setPadding(ScreenUtil.dip2px(12), ScreenUtil.dip2px(12), ScreenUtil.dip2px(12), ScreenUtil.dip2px(12));
            animationView.setBackgroundResource(com.netease.nim.uikit.R.drawable.nim_audio_animation_list_right);
            durationLabel.setTextColor(Color.BLACK);
//        }
    }

    private void refreshStatus() {// 消息状态
    }

    private void controlPlaying() {
        final FateContentInfo msgAttachment = (FateContentInfo) mFateInfo.getMsg_content();
        long duration = msgAttachment.getDur();
        setAudioBubbleWidth(duration);

        durationLabel.setTag(mFateInfo.getCreated_at());

        if (!isMessagePlaying(audioControl, mFateInfo)) {
            if (audioControl.getAudioControlListener() != null &&
                    audioControl.getAudioControlListener().equals(onPlayListener)) {
                audioControl.changeAudioControlListener(null);
            }

            updateTime(duration);
            stop();
        } else {
            audioControl.changeAudioControlListener(onPlayListener);
            play();
        }
    }

    public static int getAudioMaxEdge() {
        return (int) (0.6 * ScreenUtil.screenMin);
    }

    public static int getAudioMinEdge() {
        return (int) (0.1875 * ScreenUtil.screenMin);
    }

    private void setAudioBubbleWidth(long milliseconds) {
        long seconds = TimeUtil.getSecondsByMilliseconds(milliseconds);

        int currentBubbleWidth = calculateBubbleWidth(seconds, NimUIKitImpl.getOptions().audioRecordMaxTime);
        ViewGroup.LayoutParams layoutParams = containerView.getLayoutParams();
        layoutParams.width = currentBubbleWidth;
        containerView.setLayoutParams(layoutParams);
    }

    private int calculateBubbleWidth(long seconds, int MAX_TIME) {
        int maxAudioBubbleWidth = getAudioMaxEdge();
        int minAudioBubbleWidth = getAudioMinEdge();

        int currentBubbleWidth;
        if (seconds <= 0) {
            currentBubbleWidth = minAudioBubbleWidth;
        } else if (seconds > 0 && seconds <= MAX_TIME) {
            currentBubbleWidth = (int) ((maxAudioBubbleWidth - minAudioBubbleWidth) * (2.0 / Math.PI)
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

    private void updateTime(long milliseconds) {
        long seconds = TimeUtil.getSecondsByMilliseconds(milliseconds);

        if (seconds >= 0) {
            durationLabel.setText(seconds + "\"");
        } else {
            durationLabel.setText("");
        }
    }

    protected boolean isMessagePlaying(FateAudioControl audioControl, FateInfo message) {
        if (audioControl.getPlayingAudio() != null && audioControl.getPlayingAudio().getCreated_at()== message.getCreated_at()) {
            return true;
        } else {
            return false;
        }
    }

    private MessageAudioControl.AudioControlListener onPlayListener = new MessageAudioControl.AudioControlListener() {

        @Override
        public void updatePlayingProgress(Playable playable, long curPosition) {
            if (!isTheSame(mFateInfo.getCreated_at())) {
                return;
            }

            if (curPosition > playable.getDuration()) {
                return;
            }
            updateTime(curPosition);
        }

        @Override
        public void onAudioControllerReady(Playable playable) {
            if (!isTheSame(mFateInfo.getCreated_at())) {
                return;
            }

            play();
        }

        @Override
        public void onEndPlay(Playable playable) {
            if (!isTheSame(mFateInfo.getCreated_at())) {
                return;
            }

            updateTime(playable.getDuration());

            stop();
        }


    };

    private void play() {
        if (animationView.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) animationView.getBackground();
            animation.start();
        }
    }

    private void stop() {
        if (animationView.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) animationView.getBackground();
            animation.stop();
            endPlayAnim();
        }
    }

    private void initPlayAnim() {
        if (isReceivedMessage()) {
            animationView.setBackgroundResource(com.netease.nim.uikit.R.drawable.nim_audio_animation_list_left);
        } else {
            animationView.setBackgroundResource(com.netease.nim.uikit.R.drawable.nim_audio_animation_list_right);
        }
    }

    private void endPlayAnim() {
        if (isReceivedMessage()) {
            animationView.setBackgroundResource(com.netease.nim.uikit.R.drawable.nim_audio_animation_list_left_white_3);
        } else {
            animationView.setBackgroundResource(com.netease.nim.uikit.R.drawable.nim_audio_animation_list_right_3);
        }
    }

    private boolean isTheSame(long uuid) {
        String current = durationLabel.getTag().toString();
        return !TextUtils.isEmpty(current) && current.equals(String.valueOf(uuid));
    }

    @Override
    protected int leftBackground() {
//        return R.drawable.nim_message_left_white_bg;
        return 0;
    }

    @Override
    protected int rightBackground() {
//        return R.drawable.nim_message_right_blue_bg;
        return 0;
    }
}
