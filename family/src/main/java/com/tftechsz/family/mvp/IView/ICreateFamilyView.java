package com.tftechsz.family.mvp.IView;

import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.common.base.MvpView;

public interface ICreateFamilyView extends MvpView {


    void createFamilySuccess(FamilyInfoDto data);

    void uploadAvatarSuccess(String path);
}
