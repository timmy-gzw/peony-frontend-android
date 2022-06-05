package com.tftechsz.im.adapter;

import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.common.ui.imageview.MsgThumbImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.media.BitmapDecoder;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.tftechsz.im.R;
import com.tftechsz.im.model.FateContentInfo;
import com.tftechsz.im.mvp.ui.activity.FateMsgActivity;
import com.tftechsz.common.widget.NumIndicator;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.impl.DefaultPercentProgress;

import java.io.File;

public class FateViewHolderPicture extends FateViewHolderBase {


    public FateViewHolderPicture(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return com.netease.nim.uikit.R.layout.nim_message_item_picture;
    }

    protected MsgThumbImageView thumbnail;
    protected View progressCover;
    protected TextView progressLabel;

    @Override
    public void onItemClick() {
        FateMsgActivity fateMsgActivity = (FateMsgActivity)context;
        Mojito.with(context)
                .urls(mFateInfo.getMsg_content().getUrl())
                .position(0, 0, 1)
                .views(fateMsgActivity.getMessageListView(), R.id.iv1_5)
                .autoLoadTarget(false)
                .setIndicator(new NumIndicator(fateMsgActivity))
                .setProgressLoader(DefaultPercentProgress::new)
                .start();
    }

    @Override
    public void inflateContentView() {
        thumbnail = findViewById(com.netease.nim.uikit.R.id.message_item_thumb_thumbnail);
        progressBar = findViewById(com.netease.nim.uikit.R.id.message_item_thumb_progress_bar); // 覆盖掉
        progressCover = findViewById(com.netease.nim.uikit.R.id.message_item_thumb_progress_cover);
        progressLabel = findViewById(com.netease.nim.uikit.R.id.message_item_thumb_progress_text);
    }

    @Override
    public void bindContentView() {
        FateContentInfo msgAttachment = (FateContentInfo) mFateInfo.getMsg_content();
        String path = msgAttachment.getUrl();
        LogUtil.e("=================",path +"=======" +msgAttachment.getH() +":"+msgAttachment.getW() );
        loadThumbnailImage(path, true, "");
//        if (!TextUtils.isEmpty(thumbPath)) {
//            loadThumbnailImage(thumbPath, false, msgAttachment.getExtension());
//        } else if (!TextUtils.isEmpty(path)) {
//            loadThumbnailImage(thumbFromSourceFile(path), true, msgAttachment.getExtension());
//        } else {
//            loadThumbnailImage(null, false, msgAttachment.getExtension());
//        }
        refreshStatus();

    }

    private void refreshStatus() {
        progressCover.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        progressLabel.setVisibility(View.GONE);
    }

    private void loadThumbnailImage(String path, boolean isOriginal, String ext) {
        setImageSize(path);
        thumbnail.loadAsUrl(path, getImageMaxEdge(), getImageMaxEdge(), maskBg(), ext);
    }

    private void setImageSize(String thumbPath) {
        int[] bounds = null;
        if (thumbPath != null) {
            bounds = BitmapDecoder.decodeBound(new File(thumbPath));
        }
        if (bounds == null) {
            FateContentInfo attachment = (FateContentInfo) mFateInfo.getMsg_content();
            bounds = new int[]{attachment.getW(), attachment.getH()};
        }

        if (bounds != null) {
            ImageUtil.ImageSize imageSize = ImageUtil.getThumbnailDisplaySize(bounds[0], bounds[1], getImageMaxEdge(), getImageMinEdge());
            setLayoutParams(imageSize.width, imageSize.height, thumbnail);
        }
    }

    private int maskBg() {
        return com.netease.nim.uikit.R.drawable.nim_message_item_round_bg;
    }

    public static int getImageMaxEdge() {
        return (int) (165.0 / 320.0 * ScreenUtil.screenWidth);
    }

    public static int getImageMinEdge() {
        return (int) (76.0 / 320.0 * ScreenUtil.screenWidth);
    }

}
