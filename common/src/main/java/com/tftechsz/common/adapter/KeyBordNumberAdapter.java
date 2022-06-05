package com.tftechsz.common.adapter;

import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.KeyBordDto;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 键盘数字
 */
public class KeyBordNumberAdapter extends BaseQuickAdapter<KeyBordDto, BaseViewHolder> {

    public KeyBordNumberAdapter(@Nullable List<KeyBordDto> data) {
        super(R.layout.item_key_bord_number, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, KeyBordDto item) {
        LinearLayout llRoot = helper.getView(R.id.item_root);
        TextView tvNumber = helper.getView(R.id.tv_number);
        tvNumber.setText(item.number);
        helper.setText(R.id.tv_en_number, item.enKey);
        helper.setGone(R.id.tv_en_number, TextUtils.equals(item.number, "0"));
        if (TextUtils.isEmpty(item.number)) {
            llRoot.setBackgroundResource(R.color.transparent);
        } else if (TextUtils.equals(item.number, "del")) {
            llRoot.setBackgroundResource(R.mipmap.chat_ic_keybord_del);
            tvNumber.setText("");
        } else
            llRoot.setBackgroundResource(R.mipmap.chat_ic_keybord);

    }
}
