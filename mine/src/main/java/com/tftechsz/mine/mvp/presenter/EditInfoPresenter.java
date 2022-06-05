package com.tftechsz.mine.mvp.presenter;

import android.text.InputFilter;
import android.widget.EditText;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.widget.CustomFilter;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.mvp.IView.IEditInfoView;

import java.util.HashMap;

public class EditInfoPresenter extends BasePresenter<IEditInfoView> {

    public MineApiService service;

    public EditInfoPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }

    /**
     * 更新用户信息
     */
    public void updateUserInfo(int type, String nickname) {
        HashMap<String, String> map = new HashMap<>();
        if (type == 1) {
            map.put("nickname", nickname);
        } else if (type == 2) {
            map.put("job", nickname);
        } else if (type == 3) {
            map.put("desc", nickname);
        }
        addNet(service.updateUserInfo(createRequestBody(map)).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if(getView() == null) return;
                        getView().updateUserInfoSuccess(type,nickname);
                    }
                }));
    }


    public void setLength(EditText editText,int length){
        editText.setFilters(new InputFilter[]{new CustomFilter(length)});
    }

}
