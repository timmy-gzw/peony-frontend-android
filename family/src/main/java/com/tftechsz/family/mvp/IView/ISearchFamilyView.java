package com.tftechsz.family.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.FamilyIdDto;

public interface ISearchFamilyView extends  MvpView {


    void getSearchSuccess(FamilyIdDto data);



}
