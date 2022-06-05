package com.tftechsz.family.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.UserShareDto;

/**
 * 包 名 : com.tftechsz.family.mvp.IView
 * 描 述 : TODO
 */
public interface IShareView extends MvpView {
    void getUserShareListSuccess(UserShareDto data);

    void getUserShareListError();
}
