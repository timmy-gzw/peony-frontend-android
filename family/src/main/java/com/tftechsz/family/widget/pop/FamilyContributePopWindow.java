package com.tftechsz.family.widget.pop;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import com.tftechsz.family.R;
import com.tftechsz.common.utils.Utils;

import razerdp.basepopup.BasePopupWindow;

/**
 * 贡献弹窗
 */
public class FamilyContributePopWindow extends BasePopupWindow implements View.OnClickListener {


    public FamilyContributePopWindow(Context context) {
        super(context);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {

        findViewById(R.id.tv_day).setOnClickListener(this);
        findViewById(R.id.tv_month).setOnClickListener(this);
        findViewById(R.id.tv_total).setOnClickListener(this);
    }

    @Override
    public View onCreateContentView() {
        View v = createPopupById(R.layout.pop_family_contribute);
        return v;
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return Utils.getTopScaleAnimation(0.9f, 0.0f, true);
        //return AnimationUtils.loadAnimation(mContext, R.anim.pop_center_enter_anim);
    }


    @Override
    protected Animation onCreateDismissAnimation() {
        return Utils.getTopScaleAnimation(0.9f, 0.0f, false);
        //return AnimationUtils.loadAnimation(mContext, R.anim.pop_center_exit_anim);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_day) {
            if (listener != null)
                listener.contributeChoose(1);
            dismiss();
        } else if (id == R.id.tv_month) {
            if (listener != null)
                listener.contributeChoose(2);
            dismiss();
        } else if (id == R.id.tv_total) {
            if (listener != null)
                listener.contributeChoose(3);
            dismiss();
        }
    }

    public interface OnSelectListener {
        void contributeChoose(int type);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
