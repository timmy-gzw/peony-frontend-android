package com.tftechsz.party.widget.pop;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.party.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * 派对公告
 */
public class PartyAnnouncementPopWindow extends BasePopupWindow {
    private final TextView mTvTitle, mTvContent;

    public PartyAnnouncementPopWindow(Context context) {
        super(context);
        setBackground(R.color.transparent);
        mTvTitle = findViewById(R.id.tv_title);
        mTvContent = findViewById(R.id.tv_content);
    }

    /**
     * 设置公告信息
     */
    public void setAnnouncementInfo(String title, String content) {
        if (!TextUtils.isEmpty(title))
            mTvTitle.setText(title);
        if (!TextUtils.isEmpty(content))
            mTvContent.setText(content);
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_party_announcement);
    }
}
