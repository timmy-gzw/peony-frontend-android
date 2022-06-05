package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.DressListDto;

import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.IView
 * 描 述 : TODO
 */
public interface IDressUpView extends MvpView {
    void getCategoryListSuccess(List<DressListDto> data);

    void getCategoryListError();
}
