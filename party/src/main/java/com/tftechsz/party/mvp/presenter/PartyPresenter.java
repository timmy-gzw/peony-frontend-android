package com.tftechsz.party.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ServiceUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.music.base.BaseMusicHelper;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.party.R;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.dto.JoinPartyDto;
import com.tftechsz.party.entity.dto.PartyDto;
import com.tftechsz.party.mvp.IView.IPartyView;
import com.tftechsz.party.mvp.ui.activity.PartyRoomActivity;
import com.tftechsz.party.service.FloatPartyWindowService;

public class PartyPresenter extends BasePresenter<IPartyView> {

    private CustomPopWindow customPopWindow;
    public PartyApiService service;

    public PartyPresenter() {
        service = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
    }


    /**
     * 获取party列表
     */
    public void getPartyList(int page) {
        addNet(service.getPartyList(page).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<PartyDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartyDto> response) {
                        if (null != getView() && response.getData() != null)
                            getView().getPartyListSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null != getView())
                            getView().getPartyListFail();
                    }
                }));
    }


    public void showTipPop(Activity activity) {
        if (customPopWindow == null)
            customPopWindow = new CustomPopWindow(activity);
        customPopWindow.setContent("您当前在麦位上，需要下麦后才能进入其他派对哦");
        customPopWindow.setSingleButtong("我知道了", Utils.getColor(R.color.color_normal));
        customPopWindow.showPopupWindow();
    }


    /**
     * 加入离开party
     */
    public void joinParty(Activity activity, int loginStatus, int messageNum, String roomId, int id, String roomThumb, String roomBg, String type) {
        if (loginStatus == 1 || loginStatus == 3 || loginStatus == 4) {
            Utils.toast("IM正在重连请稍后");
            return;
        }
        if (!ClickUtil.canOperate()) {
            return;
        }
        boolean isOnSeat = MMKVUtils.getInstance().decodeBoolean(Constants.PARTY_IS_ON_SEAT);
        int lastParty = 0;
        int partyId = MMKVUtils.getInstance().decodeInt(Constants.PARTY_ID);
        if (ServiceUtils.isServiceRunning(FloatPartyWindowService.class) && isOnSeat && partyId != id) {
            showTipPop(activity);
            return;
        }
        if (ServiceUtils.isServiceRunning(FloatPartyWindowService.class))
            lastParty = partyId;
        int finalLastParty = lastParty;
        addNet(service.joinParty(id, type).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<JoinPartyDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<JoinPartyDto> response) {
                        if (null == getView() || response.getData() == null) return;
                        String yunId = MMKVUtils.getInstance().decodeString(Constants.PARAM_YUN_ROOM_ID);
                        if (!TextUtils.isEmpty(yunId) && partyId != id) {
                            if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
                                BaseMusicHelper.get().getPartyService().releaseAudience();
                            }
                            NIMClient.getService(ChatRoomService.class).exitChatRoom(yunId);
                        }
                        startPartyRoom(activity, messageNum, roomId, id, roomThumb, roomBg, response.getData(), finalLastParty);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                    }
                }));
    }

    /**
     * 跳转party页面
     */
    public void startPartyRoom(Activity activity, int messageNum, String roomId, int id, String roomThumb, String roomBg, JoinPartyDto data, int lastPartyId) {
        Intent intent = new Intent(activity, PartyRoomActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("id", id);
        intent.putExtra("roomThumb", roomThumb);
        intent.putExtra("roomBg", roomBg);
        intent.putExtra("partyData", data);
        intent.putExtra("messageNum", messageNum);
        intent.putExtra("lastPartyId", lastPartyId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        UserProviderService serviceUser = ARouter.getInstance().navigation(UserProviderService.class);
        if (data == null || serviceUser == null) {
            return;
        }
        ARouter.getInstance()
                .navigation(MineService.class)
                .trackEvent("点击进入房间", "enter_voice_room_click", "", JSON.toJSONString(new NavigationLogEntity(serviceUser.getUserId(), roomId, System.currentTimeMillis(), data.getRoom().getRoom_name(), data.getRoom().getBg_icon(), 1, CommonUtil.getOSName(), Constants.APP_NAME)), null);

    }

}
