package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.ChargeInfoDto;
import com.tftechsz.mine.entity.dto.ChargeItemDto;

import java.util.List;

public interface IChargeSettingView extends MvpView {


    void getChargeSuccess(ChargeInfoDto data);

    void updateChargeSuccess(String field,int value, Boolean data,String content);

    void getChargeItemSuccess(ChargeItemDto data);


    void selectPrice(List<String> list, ChargeItemDto chargeItem, int type, int position);

}
