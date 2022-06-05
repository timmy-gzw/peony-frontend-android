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
 * 红包推送
 */
public class MsgViewHolderRedReceive extends MsgViewHolderBase {

    private String formatStr = "%1$s领取了%2$s的红包";
    private TextView mDesc;
    private ChatMsg.GoldRedReceive mReceive;

    public MsgViewHolderRedReceive(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_redreceive;
    }

    @Override
    public void inflateContentView() {
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
        mReceive = JSON.parseObject(chatMsg.content, ChatMsg.GoldRedReceive.class);
        String format = "";
        if (mReceive != null) {
            if (TextUtils.isEmpty(NimUIKitImpl.getAccount())) {
                format = String.format(formatStr, mReceive.receive_user, mReceive.create_red_packet_user);
            } else {
                int account = Integer.parseInt(NimUIKitImpl.getAccount());
                if (mReceive.receive_user_id == mReceive.create_red_packet_user_id) { //发送和接收都是自己
                    if (account == mReceive.receive_user_id) {//自己领取了自己发的红包
                        format = "你领取了自己发的红包";
                    } else { //别人领取了自己发的红包
                        format = String.format(formatStr, mReceive.receive_user, "自己发");
                    }
                } else {
                    if (account == mReceive.receive_user_id) { // 自己领取了别人的红包
                        format = String.format(formatStr, "你", mReceive.create_red_packet_user);
                    } else if (account == mReceive.create_red_packet_user_id) {  //别人领取了自己的红包
                        format = String.format(formatStr, mReceive.receive_user, "你");
                    } else { // A 领取了 B的红包
                        format = String.format(formatStr, mReceive.receive_user, mReceive.create_red_packet_user);
                    }
                }
            }

            SpannableStringBuilder builder = new SpannableStringBuilder(format);
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    if (NimUIKitImpl.getSessionListener() != null)
                        NimUIKitImpl.getSessionListener().getRedEnvelopeDetail(mReceive.red_packet_id);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, format.length() - 2, format.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FC5858")), format.length() - 2, format.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mDesc.setText(builder);
            mDesc.setMovementMethod(LinkMovementMethod.getInstance());
        }
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
