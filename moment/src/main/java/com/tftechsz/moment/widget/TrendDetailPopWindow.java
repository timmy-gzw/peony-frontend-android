package com.tftechsz.moment.widget;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.ReportPopWindow;
import com.tftechsz.moment.R;

import razerdp.basepopup.BasePopupWindow;

/**
 *  动态详情更多弹窗
 */
public class TrendDetailPopWindow extends BasePopupWindow implements View.OnClickListener {

    private final Context mContext;
    private final int blogId;
    private TextView mTvAttention;
    private int mIsFollow;

    public TrendDetailPopWindow(Context context, int blogId, int isFollow) {
        super(context);
        this.blogId = blogId;
        mContext = context;
        this.mIsFollow = isFollow;
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        findViewById(R.id.tv_report).setOnClickListener(this);
        findViewById(R.id.tv_attention).setOnClickListener(this);   //关注
        mTvAttention = findViewById(R.id.tv_attention);
        if (mIsFollow == 1) {
            mTvAttention.setText("取消关注");
        }else{
            mTvAttention.setText("关注");
        }
    }

    @Override
    public View onCreateContentView() {
        View v = createPopupById(R.layout.pop_trend_detail);
        return v;
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return Utils.getTopScaleAnimation(0.9f, 0.0f, true);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return Utils.getTopScaleAnimation(0.9f, 0.0f, false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_report) {
            ARouterUtils.toBeforeReportActivity(blogId, 2);
            dismiss();
        } else if (id == R.id.tv_attention) {  //关注
            if (listener != null)
                listener.attention();
            dismiss();
        }
    }

    public interface OnSelectListener {
        void attention();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
