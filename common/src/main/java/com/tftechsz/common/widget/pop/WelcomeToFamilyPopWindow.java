package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;

import com.tftechsz.common.R;
import com.tftechsz.common.utils.ClickUtil;

/**
 * 包 名 : com.tftechsz.common.widget.pop
 * 描 述 : 家族欢迎pop
 */
public class WelcomeToFamilyPopWindow extends BaseBottomPop {
    private CallBack mCallBack;

    public WelcomeToFamilyPopWindow(Context context, CallBack callBack) {
        super(context);
        mCallBack = callBack;
        setOutSideDismiss(false);
        initUI();
    }

    private void initUI() {
        findViewById(R.id.welcome_btn).setOnClickListener(v -> {
            if (!ClickUtil.canOperate()) {
                return;
            }
            mCallBack.click();
        });
        findViewById(R.id.del).setOnClickListener(v -> dismiss());
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_welcome_to_family);
    }

    public interface CallBack {
        void click();
    }
}
