package com.tftechsz.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 包 名 : com.tftechsz.common.widget
 * 描 述 : TODO
 */
public class ShowAllTextView extends AppCompatTextView {
    private boolean showTail = true;
    private boolean isEllipsed = false;
    private static final String LINE_BREAKER = "\n";
    private String tailText = " 全文";

    public ShowAllTextView(Context context) {
        this(context, null);
    }

    public ShowAllTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowAllTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //不显示小尾巴或者已经处理过的不再处理
        if (!showTail || isEllipsed) {
            return;
        }
        int lineCount = getLineCount();
        Layout layout = getLayout();
        int maxLines = getMaxLines();
        //行数没有到maxLines不处理
        if (maxLines == 0 || lineCount < maxLines || TextUtils.isEmpty(getText())) ;
        else {
            int lineEndIndex = layout.getLineEnd(maxLines - 1);//第maxLines行的首字符offset
            int lineStartIndex = layout.getLineStart(maxLines - 1);//第(maxLines - 1)行的首字符offset
            if (lineEndIndex >= getText().length()) return;

            CharSequence mustShowText = getText().subSequence(0, lineStartIndex);
            float tailWidth = getPaint().measureText(tailText);
            CharSequence lastLineText;
            //最后一个字是个换行符就把这个换行符去掉，不然不能在那一行后面增加文字了
            if (LINE_BREAKER.equals(String.valueOf(getText().charAt(lineEndIndex - 1)))) {
                lastLineText = getText().subSequence(lineStartIndex, lineEndIndex - 1);
            } else {
                lastLineText = getText().subSequence(lineStartIndex, lineEndIndex);
            }
            int availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            CharSequence ellipsizeLastLineText = TextUtils.ellipsize(lastLineText, getPaint(), availableWidth - tailWidth, TextUtils.TruncateAt.END);
            if (ellipsizeLastLineText.length() > 2 && ellipsizeLastLineText != lastLineText) {
                lastLineText = ellipsizeLastLineText.subSequence(0, ellipsizeLastLineText.length());
            }
            SpannableStringBuilder ssb = new SpannableStringBuilder(mustShowText);
            ssb.append(lastLineText).append(tailText);
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#7F89F3")), ssb.length() - tailText.length(), ssb.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            setText(ssb);
            //重置一下这个位
            isEllipsed = false;
        }
    }
}
