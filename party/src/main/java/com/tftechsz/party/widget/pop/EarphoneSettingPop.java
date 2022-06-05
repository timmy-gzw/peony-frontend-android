package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.hjq.toast.ToastUtils;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.tftechsz.common.music.base.BaseMusicHelper;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;

/**
 * 耳反设置
 */
public class EarphoneSettingPop extends BaseBottomPop {
    private ImageView mIvEarphone;
    private int mHeadStatus = 0;
    private boolean mEarStatus = false;

    public EarphoneSettingPop(Context context) {
        super(context);
        initUI();
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_earphone_setting);
    }


    private void initUI() {
        mIvEarphone = findViewById(R.id.iv_earphone);
        mIvEarphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHeadStatus == 0) {
                    ToastUtils.show("使用耳机才能使用此功能");
                } else {
                    if (!mEarStatus) {
                        NERtcEx.getInstance().enableEarback(true, 100);
                        mEarStatus = true;
                        mIvEarphone.setImageResource(R.mipmap.ic_switch_selector);
                        if (listener != null)
                            listener.enableEarBack(true);
                    } else {
                        NERtcEx.getInstance().enableEarback(false, 0);
                        mEarStatus = false;
                        mIvEarphone.setImageResource(R.mipmap.ic_switch_normal);
                        if (listener != null)
                            listener.enableEarBack(false);
                    }
                }
            }
        });
    }

    public void setEnableEarBack(boolean earStatus) {
        this.mEarStatus = earStatus;
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
            mEarStatus = BaseMusicHelper.get().getPartyService().getEnableEarBack();
        }
        mIvEarphone.setImageResource(mEarStatus ? R.mipmap.ic_switch_selector : R.mipmap.ic_switch_normal);
    }

    public void setHeadStatus(int headStatus) {
        mHeadStatus = headStatus;
        if (mHeadStatus == 0) {
            mIvEarphone.setImageResource(R.mipmap.ic_switch_normal);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public interface OnSelectListener {
        void enableEarBack(boolean status);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }


}
