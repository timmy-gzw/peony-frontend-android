package com.tftechsz.mine.mvp.presenter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.TimeUtils;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.NearUserDto;
import com.tftechsz.mine.entity.req.CompleteReq;
import com.tftechsz.mine.mvp.IView.IImproveInfoView;
import com.tftechsz.mine.utils.UserManager;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ImproveInfoPresenter extends BasePresenter<IImproveInfoView> {

    public MineApiService service;

    public ImproveInfoPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }


    /**
     * 用户信息完善
     */
    public void completeInfo(CompleteReq pram) {
        if (getView() != null)
            getView().showLoadingDialog();
        CommonUtil.getToken(new com.tftechsz.common.utils.CommonUtil.OnSelectListener() {
            @Override
            public void onSure() {
                addNet(service.completeUserInfo(pram).compose(BasePresenter.applySchedulers())
                        .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                            @Override
                            public void onSuccess(BaseResponse<String> response) {
                                if (null == getView()) return;
                                getView().completeInfoSuccess(response.getData());
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                super.onFail(code, msg);
                                if (getView() != null)
                                    getView().hideLoadingDialog();
                            }
                        }));
            }
        });
    }


    /**
     * 第三方登录获取信息；
     */
    public void getCompleteInfo() {
        addNet(service.getCompleteUserInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<CompleteReq>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<CompleteReq> response) {
                        if (null == getView()) return;
                        getView().getCompleteInfoSuccess(response.getData());
                    }
                }));
    }


    public void uploadAvatar(String path) {
        if (getView() != null)
            getView().showLoadingDialog();
        if (path == null) {
            return;
        }
        File file = new File(path);
        UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_USER_TYPE, UploadHelper.PATH_USER_AVATAR, UploadHelper.TYPE_IMAGE, file, new UploadHelper.OnUploadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onLoading(long cur, long total) {

            }

            @Override
            public void onSuccess(String url) {
                if (null == getView()) return;
                getView().hideLoadingDialog();
                getView().uploadAvatarSucceeded(url);
            }

            @Override
            public void onError() {
                if (null == getView()) return;
                getView().hideLoadingDialog();
            }
        });

    }


    /**
     * 选择生日
     *
     * @param context
     */
    public void timeChoose(Context context, ViewGroup decorView, int year, int moth, int day) {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        selectedDate.set(year, moth - 1, day);
        //正确设置方式 原因：注意事项有说明
        startDate.set(1900, 0, 1);
//        endDate.set(TimeUtils.getCurrentYear(), TimeUtils.getCurrentMonth() - 1, TimeUtils.getCurrentDay2());
        String[] split = Utils.getOldYearDate(-18).split("-");
        endDate.set(Integer.parseInt(split[0]), 11, 31);
        TimePickerView pvTime = new TimePickerBuilder(context, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                if (null == getView()) return;
                getView().chooseBirthday(TimeUtils.date2Str(date, "yyyy-MM-dd"));
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(20)//标题文字大小
                .setTitleText("")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDecorView(decorView)
                .build();
        pvTime.show();
    }


    /**
     * 获取用户信息
     *
     * @param activity
     */
    public void getUserInfo(Activity activity) {
        addNet(service.getUserInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (response.getData() != null && getView() != null) {
                            UserManager.getInstance().setUserInfo(response.getData());
                            appInitUser(activity);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 获取配置是否显示附近列表
     */
    public void appInitUser(Activity activity) {
        MineApiService service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        addNet(service.initNearUser().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<NearUserDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<NearUserDto> response) {
                        if (response.getData() != null && getView() != null) {
                            MMKVUtils.getInstance().encode(Constants.SHOW_NEAR_USER, response.getData().show_near_user);
                            MMKVUtils.getInstance().encode(Constants.SHOW_PARTY_ICON, response.getData().show_party_icon);
                            ARouterUtils.toPathWithId(ARouterApi.MAIN_MAIN);
                            activity.finish();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() != null)
                            getView().hideLoadingDialog();
                    }
                }));
    }

    /**
     * 获取粘贴板数据
     *
     * @return
     */
    public String getReferralCode(Activity context) {
        String referralCode = "";
        ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();
        if (data != null) {
            ClipData.Item item = data.getItemAt(0);
            if (item != null && !TextUtils.isEmpty(item.getText())) {
                referralCode = item.getText().toString();
            }
        }
        String startIndex = "ly#!V{";
        int strStartIndex = referralCode.indexOf(startIndex);
        int strEndIndex = referralCode.indexOf("}V!#yl");
        if (strStartIndex < 0) {
            return "";
        }
        if (strEndIndex < 0) {
            return "";
        }
        referralCode = referralCode.substring(strStartIndex, strEndIndex).substring(startIndex.length());
        return referralCode;
    }

    public void getRandomNikeName(View view) {
        addNet(service.getRandomNikeName().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView()) return;
                        if (view != null) {
                            view.postDelayed(() -> {
                                view.clearAnimation();
                                getView().getRandomNicknameSucceeded(response.getData());
                            }, 300);
                        } else {
                            getView().getRandomNicknameSucceeded(response.getData());
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (view != null) {
                            view.clearAnimation();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (view != null) view.clearAnimation();
                    }
                }));
    }
}
