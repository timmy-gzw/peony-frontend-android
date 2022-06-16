package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.im.R;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CountBackUtils;

import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.ScaleConfig;
import razerdp.util.animation.TranslationConfig;

/**
 *  挂断弹窗
 */
public class CallHangUpPopWindow extends BasePopupWindow implements View.OnClickListener {
    private CountBackUtils countBackUtils;
    private final int mType;    //0:未接听挂断   1接听后挂断
    private TextView tvHungUp, tvContent;
    private ConfigInfo configInfo;

    public CallHangUpPopWindow(Context context, int type) {
        super(context);
        mType = type;
        initUI();
    }

    private void initUI() {
        countBackUtils = new CountBackUtils();
        tvHungUp = findViewById(R.id.tv_hung_up);
        tvHungUp.setOnClickListener(this);
        findViewById(R.id.tv_continue).setOnClickListener(this);
        tvContent = findViewById(R.id.tv_content);
        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        configInfo = service.getConfigInfo();
    }


    public void startTime() {
        if (mType == 0) {
            if (configInfo != null && configInfo.sys != null && configInfo.sys.content != null)
                tvContent.setText(configInfo.sys.content.boy_call_before_text);
        } else {
            if (configInfo != null && configInfo.sys != null && configInfo.sys.content != null)
                tvContent.setText(configInfo.sys.content.boy_call_after_text);
        }
        tvHungUp.setText("3s");
        countBackUtils.countBack(3, new CountBackUtils.Callback() {
            @Override
            public void countBacking(long time) {
                tvHungUp.setText(time + "s");
                tvHungUp.setEnabled(false);
            }

            @Override
            public void finish() {
                tvHungUp.setText("残忍挂断");
                tvHungUp.setEnabled(true);
            }
        });
    }


    @Override
    public View onCreateContentView() {
        View v = createPopupById(R.layout.pop_call_hang_up);
        return v;
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationHelper.asAnimation()
                .withScale(ScaleConfig.CENTER)
                .toShow();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationHelper.asAnimation()
                .withScale(ScaleConfig.CENTER)
                .toDismiss();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        countBackUtils.destroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_hung_up) {
            if (listener != null)
                listener.hangUp();
            dismiss();
        } else if (id == R.id.tv_continue) {
            dismiss();
        }
    }

    public interface OnSelectListener {
        void hangUp();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
