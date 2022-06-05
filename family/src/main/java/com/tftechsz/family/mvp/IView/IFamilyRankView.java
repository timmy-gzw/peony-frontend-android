package com.tftechsz.family.mvp.IView;

import com.tftechsz.family.entity.dto.FamilyRankDto;
import com.tftechsz.common.base.MvpView;

public interface IFamilyRankView extends MvpView {

    void getFamilyRankSuccess(FamilyRankDto data);


}
