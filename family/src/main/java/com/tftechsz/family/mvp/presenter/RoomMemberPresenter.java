package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.mvp.IView.IRoomMemberView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

import java.util.List;

public class RoomMemberPresenter extends BasePresenter<IRoomMemberView> {

    public FamilyApiService service;

    public RoomMemberPresenter() {
        service = RetrofitManager.getInstance().createChatRoomApi(FamilyApiService.class);
    }

    /**
     * 聊天室成员获取
     */
    public void getRoomMember(int page, String rid, boolean isShow) {
        addNet(service.getRoomMember(page, rid).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyMemberDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyMemberDto>> response) {
                        if (null == getView()) return;
                        getView().getMemberSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }



}
