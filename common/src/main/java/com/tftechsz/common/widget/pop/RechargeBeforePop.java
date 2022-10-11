package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.R;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;

public class RechargeBeforePop extends BaseBottomPop {

    UserProviderService service;

    public RechargeBeforePop(Context context) {
        super(context);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        initView();
    }

    private void initView() {
        ConfigInfo configInfo = service.getConfigInfo();
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.tv_recharge).setOnClickListener(v -> {
            if (listener != null)
                listener.recharge();
            dismiss();
        });
        if(CommonUtil.isGa()){
            findViewById(R.id.cl_get_coin).setVisibility(View.GONE);
        }
        findViewById(R.id.tv_task).setOnClickListener(v -> {
            if (null != configInfo && configInfo.share_config != null && configInfo.share_config.my != null) {
                for (int i = 0; i < configInfo.share_config.my.size(); i++) {
                    if (configInfo.share_config.my.get(i).link.contains(Interfaces.LINK_PEONY_NEWTASK))
                        CommonUtil.performLink(mContext, configInfo.share_config.my.get(i), i, 0);
                }
            }
            dismiss();
        });
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_recharge_before);
    }


    public interface OnSelectListener {
        void recharge();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

}
