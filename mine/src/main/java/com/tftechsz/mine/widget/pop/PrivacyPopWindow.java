package com.tftechsz.mine.widget.pop;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.mine.widget.TextClick;
import com.yl.lib.sentry.hook.PrivacySentry;

/**
 * 隐私协议
 */
public class PrivacyPopWindow extends BaseCenterPop implements View.OnClickListener {



    public PrivacyPopWindow(Context context) {
        super(context);
        mContext = context;
        initUI();
        setOutSideDismiss(false);
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_privacy);
    }

    private void initUI() {
        TextView tvContent = findViewById(R.id.tv_content);
        String res = tvContent.getText().toString();
        SpannableStringBuilder builder = new SpannableStringBuilder(res);
        int stat = res.indexOf("《");
        builder.setSpan(new TextClick(mContext, 0), stat, stat + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#7F89F3")), stat, stat + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new TextClick(mContext, 1), stat + 9, stat + 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#7F89F3")), stat + 9, stat + 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvContent.setText(builder);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        findViewById(R.id.tv_agree).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_agree) {
            PrivacySentry.Privacy.INSTANCE.updatePrivacyShow();
            MMKVUtils.getInstance().encode(Constants.IS_AGREE_AGREEMENT, 1);
            if (privacyListener != null) {
                privacyListener.agree();
                dismiss();
            }

        } else if (id == R.id.tv_cancel) {
            if (privacyListener != null) {
                privacyListener.cancel();
            }
            dismiss();
        }
    }

    private PrivacyListener privacyListener;

    public void setPrivacyListener(PrivacyListener privacyListener) {
        this.privacyListener = privacyListener;
    }

    public interface PrivacyListener {
        void agree();
        void cancel();
    }


}
