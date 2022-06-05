package com.tftechsz.party.serviceImpl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ServiceUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.iservice.PartyService;
import com.tftechsz.common.music.base.BaseMusicHelper;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.party.mvp.ui.activity.PartyRoomActivity;
import com.tftechsz.party.service.FloatPartyWindowService;

@Route(path = ARouterApi.PARTY_FLOAT_SERVICE, name = "支付")
public class PartyServiceImpl implements PartyService {


    @Override
    public void init(Context context) {
    }


    @Override
    public boolean isRunFloatService() {
        return ServiceUtils.isServiceRunning(FloatPartyWindowService.class);
    }

    @Override
    public void stopFloatService() {
        if (ServiceUtils.isServiceRunning(FloatPartyWindowService.class)) {
            ServiceUtils.stopService(FloatPartyWindowService.class);
        }
        String roomId = MMKVUtils.getInstance().decodeString(Constants.PARAM_YUN_ROOM_ID);
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
            BaseMusicHelper.get().getPartyService().leaveRoom();
            BaseMusicHelper.get().getPartyService().releaseAudience();
            BaseMusicHelper.get().getPartyService().removeCallBack();
            BaseMusicHelper.get().getPartyService().onDestroy();
        }
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPlayService() != null && BaseMusicHelper.get().getPlayService().isPlaying()) {
            BaseMusicHelper.get().getPlayService().stop();
        }
    }


    @Override
    public boolean isOnSeat() {
        FloatPartyWindowService floatPartyWindowService = new FloatPartyWindowService();
        return floatPartyWindowService.isOnSeat();
    }

    @Override
    public void finishPartyActivity() {
        AppManager.getAppManager().finishActivity(PartyRoomActivity.class);
    }

    @Override
    public boolean isRunActivity() {
        return AppManager.getAppManager().getActivity(PartyRoomActivity.class) != null;
    }


}
