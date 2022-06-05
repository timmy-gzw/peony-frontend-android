package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.netease.lava.nertc.sdk.NERtcEx;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;

/**
 * 声音设置
 */
public class VoiceSettingPop extends BaseBottomPop {

    private ImageView mIvEarphone;

    public VoiceSettingPop(Context context) {
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
                NERtcEx.getInstance().enableEarback(true,100);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    public interface IOnSpecialEffectsListener {
        void specialEffectsListener(int type, int status);
    }
}
