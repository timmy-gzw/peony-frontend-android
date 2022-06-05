package com.tftechsz.family.mvp.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.tftechsz.family.R;
import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyConfigDto;
import com.tftechsz.family.entity.req.JoinRuleReq;
import com.tftechsz.family.mvp.IView.IChangeJoinConditionView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

import java.util.List;

public class ChangeJoinConditionPresenter extends BasePresenter<IChangeJoinConditionView> {

    public FamilyApiService service;
    public FamilyApiService serviceConfig;

    public ChangeJoinConditionPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
        serviceConfig = RetrofitManager.getInstance().createConfigApi(FamilyApiService.class);
    }

    /**
     * 获取加入条件
     */
    public void getJoinRule() {
        addNet(service.getJoinRule().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<JoinRuleReq>>() {
                    @Override
                    public void onSuccess(BaseResponse<JoinRuleReq> response) {
                        if (null == getView()) return;
                        getView().getChangeJoinRule(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                    }
                }));
    }

    /**
     * 更改条件
     */
    public void changeJoinRule(JoinRuleReq data) {
        addNet(service.joinRule(data).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().changeJoinRuleSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                    }
                }));
    }


    /**
     * 获取家族配置信息
     */
    public void getFamilyConfig() {
        addNet(serviceConfig.getFamilyConfig().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<FamilyConfigDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyConfigDto> response) {
                        if (null == getView()) return;
                        getView().getFamilyConfigSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                    }
                }));
    }


    /**
     * 魅力等级
     * type 1: 男 2？：女
     */
    public void chooseLevel(Context context,int type, List<String> list, RelativeLayout relativeLayout,String level) {
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.equals(level, list.get(i))) {
                position = i;
            }
        }
        OptionsPickerView  pvLevel = new OptionsPickerBuilder(context, (options1, option2, options3, v) -> {
            if (null == getView()) return;
            getView().getChooseLevel(type,list.get(options1));
        })
                .setDecorView(relativeLayout)
                .build();
        pvLevel.setTitleText(type == 1 ? "男性财富等级最低" : "女性魅力等级最低");
        pvLevel.setSelectOptions(position);
        pvLevel.setPicker(list);
        pvLevel.show();
    }

    /**
     * 设置图片
     *
     * @param tv1
     * @param tv2
     */
    public void setDrawable(Context context, TextView tv1, TextView tv2,TextView tv3) {
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.family_ic_check_selector);
        Drawable drawable1 = ContextCompat.getDrawable(context, R.mipmap.family_ic_check_normal);
        tv1.setCompoundDrawablesWithIntrinsicBounds(drawable,
                null, null, null);
        tv2.setCompoundDrawablesWithIntrinsicBounds(drawable1,
                null, null, null);
        tv3.setCompoundDrawablesWithIntrinsicBounds(drawable1,
                null, null, null);
    }

}
