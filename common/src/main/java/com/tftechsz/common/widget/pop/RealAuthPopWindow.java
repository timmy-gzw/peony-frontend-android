package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.R;
import com.tftechsz.common.adapter.RealPopAdapter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 *  弹出实名认证弹窗
 */
public class RealAuthPopWindow extends BaseCenterPop implements View.OnClickListener {

    private String popType = Interfaces.SHOW_IS_REAL; //默认真人认证
    private RecyclerView mRealRecy;

    public RealAuthPopWindow(Context context) {
        super(context);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_real_auth);
    }

    private void initUI() {
        RelativeLayout rootSelf = findViewById(R.id.self_root);
        ImageView topbg = findViewById(R.id.top_bg);
        TextView real_btn = findViewById(R.id.real_btn);
        mRealRecy = findViewById(R.id.real_recy);

        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        ConfigInfo configInfo = service.getConfigInfo();
        if (null != configInfo && null != configInfo.sys && null != configInfo.sys.content)
            switch (popType) {
                case Interfaces.SHOW_IS_REAL: //显示真人pop
                case Interfaces.SHOW_IS_PARTY_SELF: //派对实名
                    findViewById(R.id.real_root).setVisibility(View.VISIBLE);
                    TextView realHint = findViewById(R.id.real_hint);
                    findViewById(R.id.iv_del).setVisibility(View.VISIBLE);
                    if (TextUtils.equals(popType, Interfaces.SHOW_IS_REAL)) {
                        topbg.setImageResource(R.mipmap.peony_zrrztc_tb_png);
                        real_btn.setText("真人认证");
                        addRecyData(configInfo.sys.content.real_icon);
                        realHint.setText(configInfo.sys.content.real_warn);
                    } else if (TextUtils.equals(popType, Interfaces.SHOW_IS_PARTY_SELF)) {
                        real_btn.setText("实名认证");
                        topbg.setImageResource(R.mipmap.peony_zrrztc_tb);
                        addRecyData(configInfo.sys.content.party_self_icon);
                        realHint.setText(configInfo.sys.content.party_self_warn);
                    }
                    break;

                case Interfaces.SHOW_IS_SELF:
                    rootSelf.setVisibility(View.VISIBLE);
                    findViewById(R.id.img_red).setVisibility(View.GONE);
                    TextView tvTitle = findViewById(R.id.tv_title);
                    tvTitle.setText(configInfo.sys.content.before_girl_to_boy_not_self);
                    break;
            }
        ImageView ivAvatar = findViewById(R.id.iv_avatar);
        findViewById(R.id.iv_del).setOnClickListener(this);
        findViewById(R.id.real_btn).setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.tv_real_name).setOnClickListener(this);
        GlideUtils.loadRoundImage(mContext, ivAvatar, service.getUserInfo().getIcon(), service.getUserInfo().getSex() == 1 ? R.mipmap.mine_ic_boy_default : R.mipmap.mine_ic_girl_default);
    }

    private void addRecyData(List<ConfigInfo.RealIcon> real_icon) {
        if (real_icon != null && real_icon.size() > 0) {
            int size = real_icon.size();
            mRealRecy.setLayoutManager(new GridLayoutManager(getContext(), Math.min(size, 4)));
            RealPopAdapter adapter = new RealPopAdapter(real_icon);
            mRealRecy.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_real_name || id == R.id.real_btn) {
            switch (popType) {
                case Interfaces.SHOW_IS_REAL:
                    getRealInfo();
                    break;

                case Interfaces.SHOW_IS_SELF:
                case Interfaces.SHOW_IS_PARTY_SELF:
                    getSelfInfo();
                    break;

            }
        } else if (id == R.id.iv_close || id == R.id.iv_del) {
            dismiss();
        }
    }

    /* ** 获取是否实名认证*/
    private void getSelfInfo() {
        MineService mineService = ARouter.getInstance().navigation(MineService.class);
        mineService.getSelfInfo(new ResponseObserver<BaseResponse<RealStatusInfoDto>>() {
            @Override
            public void onSuccess(BaseResponse<RealStatusInfoDto> response) {
                if (null != response.getData()) {   //用户审核状态 -1.未认证,0.等待审核，1.审核完成，2.审核驳回
                    if (TextUtils.equals(popType, Interfaces.SHOW_IS_PARTY_SELF)) {
                        CommonUtil.performSelf(response.getData(), ARouterApi.ACTIVITY_SELF_CHECK, popType);
                    } else {
                        CommonUtil.performSelf(response.getData());
                    }
                    dismiss();
                }
            }
        });
    }


    /**
     * 获取是否真人认证
     */
    public void getRealInfo() {
        MineService mineService = ARouter.getInstance().navigation(MineService.class);
        mineService.getRealInfo(new ResponseObserver<BaseResponse<RealStatusInfoDto>>() {
            @Override
            public void onSuccess(BaseResponse<RealStatusInfoDto> response) {
                if (null != response.getData()) {   //用户审核状态 -1.未认证,0.等待审核，1.审核完成，2.审核驳回
                    CommonUtil.performReal(response.getData());
                    dismiss();
                }
            }
        });
    }

    public void setPopType(String popType) {
        this.popType = popType;
        initUI();
    }
}
