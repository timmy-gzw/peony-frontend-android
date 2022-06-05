package com.netease.nim.uikit.business.session.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.util.Base64;
import com.netease.nim.uikit.common.util.VipUtils;
import com.netease.nim.uikit.impl.NimUIKitImpl;

import java.io.UnsupportedEncodingException;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * 邀请好友
 */
public class MsgViewHolderFamilyInvite extends MsgViewHolderBase {

    private ChatMsg.FamilyShareMessage mMessage;
    private ConstraintLayout mRoot;
    private ImageView mIcon;
    private TextView mTitle, mApply;
    private Gson gson;

    public MsgViewHolderFamilyInvite(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_family_invite;
    }

    @Override
    public void inflateContentView() {
        if (gson == null)
            gson = new Gson();
        mRoot = findViewById(R.id.message_item_root);
        mIcon = findViewById(R.id.iv_head);
        mTitle = findViewById(R.id.tv_title);
        mApply = findViewById(R.id.tv_apply);
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
    }

    private void layoutByDirection() {
        if (isReceivedMessage()) {
            int leftBg = getLeftBg();
            VipUtils.setPersonalise(mRoot, leftBg, false);
            setTextColor(leftBg);
        } else {
            int rightBg = getRightBg();
            VipUtils.setPersonalise(mRoot, rightBg, true);
            setTextColor(rightBg);
        }
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        mMessage = gson.fromJson(chatMsg.content, ChatMsg.FamilyShareMessage.class);
        if (mMessage != null) {
            try {
                mTitle.setText(Base64.decode(mMessage.share_msg));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Glide.with(context)
                    .asBitmap()
                    .load(mMessage.img_url)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .into(mIcon);
        }
    }

    private void setTextColor(int id) {
        if (id == 17) {
            mTitle.setTextColor(Utils.getApp().getResources().getColor(R.color.color_FFEDAE));
            mApply.setTextColor(Utils.getApp().getResources().getColor(R.color.red));
        } else if (id == 11) {
            mRoot.setPadding(ConvertUtils.dp2px(18), ConvertUtils.dp2px(25), ConvertUtils.dp2px(15), ConvertUtils.dp2px(25));
        } else {
            mTitle.setTextColor(Utils.getApp().getResources().getColor(R.color.color_333333));
            mApply.setTextColor(Utils.getApp().getResources().getColor(R.color.blue_7f89f3));
        }
    }

    @Override
    public void onItemClick() {
        if (NimUIKitImpl.getSessionListener() != null)
            NimUIKitImpl.getSessionListener().toFamilyDetail(mMessage.id, mMessage.invite_id);
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
