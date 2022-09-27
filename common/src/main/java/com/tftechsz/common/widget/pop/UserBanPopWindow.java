package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * 用户封禁
 */
public class UserBanPopWindow extends BaseCenterPop{

    private TextView tvContent,tvBtn;

    public UserBanPopWindow(Context context) {
        this(context,null);
    }

    public UserBanPopWindow(Context context, View.OnClickListener listener) {
        super(context);
        tvContent = findViewById(R.id.tv_content);
        tvBtn = findViewById(R.id.tv_btn);
        tvBtn.setOnClickListener(v -> {
            dismiss();
            if(listener != null){
                listener.onClick(v);
            }
        });
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        stringBuilder.append("由于该用户“存在严重违规行为”严重影响社区氛围，经官方判定封号，一起维护健康文明的社区环境，人人有责。");
        stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#FD2000")), 5, 15,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tvContent.setText(stringBuilder);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_user_ban);
    }

    @Override
    public BasePopupWindow setBackPressEnable(boolean backPressEnable) {
        return super.setBackPressEnable(false);
    }
}
