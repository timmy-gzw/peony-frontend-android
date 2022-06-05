package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.IntegralDto;

public interface IMineIntegralView extends  MvpView {


    void getIntegralSuccess(IntegralDto data);
}
