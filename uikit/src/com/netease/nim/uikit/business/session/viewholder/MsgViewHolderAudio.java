package com.netease.nim.uikit.business.session.viewholder;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.audio.MessageAudioControl;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.media.audioplayer.Playable;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.VipUtils;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.Map;

import androidx.core.content.ContextCompat;

/**
 * Created by zhoujianghua on 2015/8/5.
 */
public class MsgViewHolderAudio extends MsgViewHolderBase {

    public MsgViewHolderAudio(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    private TextView tvIntegral;
    public static final int CLICK_TO_PLAY_AUDIO_DELAY = 500;

    private TextView durationLabel;
    private FrameLayout containerView;
    private View unreadIndicator;
    private ImageView animationView;
    private LottieAnimationView audioLottie;

    private MessageAudioControl audioControl;

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_audio;
    }

    @Override
    public void inflateContentView() {
        durationLabel = findViewById(R.id.message_item_audio_duration);
        containerView = findViewById(R.id.message_item_audio_container);
        unreadIndicator = findViewById(R.id.message_item_audio_unread_indicator);
        audioLottie = findViewById(R.id.audio_lottie);

        animationView = findViewById(R.id.message_item_audio_playing_animation);
        audioControl = MessageAudioControl.getInstance(context);
        tvIntegral = findViewById(R.id.tv_integral);
        tvRead = findViewById(R.id.tv_read);
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
            if (message.getDirect() == MsgDirectionEnum.In && message.getAttachStatus() != AttachStatusEnum.transferred) {
                if (message.getAttachStatus() == AttachStatusEnum.fail || message.getAttachStatus() == AttachStatusEnum.def) {
                    NIMClient.getService(MsgService.class).downloadAttachment(message, false);
                }
                return;
            }

            if (message.getStatus() != MsgStatusEnum.read) {
                // 将未读标识去掉,更新数据库
                unreadIndicator.setVisibility(View.GONE);
            }

            initPlayAnim(); // 设置语音播放动画

            audioControl.startPlayAudioDelay(CLICK_TO_PLAY_AUDIO_DELAY, message, onPlayListener);

            audioControl.setPlayNext(!NimUIKitImpl.getOptions().disableAutoPlayNextAudio, adapter, message);
        }
    }

    private void layoutByDirection() {
        //拥有积分的时候
        if (isReceivedMessage()) {
            setGravity(audioLottie, Gravity.START | Gravity.CENTER_VERTICAL);
            setGravity(durationLabel, Gravity.END | Gravity.CENTER_VERTICAL);
            tvRead.setVisibility(View.GONE);
            tvIntegral.setVisibility(View.GONE);
            int id = getLeftBg();
            setLottieZip(id, false);
            VipUtils.setPersonalise(containerView, id, false);

            Map<String, Object> extension = message.getRemoteExtension();
            if (extension != null && extension.get("self_attach") != null) {
                setAttach((String) extension.get("self_attach"));
            }
        } else {
            tvIntegral.setVisibility(View.GONE);
            setGravity(audioLottie, Gravity.END | Gravity.CENTER_VERTICAL);
            setGravity(durationLabel, Gravity.START | Gravity.CENTER_VERTICAL);
            unreadIndicator.setVisibility(View.GONE);
            setRead();
            int id = getRightBg();
            setLottieZip(id, true);
            VipUtils.setPersonalise(containerView, id, true);
        }
    }

    private void setLottieZip(int id, boolean isEnd) {
        //Log.e("MsgViewHolderAudio.setLottieZip", "设置语音lottie" + id + " -- " + isEnd);
        StringBuilder sb = new StringBuilder("audio_");
        switch (id) {
            case 8:
            case 9:
            case 11:
            case 16:
            case 17:
            case 29:
            case 103: //103为白色
                sb.append(id);
                break;

            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 2001:
            case 2002:
            case 2003:
            case 2004:
            case 2005:
            case 2006:
            case 2007:
                sb.append(103);
                break;

            default:
                sb.append("0");
                break;
        }
        audioLottie.setAnimation(sb.append(isEnd ? "_end.zip" : "_start.zip").toString());

        animationView.setImageResource(isEnd ? R.drawable.nim_audio_animation_list_right_3 : R.drawable.nim_audio_animation_list_left_white_3);
        animationView.setVisibility(View.VISIBLE);
        audioLottie.setVisibility(View.GONE);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) animationView.getLayoutParams();
        if (isEnd) {
            lp.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        } else {
            lp.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        }
        animationView.setLayoutParams(lp);
    }

    private void setAttach(String attach) {
        ChatMsg chatMsg = ChatMsgUtil.parseAttachMessage(attach);
        if (chatMsg != null && !TextUtils.isEmpty(chatMsg.msg_integral)) {
            tvIntegral.setText(chatMsg.msg_integral);
            if (chatMsg.msg_icon_type == 2) {
                tvIntegral.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(NimUIKit.getContext(), R.drawable.peony_ltk_icon), null, null, null);
            } else {
                tvIntegral.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(NimUIKit.getContext(), R.drawable.nim_ic_chat_money), null, null, null);
            }
            tvIntegral.setVisibility(View.VISIBLE);
        } else {
            tvIntegral.setVisibility(View.GONE);
        }
    }

    private void refreshStatus() {// 消息状态
        AudioAttachment attachment = (AudioAttachment) message.getAttachment();
        MsgStatusEnum status = message.getStatus();
        AttachStatusEnum attachStatus = message.getAttachStatus();

        // alert button
        if (TextUtils.isEmpty(attachment.getPath())) {
            if (attachStatus == AttachStatusEnum.fail || status == MsgStatusEnum.fail) {
                alertButton.setVisibility(View.VISIBLE);
            } else {
                alertButton.setVisibility(View.GONE);
            }
        }

        // progress bar indicator
        if (status == MsgStatusEnum.sending || attachStatus == AttachStatusEnum.transferring) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }

        // unread indicator
        if (!NimUIKitImpl.getOptions().disableAudioPlayedStatusIcon
                && isReceivedMessage()
                && attachStatus == AttachStatusEnum.transferred
                && status != MsgStatusEnum.read) {
            unreadIndicator.setVisibility(View.VISIBLE);
        } else {
            unreadIndicator.setVisibility(View.GONE);
        }
        if(!isReceivedMessage()){
            durationLabel.setTextColor(NimUIKit.getContext().getResources().getColor(R.color.white));
        }
    }

    private void controlPlaying() {
        final AudioAttachment msgAttachment = (AudioAttachment) message.getAttachment();
        long duration = msgAttachment.getDuration();
        setAudioBubbleWidth(duration);

        durationLabel.setTag(message.getUuid());

        if (!isMessagePlaying(audioControl, message)) {
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
        } else if (seconds <= MAX_TIME) {
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
            durationLabel.setText(String.format("%s\"", seconds));
        } else {
            durationLabel.setText("");
        }
    }

    protected boolean isMessagePlaying(MessageAudioControl audioControl, IMMessage message) {
        return audioControl.getPlayingAudio() != null && audioControl.getPlayingAudio().isTheSame(message);
    }

    private final MessageAudioControl.AudioControlListener onPlayListener = new MessageAudioControl.AudioControlListener() {

        @Override
        public void updatePlayingProgress(Playable playable, long curPosition) {
            if (isTheSame(message.getUuid())) {
                return;
            }

            if (curPosition > playable.getDuration()) {
                return;
            }
//            Log.e("MsgViewHolderAudio.updatePlayingProgress", "总时长:" + playable.getDuration() + "   进度: " + ( curPosition));
            updateTime(curPosition);
        }

        @Override
        public void onAudioControllerReady(Playable playable) {
            if (isTheSame(message.getUuid())) {
                return;
            }
            play();
        }

        @Override
        public void onEndPlay(Playable playable) {
            if (isTheSame(message.getUuid())) {
                return;
            }
            updateTime(playable.getDuration());
            stop();
        }


    };

    private void play() {
        audioLottie.setVisibility(View.VISIBLE);
        animationView.setVisibility(View.GONE);
        audioLottie.setProgress(0);
        audioLottie.playAnimation();
    }

    private void stop() {
        animationView.setVisibility(View.VISIBLE);
        audioLottie.setVisibility(View.GONE);
        audioLottie.cancelAnimation();
        audioLottie.setProgress(1);
    }

    private void initPlayAnim() {
        play();
    }


    private boolean isTheSame(String uuid) {
        String current = durationLabel.getTag().toString();
        return TextUtils.isEmpty(uuid) || !uuid.equals(current);
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
