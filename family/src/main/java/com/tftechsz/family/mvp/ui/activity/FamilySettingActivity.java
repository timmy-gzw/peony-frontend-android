package com.tftechsz.family.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.makeramen.roundedimageview.RoundedImageView;
import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.family.mvp.IView.IFamilySettingView;
import com.tftechsz.family.mvp.presenter.FamilySettingPresenter;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.MasterChangeStatus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.StringUtils;
import com.tftechsz.common.widget.CommonItemView;
import com.tftechsz.common.widget.pop.CustomPopWindow;

import java.util.List;

import androidx.annotation.Nullable;

public class FamilySettingActivity extends BaseMvpActivity<IFamilySettingView, FamilySettingPresenter> implements View.OnClickListener, IFamilySettingView {
    private final int REQUEST_PHOTO = 10000;
    private final int REQUEST_CONDITION = 10001;
    private CommonItemView mItemName, mItemChangeHead, mItemSign, mItemAnnouncement, mItemApply, mItemCondition, mItemBlack, mItemMute;
    private TextView mTvSign, mTvAnnouncement;  //宣言  公告
    private RoundedImageView mIvAvatar;
    private ImageView mIvArrow;
    private TextView mTvExit, mTvRule;
    private FamilyInfoDto mData;
    private String mPath;
    private CustomPopWindow mCustomPopWindow;
    private UserProviderService service;


    @Override
    public FamilySettingPresenter initPresenter() {
        return new FamilySettingPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_family_setting;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().setTitle("家族设置").showBack(true).build();
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mIvArrow = findViewById(R.id.iv_arrow);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mItemName = findViewById(R.id.item_name);
        mTvSign = findViewById(R.id.tv_sign);
        mTvAnnouncement = findViewById(R.id.tv_announcement);
        mItemSign = findViewById(R.id.item_sign);
        mItemAnnouncement = findViewById(R.id.item_announcement);
        mItemChangeHead = findViewById(R.id.item_change_head);  //转让族长
        mItemApply = findViewById(R.id.item_apply);  //申请审核
        mItemCondition = findViewById(R.id.item_condition);   //条件
        mItemBlack = findViewById(R.id.item_black);  //黑名单
        mTvRule = findViewById(R.id.tv_rule);
        mTvExit = findViewById(R.id.tv_exit);
        mItemMute = findViewById(R.id.item_mute);
        initListener();

    }

    private void initListener() {
        findViewById(R.id.rl_avatar).setOnClickListener(this);
        mItemChangeHead.setOnClickListener(this);
        mItemName.setOnClickListener(this);
        mItemBlack.setOnClickListener(this);
        mItemCondition.setOnClickListener(this);
        mTvAnnouncement.setOnClickListener(this);
        mItemSign.setOnClickListener(this);
        mItemAnnouncement.setOnClickListener(this);
        mItemApply.setOnClickListener(this);
        mItemMute.setOnClickListener(this);
        mTvExit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mData = getIntent().getParcelableExtra("familyData");
        if (mData == null) return;
        //id" => 4, "title" => "长老" ],[ "id" => 32, "title" => "副族长" ],[ "id" => 64, "title" => "族长" ],
        //权限管理：
        //   副族长：申请审核、编辑家族宣言、更改家族加入条件、退出家族、举报、取消
        //   长老：退出家族、举报、取消
        //   普通成员：退出家族、举报、取消
        int length = 200;
        int size = Math.min(StringUtils.judgeTextLength(mData.intro), length);
        int aSize = Math.min(StringUtils.judgeTextLength(mData.announcement), length);
        if (mData.my_role_id == 64) {  //族长
            mItemSign.setRightText(size + "/" + length);
            mItemAnnouncement.setRightText(aSize + "/" + length);
            mTvExit.setText("解散家族");
            mTvRule.setText(mData.icon_desc);
        } else if (mData.my_role_id == 32) {   //副族长
            mItemSign.setRightText(size + "/" + length);
            mItemAnnouncement.setRightText(aSize + "/" + length);
            mItemMute.setVisibility(View.GONE);
            mItemChangeHead.setVisibility(View.GONE);
            mTvExit.setText("退出家族");
            mTvRule.setText(mData.icon_desc);
        } else if (mData.my_role_id == 4) {  //长老
            mIvArrow.setVisibility(View.INVISIBLE);
            mItemName.getIvRight().setVisibility(View.GONE);
            mItemCondition.setVisibility(View.GONE);
            mItemBlack.setVisibility(View.GONE);
            mItemMute.setVisibility(View.GONE);
            mItemChangeHead.setVisibility(View.GONE);
            mItemAnnouncement.getIvRight().setVisibility(View.GONE);
            mItemSign.getIvRight().setVisibility(View.GONE);
            mTvExit.setText("退出家族");
            mTvRule.setText("");
        } else {
            mIvArrow.setVisibility(View.INVISIBLE);
            mItemName.getIvRight().setVisibility(View.GONE);
            mItemAnnouncement.getIvRight().setVisibility(View.GONE);
            mItemSign.getIvRight().setVisibility(View.GONE);
            mItemCondition.setVisibility(View.GONE);
            mItemChangeHead.setVisibility(View.GONE);
            mItemBlack.setVisibility(View.GONE);
            mItemApply.setVisibility(View.GONE);
            mItemMute.setVisibility(View.GONE);
            mTvExit.setText("退出家族");
            mTvRule.setText("");
        }
        if (mData.rules != null) {
            if (mData.rules.type == 0) {
                mItemCondition.setRightText("允许任何人申请加入");
            } else if (mData.rules.type == 2) {
                mItemCondition.setRightText("无须审核自动加入");
            } else {
                if (mData.rules.rules != null) {
                    mItemCondition.setRightText("男性财富" + mData.rules.rules.boy_rich_level + "级\n女性魅力" + mData.rules.rules.girl_charm_level + "级");
                }
            }
        }
        mItemMute.getMySwitch().setChecked(mData.is_mute == 1);
        GlideUtils.loadImage(this, mIvAvatar, mData.icon);
        mItemName.setRightText(mData.family_name);
        if (TextUtils.isEmpty(mData.intro)) {
            mTvSign.setText("暂无宣言");
        } else {
            mTvSign.setText(mData.intro);
        }
        if (TextUtils.isEmpty(mData.announcement)) {
            mTvAnnouncement.setText("暂无公告");
        } else {
            mTvAnnouncement.setText(mData.announcement);
        }
        mItemBlack.setRightText("点击查看黑名单列表");
        initBus();
        p.master_change_status();
    }


    /**
     * 通知更新
     */
    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_FAMILY_INFO) {  //更新信息
                                finish();
                            } else if (event.type == Constants.NOTIFY_UPDATE_EXIT_FAMILY) {
                                ARouterUtils.toMainMessageTab();
                            }
                        }
                ));

    }

    @Override
    protected void onResume() {
        super.onResume();
        setApply();
    }

    private void setApply() {
        String apply = MMKVUtils.getInstance().decodeString(service.getUserId() + Constants.FAMILY_APPLY);
        if (!TextUtils.isEmpty(apply)) {
            ChatMsg.ApplyMessage applyMessage = JSON.parseObject(apply, ChatMsg.ApplyMessage.class);
            if (applyMessage != null && applyMessage.num > 0) {
                mItemApply.setRightText(applyMessage.num + "条申请消息待处理");
                mItemApply.setRightTextColor(this, R.color.red);
            } else {
                mItemApply.setRightText("");
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_avatar) {
            if (mData.my_role_id == 64 || mData.my_role_id == 32)
                ChoosePicUtils.picSingle(this, REQUEST_PHOTO);
        } else if (id == R.id.item_black) {
            Intent intent = new Intent(this, FamilyBlackListActivity.class);
            intent.putExtra(Interfaces.EXTRA_ID, mData.family_id);
            startActivity(intent);
        } else if (id == R.id.item_name) {
            if (mData.my_role_id == 64 || mData.my_role_id == 32)
                EditFamilyInfoActivity.startForName(this, mData.family_name);
        } else if (id == R.id.item_sign) {
            if (mData.my_role_id == 64 || mData.my_role_id == 32)
                EditFamilyInfoActivity.startForSign(this, mData.intro);
        } else if (id == R.id.item_announcement) {
            if (mData.my_role_id == 64 || mData.my_role_id == 32)
                EditFamilyInfoActivity.startForAnnouncement(this, mData.announcement, mData.my_role_id);
        } else if (id == R.id.item_condition) {  //加入条件
            Intent intent = new Intent(this, ChangeJoinConditionActivity.class);
            startActivityForResult(intent, REQUEST_CONDITION);
        } else if (id == R.id.item_apply) {
            Intent intent = new Intent(this, ApplyVerifyActivity.class);
            intent.putExtra("familyId", mData.family_id);
            startActivity(intent);
        } else if (id == R.id.item_mute) {
            getP().muteAll(mData.family_id + "", mData.is_mute == 1 ? 0 : 1);
        } else if (id == R.id.item_change_head) {  //转让族长
            String text = "家族长象征权力与义务，请先与对方沟通好再转让，转让后您将变为普通成员，确认继续吗？";
            if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.family != null) {
                text = service.getConfigInfo().sys.family.chang_master_desc;
            }
            if (mCustomPopWindow == null)
                mCustomPopWindow = new CustomPopWindow(mContext, 1)
                        .setContent(new SpannableStringUtils.Builder()
                                .append(text)
                                .create())
                        .setLeftButton("我再想想")
                        .setRightButton("继续")
                        .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                            @Override
                            public void onCancel() {
                            }

                            @Override
                            public void onSure() {
                                Intent intent = new Intent(FamilySettingActivity.this, FamilyMemberActivity.class);
                                intent.putExtra("familyId", mData.family_id);
                                intent.putExtra("from", 3);
                                intent.putExtra("tid", mData.tid);
                                startActivity(intent);
                            }
                        });
            mCustomPopWindow.showPopupWindow();
        } else if (id == R.id.tv_exit) {  //退出解散家族
            if (mData.my_role_id == 64) {
                EditFamilyInfoActivity.startForDissolution(this);
            } /*else if (mData.my_role_id == 32) {  //副族长
                EditFamilyInfoActivity.startForExit(this);
            }*/ else {
                getP().showExitPopSecond(this);
            }
        }

    }

    @Override
    public void updateFamilyIconSuccess(Boolean data) {
        if (data) {
            GlideUtils.loadRouteImage(this, mIvAvatar, mPath, R.mipmap.family_icon_default_avatar);
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
        }
    }

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
    public void muteAllSuccess(Boolean data, int mute) {
        if (data && mData != null) {
            mData.is_mute = mute;
            mItemMute.getMySwitch().setChecked(mData.is_mute == 1);
        }
    }

    @Override
    public void masterChangeStatusSuccess(MasterChangeStatus data) {
        if (data.status == 1) {
            mItemChangeHead.setRightText(data.tips);
            mItemChangeHead.setRightTextColor(this, R.color.red);
            mItemChangeHead.setOnClickListener(v -> toastTip(data.toast));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_PHOTO) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (null != selectList && selectList.size() > 0) {

                if (selectList.get(0).isCompressed()) {
                    mPath = selectList.get(0).getCompressPath();
                } else {
                    mPath = selectList.get(0).getPath();
                }
                p.uploadAvatar(mPath);
            }

        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CONDITION) {
            finish();
        }
    }
}
