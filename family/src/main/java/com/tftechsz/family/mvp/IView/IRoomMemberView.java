package com.tftechsz.family.mvp.IView;

import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.common.base.MvpView;

import java.util.List;

public interface IRoomMemberView extends MvpView {


    void getMemberSuccess(List<FamilyMemberDto> data);



}
