package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.view.View;

import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;

/**
 * 上麦场景
 */
public class MacScenePopWindow extends BaseBottomPop {
    private IModeScene iModeScene;

    public MacScenePopWindow(Context context, IModeScene iModeScene) {
        super(context);
        mContext = context;
        this.iModeScene = iModeScene;
        initUI();
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_mac_scene);
    }

    private void initUI() {
        findViewById(R.id.tv_mode_freedom).setOnClickListener(v -> {
            iModeScene.freedom();
            dismiss();
        });
        findViewById(R.id.tv_mode_sequence).setOnClickListener(v -> {
            iModeScene.scene();
            dismiss();
        });
    }


    @Override
    public void onDismiss() {
        super.onDismiss();

    }

    public interface IModeScene {
        void freedom();

        void scene();
    }
}
