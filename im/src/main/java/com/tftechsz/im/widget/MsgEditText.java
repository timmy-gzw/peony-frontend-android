package com.tftechsz.im.widget;


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * 模仿微信的@功能 整块删除 可编辑
 */

public class MsgEditText extends AppCompatEditText {


    public MsgEditText(Context context) {
        super(context);
        init();
    }

    public MsgEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MsgEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private StringBuilder builder;

    private onTextDeleteListener onTextDeleteListener;


    private boolean mIsSelected;
    private Range mLastSelectedRange;
    private List<Range> mRangeArrayList;
    private boolean At;

    private void init() {
        mRangeArrayList = new ArrayList<>();

    }

    public boolean isAt() {
        return At;
    }

    public void setAt(boolean at) {
        At = at;
    }

    public boolean ismIsSelected() {
        return mIsSelected;
    }

    public void setmIsSelected(boolean mIsSelected) {
        this.mIsSelected = mIsSelected;
    }

    public Range getmLastSelectedRange() {
        return mLastSelectedRange;
    }

    public void setmLastSelectedRange(Range mLastSelectedRange) {
        this.mLastSelectedRange = mLastSelectedRange;
    }

    /**
     * 添加一个块,在文字的后面添加
     *
     * @param showText 显示到界面的内容
     * @param userId   附加属性，比如用户id,邮件id之类的，如果不需要可以去除掉
     */
    public void addAtSpan(String maskText, String showText, String userId) {
        builder = new StringBuilder();
        if (!TextUtils.isEmpty(maskText)) {
            //已经添加了@
            builder.append(maskText).append(showText).append(" ");
        } else {
            builder.append(showText).append(" ");
        }
        if (getText() != null)
            getText().insert(getSelectionStart(), builder.toString());
        SpannableString sps = new SpannableString(getText());
        int start = getSelectionEnd() - builder.toString().length() - (TextUtils.isEmpty(maskText) ? 1 : 0);
        int end = getSelectionEnd();
        makeSpan(sps, new UnSpanText(start, end, builder.toString()), userId);
        setText(sps);
        setSelection(end);
    }

    //获取用户Id列表
    public String getUserIdString() {
        MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
        StringBuilder builder = new StringBuilder();
        for (MyTextSpan myTextSpan : spans) {
            String realText = getText().toString().substring(getText().getSpanStart(myTextSpan), getText().getSpanEnd(myTextSpan));
            String showText = myTextSpan.getShowText();
            if (realText.equals(showText)) {
                builder.append(myTextSpan.getUserId()).append(",");
            }
        }
        if (!TextUtils.isEmpty(builder.toString())) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    //生成一个需要整体删除的Span
    private void makeSpan(Spannable sps, UnSpanText unSpanText, String userId) {
        MyTextSpan what = new MyTextSpan(unSpanText.returnText, userId);
        int start = unSpanText.start;
        int end = unSpanText.end;
        sps.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mRangeArrayList.add(new Range(start, end));
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        //向前删除一个字符，@后的内容必须大于一个字符，可以在后面加一个空格
//        if(mRangeArrayList!=null)
//            mRangeArrayList.clear();
        if (lengthBefore == 1 && lengthAfter == 0) {
            MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
            for (MyTextSpan myImageSpan : spans) {
                if (getText().getSpanEnd(myImageSpan) == start && !text.toString().endsWith(myImageSpan.getShowText())) {
                    getText().delete(getText().getSpanStart(myImageSpan), getText().getSpanEnd(myImageSpan));
                    if (onTextDeleteListener != null)
                        onTextDeleteListener.onDeleteCharSequence(myImageSpan.userId);
                    break;
                }
            }
        }

        if (lengthAfter == 0) {
            if (mRangeArrayList != null)
                mRangeArrayList.clear();
            if (onTextDeleteListener != null)
                onTextDeleteListener.onDeleteAll();
        }

    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        //avoid infinite recursion after calling setSelection()
//        LogUtil.e("onSelectionChanged",selStart+","+selEnd);
        if (!isAt()) return;
//        if (mLastSelectedRange != null && mLastSelectedRange.isEqual(selStart, selEnd)) {
//            return;
//        }
//
//        //if user cancel a selection of mention string, reset the state of 'mIsSelected'
//        Range closestRange = getRangeOfClosestMentionString(selStart, selEnd);
//        if (closestRange != null && closestRange.to == selEnd) {
//            mIsSelected = false;
//        }

        Range nearbyRange = getRangeOfNearbyMentionString(selStart, selEnd);
        //if there is no mention string nearby the cursor, just skip
        if (nearbyRange == null) {
            return;
        }

        //禁止游标定位到@的整个字符串上
        if (selStart == selEnd) {
//            LogUtil.e("selStart","selStart==selEnd");
            setSelection(nearbyRange.getAnchorPosition(selStart));
        } else {
            if (selEnd < nearbyRange.to) {
                setSelection(selStart, nearbyRange.to);
            }
            if (selStart > nearbyRange.from) {
                setSelection(nearbyRange.from, selEnd);
            }
        }
    }

//    @Override
//    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
//        return isAt()?new HackInputConnection(super.onCreateInputConnection(outAttrs), true, this):super.onCreateInputConnection(outAttrs);
//    }

    //handle the deletion action for mention string, such as '@test'
    private class HackInputConnection extends InputConnectionWrapper {
        private EditText editText;

        HackInputConnection(InputConnection target, boolean mutable, MsgEditText editText) {
            super(target, mutable);
            this.editText = editText;
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                int selectionStart = editText.getSelectionStart();
                int selectionEnd = editText.getSelectionEnd();
                Range closestRange = getRangeOfClosestMentionString(selectionStart, selectionEnd);
                if (closestRange == null) {
                    mIsSelected = false;
                    return super.sendKeyEvent(event);
                }
                //if mention string has been selected or the cursor is at the beginning of mention string, just use default action(delete)
                if (mIsSelected || selectionStart == closestRange.from) {
                    mIsSelected = false;
                    return super.sendKeyEvent(event);
                } else {
                    //select the mention string
                    mIsSelected = true;
                    mLastSelectedRange = closestRange;
                    setSelection(closestRange.to, closestRange.from);
                }
                return true;
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            if (beforeLength == 1 && afterLength == 0) {
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    private Range getRangeOfClosestMentionString(int selStart, int selEnd) {
        if (mRangeArrayList == null) {
            return null;
        }
        for (Range range : mRangeArrayList) {
            if (range.contains(selStart, selEnd)) {
                return range;
            }
        }
        return null;
    }

    private Range getRangeOfNearbyMentionString(int selStart, int selEnd) {
        if (mRangeArrayList == null) {
            return null;
        }
        for (Range range : mRangeArrayList) {
            if (range.isWrappedBy(selStart, selEnd)) {
                return range;
            }
        }
        return null;
    }

    private static class MyTextSpan extends MetricAffectingSpan {
        private String showText;
        private String userId;

        public MyTextSpan(String showText, String userId) {
            this.showText = showText;
            this.userId = userId;
        }


        public String getShowText() {
            return showText;
        }

        public String getUserId() {
            return userId;
        }

        @Override
        public void updateMeasureState(TextPaint p) {

        }

        @Override
        public void updateDrawState(TextPaint tp) {

        }
    }

    public static class Range {
        private int from;
        private int to;

        Range(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        boolean isWrappedBy(int start, int end) {
//            LogUtil.e("isWrappedBy","start-->"+start+",from-->"+from+",end-->"+end+",to-->"+to);
            return (start > from && start < to) || (end > from && end < to);
        }

        boolean contains(int start, int end) {
            return from <= start && to >= end;
        }

        boolean isEqual(int start, int end) {
            return (from == start && to == end) || (from == end && to == start);
        }

        int getAnchorPosition(int value) {
//            LogUtil.e("getAnchorPosition","value-->"+value+",from->"+from+",to-->"+to);
            if ((value - from) - (to - value) >= 0) {
                return to;
            } else {
                return from;
            }
        }
    }

    private static class UnSpanText {
        int start;
        int end;
        String returnText;

        UnSpanText(int start, int end, String returnText) {
            this.start = start;
            this.end = end;
            this.returnText = returnText;
        }
    }

    public void setOnTextDeleteListener(onTextDeleteListener onTextDeleteListener) {
        this.onTextDeleteListener = onTextDeleteListener;
    }

    public interface onTextDeleteListener {
        void onDeleteCharSequence(CharSequence charSequence);

        void onDeleteAll();
    }

}
