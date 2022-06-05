package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.ChangeMasterDto;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.entity.dto.FamilyMemberGroupDto;
import com.tftechsz.family.mvp.IView.IFamilyMemberView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.FamilyInactiveDto;
import com.tftechsz.common.entity.FamilyRoleDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;

import java.util.List;

public class FamilyMemberPresenter extends BasePresenter<IFamilyMemberView> {

    public FamilyApiService service;

    public FamilyMemberPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }


    /**
     * 家族成员获取
     */
    public void getFamilyMember(int page, int familyId, int orderBy, boolean isShow) {
        addNet(service.getFamilyMember(page, familyId, orderBy).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyMemberGroupDto>>>(isShow ? getView() : null) {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyMemberGroupDto>> response) {
                        if (null == getView()) return;
                        getView().getMemberSuccess(response.getData(), orderBy);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 获取可以转让成员列表
     */
    public void getChangeMaster() {
        addNet(service.getChangeMaster().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyMemberGroupDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyMemberGroupDto>> response) {
                        if (null == getView()) return;
                        getView().getMemberSuccess(response.getData(), 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    public void getFamilyId(String tid) {
        mCompositeDisposable.add(service.getFamilyId(tid)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<FamilyIdDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyIdDto> response) {
                        if (null != response.getData()) {
                            getFamilyMember(1, response.getData().family_id, 0, true);
                        }
                    }
                }));
    }


    /**
     * 获取角色
     */
    public void getFamilyRole() {
        addNet(service.getFamilyRole()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<FamilyIdDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyIdDto> response) {
                        if (null != response.getData() && getView() != null) {
                            getView().getFamilyRoleSuccess(response.getData());
                        }
                    }
                }));
    }


    /**
     * 获取可以设置的角色
     */
    public void getRoleSet(int groupPosition, int childPosition, int userId, String name, int roleId) {
        addNet(service.getRole(userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyRoleDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyRoleDto>> response) {
                        if (null == getView()) return;
                        getView().getRoleSuccess(groupPosition, childPosition, response.getData(), userId, name, roleId);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 转让族长
     */
    public void changeMaster(int userId) {
        addNet(service.changeMaster(userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<ChangeMasterDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<ChangeMasterDto> response) {
                        if (null == getView()) return;
                        getView().getChangeMasterSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 转让族长
     */
    public void changeMasterNotice(int userId) {
        addNet(service.changeMasterNotice(userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().getChangeMasterNoticeSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 设置角色
     */
    public void setUserRole(int userId, int roleId) {
        addNet(service.setUserRole(userId, roleId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().setRoleSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 踢出用户
     */
    public void removeUser(int groupPosition, int childPosition, int userId) {
        addNet(service.removeUser(userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().removeRoleSuccess(groupPosition, childPosition, userId, response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 获取不活跃用户
     */
    public void getInactiveUser() {
        addNet(service.getInactiveUser().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyInactiveDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyInactiveDto>> response) {
                        if (null == getView()) return;
                        getView().getInactiveSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 获取禁用列表
     */
    public void getMuteMap(int userId) {
        addNet(service.getMuteMap().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyInactiveDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyInactiveDto>> response) {
                        if (null == getView()) return;
                        getView().getMuteMapSuccess(response.getData(), userId);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 拉黑
     */
    public void muteUser(int familyId, int userId, int type, int operation) {
        addNet(service.muteUser(familyId, userId, type, operation).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().getMuteUserSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 清楚不活跃用户
     */
    public void clearInactiveUser(int type) {
        addNet(service.clearInactiveUser(type).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().clearInactiveUserSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 拉黑
     */
    public void blackUser(int groupPosition, int childPosition, int familyId, int userId, int operation) {
        addNet(service.blackUser(familyId, userId, operation).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().blackUserSuccess(groupPosition, childPosition, userId, response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 搜索成员
     */
    public void member_list_search(int familyId, String content) {
        addNet(service.member_list_search(familyId, content).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyMemberDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyMemberDto>> response) {
                        if (null == getView()) return;
                        getView().searchMemberSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * T成员
     */
    public void batchOut(int familyId, String user_ids) {
        addNet(service.batchOut(familyId, user_ids).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().batchOutSuccess();
                    }
                }));
    }
}
