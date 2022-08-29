package com.tftechsz.mine.mvp.presenter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.ChargeInfoDto;
import com.tftechsz.mine.entity.dto.ChargeItemDto;
import com.tftechsz.mine.mvp.IView.IChargeSettingView;

import java.util.ArrayList;
import java.util.List;

public class ChargeSettingPresenter extends BasePresenter<IChargeSettingView> {
    public MineApiService service;
    public MineApiService configService;

    public ChargeSettingPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        configService = RetrofitManager.getInstance().createConfigApi(MineApiService.class);
    }


    /**
     * 获取用户收费设置信息
     */
    public void getChargeInfo() {
        addNet(service.getChargeInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<ChargeInfoDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<ChargeInfoDto> response) {
                        if (null != getView())
                            getView().getChargeSuccess(response.getData());
                    }
                }));
    }

    /**
     * 更新用户收费设置信息
     */
    public void updateChargeInfo(String field, int value, String content) {
        addNet(service.updateChargeInfo(field, value).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null != getView())
                            getView().updateChargeSuccess(field,value, response.getData(), content);
                    }
                }));
    }


    /**
     * 用户收费价格ITEM
     */
    public void getChargeItem() {
        addNet(configService.getChargeItem().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<ChargeItemDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<ChargeItemDto> response) {
                        if (null != getView())
                            getView().getChargeItemSuccess(response.getData());
                    }
                }));
    }


    /**
     * 消息价格设置
     */
    public void messagePrice(Context context, ChargeItemDto chargeItem, int type, RelativeLayout relativeLayout, int coin) {
        if (chargeItem == null)
            return;
        List<String> list = new ArrayList<>();
        List<Integer> coins = new ArrayList<>();
        List<ChargeItemDto.ChargeItem> items = new ArrayList<>();
        if (type == 1) {
            items = chargeItem.msg;
        } else if (type == 2) {
            items = chargeItem.voice;
        } else if (type == 3) {
            items = chargeItem.video;
        }
        for (ChargeItemDto.ChargeItem item : items) {
            list.add(item.name);
            coins.add(item.coin);
        }
        //设置选择position
        int position = 0;
        if (coins.size() > 0)
            for (int i = 0; i < coins.size(); i++) {
                if (coins.get(i) == coin)
                    position = i;
            }
        OptionsPickerView mPvMessagePrice = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (null != getView())
                    getView().selectPrice(list, chargeItem, type, options1);
            }
        })
                .setDecorView(relativeLayout)
                .build();
        if (type == 1) {
            mPvMessagePrice.setTitleText("消息价格");
        } else if (type == 2) {
            mPvMessagePrice.setTitleText("语音价格");
        } else if (type == 3) {
            mPvMessagePrice.setTitleText("视频价格");
        }
        mPvMessagePrice.setPicker(list);
        mPvMessagePrice.setSelectOptions(position);
        mPvMessagePrice.show();
    }


}
