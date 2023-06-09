package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.LogoutStatusDto;

/**
 * 包 名 : com.tftechsz.mine.mvp.IView
 * 描 述 : TODO
 */
public interface ILogoutView extends MvpView {
    void getLogoutStatusSuccess(LogoutStatusDto data);

    void destroyAccountSuccess(LogoutStatusDto data);

    void undestroyAccountSuccess(String data);
}
