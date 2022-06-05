package com.tftechsz.im.adapter;

import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.List;

public class FateViewHolderReplyAccost extends FateViewHolderBase {


    private RecyclerView rvGift;
    private LinearLayout avchatContent;

    public FateViewHolderReplyAccost(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return com.netease.nim.uikit.R.layout.nim_message_item_reply_accost;
    }

    @Override
    public void inflateContentView() {

        avchatContent = findViewById(com.netease.nim.uikit.R.id.message_item_avchat_content);
        rvGift = findViewById(com.netease.nim.uikit.R.id.rv_gift);
        rvGift.setLayoutManager(new LinearLayoutManager(context));
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

    }

    private void refreshContent() {
//        ChatMsg chatMsg = ChatMsgUtil.parseMessage(mFateInfo);
//        if (chatMsg == null)
//            return;
//
//        List<ChatMsg.AccostGift> accostGift = JSONObject.parseArray(chatMsg.content, ChatMsg.AccostGift.class);
//        if(accostGift!=null){
//            FateViewHolderReplyAccost.GiftAdapter adapter = new FateViewHolderReplyAccost.GiftAdapter(rvGift,accostGift);
//            rvGift.setAdapter(adapter);
//        }
    }




    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }


    public class GiftAdapter extends BaseQuickAdapter<ChatMsg.AccostGift, BaseViewHolder> {

        public GiftAdapter(RecyclerView recyclerView,  List<ChatMsg.AccostGift> data) {
            super(recyclerView, com.netease.nim.uikit.R.layout.nim_message_item_reply_accost_msg, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ChatMsg.AccostGift item, int position, boolean isScrolling) {
            ImageView typeImage = helper.getView(com.netease.nim.uikit.R.id.message_item_img);
            helper.setText(com.netease.nim.uikit.R.id.tv_gift,item.name + "x1");
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transforms(new CenterCrop(), new RoundedCorners(ScreenUtil.dip2px(4)))
                    .dontAnimate();          //缓存全尺寸
            Glide.with(context)              //配置上下文
                    .asDrawable()
                    .apply(options)
                    .load(item.image)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .into(typeImage);
        }
    }


}
