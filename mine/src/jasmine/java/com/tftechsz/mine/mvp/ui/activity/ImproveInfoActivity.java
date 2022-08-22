package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.blankj.utilcode.util.RegexUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.NetworkUtil;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.SoftHideKeyBoardUtil;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CustomFilter;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.req.CompleteReq;
import com.tftechsz.mine.mvp.IView.IImproveInfoView;
import com.tftechsz.mine.mvp.presenter.ImproveInfoPresenter;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import razerdp.util.KeyboardUtils;

/**
 * 完善个人信息
 */
public class ImproveInfoActivity extends BaseMvpActivity<IImproveInfoView, ImproveInfoPresenter> implements View.OnClickListener, IImproveInfoView {
    private final int REQUEST_CODE = 10000;
    private RelativeLayout mLlBoy, mLlGirl;  //男孩 女孩布局
    private TextView mTvBirthday;
    private EditText mEtName;   //姓名
    private EditText mEtCode;   //邀请码
    private CompleteReq mCompleteReq;  //用户信息
    private ImageView mIvIcon;
    private String mBoyUrl, mGirlUrl;
    private String mAvatar, mPath;
    private boolean isChangedBirthday;//是否修改过生日
    private boolean isChangedIcon;//是否修改过头像

    private int mType;   //登录类型   0 手机号  1 微信 2 QQ
    @Autowired
    UserProviderService service;
    private ImageView mIvRotate;
    private ImageView mIvBoy;
    private ImageView mIvGirl;
    private LinearLayout mRandomView;
    private CustomPopWindow mNoDataPop;
    private CustomPopWindow mGirlPop;
    private CustomPopWindow mBackPop;
    private View ivBack;

    @Override
    public ImproveInfoPresenter initPresenter() {
        return new ImproveInfoPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("完善个人资料")
                .build();
        findViewById(R.id.tv_complete).setOnClickListener(this);
        mIvBoy = findViewById(R.id.iv_sex_boy);
        mIvBoy.setOnClickListener(this);
        mIvGirl = findViewById(R.id.iv_sex_girl);
        mIvGirl.setOnClickListener(this);
        mTvBirthday = findViewById(R.id.tv_birthday);
        mTvBirthday.setOnClickListener(this);
        mIvRotate = findViewById(R.id.iv_rotate);
        mEtName = findViewById(R.id.et_name);
        mEtCode = findViewById(R.id.et_code);
        mIvIcon = findViewById(R.id.iv_icon);
        mRandomView = findViewById(R.id.ll_random);
        findViewById(R.id.rl_icon).setOnClickListener(this);
        mRandomView.setOnClickListener(this);

        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_BIND_PHONE_SUCCESS) {  //绑定成功
                                finish();
                            }
                        }
                ));
    }

    @Override
    protected void initData() {
        super.initData();
        mCompleteReq = new CompleteReq();
        mCompleteReq.sex = 1;
        mType = getIntent().getIntExtra("type", 0);
        mEtName.setFilters(new InputFilter[]{new CustomFilter(Constants.MAX_NAME_LENGTH)});
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mCompleteReq.sex == 2) {
                    tempGirlName = s.toString();
                } else {
                    tempBoyName = s.toString();
                }
            }
        });
        //暂时不走了
//        if (null != service.getConfigInfo() && null != service.getConfigInfo().api && null != service.getConfigInfo().api.oss && null != service.getConfigInfo().api.oss.cdn) {
//            if (null != service.getConfigInfo().sys && service.getConfigInfo().sys.user != null) {
//                mBoyUrl = service.getConfigInfo().api.oss.cdn_scheme + service.getConfigInfo().api.oss.cdn.user + service.getConfigInfo().sys.user.boy_icon;
//                mGirlUrl = service.getConfigInfo().api.oss.cdn_scheme + service.getConfigInfo().api.oss.cdn.user + service.getConfigInfo().sys.user.girl_icon;
//            }
//        }
//        GlideUtils.loadRoundImage(this, mIvSex, mGirlUrl);
        String inviteCode = MMKVUtils.getInstance().decodeString(Constants.H5_INVITE_CODE_PARAM);
        String tempCode;
        if (inviteCode.isEmpty()) {
            tempCode = getP().getReferralCode(mActivity);
        } else {
            tempCode = inviteCode;
        }
        mEtCode.postDelayed(() -> mEtCode.setText(tempCode), 100);
        p.getCompleteInfo();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_improve_info;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) { //返回
            onBackPressed();
        } else if (id == R.id.rl_icon) { //头像
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // 先判断有没有权限
                if (Environment.isExternalStorageManager()) {
                    choosePic();
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_CODE);
                }
            } else {
                choosePic();
            }
        } else if (id == R.id.iv_sex_boy) {   //男孩
            if (mCompleteReq.sex == 1) {
                return;
            }
            tempGirlName = Utils.getText(mEtName);
            tempGirlBir = Utils.getText(mTvBirthday);
            if (!isChangedIcon) {//未修改过头像
                mIvIcon.setImageResource(R.mipmap.ic_avatar_male);
            }
            mCompleteReq.sex = 1;
            setSexView(0);
        } else if (id == R.id.iv_sex_girl) {   //女孩
            if (mCompleteReq.sex == 2) {
                return;
            }
            tempBoyName = Utils.getText(mEtName);
            tempBoyBir = Utils.getText(mTvBirthday);
            if (!isChangedIcon) {//未修改过头像
                mIvIcon.setImageResource(R.mipmap.ic_avatar_female);
            }
            mCompleteReq.sex = 2;
            setSexView(1);
        } else if (id == R.id.ll_random) { //随机名称
            if (!NetworkUtil.isNetworkAvailable(mContext)) {
                toastTip("暂无网络，请连接网络后重试！");
                return;
            }
            AnimationUtil.createRotateAnimation(mIvRotate);
            p.getRandomNikeName(mIvRotate);
        } else if (id == R.id.tv_birthday) {//生日
            KeyboardUtils.close(this);
            if (!TextUtils.isEmpty(Utils.getText(mTvBirthday))) {
                String[] split = Utils.splitBirthday(Utils.getText(mTvBirthday));
                p.timeChoose(this, findViewById(R.id.rl_complete), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            } else {
                String[] split;
                if (mCompleteReq.sex == 2) {
                    split = Utils.getOldYearDate(mCompleteReq.birthday_girl).split("-");
                } else {
                    split = Utils.getOldYearDate(mCompleteReq.birthday_boy).split("-");
                }
                p.timeChoose(this, findViewById(R.id.rl_complete), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            }
        } else if (id == R.id.tv_complete) {   //完成
            if (!ClickUtil.canOperate()) return;
            if (mCompleteReq.sex == 2 && !isChangedIcon) {
                boolean isUnderReview = MMKVUtils.getInstance().decodeBoolean(Constants.KEY_IS_REVIEW, true);
                if (!isUnderReview) {
                    toastTip("请上传您的头像");
                    return;
                }
            }
            if (TextUtils.isEmpty(Utils.getText(mEtName))) {
                toastTip("请输入您的昵称");
                Utils.setFocus(mEtName);
                return;
            }
            if (TextUtils.isEmpty(mCompleteReq.birthday)) {
                toastTip("请选择您的生日");
                return;
            }
            if (!RegexUtils.isMatch("^[\\u4e00-\\u9fa5]+$", mEtName.getText().toString())) {
                toastTip("昵称仅支持中文");
                return;
            }
            if (mCompleteReq.sex == 2) {
                if (mGirlPop == null)
                    mGirlPop = new CustomPopWindow(mContext, 1)
                            .setContentText(new SpannableStringUtils.Builder()
                                    .append("您当前选择的性别为:")
                                    .append(" 女")
                                    .setForegroundColor(Utils.getColor(R.color.red))
                                    .append("\n一旦确认就不可更改，是否确认注册?")
                                    .create())
                            .setLeftButton("返回修改")
                            .setRightButton("确认注册")
                            .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                                @Override
                                public void onCancel() {
                                    mGirlPop.dismiss();
                                }

                                @Override
                                public void onSure() {
                                    mCompleteReq.nickname = tempGirlName;
                                    mCompleteReq.user_code = Utils.getText(mEtCode);
                                    mCompleteReq.icon = mAvatar;
                                    if (mType == 1) { //微信登录需要绑定手机号
                                        Intent intent = new Intent(mContext, BindPhoneActivity.class);
                                        intent.putExtra(Interfaces.EXTRA_DATA, mCompleteReq);
                                        intent.putExtra(Interfaces.EXTRA_TYPE, 0);
                                        startActivity(intent);
                                    } else {
                                        p.completeInfo(mCompleteReq);
                                    }
                                }
                            });
                mGirlPop.showPopupWindow();
            } else {
                mCompleteReq.nickname = tempBoyName;
                mCompleteReq.user_code = Utils.getText(mEtCode);
                mCompleteReq.icon = mAvatar;
                p.completeInfo(mCompleteReq);
            }
        }
    }


    private void choosePic() {
        mCompositeDisposable.add(new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        ChoosePicUtils.picSingle(this, true, new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(List<LocalMedia> selectList) {
                                if (null != selectList && selectList.size() > 0) {
                                    mPath = selectList.get(0).getCutPath();
                                    getP().uploadAvatar(mPath);
                                }
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    } else {
                        Utils.toast("请允许摄像头权限");
                    }
                }));
    }

    private void showNoDataPop() {
        if (mNoDataPop == null)
            mNoDataPop = new CustomPopWindow(mContext, 1)
                    .setContent("个人资料不完整无法提交\n" + "请您完善资料")
                    .setSingleButton()
                    .setRightButton("知道了")
                    .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                        @Override
                        public void onCancel() {
                            mNoDataPop.dismiss();
                        }

                        @Override
                        public void onSure() {

                        }
                    });
        mNoDataPop.showPopupWindow();
    }

    private void setBirthday(String birthday) {
        if (mCompleteReq.sex == 2) {
            tempGirlBir = birthday;
        } else {
            tempBoyBir = birthday;
        }
        mCompleteReq.birthday = birthday;
        mTvBirthday.setText(birthday);
    }

    @Override
    public void onBackPressed() {
        if (mBackPop == null)
            mBackPop = new CustomPopWindow(mContext, 1)
                    .setContent("差一步就完成注册了\n" + "把握缘分，早日脱单～")
                    .setSingleButton()
                    .setRightButton("继续完善")
                    .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                        @Override
                        public void onCancel() {
                            mCompleteReq.nickname = Utils.getText(mEtName);
                            mCompleteReq.user_code = Utils.getText(mEtCode);
                            if (mCompleteReq.sex == 0) {
                                mCompleteReq.sex = 1;
                            }
                            mCompleteReq.icon = mAvatar;
                            p.completeInfo(mCompleteReq);
                        }

                        @Override
                        public void onSure() {

                        }
                    });
        mBackPop.showPopupWindow();
    }

    /**
     * 上传头像成功
     *
     * @param url
     */
    @Override
    public void uploadAvatarSucceeded(String url) {
        Utils.runOnUiThread(() -> {
            mAvatar = url;
            isChangedIcon = true;
            GlideUtils.loadRouteImage(ImproveInfoActivity.this, mIvIcon, mPath);
        });
    }

    @Override
    public void getRandomNicknameSucceeded(String nikename) {
        mEtName.setText(nikename);
        Utils.setFocus(findViewById(R.id.rl_complete));
    }

    /**
     * 选择生日
     */
    @Override
    public void chooseBirthday(String day) {
        setBirthday(day);
        isChangedBirthday = true;
    }

    /**
     * 完善信息成功
     */
    @Override
    public void completeInfoSuccess(String msg) {
        //toastTip(msg);
        MobclickAgent.onEvent(ImproveInfoActivity.this, "user_register");
        SPUtils.put(Constants.IS_COMPLETE_INFO, 1);
//        /**
//         * 字节跳动推广新注册用户
//         */
//        JSONObject object = new JSONObject();
//        try {
//            object.put("user_register", UserManager.getInstance().getUserId());
//            GameReportHelper.onEventRegister(object.toString(),true);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        p.getUserInfo(this);
    }

    /**
     * 设置获取的个人信息
     */
    private String tempBoyName, tempBoyBir, tempGirlName, tempGirlBir;

    @Override
    public void getCompleteInfoSuccess(CompleteReq data) {
        if (null != data) {
            mCompleteReq = data;
            if (mCompleteReq.sex == 2) { //女性逻辑
                setSexView(1);
            } else { //男性逻辑
                tempBoyName = data.nickname;
                tempBoyBir = Utils.getOldYearDate(mCompleteReq.birthday_boy);
                if (!TextUtils.isEmpty(mCompleteReq.icon)) {
                    isChangedIcon = true;
                    mAvatar = mCompleteReq.icon;
                    GlideUtils.loadRouteImage(ImproveInfoActivity.this, mIvIcon, mAvatar);
                }
                setSexView(0);
                setBirthday(Utils.getOldYearDate(mCompleteReq.birthday_boy));
                mEtName.setText(mCompleteReq.nickname);
            }
        }
    }

    private void setSexView(int i) {
        switch (i) {
            case 0: //男
                mCompleteReq.sex = 1;
                mCompleteReq.nickname = tempBoyName;
                mCompleteReq.birthday = tempBoyBir;
                mEtName.setText(tempBoyName);
                if (!TextUtils.isEmpty(tempBoyName))
                    mEtName.setSelection(tempBoyName.length());
                mTvBirthday.setText(tempBoyBir);

                mRandomView.setVisibility(View.VISIBLE);
                mIvBoy.setSelected(true);
                mIvGirl.setSelected(false);
                break;

            case 1: //女
                mCompleteReq.sex = 2;
                mCompleteReq.nickname = tempGirlName;
                mCompleteReq.birthday = tempGirlBir;
                mEtName.setText(tempGirlName);
                if (!TextUtils.isEmpty(tempGirlName))
                    mEtName.setSelection(tempGirlName.length());
                mTvBirthday.setText(tempGirlBir);
                mRandomView.setVisibility(View.GONE);
                mIvBoy.setSelected(false);
                mIvGirl.setSelected(true);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                toastTip(getString(R.string.no_external_premiss));
            }
        }
    }
}
