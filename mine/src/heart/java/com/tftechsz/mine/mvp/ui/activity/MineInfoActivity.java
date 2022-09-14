package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.netease.nim.uikit.common.UserInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CommonItemView;
import com.tftechsz.common.widget.pop.UploadAvatarPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.IView.IMineInfoView;
import com.tftechsz.mine.mvp.presenter.MineInfoPresenter;

import java.util.List;

/**
 * 个人信息资料
 */
@Route(path = ARouterApi.ACTIVITY_MINE_INFO)
public class MineInfoActivity extends BaseMvpActivity<IMineInfoView, MineInfoPresenter> implements View.OnClickListener, IMineInfoView {
    private final int REQUEST_CODE = 10000;
    private final static int REQUEST_NAME_CODE = 10001;
    private final static int REQUEST_JOB_CODE = 10002;
    private final static int REQUEST_SIGN = 10004;
    private TextView mItemSign;  //交友宣言
    private CommonItemView mItemSex;  //性别
    private CommonItemView mItemBirthday; //生日
    private CommonItemView mItemConstellation;   //星座
    private CommonItemView mItemAddress; //所在地
    private CommonItemView mItemHomeAddress;  //家乡所在第
    private CommonItemView mItemJob; //职业
    private CommonItemView mItemHeight;  //身高
    private CommonItemView mItemWeight;  //体重
    private CommonItemView mItemIncome;  //年收入
    private RelativeLayout mRlMineInfo;
    private RoundedImageView mIvAvatar;
    private TextView mTvName, mTvNameAudit, mTvSignAudit;
    private UserInfo mUserInfo;
    private String path = "";  //头像路径
    @Autowired
    UserProviderService service;

    private TextView mIconAudit; //icon审核中
    private ConstraintLayout mClAudio; //整个语音签名条
    private LinearLayout mLlAudio; //播放录音
    private TextView mAudioTime;   //时长
    private TextView mAudioAudit;  //审核中
    private TextView mAudioNormal; //未录制
    private ImageView mIvAudio;  //播放按钮
    private MediaPlayer mMediaPlayer;  // 播放录音
    private TextView mAudioIconMsg, mAudioAudioMsg;
    private String mediaUrl;
    private int mediaTime, mediaTimeTemp;
    private String[] strHometown;//家乡截取

    public static void startActivity(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, MineInfoActivity.class);
        intent.putExtra(Interfaces.EXTRA_USER_INFO, userInfo);
        context.startActivity(intent);
    }

    @Override
    public MineInfoPresenter initPresenter() {
        return new MineInfoPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("编辑资料")
                .setBackgroundColor(0)
                .build();
        ImmersionBar.with(this).transparentStatusBar()
                .navigationBarColor(R.color.bg_color).init();
        mItemSign = findViewById(R.id.tv_right);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mItemSex = findViewById(R.id.item_sex);
        mItemBirthday = findViewById(R.id.item_birthday);
        mItemConstellation = findViewById(R.id.item_constellation);
        mItemAddress = findViewById(R.id.item_address);
        mItemHomeAddress = findViewById(R.id.item_home_address);
        mItemJob = findViewById(R.id.item_job);
        mItemHeight = findViewById(R.id.item_height);
        mItemWeight = findViewById(R.id.item_weight);
        mItemIncome = findViewById(R.id.item_income);
        mRlMineInfo = findViewById(R.id.rl_mine_info);
        mTvNameAudit = findViewById(R.id.tv_name_audit);
        mTvSignAudit = findViewById(R.id.tv_sign_audit);
        mTvName = findViewById(R.id.tv_name);
        mIconAudit = findViewById(R.id.tv_icon_audit);
        mClAudio = findViewById(R.id.cl_audio);
        mLlAudio = findViewById(R.id.ll_audio);
        mAudioTime = findViewById(R.id.audio_time);
        mIvAudio = findViewById(R.id.iv_audio);
        mAudioAudit = findViewById(R.id.tv_audio_audit);
        mAudioNormal = findViewById(R.id.tv_audio_normal);
        mAudioIconMsg = findViewById(R.id.audit_icon_msg);
        mAudioAudioMsg = findViewById(R.id.audit_audio_msg);
        initListener();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(mp -> {
            mHandler.sendEmptyMessage(0);
            mMediaPlayer.pause();
            mIvAudio.setImageResource(R.drawable.peony_me_zt_icon);
        });
    }

    private void initListener() {
        findViewById(R.id.rl_avatar).setOnClickListener(this);
        findViewById(R.id.rl_sign).setOnClickListener(this);
        findViewById(R.id.rl_name).setOnClickListener(this);
        findViewById(R.id.iv_edit_photo).setOnClickListener(this);
        mItemBirthday.setOnClickListener(this);
        mItemHomeAddress.setOnClickListener(this);
        mItemJob.setOnClickListener(this);
        mItemHeight.setOnClickListener(this);
        mItemWeight.setOnClickListener(this);
        mItemIncome.setOnClickListener(this);
        mClAudio.setOnClickListener(this);
        mLlAudio.setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);

        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_AUDIO_SIGN_SUCCESS) {   //更新语音成功
                                p.getUserInfoDetail();
                            }
                        }
                ));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_mine_info;
    }


    @Override
    protected void initData() {
        super.initData();
        mUserInfo = (UserInfo) getIntent().getSerializableExtra(Interfaces.EXTRA_USER_INFO);
        setUserInfo();
        p.getUserInfoDetail();
        p.initJsonData(this);
    }

    private void setUserInfo() {
        if (mUserInfo == null) {
            return;
        }
        //GlideUtils.loadRoundAvatarImage(this, mIvAvatar, mUserInfo.getIcon(), mUserInfo.getSex() == 1 ? R.mipmap.mine_ic_boy_default : R.mipmap.mine_ic_girl_default);   //头像
        Utils.setTextOrVisibility(mAudioIconMsg, mUserInfo.limit_audit_icon_msg);
        Utils.setTextOrVisibility(mAudioAudioMsg, mUserInfo.limit_audit_voice_msg);
        mItemSex.setRightText(mUserInfo.getSex() == 1 ? "男" : "女");
        mItemSex.getTvRight().setTextColor(ContextCompat.getColor(this, R.color.color_normal));
        //生日
        setInfo(mItemBirthday, mUserInfo.getBirthday());
        //星座
        setInfo(mItemConstellation, mUserInfo.getConstellation());
        //所在地
        setInfo(mItemAddress, mUserInfo.getCity());
        //家乡
        setFlagHometown(mItemHomeAddress, mUserInfo.hometown);
        //职业
        setInfo(mItemJob, mUserInfo.getJob());
        //身高
        setInfo(mItemHeight, mUserInfo.getHeight());
        //体重
        setInfo(mItemWeight, mUserInfo.getWeight());
        //年收入
        setInfo(mItemIncome, mUserInfo.getIncome());
        //交友宣言
        setSign();
        if (TextUtils.isEmpty(mUserInfo.audit_nickname)) {
            //姓名
            mTvName.setText(mUserInfo.getNickname());
        } else {
            mTvName.setText(mUserInfo.audit_nickname);
        }
        mTvNameAudit.setVisibility(TextUtils.isEmpty(mUserInfo.audit_nickname) ? View.GONE : View.VISIBLE);

        if (TextUtils.isEmpty(mUserInfo.audit_icon)) {   //没有审核中头像
            GlideUtils.loadRoundAvatarImage(this, mIvAvatar, mUserInfo.getIcon(), 0, mUserInfo.getSex() == 1 ? R.mipmap.mine_ic_boy_default : R.mipmap.mine_ic_girl_default);   //头像
            mIconAudit.setVisibility(View.GONE);
        } else {
            mIconAudit.setVisibility(View.VISIBLE);
            GlideUtils.loadRoundAvatarImage(this, mIvAvatar, mUserInfo.audit_icon, 0, mUserInfo.getSex() == 1 ? R.mipmap.mine_ic_boy_default : R.mipmap.mine_ic_girl_default);   //头像
        }

        if (TextUtils.isEmpty(mUserInfo.audit_voice)) {//没有审核音频文件
            mAudioAudit.setVisibility(View.GONE);
            if (TextUtils.isEmpty(mUserInfo.voice)) {   //没有音视频文件
                mLlAudio.setVisibility(View.GONE);
                mAudioNormal.setVisibility(View.VISIBLE);
            } else {
                mediaUrl = mUserInfo.voice;
                mediaTime = Utils.filterTime(mUserInfo.voice_time);
                mLlAudio.setVisibility(View.VISIBLE);
                mAudioNormal.setVisibility(View.GONE);
                mAudioTime.setText(mUserInfo.voice_time);
            }
        } else { // 有审核音频文件
            mediaUrl = mUserInfo.audit_voice;
            mediaTime = Utils.filterTime(mUserInfo.audit_voice_time);
            mLlAudio.setVisibility(View.VISIBLE);
            mAudioNormal.setVisibility(View.GONE);
            mAudioTime.setText(mUserInfo.audit_voice_time);
            mAudioAudit.setVisibility(View.VISIBLE);
        }
        resetMediaPlayer();
    }

    private void resetMediaPlayer() {
        if (TextUtils.isEmpty(mediaUrl)) {
            return;
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setDataSource(mediaUrl);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mUserInfo && mMediaPlayer.isPlaying()) {
            resetMediaPlayer();
            mHandler.sendEmptyMessageDelayed(0, 1000);
            mIvAudio.setImageResource(R.drawable.peony_me_zt_icon);
        }
    }

    @Override
    protected void onDestroy() {
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO));
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void setInfo(CommonItemView textView, String data) {
        if (TextUtils.isEmpty(data) || TextUtils.equals("0", data)) {
            textView.setRightText("待完善");
            textView.setRightTextColor(this, R.color.colorPrimary);
        } else {
            textView.setRightText(data);
            textView.setRightTextColor(this, R.color.color_normal);
        }
    }

    /**
     * 家乡设置
     */
    private void setFlagHometown(CommonItemView textView, String data) {
        if (TextUtils.isEmpty(data) || TextUtils.equals("0", data)) {
            textView.setRightText("待完善");
            textView.setRightTextColor(this, R.color.colorPrimary);
        } else {
            strHometown = data.split(" ");
            textView.setRightText(data);
            textView.setRightTextColor(this, R.color.color_normal);
        }
    }


    private void setAudioSign() {
        if (TextUtils.isEmpty(mUserInfo.audit_desc)) {
            mItemSign.setText(mUserInfo.getDesc());
            mTvSignAudit.setVisibility(View.GONE);
            mItemSign.setTextColor(ContextCompat.getColor(this, R.color.color_normal));
        } else {
            mItemSign.setText(mUserInfo.audit_desc);
            mItemSign.setTextColor(ContextCompat.getColor(this, R.color.color_normal));
            mTvSignAudit.setVisibility(View.VISIBLE);
        }
    }

    private void setSign() {
        if (TextUtils.isEmpty(mUserInfo.getDesc()) || TextUtils.equals("0", mUserInfo.getDesc())) {
            if (TextUtils.isEmpty(mUserInfo.audit_desc)) {
                mItemSign.setText("待完善");
                mItemSign.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                mTvSignAudit.setVisibility(View.GONE);
            } else {
                mItemSign.setText(mUserInfo.audit_desc);
                mItemSign.setTextColor(ContextCompat.getColor(this, R.color.color_normal));
                mTvSignAudit.setVisibility(View.VISIBLE);
            }
        } else {
            setAudioSign();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_name) {  //姓名
            if (mUserInfo != null && !TextUtils.isEmpty(mUserInfo.audit_nickname)) {
                toastTip("昵称正在审核，无法更改");
                return;
            }
            EditInfoActivity.startForName(this, REQUEST_NAME_CODE);
        } else if (id == R.id.rl_sign) {  //交友宣言
            if (mUserInfo != null && !TextUtils.isEmpty(mUserInfo.audit_desc)) {
                toastTip("交友宣言正在审核，无法更改");
                return;
            }
            if (mUserInfo != null)
                EditInfoActivity.startForSign(this, mUserInfo.getDesc(), REQUEST_SIGN);
        } else if (id == R.id.rl_avatar || id == R.id.iv_edit_photo) {  //头像
            if (mUserInfo != null) {
                if (!TextUtils.isEmpty(mUserInfo.audit_icon)) {
                    toastTip("头像正在审核，无法更改");
                    return;
                }
                if (mUserInfo.is_save_audit_icon != 1) {
                    toastTip(mUserInfo.limit_audit_icon_msg);
                    return;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // 先判断有没有权限
                if (Environment.isExternalStorageManager()) {
                    choosePic();
                } else {
                    PermissionUtil.toAllFilePermissionSetting(this, REQUEST_CODE);
                }
            } else {
                choosePic();
            }
        } else if (id == R.id.item_birthday) {   //生日
            String text = Utils.getText(mItemBirthday.getTvRight());
            if (TextUtils.isEmpty(text)) {
                if (service.getUserInfo().isGirl()) {
                    text = Utils.getOldYearDate(Interfaces.DEFAULT_GIRL_AGE);
                } else {
                    text = Utils.getOldYearDate(Interfaces.DEFAULT_BOY_AGE);
                }
            }
            String[] split = Utils.splitBirthday(text);
            if (split.length == 3) {
                getP().timeChoose(this, mRlMineInfo, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            }
        } else if (id == R.id.item_home_address) {   //家乡
            getP().showPicker(this, mRlMineInfo, strHometown);
        } else if (id == R.id.item_job) {   //工作
            EditInfoActivity.startForJob(this, mItemJob.getTvRight().getText().toString(), REQUEST_JOB_CODE);
        } else if (id == R.id.item_height) {   //身高
            if (mUserInfo != null)
                getP().chooseHeight(this, mRlMineInfo, mUserInfo.getHeight());
        } else if (id == R.id.item_weight) {   //体重
            if (mUserInfo != null)
                getP().chooseWeight(this, mRlMineInfo, mUserInfo.getWeight());
        } else if (id == R.id.item_income) {   //收入
            if (mUserInfo != null)
                getP().chooseIncome(this, mRlMineInfo, mUserInfo.getIncome());
        } else if (id == R.id.tv_save) {
            if (mUserInfo != null)
                getP().updateUserInfo(mUserInfo.getBirthday(), mUserInfo.getHometown(), mUserInfo.getHeight(), mUserInfo.getWeight(), mUserInfo.getIncome());
        } else if (id == R.id.cl_audio) { //语音签名
            if (mUserInfo != null) {
                if (!TextUtils.isEmpty(mUserInfo.audit_voice)) {
                    toastTip("语音签名正在审核，无法更改");
                    return;
                }
                if (mUserInfo.is_save_audit_voice != 1) {
                    toastTip(mUserInfo.limit_audit_voice_msg);
                    return;
                }
            }
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_VOICESIGN);
        } else if (id == R.id.ll_audio) { //播放语音
            if (!mMediaPlayer.isPlaying()) {
                mediaTimeTemp = mediaTime;
                mMediaPlayer.start();
                mIvAudio.setImageResource(R.mipmap.peony_me_bf_icon);
                mHandler.sendEmptyMessageDelayed(1, 1000);
            } else {
                resetMediaPlayer();
                mIvAudio.setImageResource(R.drawable.peony_me_zt_icon);
                mHandler.sendEmptyMessage(0);
            }
        }
    }

    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mediaTimeTemp > 0) {
                        mediaTimeTemp--;
                    }
                    mAudioTime.setText(String.format("%ss", mediaTimeTemp));
                    sendEmptyMessageDelayed(1, 1000);
                    break;

                default:
                    mAudioTime.setText(String.format("%ss", mediaTime));
                    removeCallbacksAndMessages(null);
                    break;
            }
        }
    };


    private void choosePic() {
        final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionUtil.beforeCheckPermission(this, permissions, agreeToRequest -> {
            if (agreeToRequest) {
                mCompositeDisposable.add(new RxPermissions(this)
                        .request(permissions)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                UploadAvatarPopWindow popWindow = new UploadAvatarPopWindow(this);
                                popWindow.addOnClickListener(() -> {
                                    ChoosePicUtils.picSingle(MineInfoActivity.this, true, new OnResultCallbackListener<LocalMedia>() {
                                        @Override
                                        public void onResult(List<LocalMedia> result) {
                                            if (result != null && result.size() > 0) {
                                                LocalMedia localMedia = result.get(0);
                                                if (localMedia.isCut()) {
                                                    path = localMedia.getCutPath();
                                                } else {
                                                    path = localMedia.getPath();
                                                }
                                                getP().uploadAvatar(path);
                                            }
                                        }

                                        @Override
                                        public void onCancel() {

                                        }
                                    });
                                });
                                popWindow.showPopupWindow();
                            } else {
                                Utils.toast("请允许摄像头权限");
                            }
                        }));
            }
        });
    }

    /**
     * 生日
     *
     * @param data
     */
    @Override
    public void getChooseBirthday(String data, String constellation) {
        if (mUserInfo != null) {
            mUserInfo.setBirthday(data);
            mItemBirthday.setRightText(data);
            mItemBirthday.setRightTextColor(this, R.color.color_normal);
            mUserInfo.constellation = data;
            mItemConstellation.setRightText(constellation);
            mItemConstellation.setRightTextColor(this, R.color.color_normal);
        }
    }

    /**
     * 家乡
     *
     * @param data
     */
    @Override
    public void getChooseAddress(String data) {
        if (mUserInfo != null) {
            mUserInfo.hometown = data;
            mItemHomeAddress.setRightText(data);
            mItemHomeAddress.setRightTextColor(this, R.color.color_normal);
        }
    }

    /**
     * 身高
     *
     * @param data
     */
    @Override
    public void getChooseHeight(String data) {
        if (mUserInfo != null) {
            mUserInfo.setHeight(data);
            mItemHeight.setRightText(data);
            mItemHeight.setRightTextColor(this, R.color.color_normal);
        }
    }

    /**
     * 体重
     *
     * @param data
     */
    @Override
    public void getChooseWeight(String data) {
        if (mUserInfo != null) {
            mUserInfo.setWeight(data);
            mItemWeight.setRightText(data);
            mItemWeight.setRightTextColor(this, R.color.color_normal);
        }
    }

    /**
     * 收入
     *
     * @param data
     */
    @Override
    public void getChooseIncome(String data) {
        if (mUserInfo != null) {
            mUserInfo.setIncome(data);
            mItemIncome.setRightText(data);
            mItemIncome.setRightTextColor(this, R.color.color_normal);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_NAME_CODE) {
                if (data != null && mUserInfo != null) {
                    mTvName.setText(data.getStringExtra("type"));
                    if (service.getUserInfo() != null && service.getUserInfo().isGirl()) {
                        mTvNameAudit.setVisibility(View.VISIBLE);
                        mUserInfo.audit_nickname = data.getStringExtra("type");
                    }
                }
//                mItemNickName.setRightText("审核中");
//                mItemNickName.setRightTextColor(this, R.color.colorPrimary);
            } else if (requestCode == REQUEST_JOB_CODE) {
                if (data != null && mUserInfo != null) {
                    mUserInfo.setJob(data.getStringExtra("type"));
                    setInfo(mItemJob, mUserInfo.getJob());
                }
            } else if (requestCode == REQUEST_SIGN) {
                if (data != null && mUserInfo != null) {
                    String desc = data.getStringExtra("type");
                    mItemSign.setText(desc);
                    mItemSign.setTextColor(ContextCompat.getColor(this, R.color.color_normal));
                    mUserInfo.setDesc(desc);
                    if (service.getUserInfo() != null && service.getUserInfo().isGirl()) {
                        mTvSignAudit.setVisibility(View.VISIBLE);
                        mUserInfo.audit_desc = desc;
                    }
                }

            }
        }
        if (requestCode == REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                toastTip(getString(R.string.no_external_premiss));
            }
        }
    }


    @Override
    public void updateUserInfoSuccess(String data) {
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
        ToastUtil.showToast(this, "更改成功");
        finish();
    }

    @Override
    public void uploadAvatarSuccess(String data) {
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
        //GlideUtils.loadRoundImage(this, mIvAvatar, path);
        p.getUserInfoDetail();
    }

    @Override
    public void uploadAvatarFail(String data) {
        hideLoadingDialog();
    }

    @Override
    public void getUserInfoSuccess(UserInfo data) {
        mUserInfo = data;
        setUserInfo();
    }

    @Override
    public void getPhotoSuccess(List<String> data) {

    }
}
