package com.tftechsz.im.mvp.presenter;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.mvp.iview.IChatSettingView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.AttentionService;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.pop.CustomPopWindow;

public class ChatSettingPresenter extends BasePresenter<IChatSettingView> {

    public ChatApiService service;
    AttentionService attentionService;

    public ChatSettingPresenter() {
        service = RetrofitManager.getInstance().createUserApi(ChatApiService.class);
        attentionService = ARouter.getInstance().navigation(AttentionService.class);
    }

    /**
     * 获取是否关注了
     */
    public void getIsAttention(int id) {
        attentionService.getIsAttention(id, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (getView() == null) return;
                getView().getIsAttentionSuccess(response.getData());
            }
        });

    }

    /**
     * 关注用户
     */
    public void attentionUser(int id) {
        attentionService.attentionUser(id, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (getView() == null) return;

                getView().attentionSuccess(response.getData());
            }
        });
    }


    public void showBlackPop(Context context, int userId) {
        CustomPopWindow pop = new CustomPopWindow(context);
        pop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onSure() {
                blackUser(userId);
            }
        });
        pop.setTitle("确定要拉黑TA吗");
        pop.setContent("拉黑后你将收不到对方的消息和呼叫，且在好友列表互相看不到对方");
        pop.showPopupWindow();
    }

    /**
     * 拉黑用户
     */
    public void blackUser(int id) {
        attentionService.blackUser(id, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (getView() == null) return;
                getView().blackSuccess(response.getData());
            }
        });
    }


    /**
     * 拉黑用户
     */
    public void cancelBlack(int id) {
        addNet(service.cancelBlack(id).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (getView() == null) return;
                getView().cancelBlackSuccess(response.getData());
            }
        }));
    }


}
