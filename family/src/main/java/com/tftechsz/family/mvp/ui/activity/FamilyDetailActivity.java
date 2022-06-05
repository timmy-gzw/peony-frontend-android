package com.tftechsz.family.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamMessageNotifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.tftechsz.family.R;
import com.tftechsz.family.adapter.FamilyDetailAdapter;
import com.tftechsz.family.adapter.FamilyTaskAdapter;
import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.mvp.IView.IFamilyDetailView;
import com.tftechsz.family.mvp.presenter.FamilyDetailPresenter;
import com.tftechsz.family.widget.pop.FamilySignPopWindow;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.FamilyInviteBean;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.RecruitBaseDto;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.StatusBarUtil;
import com.tftechsz.common.widget.CircleImageView;
import com.tftechsz.common.widget.CommonItemView;
import com.tftechsz.common.widget.SharePopWindow;

import java.math.BigDecimal;
import java.util.List;

/**
 * 家族详情
 */
@Route(path = ARouterApi.ACTIVITY_FAMILY_DETAIL)
public class FamilyDetailActivity extends BaseMvpActivity<IFamilyDetailView, FamilyDetailPresenter> implements View.OnClickListener, IFamilyDetailView {
    private final int REQUEST_PHOTO = 10000;
    private FamilyInfoDto mData;
    private RecyclerView mRvMember;
    private TextView mTvSign;  //家族宣言
    private TextView mTvFamilyName, mTvFamilyId;
    private TextView mTvWeekRank, mTvTotalRank;  //周威望， 总威望
    private CircleImageView mCiFamily;
    private ImageView mImMaskFamily;
    private TextView mTvAnnouncement, mTvLookAll;  //公告,查看全部
    private ImageView mIvSignIn;  //签到

    private TextView mTvMemberNumber;  //数量
    private TextView mTvPrestige;
    private ProgressBar progress;
    private TextView mTvLevel, mTvLevel1, mTvLevelSmall, mTvEndLevel, mTvLevelTip;
    private ImageView mIvLevel, mIvLevelSmall;

    private RelativeLayout mRlTop;
    private RelativeLayout mRlEnterChat, mRlEnterShare;
    private TextView mTvIsInFamily; //是否在该家族中
    private ImageView mIvToolMenu;
    private int mFamilyId;
    private GestureDetector gestureDetector;
    private FamilySignPopWindow mSignPop;  //宣言弹窗
    private Team mTeam;  //家族信息
    private TextView mTvApplyNum;  //申请审核数量
    private LinearLayout mRlBottom;

    private CommonItemView mItemNotify;

    private int fromType;

    @Autowired
    UserProviderService service;
    private SharePopWindow mPopWindow;
    private String mInviteId; //邀请人id
    private RecyclerView mRvTask;
    private FamilyTaskAdapter mTaskAdapter;
    private TextView mTaskTitile;
    private TextView mAllTask;
    private TextView mTvAnnouncementTitleVisible;

    @Override
    public FamilyDetailPresenter initPresenter() {
        return new FamilyDetailPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StatusBarUtil.fullScreen(this);
        mTvAnnouncementTitleVisible = findViewById(R.id.tv_announcement_title);
        mRvMember = findViewById(R.id.rv_member);
        mTvSign = findViewById(R.id.tv_sign);
        mImMaskFamily = findViewById(R.id.iv_mask_family);
        mCiFamily = findViewById(R.id.cl_family);
        mTvFamilyName = findViewById(R.id.tv_family_name);
        mTvFamilyId = findViewById(R.id.tv_family_id);
        mTvWeekRank = findViewById(R.id.tv_week_rank);
        mTvTotalRank = findViewById(R.id.tv_total_rank);
        progress = findViewById(R.id.progress_bar);
        mTvLevel = findViewById(R.id.tv_level);
        mTvLevel1 = findViewById(R.id.tv_level1);
        mTvLevelSmall = findViewById(R.id.tv_level_small);
        mTvEndLevel = findViewById(R.id.tv_end_level);
        mTvLevelTip = findViewById(R.id.tv_level_tip);
        mIvLevel = findViewById(R.id.iv_level);
        mIvLevelSmall = findViewById(R.id.iv_level_small);
        mTvIsInFamily = findViewById(R.id.tv_enter_chat);
        mRlEnterChat = findViewById(R.id.rl_enter_chat);
        mRlEnterShare = findViewById(R.id.rl_enter_share);
        mIvToolMenu = findViewById(R.id.toolbar_iv_menu);
        mTvApplyNum = findViewById(R.id.tv_apply_num);
        mRlTop = findViewById(R.id.rl_top);
        mRlBottom = findViewById(R.id.rl_bottom);
        mTvPrestige = findViewById(R.id.tv_prestige);
        mTvAnnouncement = findViewById(R.id.tv_announcement);
        mTvLookAll = findViewById(R.id.tv_look_all);
        mTvMemberNumber = findViewById(R.id.tv_member_number);
        mIvSignIn = findViewById(R.id.iv_sign_in);
        mTaskTitile = findViewById(R.id.tv_task_title);
        mAllTask = findViewById(R.id.tv_all_task);
        mRvTask = findViewById(R.id.rv_task);
        mItemNotify = findViewById(R.id.item_notify);  //免打扰
        setListener();
    }


    private void setListener() {
        findViewById(R.id.iv_prestige).setOnClickListener(this);
        findViewById(R.id.iv_member).setOnClickListener(this);
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mIvSignIn.setOnClickListener(this);
        mIvToolMenu.setOnClickListener(this);
        mItemNotify.setOnClickListener(this);
        findViewById(R.id.rl_member).setOnClickListener(this);   //家族成员
        mAllTask.setOnClickListener(this);   //查看所有任务
        mRlEnterChat.setOnClickListener(this);
        mRlEnterShare.setOnClickListener(this);
        mTvSign.setOnClickListener(this);
        mRvTask.setLayoutManager(new LinearLayoutManager(mContext));
        mTaskAdapter = new FamilyTaskAdapter();
        mRvTask.setAdapter(mTaskAdapter);
        mAllTask.setVisibility(getTaskH5() == null ? View.GONE : View.VISIBLE);
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (null != mData) {
                    if (mData.is_in_family == 0) return true;
                    Intent intent = new Intent(FamilyDetailActivity.this, FamilyMemberActivity.class);
                    intent.putExtra("familyId", mData.family_id);
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        mRvMember.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }


    @Override
    protected void initData() {
        super.initData();
        CommonUtil.setMaskWidth(mImMaskFamily);
        mFamilyId = getIntent().getIntExtra("familyId", 0);
        mInviteId = getIntent().getStringExtra(Interfaces.EXTRA_INVITE_ID);
        fromType = getIntent().getIntExtra("fromType", 0);
        initBus();
        p.getFamilyDetail(mFamilyId);

    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (CommonUtil.isShowApplyNum(mTvApplyNum, 0)) {
//            mTvApplyNum.setVisibility(View.VISIBLE);
//        } else {
//            mTvApplyNum.setVisibility(View.GONE);
//        }
    }

    /**
     * 通知更新
     */
    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_FAMILY_INFO//更新信息
                                    || event.type == Constants.NOTIFY_FAMILY_TASK_UPDATE //家族任务刷新
                                    || event.type == Constants.NOTIFY_REFRESH_RECOMMEND) {
                                p.getFamilyDetail(mFamilyId);
                            } else if (event.type == Constants.NOTIFY_UPDATE_DISSOLUTION_SUCCESS) {  //解散家族成功
                                finish();
                            } else if (event.type == Constants.NOTIFY_UPDATE_EXIT_FAMILY) {
                                ARouterUtils.toMainMessageTab();
                            } else if (event.type == Constants.NOTIFY_SIGN_IN_SUCCESS) { //签到成功
                                mIvSignIn.setVisibility(View.GONE);
                            } else if (event.type == Constants.NOTIFY_EXIT_MINE_DETAIL) {
                                finish();
                            }
                        }
                ));
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_family_detail;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (null == mData) return;
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.toolbar_iv_menu) {
            Intent intent = new Intent(this, FamilySettingActivity.class);
            intent.putExtra("familyData", mData);
            startActivity(intent);
        } else if (id == R.id.rl_member) {  //家族成员
            if (mData.is_in_family == 0) return;
            Intent intent = new Intent(this, FamilyMemberActivity.class);
            intent.putExtra("familyId", mData.family_id);
            startActivity(intent);
        } else if (id == R.id.tv_sign) {   //家族宣言
            if (null == mSignPop)
                mSignPop = new FamilySignPopWindow(this, mData.intro);
            mSignPop.showPopupWindow();
        } else if (id == R.id.rl_enter_chat) {
            if (mData.is_in_family == 0) {
                p.apply(mData.family_id, mInviteId);
            } else { //进入聊天页面
                if (MMKVUtils.getInstance().decodeInt(Constants.VOICE_IS_OPEN) == 1) {
                    if (fromType == 1) {
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_EXIT_MINE_DETAIL));
                        finish();
                    } else {
                        CommonUtil.startTeamChatActivity(this, mData.tid + "");
                    }
                } else {
                    CommonUtil.startTeamChatActivity(this, mData.tid + "");
                }
            }
        } else if (id == R.id.rl_enter_share) {//分享邀请好友
            if (mPopWindow == null) {
                mPopWindow = new SharePopWindow(mActivity);
            }
            mPopWindow.questData(Interfaces.SHARE_QUEST_TYPE_FAMILY);
        } else if (id == R.id.iv_prestige) {  //威望
            BaseWebViewActivity.start(this, "", mData.prestige_url, 0, 0);
        } else if (id == R.id.iv_member) {  //成员
            BaseWebViewActivity.start(this, "", mData.familysize_url, 0, 0);
        } else if (id == R.id.iv_sign_in) {   //签到
            getP().familySign("family-home");
        } else if (id == R.id.tv_all_task) {   //查看所有任务
            ConfigInfo.MineInfo taskH5 = getTaskH5();
            if (taskH5 != null) {
                CommonUtil.performLink(mContext, taskH5, 0, 18);
            }
        } else if (id == R.id.item_notify) {  //免打扰
            if (mTeam != null) {
                if (!mItemNotify.getMySwitch().isChecked()) {
                    showTeamNotifyMenu(true, false);
                } else {
                    showTeamNotifyMenu(false, true);
                }
            }
        }

    }

    public ConfigInfo.MineInfo getTaskH5() {
        ConfigInfo configInfo = service.getConfigInfo();
        if (configInfo != null && configInfo.sys != null && configInfo.sys.family != null && configInfo.sys.family.h5_task != null) {
            return configInfo.sys.family.h5_task;
        }
        return null;
    }

    /**
     * 成功获取到了数据
     *
     * @param data
     */
    @Override
    public void getFamilyDetailSuccess(FamilyInfoDto data) {
        mData = data;
        GlideUtils.loadRoundImageRadius(this, mCiFamily, data.icon);
        GlideUtils.loadImageGaussian(mActivity, mImMaskFamily, data.icon, R.mipmap.ic_default_avatar);
        mTvFamilyName.setText(data.family_name);
        mTvFamilyId.setText("ID:" + data.family_id);
        mTvWeekRank.setText("第" + data.week_rank + "名");
        mTvTotalRank.setText("第" + data.total_rank + "名");
        mTvSign.setVisibility(TextUtils.isEmpty(data.intro) ? View.INVISIBLE : View.VISIBLE);
        mTvSign.setText(data.intro);
        mRlEnterShare.setVisibility(View.GONE);
        mTvIsInFamily.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        //成员信息
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        mRvMember.setLayoutManager(layoutManager);
        List<FamilyMemberDto> list = data.member_info_list;
        FamilyDetailAdapter adapter = new FamilyDetailAdapter(list);
        mRvMember.setAdapter(adapter);
        if (data.is_in_family == 0) { //不在该家族中
            if (data.is_apply_status == 1) {
                mTvIsInFamily.setText("已申请");
                mRlEnterChat.setBackgroundResource(R.drawable.bg_gray_ee_radius25);
                mRlEnterChat.setEnabled(false);
            } else {
                mTvIsInFamily.setText("申请加入");
                mRlEnterChat.setBackgroundResource(R.drawable.bg_orange_enable);
                mRlEnterChat.setEnabled(true);
            }
            mTvApplyNum.setVisibility(View.GONE);
            mIvToolMenu.setVisibility(View.GONE);
            mRlBottom.setVisibility(View.VISIBLE);
            mTvLookAll.setVisibility(View.GONE);
        } else {
            mIvSignIn.setVisibility(data.is_sign == 0 ? View.VISIBLE : View.GONE);
            mTvIsInFamily.setText("进入聊天室");
            mTvIsInFamily.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.peony_jzzy_lts_icon, 0, 0, 0);
            mIvToolMenu.setVisibility(View.VISIBLE);
            mRlBottom.setVisibility(View.VISIBLE);
            mRlEnterChat.setBackgroundResource(R.drawable.bg_orange_enable);
            mRlEnterChat.setEnabled(true);
            mRlEnterShare.setVisibility(View.VISIBLE);
            adapter.setOnItemClickListener((adapter1, view, position) -> {
                String userId = service.getUserId() == adapter.getData().get(position).user_id ? "" : String.valueOf(adapter.getData().get(position).user_id);
                ARouterUtils.toMineDetailActivity(userId);
            });
        }
        mTvMemberNumber.setText("(" + data.member_info_list_num + ")");
        //等级
        FamilyInfoDto.FamilyLevel level = data.family_level;
        if (null != level) {
            mTvLevel.setText(level.level + ""); //等级
            mTvLevel1.setText("Lv" + level.level);
            mTvLevelSmall.setText(String.valueOf(level.level)); //等级
            GlideUtils.loadRouteImage(this, mIvLevel, level.icon);
            GlideUtils.loadRouteImage(this, mIvLevelSmall, level.icon);
            mTvLevelTip.setText("还差" + level.diff + "威望值升级");
            mTvEndLevel.setText("LV" + (level.level + 1));
            BigDecimal myl = new BigDecimal(level.total);
            BigDecimal next = new BigDecimal(level.total + "").add(new BigDecimal(level.diff + ""));
            BigDecimal bigDecimal = myl.divide(next, 2, BigDecimal.ROUND_UP).multiply(new BigDecimal("100"));
            progress.setProgress(bigDecimal.intValue());
        }
        //获取提醒设置
        if (mData.is_in_family == 0) {
            mItemNotify.setVisibility(View.GONE);
        } else {
            mTeam = NimUIKit.getTeamProvider().getTeamById(mData.tid);
            if (mTeam != null)
                mItemNotify.getMySwitch().setChecked(mTeam.getMessageNotifyType() != TeamMessageNotifyTypeEnum.All);
            mItemNotify.getMySwitch().setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(data.announcement)) {
            mTvAnnouncement.setText("暂无公告");
        } else {
            mTvAnnouncement.setText(data.announcement);
        }
        mTvAnnouncementTitleVisible.setVisibility(TextUtils.isEmpty(data.announcement) ? View.GONE : View.VISIBLE);
        mTvAnnouncement.setVisibility(TextUtils.isEmpty(data.announcement) ? View.GONE : View.VISIBLE);
        mTvPrestige.setText(data.prestige + "威望值");
        ViewGroup.LayoutParams params = mRlTop.getLayoutParams();

        if (TextUtils.isEmpty(mData.intro)) {
            params.height = DensityUtils.dp2px(this, 270);
        } else {
            params.height = DensityUtils.dp2px(this, 290);
        }
        mRlTop.setLayoutParams(params);

        if (data.task != null && data.task.size() > 0) {
            mTaskTitile.setVisibility(View.VISIBLE);
            mAllTask.setVisibility(View.VISIBLE);
            mRvTask.setVisibility(View.VISIBLE);
            mTaskAdapter.setList(data.task);
        } else {
            mTaskTitile.setVisibility(View.GONE);
            mAllTask.setVisibility(View.GONE);
            mRvTask.setVisibility(View.GONE);
        }
    }


    private void showTeamNotifyMenu(boolean check, boolean unCheck) {
        TeamMessageNotifyTypeEnum type;
        if (check) {
            type = TeamMessageNotifyTypeEnum.Mute;
        } else {
            type = TeamMessageNotifyTypeEnum.All;
        }
        NIMClient.getService(TeamService.class).muteTeam(mData.tid, type)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        mItemNotify.getMySwitch().setChecked(check);
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_MESSAGE_MUTE));
                    }

                    @Override
                    public void onFailed(int code) {
                        mItemNotify.getMySwitch().setChecked(unCheck);
                    }

                    @Override
                    public void onException(Throwable exception) {
                    }
                });
    }


    @Override
    public void getConditionSuccess(FamilyIdDto data) {

    }

    @Override
    public void applySuccess(String data) {
        mTvIsInFamily.setText("已申请");
        mRlEnterChat.setBackgroundResource(R.drawable.bg_gray_ee_radius25);
        mRlEnterChat.setEnabled(false);
        toastTip(data);
        getP().getFamilyDetail(mFamilyId);
    }

    /**
     * 离开家族
     *
     * @param data
     */
    @Override
    public void applyLeaveSuccess(Boolean data) {
        if (data) {
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_EXIT_FAMILY));
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
            ARouterUtils.toMainMessageTab();
            toastTip("退出成功");
            finish();
        }

    }

    @Override
    public void updateFamilyIconSuccess(Boolean data) {
        p.getFamilyDetail(mFamilyId);
    }

    @Override
    public void familySignSuccess(Boolean data) {
        if (data) {
            mIvSignIn.setVisibility(View.GONE);
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_SIGN_IN_SUCCESS));
        }
    }

    @Override
    public void getFamilyInviteSuccess(FamilyInviteBean data) {

    }

    @Override
    public void muteAllSuccess(Boolean data, int mute) {
        if (data && mData != null) {
            mData.is_mute = mute;
        }
    }

    @Override
    public void getRecruitBaseSuccess(RecruitBaseDto data) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_PHOTO) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (null != selectList && selectList.size() > 0) {
                String mPath;
                if (selectList.get(0).isCompressed()) {
                    mPath = selectList.get(0).getCompressPath();
                } else {
                    mPath = selectList.get(0).getPath();
                }
                p.uploadAvatar(mPath);
            }

        }
    }

}
