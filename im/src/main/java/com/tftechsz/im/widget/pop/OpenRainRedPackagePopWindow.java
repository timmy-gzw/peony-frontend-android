package com.tftechsz.im.widget.pop;

import android.app.Activity;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.im.R;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.pop.BaseCenterPop;

public class OpenRainRedPackagePopWindow extends BaseCenterPop implements View.OnClickListener {

    private final ImageView mIvGet, mIvAvatar, mIvRainTop;
    private final Activity context;
    private final TextView mTvContent, mTvTitle;
    private final ChatMsg.Rain mData;

    public OpenRainRedPackagePopWindow(Activity context, ChatMsg.Rain mData) {
        super(context);
        this.mData = mData;
        this.context = context;
        mIvAvatar = findViewById(R.id.iv_avatar);
        mIvGet = findViewById(R.id.iv_get);
        mIvRainTop = findViewById(R.id.iv_rain_top);
        mTvContent = findViewById(R.id.tv_content);
        mTvTitle = findViewById(R.id.tv_title);
        mIvGet.setOnClickListener(this);
        initData();
        setOutSideDismiss(false);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null)
                    listener.onDismiss();
            }
        });
    }


    private void initData() {
        GlideUtils.loadRouteImage(context, mIvAvatar, mData.icon);
        String s = "共抢到" + mData.coin + "金币";
        mTvTitle.setText(mData.name);
        if (mData.coin == 0) {
            mTvContent.setText("很遗憾，未能抢到红包");
            mIvGet.setImageResource(R.mipmap.chat_ic_i_know);
            mIvRainTop.setVisibility(View.INVISIBLE);
        } else {
            SpannableString ss1 = new SpannableString(s);
            int start = s.indexOf(mData.coin + "");
            ss1.setSpan(new RelativeSizeSpan(2f), start, start + ("" + mData.coin).length(), 0);
            mTvContent.setText(ss1);
            mIvGet.setImageResource(R.mipmap.chat_ic_happy_accept);
            mIvRainTop.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_open_rain_red_package);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_get) {
            dismiss();
        }

    }

    public interface OnSelectListener {
        void onDismiss();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
