package com.tftechsz.mine.widget.pop;

import android.content.Context;
import android.view.View;

import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.mine.R;

/**
 *  守护弹窗
 */
public class GuardPopWindow extends BaseCenterPop implements View.OnClickListener {


    private PrivacyListener privacyListener;

    public GuardPopWindow(Context context) {
        super(context);
        mContext = context;
        initUI();
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_guard);
    }

    private void initUI() {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_agree) {
            if(privacyListener!=null){
                privacyListener.agree();
            }
            dismiss();
        } else if (id == R.id.tv_cancel) {
            dismiss();
        }
    }

    public void setPrivacyListener(PrivacyListener privacyListener){
        this.privacyListener = privacyListener;
    }

    public interface PrivacyListener{
        void agree();
    }

}
