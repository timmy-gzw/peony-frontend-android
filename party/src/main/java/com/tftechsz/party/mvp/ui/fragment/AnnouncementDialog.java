package com.tftechsz.party.mvp.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.blankj.utilcode.util.ToastUtils;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.tftechsz.common.utils.StringUtils;
import com.tftechsz.common.widget.CustomFilter;
import com.tftechsz.party.R;

/**
 * 派对-公告设置
 */
public class AnnouncementDialog extends DialogFragment {
    private final IAnnouncementSetListener iAnnouncementSetListener;
    private EditText ediTitle, ediContent;
    String title, content;

    public AnnouncementDialog(IAnnouncementSetListener iAnnouncementSetListener, String title, String content) {
        this.iAnnouncementSetListener = iAnnouncementSetListener;
        this.title = title;
        this.content = content;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View dialogView = createDialogView(inflater, container);
        initWindowParams();
        return dialogView;
    }

    protected View createDialogView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.pop_announcement_set, container, false);
        ediTitle = view.findViewById(R.id.et_content);
        ediContent = view.findViewById(R.id.et_content2);
        ediTitle.setText(StringUtil.isEmpty(title) ? "" : title);
        ediContent.setText(StringUtil.isEmpty(content) ? "" : content);
        TextView mTvNumber = view.findViewById(R.id.tv_number);
        view.findViewById(R.id.tv_click_cancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.tv_save_pas).setOnClickListener(v -> {
            if (TextUtils.isEmpty(ediTitle.getText().toString())) {
                ToastUtils.showShort("请输入标题");
                return;
            } else if (TextUtils.isEmpty(ediContent.getText().toString())) {
                ToastUtils.showShort("请输入公告内容");
                return;
            }
            iAnnouncementSetListener.saveAnnouncement(ediTitle.getText().toString(), ediContent.getText().toString());
            dismiss();
        });
        mTvNumber.setText(StringUtils.judgeTextLength(ediContent.getText().toString()) + "/" + 1000);
        ediContent.setSelection(ediContent.getText().length());
        ediContent.addTextChangedListener(new TextWatcher() {
            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start1, int before, int count) {
//                int start = StringUtils.judgeTextLength(s.toString());
//                mTvNumber.setText(start + "/1000");
                int size = Math.min(StringUtils.judgeTextLength(ediContent.getText().toString()), 1000);
                mTvNumber.setText(size + "/1000");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ediContent.setFilters(new InputFilter[]{new CustomFilter(1000)});
        return view;
    }


    protected int getDialogWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    protected int getDialogHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private void initWindowParams() {
        Dialog dialog = getDialog();
        if (dialog != null) {

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = dialog.getWindow();
            if (window != null) {
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setBackgroundDrawableResource(R.color.Transparent);
                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.BOTTOM;
                windowAttributes.width = getDialogWidth();
                windowAttributes.height = getDialogHeight();
                window.setAttributes(windowAttributes);
            }
        }
    }


    public interface IAnnouncementSetListener {
        void saveAnnouncement(String title, String content);
    }
}
