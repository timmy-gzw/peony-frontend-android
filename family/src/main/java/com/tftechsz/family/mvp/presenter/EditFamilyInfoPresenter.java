package com.tftechsz.family.mvp.presenter;

import android.text.InputFilter;
import android.widget.EditText;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.mvp.IView.IEditFamilyInfoView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.EditTextEnterFilter;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CustomFilter;

public class EditFamilyInfoPresenter extends BasePresenter<IEditFamilyInfoView> {

    public FamilyApiService service;

    public EditFamilyInfoPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }

    /**
     * 更新家族信息
     */
    public void editFamilyInfo(String field, String nickname) {
        addNet(service.editFamilyInfo(field, nickname).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().editFamilyInfoSuccess(nickname);
                    }
                }));
    }


    /**
     * 解散家族
     */
    public void dissolutionFamily(String message) {
        addNet(service.dissolutionFamily(message).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().dissolutionFamilySuccess(response.getData());
                    }
                }));
    }

    /**
     * 退出家族
     */
    public void leave(String message) {
        addNet(service.leave(message).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || null == response.getData()) return;
                        getView().exitFamilySuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 修改语音房公告
     */
    public void editFile(String file, String name) {
        mCompositeDisposable.add(service.editFile(file, name)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (response.getData() != null) {
                            Utils.toast("修改成功");
                            getView().closeVoice(name);
                        }
                    }
                }));

    }

    public void setLength(EditText editText, int length) {
        setLength(editText, length, false);
    }

    public void setLength(EditText editText, int length, boolean isFilter) {
        editText.setFilters(new InputFilter[]{new CustomFilter(length), new EditTextEnterFilter(isFilter)});
    }

}
