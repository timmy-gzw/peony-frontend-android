package com.tftechsz.family.mvp.IView;

import com.tftechsz.family.entity.dto.ChangeMasterDto;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.entity.dto.FamilyMemberGroupDto;
import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.FamilyInactiveDto;
import com.tftechsz.common.entity.FamilyRoleDto;

import java.util.List;

public interface IFamilyMemberView extends MvpView {


    void getMemberSuccess(List<FamilyMemberGroupDto> data, int orderBy);

    void getChangeMasterSuccess(ChangeMasterDto data);

    void getChangeMasterNoticeSuccess(boolean data);

    void getRoleSuccess(int groupPosition, int childPosition, List<FamilyRoleDto> data, int userId, String name, int roleId);

    void setRoleSuccess(boolean data);

    void removeRoleSuccess(int groupPosition, int childPosition, int userId, boolean data);

    void getInactiveSuccess(List<FamilyInactiveDto> data);

    void getMuteMapSuccess(List<FamilyInactiveDto> data, int userId);

    void getMuteUserSuccess(boolean data);

    void clearInactiveUserSuccess(boolean data);

    void blackUserSuccess(int groupPosition, int childPosition, int userId, boolean data);


    void getFamilyRoleSuccess(FamilyIdDto data);

    void searchMemberSuccess(List<FamilyMemberDto> data);

    void batchOutSuccess();
}
