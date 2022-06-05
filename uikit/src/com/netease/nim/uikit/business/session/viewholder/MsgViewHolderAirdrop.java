package com.netease.nim.uikit.business.session.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.impl.NimUIKitImpl;

/**
 * 空投
 */
public class MsgViewHolderAirdrop extends MsgViewHolderBase {

    private ConstraintLayout mClAirdrop;
    private TextView mIsReceive, mTvTitle, mTvAirdropSex;
    private ImageView mIvAirdrop;
    private ChatMsg.Airdrop airdrop;

    public MsgViewHolderAirdrop(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_airdrop;
    }

    @Override
    public void inflateContentView() {
        mIsReceive = findViewById(R.id.is_receive);
        mTvTitle = findViewById(R.id.tv_title);
        mClAirdrop = findViewById(R.id.message_item_airdrop);
        mIvAirdrop = findViewById(R.id.iv_airdrop);
        mTvAirdropSex = findViewById(R.id.tv_airdrop_sex);
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
    }


    private void layoutByDirection() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        airdrop = JSONObject.parseObject(chatMsg.content, ChatMsg.Airdrop.class);
        if (airdrop != null) {
            mTvTitle.setText(airdrop.title);
            if (airdrop.rule == 1) {
                mTvAirdropSex.setText("限男士");
            } else if (airdrop.rule == 2) {
                mTvAirdropSex.setText("限女士");
            } else {
                mTvAirdropSex.setText("");
            }
        }
        if (message.getLocalExtension() != null) {
            Object obj = message.getLocalExtension().get("airdrop_type");
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
                mClAirdrop.setBackgroundResource(R.drawable.bg_airdrop_in_normal);
            } else {
                mClAirdrop.setBackgroundResource(R.drawable.bg_airdrop_in_receive);
            }
        } else {
            if (type == 0) {
                mClAirdrop.setBackgroundResource(R.drawable.bg_airdrop_out_normal);
            } else {
                mClAirdrop.setBackgroundResource(R.drawable.bg_airdrop_out_receive);
            }
        }
        mIsReceive.setVisibility(View.VISIBLE);
        mIvAirdrop.setImageResource(R.drawable.ic_small_airdrops_invalid);
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
                mIvAirdrop.setImageResource(R.drawable.ic_small_short);
                mIsReceive.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onItemClick() {
        if (NimUIKitImpl.getSessionListener() != null && airdrop != null)
            NimUIKitImpl.getSessionListener().openAirdrop(airdrop.gift_bag_id, message);
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
