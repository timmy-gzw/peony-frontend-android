package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.api.NimUIKit;
import com.tftechsz.common.R;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.RoundImageView;

/**
 * 搭讪成功去私聊弹窗
 */
public class AccostChatPop extends BaseBottomPop implements View.OnClickListener {

    private final RoundImageView ivAvatar;
    private final TextView tvUsername;
    private String toUserId;

    public AccostChatPop(Context context, String toUserId, String toUsername, String toUserAvatar) {
        super(context);
        ivAvatar = findViewById(R.id.iv_accost_avatar);
        tvUsername = findViewById(R.id.tv_accosted_username);
        findViewById(R.id.tv_to_private_chat).setOnClickListener(this);
        findViewById(R.id.iv_close_accost_pop).setOnClickListener(this);

        setChatUserInfo(toUserId, toUsername, toUserAvatar);

        setOutSideDismiss(false);
        setOutSideTouchable(true);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.layout_accost_to_chat_pop);
    }

    public void setChatUserInfo(String toUserId, String toUsername, String toUserAvatar) {
        this.toUserId = toUserId;
        if (ivAvatar != null) {
            GlideUtils.loadCircleImage(getContext(), ivAvatar, toUserAvatar);
        }
        if (tvUsername != null) {
            tvUsername.setText(toUsername);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close_accost_pop) {
            dismiss();
        } else if (id == R.id.tv_to_private_chat) {
            if (!TextUtils.isEmpty(toUserId)) {
                ARouterUtils.toChatP2PActivity(toUserId, NimUIKit.getCommonP2PSessionCustomization(), null);
            }
            dismiss();
        }
    }
}
