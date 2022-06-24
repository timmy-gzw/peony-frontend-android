package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tftechsz.common.R;

public class MatchPopWindow extends BaseCenterPop{

    private TextView mTvTitle,mTvContent,mTvSure;
    private View.OnClickListener closeListener;

    public MatchPopWindow(Context context,View.OnClickListener closeListener) {
        super(context);
        this.closeListener = closeListener;
        initUI();
    }

    private void initUI() {
        mTvTitle = findViewById(R.id.tv_title);
        mTvContent = findViewById(R.id.tv_content);
        findViewById(R.id.iv_close).setOnClickListener(v->{
            dismiss();
            if(null != closeListener){
                closeListener.onClick(v);
            }
        });
        mTvSure = findViewById(R.id.tv_sure);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_match);
    }

    public MatchPopWindow setTitle(String s){
        this.mTvTitle.setText(s);
        return this;
    }

    public MatchPopWindow setContent(String s){
        this.mTvContent.setText(s);
        return this;
    }

    public MatchPopWindow setSure(String s){
        this.mTvSure.setText(s);
        return this;
    }

    public MatchPopWindow onSureClickListener(View.OnClickListener listener){
        this.mTvSure.setOnClickListener(v->{
            dismiss();
            listener.onClick(v);
        });
        return this;
    }
}
