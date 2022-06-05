package com.tftechsz.family.mvp.IView;

import com.tftechsz.family.entity.dto.FamilyConfigDto;
import com.tftechsz.family.entity.req.JoinRuleReq;
import com.tftechsz.common.base.MvpView;

public interface IChangeJoinConditionView extends MvpView {

    void changeJoinRuleSuccess(Boolean data);

    void getChangeJoinRule(JoinRuleReq data);

    void getChooseLevel(int type, String data);

    void getFamilyConfigSuccess(FamilyConfigDto data);

}
