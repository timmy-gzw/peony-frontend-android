package com.tftechsz.party.mvp.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.EditAuditFieldDto;
import com.tftechsz.party.entity.PartyEditBean;
import com.tftechsz.party.entity.PartyManagerBgBean;
import com.tftechsz.party.mvp.IView.IPartySettingView;

import java.io.File;
import java.util.List;

/**
 * 派对设置
 */
public class PartySettingPresenter extends BasePresenter<IPartySettingView> {

    public PartyApiService service;

    public PartySettingPresenter() {
        service = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
    }

    public void getEditParty(int partyRoomId) {
        addNet(service.getPartyEdit(partyRoomId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<PartyEditBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartyEditBean> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().editData(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);

                    }
                }));

    }

    public void getEditPartyBg() {
        addNet(service.getPartyEditBg().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<PartyManagerBgBean>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<PartyManagerBgBean>> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().addBgs(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);

                    }
                }));

    }

    /**
     * icon_value	string	是	封面
     * room_name	string	是	房间名称
     * fight_pattern	int	是	连麦模式 1：普通模式 2：PK模式
     * is_mute	int	是	关闭公屏， 0 不关闭，1关闭
     * microphone_pattern	int	是	上麦模式，1：自由模式 2：麦序模式
     * announcement	string	是	公告
     * title	string	是	公告title
     * bg_icon_value	string	是	背景图
     */
    public void commitEditParty(/*String icon_value, String room_name,*/ int fight_pattern,
                                int is_mute, int microphone_pattern/*, String announcement
            , String title*/, String bg_icon_value, int party_room_id) {
        addNet(service.editParty(/*icon_value, room_name, */fight_pattern, is_mute, microphone_pattern, /*announcement, title,*/ bg_icon_value, party_room_id).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView() || response.getData() == null) return;
                        if (!TextUtils.isEmpty(response.getData()) && getView().getContext() != null) {
                            ToastUtil.showToast(getView().getContext(), response.getData());
                        }
                        getView().commit();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);

                    }
                }));

    }

    /**
     * 关闭派对
     */
    public void closeParty(int party_room_id) {
        addNet(service.closeParty(party_room_id).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().closePartySuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);

                    }
                }));

    }

    /**
     * 管理员同意用户上麦申请
     * "room_id": "1",         // 房间ID
     * * "id": "1",              // 申请ID
     * * "to": "1",
     */
    public void agreeUserSequence(int room_id, int id, int to) {
        addNet(service.agreeUserSequence(room_id, id, to).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().closePartySuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);

                    }
                }));

    }


    /**
     * 上传封面
     */
    public void uploadAvatar(String path) {
        getView().showLoadingDialog();
        File file = new File(path);
        UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_USER_TYPE, UploadHelper.PATH_USER_PARTY, UploadHelper.TYPE_IMAGE, file, new UploadHelper.OnUploadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onLoading(long cur, long total) {

            }

            @Override
            public void onSuccess(String url) {
                getView().hideLoadingDialog();
                getView().setImgCover(path, url);

            }

            @Override
            public void onError() {
                if (null == getView()) return;
                getView().hideLoadingDialog();
            }
        });

    }

    /**
     * 修改派对名称
     */
    public void updatePartyName(EditAuditFieldDto editAuditFieldDto, int id, Context context, EditAuditFieldDto.EditAuditFieldDtoInner inner
            , EditAuditFieldDto.EditAuditFieldImgDtoInner editAuditFieldImgDtoInner) {
        addNet(service.editAuditField(new Gson().toJson(editAuditFieldDto == null ? (inner == null ? editAuditFieldImgDtoInner : inner) : editAuditFieldDto), id).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView() || response.getData() == null) return;
                        if (!TextUtils.isEmpty(response.getData())) {
                            ToastUtil.showToast(context, response.getData());
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);

                    }
                }));

    }

    /**
     * 派对审核信息检查
     */
    public void checkAuditing(int id, String field,int tag) {
        addNet(service.checkAuditing(id, field).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() ) return;
                     getView().checkStatusCallBack(tag);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        getView().checkStatusCallBack(-1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getView().checkStatusCallBack(-1);
                    }
                }));

    }
}
