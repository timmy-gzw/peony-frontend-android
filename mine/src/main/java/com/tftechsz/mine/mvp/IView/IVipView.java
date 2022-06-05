package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.VipConfigBean;
import com.tftechsz.mine.entity.DressUpBean;
import com.tftechsz.mine.entity.VipPriceBean;
import com.tftechsz.mine.entity.VipPrivilegeBean;

import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.IView
 * 描 述 : TODO
 */
public interface IVipView extends MvpView {
    void getVipPriceSuccess(List<VipPriceBean> bean);

    void getVipPrivilegeSuccess(List<VipPrivilegeBean> bean);

    void getVipConfigSuccess(VipConfigBean data);

    void getDressUpSuccess(List<DressUpBean> data);

    void postVipConfigSuccess();
}
