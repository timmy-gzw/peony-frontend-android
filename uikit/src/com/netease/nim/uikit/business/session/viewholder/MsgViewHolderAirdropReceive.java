package com.netease.nim.uikit.business.session.viewholder;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.impl.NimUIKitImpl;

/**
 * 空投打开im
 */
public class MsgViewHolderAirdropReceive extends MsgViewHolderBase {

    private TextView mDesc;

    public MsgViewHolderAirdropReceive(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_redreceive;
    }

    @Override
    public void inflateContentView() {
        ImageView imageView = findViewById(R.id.ic);
        imageView.setVisibility(View.GONE);
        mDesc = findViewById(R.id.desc);
    }


    @Override
    public void bindContentView() {
        layoutByDirection();
    }

    private void layoutByDirection() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        String format = "";
        String formatStr = "%1$s领取了%2$s的";
        ChatMsg.AirdropOpen airdropOpen = JSON.parseObject(chatMsg.content, ChatMsg.AirdropOpen.class);
        if (airdropOpen == null) return;
        if (TextUtils.isEmpty(NimUIKitImpl.getAccount())) {
            format = String.format(formatStr, airdropOpen.to_user_name, airdropOpen.from_user_name);
        } else {
            int account = Integer.parseInt(NimUIKitImpl.getAccount());
            if (airdropOpen.from_user_id == airdropOpen.to_user_id) { //发送和接收都是自己
                if (account == airdropOpen.to_user_id) {//自己领取了自己发的红包
                    format = "你领取了自己发的";
                } else { //别人领取了自己发的
                    format = String.format(formatStr, airdropOpen.to_user_name, "自己发");
                }
            } else {
                if (account == airdropOpen.to_user_id) { // 自己领取了别人的红包
                    format = String.format(formatStr, "你", airdropOpen.from_user_name);
                } else if (account == airdropOpen.from_user_id) {  //别人领取了自己的红包
                    format = String.format(formatStr, airdropOpen.to_user_name, "你");
                } else { // A 领取了 B的红包
                    format = String.format(formatStr, airdropOpen.to_user_name, airdropOpen.from_user_name);
                }
            }
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(format + airdropOpen.text + airdropOpen.des + airdropOpen.optimum);
        int start = builder.toString().indexOf(airdropOpen.text);
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (NimUIKitImpl.getSessionListener() != null)
                    NimUIKitImpl.getSessionListener().openAirdropDetail(airdropOpen.gift_bag_id);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, start, start + airdropOpen.text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#F8D423")), start, start + airdropOpen.text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mDesc.setText(builder);
        mDesc.setMovementMethod(LinkMovementMethod.getInstance());

    }


    @Override
    protected void bindHolder(BaseViewHolder holder) {
        super.bindHolder(holder);
    }

    @Override
    protected boolean isShowHeadImage() {
        return false;
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
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
