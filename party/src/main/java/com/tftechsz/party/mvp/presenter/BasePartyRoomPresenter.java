package com.tftechsz.party.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ServiceUtils;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.util.VipUtils;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.party.R;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.TurntableCheckBean;
import com.tftechsz.party.entity.dto.JoinPartyDto;
import com.tftechsz.party.entity.dto.PartyDto;
import com.tftechsz.party.entity.dto.PartyPkSaveDto;
import com.tftechsz.party.entity.dto.PartyUserInfoDto;
import com.tftechsz.party.entity.req.SavePkReq;
import com.tftechsz.party.entity.req.StartPkReq;
import com.tftechsz.party.mvp.IView.IBasePartyRoomView;
import com.tftechsz.party.service.FloatPartyWindowService;
import com.tftechsz.party.widget.pop.LeaveOnSeatPopWindow;
import com.tftechsz.party.widget.pop.PartyExpressionPop;
import com.tftechsz.party.widget.pop.PartyRankPopWindow;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;

import static com.tftechsz.common.Constants.PAR_LOVE_SWITCH;

public class BasePartyRoomPresenter extends BasePresenter<IBasePartyRoomView> {
    private LeaveOnSeatPopWindow mLeavePop;   //离开麦位弹窗
    private CustomPopWindow permissionPop; //权限弹窗
    private PartyRankPopWindow mPartyRankPopWindow;  //魅力榜单弹窗
    private PartyExpressionPop mExpressionPop;  //派对表情
    public PartyApiService service;
    public PartyApiService imService;
    public PublicService publicService;
    private long currentTime;
    private boolean isSendPicture;
    ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    public BasePartyRoomPresenter() {
        service = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
        imService = RetrofitManager.getInstance().createIMApi(PartyApiService.class);
        publicService = RetrofitManager.getInstance().createUploadCheatApi(PublicService.class);
    }


    /**
     * 加入离开party
     */
    public void joinParty(int id, String type) {
        long now = System.currentTimeMillis();
        if ((now - currentTime) > 1500) {
            currentTime = System.currentTimeMillis();
            addNet(service.joinParty(id, type).compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<JoinPartyDto>>() {
                        @Override
                        public void onSuccess(BaseResponse<JoinPartyDto> response) {
                            if (null == getView() || response.getData() == null) return;
                            getView().joinPartySuccess(response.getData(), type);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            super.onFail(code, msg);
                            if (null == getView()) return;
                        }
                    }));
        }
    }

    /**
     * 加入离开party
     */
    public void joinLeaveParty(String from_type, int id, int isWindow) {
        addNet(imService.joinLeaveParty(from_type, id, isWindow).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<PartyDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartyDto> response) {
                        if (null == getView() || response.getData() == null) return;

                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                    }
                }));
    }

    /**
     * 申请上麦
     */
    public void applySeat(int id, int index, int pk_info_id) {
        addNet(service.applySeat(id, index, pk_info_id).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                    }
                }));
    }

    /**
     * 是否显示派对转盘按钮
     */
    public void wheelSwitch(int roomId) {
        addNet(service.wheelSwitch(roomId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<TurntableCheckBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<TurntableCheckBean> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().switchWheel(response.getData().is_open);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                    }
                }));
    }

    /**
     * 下麦
     */
    public void leaveSeat(int id, int index, int status) {
        addNet(service.leaveSeat(id, index, status).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().leaveSeatSuccess(index);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                    }
                }));
    }

    public void getGift(String id, List<VoiceRoomSeat> users) {
        getGift(id, users, false);
    }

    /**
     * 开始pk
     */
    public void startPartyPk(int party_id, int pk_info_id) {
        StartPkReq startPkReq = new StartPkReq();
        startPkReq.party_id = party_id;
        startPkReq.pk_info_id = pk_info_id;
        addNet(service.startPk(startPkReq).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;

                    }
                }));
    }


    /**
     * 开始pk
     */
    public void startPkAgain(SavePkReq savePkReq) {
        addNet(service.startPkAgain(savePkReq).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<PartyPkSaveDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartyPkSaveDto> response) {
                        if (null == getView() || response.getData() == null) return;

                    }
                }));
    }


    /**
     * 结束pk
     */
    public void stopPartyPk(int party_id, int pk_info_id) {
        StartPkReq startPkReq = new StartPkReq();
        startPkReq.party_id = party_id;
        startPkReq.pk_info_id = pk_info_id;
        addNet(service.stopPk(startPkReq).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().stopPkSuccess();
                    }
                }));
    }

    /**
     * 开始惩罚
     */
    public void startPunish(int party_id, int pk_info_id) {
        StartPkReq startPkReq = new StartPkReq();
        startPkReq.party_id = party_id;
        startPkReq.pk_info_id = pk_info_id;
        addNet(service.startPunish(startPkReq).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                    }
                }));
    }

    /**
     * 结束惩罚
     */
    public void stopPunish(int party_id, int pk_info_id) {
        StartPkReq startPkReq = new StartPkReq();
        startPkReq.party_id = party_id;
        startPkReq.pk_info_id = pk_info_id;
        addNet(service.stopPunish(startPkReq).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                    }
                }));
    }


    /**
     * 结束惩罚
     */
    public void endPk(int id) {
        StartPkReq startPkReq = new StartPkReq();
        startPkReq.party_id = id;
        addNet(service.endPK(startPkReq).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                    }
                }));
    }

    /**
     * 显示结束PK
     *
     * @param activity
     */
    public void showEndPkPop(Activity activity, int party_id, int pk_info_id) {
        if (permissionPop == null)
            permissionPop = new CustomPopWindow(activity);
        permissionPop.setContent("当前正在PK中，是否提前结束本局游戏？");
        permissionPop.setLeftButton("点错了");
        permissionPop.setRightButton("结束");
        permissionPop.setLeftColor(R.color.color_normal);
        permissionPop.setRightColor(R.color.red);
        permissionPop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
                permissionPop.dismiss();
            }

            @Override
            public void onSure() {
                stopPartyPk(party_id, pk_info_id);
            }
        });
        permissionPop.showPopupWindow();
    }


    /**
     * 显示关闭PK模式
     *
     * @param activity
     */
    public void showEndPk(Activity activity, int id) {
        if (permissionPop == null)
            permissionPop = new CustomPopWindow(activity);
        permissionPop.setContent("当前正在PK中，确认关闭PK模式吗？");
        permissionPop.setLeftButton("取消");
        permissionPop.setRightButton("确认");
        permissionPop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
                permissionPop.dismiss();
            }

            @Override
            public void onSure() {
                endPk(id);
            }
        });
        permissionPop.showPopupWindow();
    }


    /**
     * 显示魅力榜单
     */
    public void setHourPop(Activity activity, JoinPartyDto mData, int id) {
        if (mData != null && mData.getRoom() != null) {
            if (mPartyRankPopWindow == null)
                mPartyRankPopWindow = new PartyRankPopWindow(activity, mData.getRoom().getRank_link(), id);
            mPartyRankPopWindow.showPopupWindow();
        }
    }

    /**
     * 显示被踢出拉黑弹窗
     */
    public void showShotOffPop(Activity activity, String fontColor, String msg) {
        if (permissionPop == null)
            permissionPop = new CustomPopWindow(activity);
        permissionPop.setOutSideDismiss(false)
                .setBackPressEnable(false);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(msg);
        if (!TextUtils.isEmpty(fontColor)) {
            int start = builder.toString().indexOf(fontColor);
            builder.setSpan(new UnderlineSpan(), start, start + fontColor.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        permissionPop.setContent(builder);
        permissionPop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure() {
                if (getView() != null)
                    getView().finishActivity();
            }
        });
        permissionPop.setSingleButtong("知道了", Utils.getColor(R.color.color_normal));
        permissionPop.showPopupWindow();
    }


    /**
     * @param clickOnSeatDown 是够点击的麦下评论区用户
     */
    public void getGift(String id, List<VoiceRoomSeat> users, boolean clickOnSeatDown) {
        if (!ClickUtil.canOperate()) return;
        if (null != getView()) {
            getView().getGiftSuccess(id, users, clickOnSeatDown);
        }
    }

    /**
     * 判断是否有管理员权限
     */
    public boolean isAdmin(JoinPartyDto mData) {
        boolean hasPre = false;
        if (mData != null && mData.getUser() != null) {
            PartyUserInfoDto userInfoDto = mData.getUser();
            if (userInfoDto.getRole_id() == 128 || userInfoDto.getRole_id() == 64 || userInfoDto.getRole_id() == 32)
                hasPre = true;
        }
        return hasPre;
    }

    /**
     * 判断是否有管理员权限和房主权限
     */
    public boolean isAdminAndOwner(JoinPartyDto mData) {
        boolean hasPre = false;
        if (mData != null && mData.getUser() != null) {
            PartyUserInfoDto userInfoDto = mData.getUser();
            if (userInfoDto.getRole_id() == 64 || userInfoDto.getRole_id() == 32)
                hasPre = true;
        }
        return hasPre;
    }


    /**
     * 判断是否超级管理员
     */
    public boolean isSuper(JoinPartyDto mData) {
        boolean hasPre = false;
        if (mData != null && mData.getUser() != null) {
            PartyUserInfoDto userInfoDto = mData.getUser();
            if (userInfoDto.getRole_id() == 128)
                hasPre = true;
        }
        return hasPre;
    }

    /**
     * 获取当前麦位
     */
    public int getIndex(JoinPartyDto mData, int userId) {
        int index = 0;
        if (mData != null && mData.getMicrophone() != null && mData.getMicrophone().size() > 0) {
            for (int i = 0; i < mData.getMicrophone().size(); i++) {
                if (mData.getMicrophone().get(i).getUser_id() == userId) {
                    index = mData.getMicrophone().get(i).getIndex();
                }
            }
        }
        return index;
    }

    /**
     * 获取当前用户麦位数据
     */
    public VoiceRoomSeat getVoiceSeat(JoinPartyDto mData, int userId) {
        VoiceRoomSeat seat = null;
        if (mData != null && mData.getMicrophone() != null && mData.getMicrophone().size() > 0) {
            for (int i = 0; i < mData.getMicrophone().size(); i++) {
                if (mData.getMicrophone().get(i).getUser_id() == userId) {
                    seat = mData.getMicrophone().get(i);
                }
            }
        }
        return seat;
    }


    /**
     * 获取发送随机文案
     *
     * @param nick
     * @return
     */
    public String getSendMsg(String nick) {
        List<String> list = new ArrayList<>();
        list.add("欢迎" + nick + "，交个朋友吧");
        list.add("欢迎" + nick + "小可爱～");
        list.add("欢迎" + nick + "，参见这位大人");
        list.add("欢迎" + nick + "，来互动下哦");
        Random random = new Random();
        int n = random.nextInt(list.size());
        return list.get(n);
    }

    /**
     * 显示权限弹窗
     */
    public void showPermission(Activity activity) {
        if (permissionPop == null)
            permissionPop = new CustomPopWindow(activity);
        permissionPop.setContent("您的手机没有授予悬浮权限，请开启后重试");
        permissionPop.setLeftButton("暂不开启");
        permissionPop.setRightButton("去开启");
        permissionPop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
                if (getView() != null)
                    getView().finishActivity();
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSure() {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, 1000);
            }
        });
        if (!permissionPop.isShowing())
            permissionPop.showPopupWindow();
    }


    /**
     * 显示离开麦位弹窗
     */
    public void showLeavePop(Activity activity, int id, VoiceRoomSeat seat) {
        if (mLeavePop == null)
            mLeavePop = new LeaveOnSeatPopWindow(activity, "");
        mLeavePop.addOnClickListener(() -> leaveSeat(id, seat.getIndex(), 1));
        mLeavePop.showPopupWindow();
    }


    public void smallWindow(Activity activity, int messageNum, String roomId, int id, String roomThumb, String roomBg, boolean isOnSeat, int index, int fightPattern) {
        if (Build.VERSION.SDK_INT >= 23) {  //请求浮窗权限
            if (!Settings.canDrawOverlays(activity)) {   //
                showPermission(activity);
            } else {
                activity.finish();
                if (ServiceUtils.isServiceRunning(FloatPartyWindowService.class)) {
                    return;
                }
                Intent intent = new Intent(activity, FloatPartyWindowService.class);
                intent.putExtra("roomId", roomId);
                intent.putExtra("id", id);
                intent.putExtra("isOnSeat", isOnSeat);
                intent.putExtra("fightPattern", fightPattern);
                intent.putExtra("index", index);
                intent.putExtra("messageNum", messageNum);
                intent.putExtra("roomBg", roomBg);
                intent.putExtra("roomThumb", roomThumb);
                activity.startService(intent);
            }
        }
    }


    /**
     * 解除绑定
     */
    public void unBind() {
        if (ServiceUtils.isServiceRunning(FloatPartyWindowService.class)) {
            ServiceUtils.stopService(FloatPartyWindowService.class);
        }
    }


    /**
     * 播放svga动画
     */
    public void playAirdrop(SVGAParser svgaParser, SVGAImageView svgaAirDrop, String name) {
        svgaParser.decodeFromAssets(name, new SVGAParser.ParseCompletion() {

            @Override
            public void onError() {

            }

            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                if (svgaAirDrop != null) {
                    SVGADrawable drawable = new SVGADrawable(videoItem);
                    svgaAirDrop.setImageDrawable(drawable);
                    svgaAirDrop.startAnimation();
                }
            }
        }, null);
    }

    /**
     * 播放svga动画
     */
    public void playAirdrop(SVGAParser svgaParser, SVGAImageView svgaAirDrop, int avatar_id, ImageView iv_frame) {
        String name = String.format("avatar/party_avatar_%s.svga", avatar_id);
        svgaParser.decodeFromAssets(name, new SVGAParser.ParseCompletion() {

            @Override
            public void onError() {
                VipUtils.setPersonalise(iv_frame, avatar_id, false, true);
            }

            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                if (svgaAirDrop != null) {
                    SVGADrawable drawable = new SVGADrawable(videoItem);
                    svgaAirDrop.setImageDrawable(drawable);
                    svgaAirDrop.startAnimation();
                }
            }
        }, null);
    }

    /**
     * 0房管，1 拉黑，2踢出，3禁言
     */
    public void setManagerPartySetAct(String roomId, String userId, int type, int opt_type) {
        addNet(service.roomManagerSetAct(roomId, userId, type, opt_type).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().toastTip(response.getData());
                        getView().dismissDialogs(type, -1);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                    }
                }));


    }

    /**
     * 取消权限
     */
    public void setCancelManager(String roomId, String userId, int type) {

        addNet(service
                .setAct(roomId, type, userId)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (response.getData() != null) {
                            getView().toastTip(response.getData());

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 抱上麦序
     */
    public void holdOnMai(int roomId, String to) {
        addNet(service.inviteMac(roomId, to).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().toastTip(response.getData());

                        getView().dismissDialogs(3, -1);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                    }
                }));
    }

    /**
     * 抱下麦序
     */
    public void holdOnMaiKick(int roomId, String to) {
        addNet(service.inviteMacKick(roomId, to).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().toastTip(response.getData());

                        getView().dismissDialogs(4, -1);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                    }
                }));
    }


    /**
     * 用户同意上麦
     */
    public void agreeSeat(int roomId, int userId, int inviter) {
        addNet(service.agreeSeat(roomId, userId, inviter).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView() || response.getData() == null) return;
                    }
                }));

    }


    /**
     * 爱意值开关
     * status  // 1/0 开/关
     */
    public void loveSwitch(int roomId, int status) {
        addNet(service.loveSwitch(roomId, status).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().dismissDialogs(PAR_LOVE_SWITCH, status);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (code == 0) {
                            getView().dismissDialogs(PAR_LOVE_SWITCH, status);
                        }
                    }
                }));

    }


    /**
     * 闭麦开关
     * status  // 1/0 开/关
     */
    public void muteMicrophone(int roomId, int to, int index, int status) {
        addNet(service.muteMicrophone(roomId, to, index, status).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                    }
                }));

    }


    /**
     * 拒绝上麦
     */
    public void refuseOnSeat(int roomId, int userId) {
        addNet(service.refuseOnSeat(roomId, userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                    }
                }));

    }

    /**
     * 锁定麦位
     * lock：1锁定 0：解锁
     */
    public void lockMicrophone(int roomId, int index, int status) {
        addNet(service.lockMicrophone(roomId, index, status).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || response.getData() == null) return;
                    }
                }));
    }

    /**
     * 上传反作弊token
     */
    public void uploadCheat() {
        singleThreadExecutor.execute(() -> {
            com.tftechsz.common.utils.CommonUtil.getToken(new com.tftechsz.common.utils.CommonUtil.OnSelectListener() {
                @Override
                public void onSure() {
                    mCompositeDisposable.add(publicService.uploadCheat("party_screen_chat", "").compose(BasePresenter.applySchedulers())
                            .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                                @Override
                                public void onSuccess(BaseResponse<Boolean> response) {

                                }
                            }));
                }
            });
        });
    }

    /**
     * 获取派对大表情
     */
    public void getPicture(Activity activity, int partyId) {
        addNet(service.getPictureList().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<GiftDto>>>() {

                    @Override
                    public void onSuccess(BaseResponse<List<GiftDto>> response) {
                        if (getView() == null || response.getData() == null) return;
                        if (mExpressionPop == null)
                            mExpressionPop = new PartyExpressionPop(activity);
                        mExpressionPop.setData(partyId, response.getData());
                        mExpressionPop.addOnClickListener(new PartyExpressionPop.OnSelectListener() {
                            @Override
                            public void choosePicture(GiftDto giftDto) {
                                sendChoosePicture(giftDto.id, partyId);
                            }
                        });
                        mExpressionPop.showPopupWindow();

                    }
                }));

    }

    public void setIsSendPic(boolean isSendPic) {
        isSendPicture = isSendPic;
    }


    private void sendChoosePicture(int id, int partyId) {
        if (isSendPicture) {
            Utils.toast("你点的太快了～");
            return;
        }
        addNet(service.choosePicture(id, partyId)
                .compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null || response.getData() == null) return;
                        getView().choosePictureSuccess();
                        isSendPicture = true;
                    }
                }));
    }


    /**
     * 获取聊天框背景
     */
    public int getChatBg(String fromAccount) {
        int bg = 0;
        NimUserInfo nimUserInfo = (NimUserInfo) NimUIKit.getUserInfoProvider().getUserInfo(fromAccount);
        if (nimUserInfo != null && nimUserInfo.getExtension() != null) {
            try {
                ChatMsg.Vip vip = JSON.parseObject(nimUserInfo.getExtension(), ChatMsg.Vip.class);
                if (vip != null) {
                    bg = vip.chat_bubble;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bg;
    }

    /**
     * 上传日志
     * 1\房间曝光   4\语音房私信页面曝光   5\语音房在线列表曝光
     * 2\点击上麦
     * 3\房间公告曝光
     * 6\转盘入口点击
     * 7\转盘页面曝光
     * 8\抽奖按钮点击
     * 9.10\转盘榜单曝光  抽奖记录页面曝光
     */
    public void upLog(int type, int from_type, String content, int tag, UserProviderService service, String mRoomId, JoinPartyDto mData) {
        if (type == 1) {
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent("房间曝光", "voice_room_visit", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), mRoomId, System.currentTimeMillis(), mData.getRoom().getRoom_name(), mData.getRoom().getIcon(), 1, CommonUtil.getOSName(), Constants.APP_NAME)), null);

        } else if (type == 2) {
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent("点击上麦", "voice_room_upper_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), mRoomId, System.currentTimeMillis(), mData.getRoom().getRoom_name(), mData.getRoom().getIcon(), 1, CommonUtil.getOSName(), Constants.APP_NAME, from_type)), null
                    );

        } else if (type == 3) {
            //from_type(1.点击房间公告按钮2.进入房间自动曝光3.其他)
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent("房间公告曝光", "room_notice_visit", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), mRoomId, System.currentTimeMillis(), mData.getRoom().getRoom_name(), mData.getRoom().getIcon(), 1, CommonUtil.getOSName(), Constants.APP_NAME, from_type, content)), null
                    );
        } else if (type == 4 || type == 5) {
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent(type == 4 ? "语音房私信页面曝光" : "语音房在线列表曝光", type == 4 ? "voice_room_msg_list_visit" : "voice_room_online_list_visit", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), mRoomId, System.currentTimeMillis(), mData.getRoom().getRoom_name(), mData.getRoom().getIcon(), 1, CommonUtil.getOSName(), Constants.APP_NAME)), null);

        } else if (type == 6) {
            //from_type(语音房间入口)
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent("转盘入口点击", "turntable_entrance_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), from_type, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME, mRoomId)), null
                    );
        } else if (type == 7) {
            // type(1.初级场2.高级场)
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent("转盘页面曝光", "turntable_visit", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), 1, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME, from_type)), null
                    );
        } else if (type == 8) {
            // type(1.1次2.10次3.100次))
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent("抽奖按钮点击", "turntable_draw_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), 1, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME, from_type, tag)), null
                    );
        } else if (type == 9 || type == 10) {
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent(type == 9 ? "转盘榜单曝光" : "抽奖记录页面曝光", type == 9 ? "turntable_rank_visit" : "turntable_draw_record_visit", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME, 1)), null
                    );
        }


    }

    public void showVolume(ImageView view, ImageView view1, int volume) {
        if (volume == 0) {
            view.clearAnimation();
            view1.clearAnimation();
            view.setVisibility(View.INVISIBLE);
            view1.setVisibility(View.INVISIBLE);
        } else {
            AnimationUtil.setAnimation(view, view1);
        }
    }

    public void showPKVolume(ImageView view, ImageView view1, int volume) {
        if (volume == 0) {
            view.clearAnimation();
            view1.clearAnimation();
            view1.setVisibility(View.INVISIBLE);
        } else {
            AnimationUtil.setAnimation(view, view1);
        }
    }


    @Override
    public void detachView() {
        super.detachView();
        try {
            singleThreadExecutor.shutdown();
            if (!singleThreadExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                singleThreadExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            singleThreadExecutor.shutdownNow();
        }
    }
}
