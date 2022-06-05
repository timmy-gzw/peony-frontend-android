package com.netease.nim.uikit.business.session.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

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
public class MsgViewHolderRedEnvelope extends MsgViewHolderBase {

    private TextView mDesc;
    private ConstraintLayout mRoot;
    private ChatMsg.GoldRedEnvelope mRedEnvelope;
    private TextView mIsReceive;
    private ImageView mIvRed;

    public MsgViewHolderRedEnvelope(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_redenvelope;
    }

    @Override
    public void inflateContentView() {
        mDesc = findViewById(R.id.red_desc);
        mRoot = findViewById(R.id.message_item_red_root);
        mIvRed = findViewById(R.id.iv_red);
        mIsReceive = findViewById(R.id.is_receive);
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
    }

    private void layoutByDirection() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;

        mRedEnvelope = JSON.parseObject(chatMsg.content, ChatMsg.GoldRedEnvelope.class);
        if (mRedEnvelope != null) {
            mDesc.setText(mRedEnvelope.des);
        }

        if (message.getLocalExtension() != null) {
            Object obj = message.getLocalExtension().get("red_package_type");
            if (obj != null) {
                setRootBg((int) obj);
                return;
            }
        }
        setRootBg(0);
    }

    private void setRootBg(int type) {
        if (isReceivedMessage()) {
            if (type == 0) {
                mRoot.setBackgroundResource(R.drawable.bg_red_envelope_in_normal);
            } else {
                mRoot.setBackgroundResource(R.drawable.bg_red_envelope_in_receive);
            }
        } else {
            if (type == 0) {
                mRoot.setBackgroundResource(R.drawable.bg_red_envelope_out_normal);
            } else {
                mRoot.setBackgroundResource(R.drawable.bg_red_envelope_out_receive);
            }
        }

        mIsReceive.setVisibility(View.VISIBLE);
        mIvRed.setImageResource(R.drawable.ly13_jz_red03_icon);
        switch (type) {
            case 1:
                mIsReceive.setText("已领取");
                break;

            case 2:
                mIsReceive.setText("已领完");
                break;

            case 3:
                mIsReceive.setText("已过期");
                break;

            default:
                mIvRed.setImageResource(R.drawable.ly13_jz_red02_icon);
                mIsReceive.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onItemClick() {
        if (NimUIKitImpl.getSessionListener() != null)
            NimUIKitImpl.getSessionListener().openRedEnvelope(mRedEnvelope.red_packet_id, message);
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


}
