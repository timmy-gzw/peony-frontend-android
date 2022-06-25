package com.netease.nim.uikit.business.session.viewholder;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.VipUtils;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

/**
 * 搭讪
 */
public class MsgViewHolderAccost extends MsgViewHolderBase {

    private ImageView typeImage;
    private TextView tvContent;
    private ImageView ivRedPackage;
    private LinearLayout avchatContent;
    private LinearLayout rlAccost;  //自己搭讪时的图片
    private TextView mTvGift;

    public MsgViewHolderAccost(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_accost;
    }

    @Override
    public void inflateContentView() {
        tvContent = findViewById(R.id.tv_content);
        typeImage = findViewById(R.id.message_item_img);
        ivRedPackage = findViewById(R.id.iv_red_package);
        avchatContent = findViewById(R.id.message_item_avchat_content);
        rlAccost = findViewById(R.id.rl_accost);
        mTvGift = findViewById(R.id.tv_gift);
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
        if (isReceivedMessage()) {   //接收消息
            avchatContent.setGravity(Gravity.START);
            ivRedPackage.setVisibility(View.VISIBLE);
            rlAccost.setVisibility(View.GONE);
            VipUtils.setPersonalise(tvContent, getLeftBg(), false);
        } else {
            rlAccost.setVisibility(View.VISIBLE);
            ivRedPackage.setVisibility(View.GONE);
            avchatContent.setGravity(Gravity.END);
            VipUtils.setPersonalise(tvContent, getRightBg(), true);
        }
    }

    private void refreshContent() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.Accost accostGift = JSON.parseObject(chatMsg.content, ChatMsg.Accost.class);
        if (accostGift != null) {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontTransform()//这个方法就是取消图片变化效果
                    .transforms(new CenterCrop(), new RoundedCorners(ScreenUtil.dip2px(4)))
                    .dontAnimate();
            mTvGift.setText(accostGift.gift_info.name);
            Glide.with(context)                             //配置上下文
                    .asBitmap()
                    .apply(options)
                    .load(accostGift.gift_info.image)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .into(typeImage);
            tvContent.setText(accostGift.msg);
        }
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }
}
