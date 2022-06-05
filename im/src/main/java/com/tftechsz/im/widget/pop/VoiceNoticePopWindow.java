package com.tftechsz.im.widget.pop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.im.R;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.widget.FamilyManagerClickSpan;
import com.tftechsz.im.widget.VoiceChatView;
import com.tftechsz.common.http.RetrofitManager;

import io.reactivex.disposables.CompositeDisposable;
import razerdp.basepopup.BasePopupWindow;

/**
 * 查看公告
 */
public class VoiceNoticePopWindow extends BasePopupWindow implements View.OnClickListener {
    private final CompositeDisposable mCompositeDisposable;
    private TextView mTvAnnouncement;
    private final Context context;
    VoiceChatView.OnSelectCheckListener onSelectCheckListener;

    @SuppressLint("RtlHardcoded")
    public VoiceNoticePopWindow(Context context, VoiceChatView.OnSelectCheckListener onSelectCheckListener) {
        super(context);
        this.context = context;
        ChatApiService chatApiService = RetrofitManager.getInstance().createFamilyApi(ChatApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        this.onSelectCheckListener = onSelectCheckListener;
        initUI();
        setPopupGravity(Gravity.LEFT | Gravity.TOP);
        setOffsetX(10);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_voice_notice);
    }

    private void initUI() {
        mTvAnnouncement = findViewById(R.id.tv_announcement);
    }

    /**
     * 设置公告
     */
    public void setAnnouncement(String announcement, boolean isAdmin, boolean isUpdateText) {

        String str = isUpdateText ? "编辑" : "审核中";
        SpannableStringBuilder spannableString;
        if (isAdmin) {
            if (TextUtils.isEmpty(announcement)) {
                announcement = "还没有语音房公告，编辑好公告让大家知道怎么玩的吧";
            }
            spannableString = new SpannableStringBuilder(announcement + str);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#7F89F3"));
            spannableString.setSpan(colorSpan, announcement.length(), announcement.length() + str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//            int start = spannableString.toString().indexOf(announcement);
            spannableString.setSpan(new FamilyManagerClickSpan(this, announcement, new OnSelectListener() {

                @Override
                public void showPopup(int d) {

                }

                @Override
                public void clickEdit(String str) {
                    if (onSelectCheckListener != null) {
                        onSelectCheckListener.check();
                    }
                }
            }), announcement.length(), announcement.length() + str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new UnderlineSpan(), announcement.length(), announcement.length() + str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvAnnouncement.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            if (TextUtils.isEmpty(announcement)) {
                announcement = "当前暂无语音房公告";
            }
            spannableString = new SpannableStringBuilder(announcement);
        }
        mTvAnnouncement.setText(spannableString);
        mTvAnnouncement.post(() -> {
        //适配显示位置问题
            int txtPart = mTvAnnouncement.getLineCount();
            if (txtPart == 1) {
                setOffsetY(DensityUtils.dp2px(context, 22));
            } else {
                setOffsetY(DensityUtils.dp2px(context, DensityUtils.isDpWithHW(context, 1080, 2028) ? txtPart * 20 + 8 : txtPart * 20 - 7));
            }
            update();
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.tv_sure) {

        }
    }

    public interface OnSelectListener {
        void showPopup(int d);

        void clickEdit(String str);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
