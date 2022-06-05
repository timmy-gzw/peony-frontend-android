package com.tftechsz.family.mvp.IView;

import com.tftechsz.family.entity.dto.FamilyRecruitDto;
import com.tftechsz.common.base.MvpView;

import java.util.List;

/**
 * 包 名 : com.tftechsz.family.mvp.IView
 * 描 述 : TODO
 */
public interface IFamilyRecruitView extends MvpView {
    void getRecruitListSuccess(List<FamilyRecruitDto> data);

    void getRecruitListFail();
}
