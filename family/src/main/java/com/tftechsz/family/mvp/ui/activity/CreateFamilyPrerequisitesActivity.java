package com.tftechsz.family.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.blankj.utilcode.util.ConvertUtils;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.family.R;
import com.tftechsz.family.adapter.CreateFamilyFunctionAdapter;
import com.tftechsz.family.databinding.ActCreateFamilyPrerequisitesBinding;
import com.tftechsz.family.entity.dto.CreateFamilyFunctionDto;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 包 名 : com.tftechsz.family.mvp.ui.activity
 * 描 述 : 创建家族前置条件
 */
public class CreateFamilyPrerequisitesActivity extends BaseMvpActivity implements View.OnClickListener {
    @Autowired
    UserProviderService service;
    private ActCreateFamilyPrerequisitesBinding mBind;
    private List<CreateFamilyFunctionDto> mList = new ArrayList<>();

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_create_family_prerequisites);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true).setTitle("创建家族").build();
        mBind.tvRealStatus.setOnClickListener(this);
        mBind.tvSelfStatus.setOnClickListener(this);
        mBind.tvNext.setOnClickListener(this);
        initStatus();

        mBind.recy.setLayoutManager(new GridLayoutManager(this, 3));
        mList.add(new CreateFamilyFunctionDto(R.mipmap.icon_jz_mflt, "免费聊天"));
        mList.add(new CreateFamilyFunctionDto(R.mipmap.icon_jz_cdhb, "超多红包"));
        mList.add(new CreateFamilyFunctionDto(R.mipmap.icon_jz_zshd, "专属活动"));
        mList.add(new CreateFamilyFunctionDto(R.mipmap.icon_jz_sxf, "高调晒幸福"));
        mList.add(new CreateFamilyFunctionDto(R.mipmap.icon_jz_glqx, "管理权限"));
        mList.add(new CreateFamilyFunctionDto(R.mipmap.icon_jz_kstd, "快速脱单"));
        CreateFamilyFunctionAdapter adapter = new CreateFamilyFunctionAdapter(mList);
        mBind.recy.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBind.tvRealStatus.postDelayed(this::initStatus, 300);
    }

    public void initStatus() {
        UserInfo userInfo = service.getUserInfo();
        mBind.tvRealStatus.setCompoundDrawablePadding(ConvertUtils.dp2px(userInfo.isReal() ? 5 : 0));
        mBind.tvSelfStatus.setCompoundDrawablePadding(ConvertUtils.dp2px(userInfo.isSelf() ? 5 : 0));
        mBind.setIsReal(userInfo.isReal());
        mBind.setIsSelf(userInfo.isSelf());
        mBind.setIsNext(userInfo.isReal() && userInfo.isSelf());
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_real_status) { //真人去认证
            mCompositeDisposable.add(RetrofitManager.getInstance().createUserApi(PublicService.class)
                    .getRealInfo().compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<RealStatusInfoDto>>() {
                        @Override
                        public void onSuccess(BaseResponse<RealStatusInfoDto> response) {
                            performReal(response.getData());
                        }
                    }));
        } else if (id == R.id.tv_self_status) { //实名去认证
            if (!mBind.getIsReal()) {
                toastTip("请先完成真人认证!");
                return;
            }
            mCompositeDisposable.add(RetrofitManager.getInstance().createUserApi(PublicService.class)
                    .getSelfInfo().compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<RealStatusInfoDto>>() {
                        @Override
                        public void onSuccess(BaseResponse<RealStatusInfoDto> response) {
                            performSelf(response.getData());
                        }
                    }));
        } else if (id == R.id.tv_next) { //下一步
            if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.family != null) {
                BaseWebViewActivity.start(this, "", service.getConfigInfo().sys.family.create_protocol_h5, 0, 0);
                finish();
            }
        }
    }

    private void performReal(RealStatusInfoDto response) { //用户审核状态 -1.未认证,0.等待审核，1.审核完成，2.审核驳回
        Utils.isOpenAuth(data -> {
            if (data) {
                if (response.status == -1 || response.status == 2) {
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_REAL_AUTHENTICATION_NEW);
                } else if (response.status == 0) {
                    Utils.toast(com.tftechsz.common.R.string.auditing);
                } else {
                    mBind.setIsReal(true);
                    mBind.tvRealStatus.setCompoundDrawablePadding(ConvertUtils.dp2px(5));
                }
            } else {
                if (response.status == -1) {
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_REAL_AUTHENTICATION);
                } else if (response.status == 0 || response.status == 2) {
                    ARouterUtils.toRealAuthentication(response.status, null, response);
                } else {
                    mBind.tvRealStatus.setCompoundDrawablePadding(ConvertUtils.dp2px(5));
                    mBind.setIsReal(true);
                }
            }
        });
    }

    // //用户审核状态 -1.未认证,0.等待审核，1.审核完成，2.审核驳回
    public void performSelf(RealStatusInfoDto data) {
        if (data == null) {
            return;
        }
        Utils.isOpenAuth(datas -> {
            if (datas) {
                if (data.status == -1 || data.status == 2) {
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_REAL);
                } else if (data.status == 0) {
                    //ARouterUtils.toRealSucessActivity(data.status);
                } else {
                    mBind.setIsSelf(true);
                    mBind.setIsNext(!mBind.tvRealStatus.isEnabled());
                    mBind.tvSelfStatus.setCompoundDrawablePadding(ConvertUtils.dp2px(5));
                }
            } else {
                if (data.status == -1) {
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_REAL);
                } else if (data.status == 0 || data.status == 2) {
                    ARouterUtils.toRealSuccessActivity(data.status);
                } else {
                    mBind.setIsSelf(true);
                    mBind.setIsNext(!mBind.tvRealStatus.isEnabled());
                    mBind.tvSelfStatus.setCompoundDrawablePadding(ConvertUtils.dp2px(5));
                }
            }
        });
    }
}
