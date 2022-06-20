package com.tftechsz.main.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.main.R;

/**
 * 挽留弹窗
 */
public class RetainNoMessagePopWindow extends BaseCenterPop implements View.OnClickListener {

    private final TextView tvTip;

    public RetainNoMessagePopWindow(Context context) {
        super(context);
        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        tvTip = findViewById(R.id.tv_tip);
        findViewById(R.id.tv_quit).setOnClickListener(this);
        findViewById(R.id.tv_stay).setOnClickListener(this);
        if (service.getUserInfo() != null && service.getUserInfo().isBoy()) {
            tvTip.setText("小哥哥要离开了吗，嘤嘤嘤～");
        } else {
            tvTip.setText("小姐姐要离开了吗，嘤嘤嘤～");
        }
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_retain_no_message);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.tv_quit) {
            dismiss();
            AppManager.getAppManager().finishAllActivity();
        } else if (id == R.id.tv_stay) {
            dismiss();
        }

    }
}
