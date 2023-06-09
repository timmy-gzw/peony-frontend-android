package com.tftechsz.im.adapter;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

public class FateViewHolderGift  extends FateViewHolderBase implements FateAdapter.OnScrollListener  {

    private ImageView typeImage;
    private FrameLayout mFlGift;
    private FrameLayout mFlImage;
    private LinearLayout mLlContent;
    private TextView tvSend;
    private TextView mTvGift;
    public FateViewHolderGift(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return com.netease.nim.uikit.R.layout.nim_message_item_gift;
    }

    @Override
    public void inflateContentView() {
        typeImage = findViewById(com.netease.nim.uikit.R.id.message_item_img);
        mFlGift = findViewById(com.netease.nim.uikit.R.id.fl_gift);
        mLlContent = findViewById(com.netease.nim.uikit.R.id.ll_content);
        mFlImage = findViewById(com.netease.nim.uikit.R.id.fl_image);
        tvSend = findViewById(com.netease.nim.uikit.R.id.tv_send);
        mTvGift = findViewById(com.netease.nim.uikit.R.id.tv_gift);
        getFateAdapter().addOnScrollListener(this);
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
        try {
            refreshContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void layoutByDirection() {
//        if (isReceivedMessage()) {   //接收消息
//            setGravity(mLlContent, Gravity.END | Gravity.CENTER_VERTICAL);
//            setGravity(mFlImage, Gravity.END | Gravity.CENTER_VERTICAL);
//            tvSend.setText("收到礼物");
//            mLlContent.setPadding(0, 0, ScreenUtil.dip2px(103), 0);
//            mLlContent.setGravity(Gravity.END);
//            mFlGift.setPadding(ScreenUtil.dip2px(57), 0, 0, 0);
//            typeImage.setPadding(ScreenUtil.dip2px(5), 0, 0, 0);
//            mFlImage.setBackgroundResource(com.netease.nim.uikit.R.drawable.chat_ic_accost_left);
//            mTvGift.setGravity(Gravity.END);
//        } else {
            setGravity(mFlImage, Gravity.START | Gravity.CENTER_VERTICAL);
            setGravity(mLlContent, Gravity.START | Gravity.CENTER_VERTICAL);
            mLlContent.setGravity(Gravity.START);
            mTvGift.setGravity(Gravity.START);
            mFlGift.setPadding(0, 0, ScreenUtil.dip2px(52), 0);
            mLlContent.setPadding(ScreenUtil.dip2px(102), 0, 0, 0);
            typeImage.setPadding(0, 0, ScreenUtil.dip2px(5), 0);
            tvSend.setText("送出礼物");
            mFlImage.setBackgroundResource(com.netease.nim.uikit.R.drawable.chat_ic_accost_right);
//        }
    }

    private void refreshContent() {
//        ChatMsg chatMsg = ChatMsgUtil.parseMessage(mFateInfo);
//        if (chatMsg == null)
//            return;
//        ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
//        if (gift != null) {
//            mTvGift.setText(gift.gift_info.name + "x" + gift.gift_num);
//            RequestOptions options = new RequestOptions()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .transforms(new CenterCrop(), new RoundedCorners(ScreenUtil.dip2px(4)))
//                    .dontAnimate();          //缓存全尺寸
//            Glide.with(context)                             //配置上下文
//                    .asDrawable()
//                    .apply(options)
//                    .load(gift.gift_info.image)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
//                    .into(typeImage);
//        }
    }

    @Override
    protected void bindHolder(BaseViewHolder holder) {
        super.bindHolder(holder);

    }


    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    @Override
    public void getScrollPosition(float sx, RecyclerView recyclerView, int rvHeight, View view, int last) {


    }

}
