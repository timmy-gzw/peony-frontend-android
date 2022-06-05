package com.tftechsz.moment.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.CircleBean;
import com.tftechsz.moment.mvp.entity.NoticeBean;

import java.util.List;

public

/**
 * 包 名 : com.tftechsz.moment.mvp.IView
 * 描 述 : TODO
 */
interface INoticeView  extends MvpView {
    void getNoticeSucess(List<NoticeBean> data);

    void getNoticeFial();

    void commentSuccess(Boolean data);

    void getInfoSucess(CircleBean data);

    void getInfoFial(String msg);
}
