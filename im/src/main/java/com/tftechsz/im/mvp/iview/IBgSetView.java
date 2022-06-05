package com.tftechsz.im.mvp.iview;

import com.tftechsz.im.model.BgSetBean;
import com.tftechsz.common.base.MvpView;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * 包 名 : com.tftechsz.im.mvp.iview
 * 描 述 : TODO
 */
public interface IBgSetView extends MvpView {
    void getBgConfigSuccess(List<BgSetBean> data);

    void setBgSuccess(@Nullable String bg);
}
