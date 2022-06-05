package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.AccostSettingListBean;

import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.IView
 * 描 述 : 自定义招呼view
 */
public interface IAccostSettingView extends MvpView {

    void getAccostListSuccess(List<AccostSettingListBean> data);

    void addAccostSettingSuccess();

    void addAccostSettingError();

    void updateAccostSettingSuccess(int position);

    void delAccostSettingSuccess(int position);

}
