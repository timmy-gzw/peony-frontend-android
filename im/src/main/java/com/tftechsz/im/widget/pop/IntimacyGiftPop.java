package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.im.R;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.pop.BaseCenterPop;

/**
 * 包 名 : com.tftechsz.im.widget.pop
 * 描 述 : 亲密度礼物弹窗
 */
public class IntimacyGiftPop extends BaseCenterPop {
    public IntimacyGiftPop(Context context) {
        super(context);
        findViewById(R.id.btn).setOnClickListener(v -> dismiss());
    }

    private void initUI(ChatMsg.IntimacyGift dto) {
        ImageView img = findViewById(R.id.img);
        TextView tips = findViewById(R.id.tips);
        TextView desc = findViewById(R.id.desc);

        GlideUtils.loadRouteImage(getContext(), img, dto.url);
        tips.setText(dto.tips);
        if (!TextUtils.isEmpty(dto.desc)) {
            desc.setVisibility(View.VISIBLE);
            desc.setText(dto.desc);
        } else {
            desc.setVisibility(View.GONE);
        }
    }

    public void setData(ChatMsg.IntimacyGift dto) {
        initUI(dto);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_intimacy_gift);
    }
}
