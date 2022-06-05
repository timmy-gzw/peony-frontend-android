package com.tftechsz.im.mvp.iview;

import com.tftechsz.im.model.dto.CallLogDto;
import com.tftechsz.common.base.MvpView;

import java.util.List;

/**
 * 包 名 : com.tftechsz.im.mvp.iview
 * 描 述 : TODO
 */
public interface ICallLogView  extends MvpView {
    void getCallListSuccess(List<CallLogDto> data);

    void getCallListError();
}
