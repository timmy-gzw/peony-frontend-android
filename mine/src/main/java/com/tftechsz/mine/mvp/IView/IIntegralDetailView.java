package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.ExchangeRecord;

import java.util.List;

public interface IIntegralDetailView extends  MvpView {

    void getIntegralDetailSuccess(List<ExchangeRecord> data);

    void getIntegralDetailFail(String data);

}
