package com.netease.nim.uikit.business.session.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.imageview.MsgThumbImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.media.BitmapDecoder;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;

import java.io.File;
import java.util.Map;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public abstract class MsgViewHolderThumbBase extends MsgViewHolderBase {
    private TextView tvIntegral;
    public MsgViewHolderThumbBase(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    protected MsgThumbImageView thumbnail;
    protected View progressCover;
    protected TextView progressLabel;

    @Override
    public void inflateContentView() {
        thumbnail = findViewById(R.id.message_item_thumb_thumbnail);
        progressBar = findViewById(R.id.message_item_thumb_progress_bar); // 覆盖掉
        progressCover = findViewById(R.id.message_item_thumb_progress_cover);
        progressLabel = findViewById(R.id.message_item_thumb_progress_text);
        tvIntegral = findViewById(R.id.tv_integral);
        tvRead = findViewById(R.id.tv_read);
    }

    @Override
    public void bindContentView() {
        FileAttachment msgAttachment = (FileAttachment) message.getAttachment();
        String path = msgAttachment.getPath();
        String thumbPath = msgAttachment.getThumbPath();
        if (!TextUtils.isEmpty(thumbPath)) {
            loadThumbnailImage(thumbPath, false, msgAttachment.getExtension());
        } else if (!TextUtils.isEmpty(path)) {
            loadThumbnailImage(thumbFromSourceFile(path), true, msgAttachment.getExtension());
        } else {
            loadThumbnailImage(null, false, msgAttachment.getExtension());
            if (message.getAttachStatus() == AttachStatusEnum.transferred
                    || message.getAttachStatus() == AttachStatusEnum.def) {
                downloadAttachment(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        loadThumbnailImage(msgAttachment.getThumbPath(), false, msgAttachment.getExtension());
                        refreshStatus();
                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
            }
        }
        refreshStatus();
        if (isReceivedMessage()) {
            tvRead.setVisibility(View.GONE);
            tvIntegral.setVisibility(View.GONE);
            Map<String, Object> extension = message.getRemoteExtension();
            if (extension != null && extension.get("self_attach") != null) {
                setAttach((String) extension.get("self_attach"));
            }
        } else {
            tvIntegral.setVisibility(View.GONE);
            setRead();
        }
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

    private void refreshStatus() {
        FileAttachment attachment = (FileAttachment) message.getAttachment();
        if (TextUtils.isEmpty(attachment.getPath()) && TextUtils.isEmpty(attachment.getThumbPath())) {
            if (message.getAttachStatus() == AttachStatusEnum.fail || message.getStatus() == MsgStatusEnum.fail) {
                alertButton.setVisibility(View.VISIBLE);
            } else {
                alertButton.setVisibility(View.GONE);
            }
        }

        if (message.getStatus() == MsgStatusEnum.sending
                || (isReceivedMessage() && message.getAttachStatus() == AttachStatusEnum.transferring)) {
            progressCover.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressLabel.setVisibility(View.VISIBLE);
            progressLabel.setText(StringUtil.getPercentString(getMsgAdapter().getProgress(message)));
        } else {
            progressCover.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            progressLabel.setVisibility(View.GONE);
        }
    }

    private void loadThumbnailImage(String path, boolean isOriginal, String ext) {
        setImageSize(path);
        if (path != null) {
            //thumbnail.loadAsPath(thumbPath, getImageMaxEdge(), getImageMaxEdge(), maskBg());
            thumbnail.loadAsPath(path, getImageMaxEdge(), getImageMaxEdge(), maskBg(), ext);
        } else {
            thumbnail.loadAsResource(R.drawable.nim_image_default, maskBg());
        }
    }

    private void setImageSize(String thumbPath) {
        int[] bounds = null;
        if (thumbPath != null) {
            bounds = BitmapDecoder.decodeBound(new File(thumbPath));
        }
        if (bounds == null) {
            if (message.getMsgType() == MsgTypeEnum.image) {
                ImageAttachment attachment = (ImageAttachment) message.getAttachment();
                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
            } else if (message.getMsgType() == MsgTypeEnum.video) {
                VideoAttachment attachment = (VideoAttachment) message.getAttachment();
                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
            }
        }

        if (bounds != null) {
            ImageUtil.ImageSize imageSize = ImageUtil.getThumbnailDisplaySize(bounds[0], bounds[1], getImageMaxEdge(), getImageMinEdge());
            setLayoutParams(imageSize.width, imageSize.height, thumbnail);
        }
    }

    private int maskBg() {
        return R.drawable.nim_message_item_round_bg;
    }

    public static int getImageMaxEdge() {
        return (int) (165.0 / 320.0 * ScreenUtil.screenWidth);
    }

    public static int getImageMinEdge() {
        return (int) (76.0 / 320.0 * ScreenUtil.screenWidth);
    }

    protected abstract String thumbFromSourceFile(String path);
}
