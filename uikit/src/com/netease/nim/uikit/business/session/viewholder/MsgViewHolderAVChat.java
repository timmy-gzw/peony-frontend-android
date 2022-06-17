package com.netease.nim.uikit.business.session.viewholder;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.Utils;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.VipUtils;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;

/**
 * 语音视频
 */
public class MsgViewHolderAVChat extends MsgViewHolderBase {

    private ImageView typeImage;
    private TextView statusLabel;
    private FrameLayout avchatContent;

    public MsgViewHolderAVChat(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_avchat;
    }

    @Override
    public void inflateContentView() {
        typeImage = findViewById(R.id.message_item_avchat_type_img);
        statusLabel = findViewById(R.id.message_item_avchat_state);
        avchatContent = findViewById(R.id.message_item_avchat_content);
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
        refreshContent();
    }

    private void layoutByDirection() {
        typeImage.setColorFilter(Utils.getApp().getResources().getColor(R.color.color_black_333333));
        if (isReceivedMessage()) {
            setGravity(typeImage, Gravity.START | Gravity.CENTER_VERTICAL);
            setGravity(statusLabel, Gravity.END | Gravity.CENTER_VERTICAL);
            statusLabel.setPadding(ScreenUtil.dip2px(30), 0, 0, 0);
            VipUtils.setPersonalise(avchatContent, getLeftBg(), false);
        } else {
            setGravity(statusLabel, Gravity.START | Gravity.CENTER_VERTICAL);
            setGravity(typeImage, Gravity.END | Gravity.CENTER_VERTICAL);
            statusLabel.setPadding(0, 0, ScreenUtil.dip2px(30), 0);
            VipUtils.setPersonalise(avchatContent, getRightBg(), true);
        }
    }

    private void refreshContent() {

        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.CallMsg callMsg = JSON.parseObject(chatMsg.content, ChatMsg.CallMsg.class);
        if (chatMsg.type == 2) {
            typeImage.setImageResource(R.drawable.avchat_left_type_video);
        } else {
            typeImage.setImageResource(R.drawable.avchat_left_type_audio);
        }
        String textString = "已取消";
        if (callMsg != null) {
            if (callMsg.text_type == 2) {
                typeImage.setImageResource(R.drawable.avchat_left_type_video);
            } else if (callMsg.text_type == 1) {
                typeImage.setImageResource(R.drawable.avchat_left_type_audio);
            }
            if (callMsg.state == ChatMsg.EVENT_ACCEPT) {
            } else if (callMsg.state == ChatMsg.EVENT_REJECT) {
                if (isReceivedMessage()) {  //接收
                    textString = "已拒绝";
                } else {
                    textString = "对方已拒绝";
                }
            } else if (callMsg.state == ChatMsg.EVENT_END) {
                if (callMsg.close_type == 1) {    // 呼叫超时
                    if (isReceivedMessage()) {  //接收
                        textString = "对方已取消";
                    } else {
                        textString = "对方无应答";
                    }
                } else {
                    if (callMsg.answer_time == 0) {
                        if (isReceivedMessage()) {  //接收
                            textString = "对方已取消";
                        } else {
                            textString = "已取消";
                        }
                    } else {
                        textString = "通话结束 " + TimeUtil.secToTime(callMsg.answer_time);
                    }
                }
            } else if (callMsg.state == ChatMsg.EVENT_DEFAULT) {

            } else if (callMsg.state == ChatMsg.EVENT_BUSY) {
                textString = "忙碌中";
            } else if (callMsg.state == ChatMsg.EVENT_NOT_ACCEPT) {
                if (isReceivedMessage()) {  //接收
                    textString = "对方已取消";
                } else {
                    textString = "对方无应答";
                }
            } else if (callMsg.state == ChatMsg.EVENT_OFF_LINE) {
                if (isReceivedMessage()) {  //接收
                    textString = "对方已取消";
                } else {
                    textString = "对方不在线";
                }
            } else if (callMsg.state == ChatMsg.EVENT_ERROR) {
                textString = "网络异常，通话结束";
            }
        }
        if(!isReceivedMessage()){
            statusLabel.setTextColor(NimUIKit.getContext().getResources().getColor(R.color.white));
            typeImage.setColorFilter(Color.WHITE);
        }
        statusLabel.setText(textString);
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
