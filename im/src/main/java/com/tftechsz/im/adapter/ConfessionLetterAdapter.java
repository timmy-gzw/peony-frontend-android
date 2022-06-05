package com.tftechsz.im.adapter;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.CoupleLetterDto;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

public class ConfessionLetterAdapter extends BaseQuickAdapter<CoupleLetterDto, BaseViewHolder> {
    public ConfessionLetterAdapter() {
        super(R.layout.item_confession_letter);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, CoupleLetterDto item) {
        baseViewHolder.setText(R.id.tv_name, item.nickname)
                .setText(R.id.tv_detail, item.desc);
        GlideUtils.loadImage(getContext(), baseViewHolder.getView(R.id.iv_left), item.icon);
        SpannableStringBuilder span = new SpannableStringBuilder();
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(item.title);
        span.append(stringBuffer);
        int start = stringBuffer.toString().indexOf(item.nickname);
        if (start >= 0) {
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#7F89F3")), start, start + item.nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6A9D")), 0, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6A9D")), start + item.nickname.length(), item.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            baseViewHolder.setText(R.id.tv_content, span);
        } else {
            baseViewHolder.setText(R.id.tv_content, item.title);
        }

        if (item.is_compete) {  //竞争者表白信息
            baseViewHolder.setImageResource(R.id.iv_title, R.mipmap.chat_ic_confession_letter_1);
        } else {
            baseViewHolder.setImageResource(R.id.iv_title, R.mipmap.chat_ic_confession_letter);
        }
        if (item.sex == 1) {
            baseViewHolder.setImageResource(R.id.iv_left_head, R.mipmap.chat_bg_group_couple_head_boy);
        } else {
            baseViewHolder.setImageResource(R.id.iv_left_head, R.mipmap.chat_bg_group_couple_head_girl);
        }

    }
}
