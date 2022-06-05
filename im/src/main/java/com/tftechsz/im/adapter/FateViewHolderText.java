package com.tftechsz.im.adapter;

import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;

public class FateViewHolderText  extends FateViewHolderBase {

    protected TextView bodyTextView;
    private TextView tvIntegral;

    public FateViewHolderText(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return com.netease.nim.uikit.R.layout.nim_message_item_text;
    }

    @Override
    public void inflateContentView() {
        bodyTextView = findViewById(com.netease.nim.uikit.R.id.nim_message_item_text_body);
        tvIntegral = findViewById(com.netease.nim.uikit.R.id.tv_integral);
    }

    @Override
    public void bindContentView() {
        layoutDirection();
    }

    private void layoutDirection() {
        //LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bodyTextView.getLayoutParams();
//        if (isReceivedMessage()) {
//            tvIntegral.setVisibility(View.GONE);
//            bodyTextView.setBackgroundResource(NimUIKitImpl.getOptions().messageLeftBackground);
//            bodyTextView.setTextColor(ContextCompat.getColor(context, com.netease.nim.uikit.R.color.color_black_333333));
////            lp.rightMargin = ScreenUtil.dip2px(65);
//            bodyTextView.setPadding(ScreenUtil.dip2px(12), ScreenUtil.dip2px(12), ScreenUtil.dip2px(12), ScreenUtil.dip2px(12));
//        } else {
            tvIntegral.setVisibility(View.GONE);
            bodyTextView.setBackgroundResource(NimUIKitImpl.getOptions().messageRightBackground);
            bodyTextView.setTextColor(ContextCompat.getColor(context, com.netease.nim.uikit.R.color.color_black_333333));
//            lp.leftMargin = ScreenUtil.dip2px(65);
            bodyTextView.setPadding(ScreenUtil.dip2px(12), ScreenUtil.dip2px(12), ScreenUtil.dip2px(12), ScreenUtil.dip2px(12));
//        }
        //bodyTextView.setLayoutParams(lp);

        tvIntegral.setOnClickListener(v -> NimUIKitImpl.getSessionListener().onAddAmount());

        bodyTextView.setOnClickListener(v -> onItemClick());
        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), bodyTextView, getDisplayText(), ImageSpan.ALIGN_BOTTOM);


//        MoonUtil.identifyRecentVHFaceExpressionAndTags(NimUIKit.getContext(), bodyTextView,getDisplayText(), ImageSpan.ALIGN_BOTTOM, 0.45f);
        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setLinkTextColor(Color.BLACK);
        bodyTextView.setOnLongClickListener(longClickListener);
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    protected String getDisplayText() {
        return mFateInfo.getMsg_content().getContent();
    }
}
