package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.VisitorDto;


public interface IVisitorView extends MvpView {


    void getVisitorSuccess(VisitorDto data);

}
