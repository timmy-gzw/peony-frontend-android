package com.tftechsz.family.mvp.IView;

import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.common.event.RecruitBaseDto;
import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.FamilyInviteBean;

public interface IFamilyDetailView extends MvpView {

    void getFamilyDetailSuccess(FamilyInfoDto data);

    void getConditionSuccess(FamilyIdDto data);

    void applySuccess(String data);

    void applyLeaveSuccess(Boolean data);

    void updateFamilyIconSuccess(Boolean data);

    void familySignSuccess(Boolean data);

    void getFamilyInviteSuccess(FamilyInviteBean data);

    void muteAllSuccess(Boolean data,int mute);

    void getRecruitBaseSuccess(RecruitBaseDto data);
}
