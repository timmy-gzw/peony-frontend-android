package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.FileUtils;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.permissions.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.OcrCheckDto;
import com.tftechsz.common.entity.RealCheckDto;
import com.tftechsz.mine.mvp.IView.IRealNameView;
import com.tftechsz.mine.mvp.presenter.RealNamePresenter;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 实名认证
 */
@Route(path = ARouterApi.ACTIVITY_REAL)
public class RealNameActivity extends BaseMvpActivity<IRealNameView, RealNamePresenter> implements View.OnClickListener, IRealNameView {

    public final int RESULT_FONT_CARD = 10000;
    public final int RESULT_BACK_CARD = 10001;

    private EditText mEtName, mEtPhone;
    private ImageView mIvFontCard, mIvBackCard;
    private String mFontPath, mBackPath;
    @Autowired
    UserProviderService service;
    private TextView mTvHint;
    private RxPermissions mRxPermissions;
    private Uri photoUri;
    private File mFontFile;
    private File mBackFile;
    private CustomPopWindow mPopWindow;
    private String mFileName;
    private CharSequence mWechat;

    @Override
    public RealNamePresenter initPresenter() {
        return new RealNamePresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("实名认证")
                .build();
        mEtName = findViewById(R.id.et_name);
        mEtPhone = findViewById(R.id.et_phone);
        mIvBackCard = findViewById(R.id.iv_back_card);
        mIvFontCard = findViewById(R.id.iv_font_card);
        mTvHint = findViewById(R.id.tv_hint);
        initListener();
        mRxPermissions = new RxPermissions(mActivity);

        ConfigInfo configInfo = service.getConfigInfo();
        if (configInfo != null && configInfo.sys != null && configInfo.sys.feedback_contact != null) {
            mWechat = Utils.matchWechatNumber(configInfo.sys.feedback_contact.wechat);
        } else {
            mWechat = "peonygf";
        }
    }

    private void initListener() {
        findViewById(R.id.tv_commit).setOnClickListener(this);   // 提交
        mIvFontCard.setOnClickListener(this);
        mIvBackCard.setOnClickListener(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_real_name;
    }

    @Override
    protected void initData() {
        super.initData();
        mTvHint.setText(new SpannableStringUtils.Builder().append("无法完成实名认证，添加官方微信公众号: ")
                .append(mWechat)
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", mWechat);
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        toastTip("复制成功");
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        ds.setColor(Utils.getColor(R.color.blue_7F89F3));
                        ds.setUnderlineText(true);
                    }
                })
                .create());
        mTvHint.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canOperate()) return;
        int id = v.getId();
        if (id == R.id.tv_commit) {  //// 提交
            String name = Utils.getText(mEtName);
            String phone = Utils.getText(mEtPhone);
            /*if (Utils.checkNull(mEtName, "姓名不能为空")) {
                return;
            }
            if (!Utils.regexChineseName(mConfigInfo, name)) {
                Utils.setFocus(mEtName);
                toastTip("姓名格式错误");
                return;
            }
            if (Utils.checkNull(mEtPhone, "身份证号不能为空")) {
                return;
            }
            if (!Utils.regexIdCard(mConfigInfo, phone)) {
                Utils.setFocus(mEtPhone);
                toastTip("身份证格式错误");
                return;
            }*/
            if (mFontFile == null) {
                toastTip("身份证人像面不能为空");
                return;
            }
            if (mBackFile == null) {
                toastTip("身份证国徽面不能为空");
                return;
            }
            runOnUiThread(() -> GlobalDialogManager.getInstance().show(getFragmentManager(), "正在上传中，请稍等..."));
            p.uploadRealNameAvatar(mFontFile.getAbsolutePath(), RESULT_FONT_CARD);
        } else if (id == R.id.iv_font_card) {   //正面
            mCompositeDisposable.add(mRxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            ARouterUtils.toRealCamera(mActivity, 1);
                            //startOpenCamera(RESULT_FONT_CARD);
//            ChoosePicUtils.picSingle(this, RESULT_FONT_CARD);
                        } else {
                            toastTip("权限被禁止，无法打开相机");
                        }
                    }));
        } else if (id == R.id.iv_back_card) {  //反面
            mCompositeDisposable.add(mRxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            ARouterUtils.toRealCamera(mActivity, 2);
                            //startOpenCamera(RESULT_BACK_CARD);
//            ChoosePicUtils.picSingle(this, RESULT_BACK_CARD);
                        } else {
                            toastTip("权限被禁止，无法打开相机");
                        }
                    }));
        }
    }

    private void startOpenCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       /* if (SdkVersionUtils.checkedAndroid_Q()) {
            photoUri = Utils.createImageUri(mContext);
        } else {*/
        //图片的文件名
        mFileName = getCacheDir().getAbsolutePath() + File.separator + (requestCode == RESULT_FONT_CARD ? "font_card_" : "back_card_") + System.currentTimeMillis() + ".jpg";
        FileUtils.createOrExistsFile(mFileName);
        photoUri = Utils.parUri(this, new File(mFileName));
        // }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        mActivity.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_FONT_CARD) {
                mFontFile = new File(mFileName);
                GlideUtils.loadImage(mContext, mIvFontCard, mFileName);
            } else if (requestCode == RESULT_BACK_CARD) {
                mBackFile = new File(mFileName);
                GlideUtils.loadImage(mContext, mIvBackCard, mFileName);
            } else if (data != null && requestCode == Interfaces.EXTRA_REAL_CAMERA_SFZ_FONT) {//自定义相机前照
                String name = data.getStringExtra(Interfaces.EXTRA_PATH);
                if (name != null) {
                    mFontFile = new File(name);
                    GlideUtils.loadRouteImage(this, mIvFontCard, name);
                }
            } else if (data != null && requestCode == Interfaces.EXTRA_REAL_CAMERA_SFZ_BACK) {//自定义相机后照
                String name = data.getStringExtra(Interfaces.EXTRA_PATH);
                if (name != null) {
                    mBackFile = new File(name);
                    GlideUtils.loadImage(mContext, mIvBackCard, name);
                }
            }

            /*String path = "";
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (null != selectList && selectList.size() > 0) {
                if (selectList.get(0).isCompressed()) {
                    path = selectList.get(0).getCompressPath();
                } else {
                    path = selectList.get(0).getPath();
                }
            }
            showLoadingDialog();
            if (requestCode == RESULT_FONT_CARD) {
                //p.uploadRealNameAvatar(path, RESULT_FONT_CARD);
                mFontFile = new File(path);
                GlideUtils.loadRouteImage(this, mIvFontCard, path);
                mIvFont.setVisibility(View.GONE);
            } else if (requestCode == RESULT_BACK_CARD) {
                //p.uploadRealNameAvatar(path, RESULT_BACK_CARD);
                mBackFile = new File(path);
                GlideUtils.loadRouteImage(this, mIvBackCard, path);
                mIvBack.setVisibility(View.GONE);
            }*/
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.delete(mFontFile);
        FileUtils.delete(mBackFile);
    }

    @Override
    public void uploadRealNameInfoSuccess(Boolean data) {
        hideLoadingDialog();
        finish();
        ARouterUtils.toRealSuccessActivity(0);
    }

    @Override
    public void uploadRealNameInfoNewSuccess(RealCheckDto data) {
        hideLoadingDialog();
        if (data.pass) {
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
            ARouterUtils.toRealSuccessActivity(1);
        } else {
            ARouterUtils.toRealSuccessActivity(2, data.msg);
        }
        //Utils.toast(data.msg);
        finish();
    }

    @Override
    public void uploadCardSuccess(String data, int code) {
        if (code == RESULT_FONT_CARD) {
            mFontPath = data;
            p.uploadRealNameAvatar(mBackFile.getAbsolutePath(), RESULT_BACK_CARD);
        } else {
            hideLoadingDialog();
            mBackPath = data;
            runOnUiThread(() -> GlobalDialogManager.getInstance().show(getFragmentManager(), "正在识别中，请稍等..."));
            Utils.isOpenAuth(data1 -> {
                if (data1) {
                    p.ocrCheck(mFontPath, mBackPath);
                } else {
                    p.uploadRealNameInfo("", "", mFontPath, mBackPath);
                }
            });
        }
    }

    @Override
    public void uploadRealNameInfoError() {
        //finish();
    }

    @Override
    public void ocrCheckSuccess(OcrCheckDto data) {
        SpannableStringBuilder ssb = new SpannableStringUtils.Builder()
                .append("姓名: ")
                .setFontSize(15, true)
                .append(data.realname)
                .setFontSize(17, true)
                .setBold()
                .append("\n身份证: ")
                .setFontSize(15, true)
                .append(data.identity)
                .setFontSize(17, true)
                .setBold()
                .append("\n\n")
                .append(data.tips)
                .setFontSize(13, true)
                .setForegroundColor(Utils.getColor(R.color.color_cc))
                .create();

        if (mPopWindow == null) {
            mPopWindow = new CustomPopWindow(mContext)
                    .setTitle("识别结果")
                    .setLeftButton("返回重拍")
                    .setRightButton("立即认证")

                    .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                        @Override
                        public void onCancel() {
                            mPopWindow.dismiss();
                        }

                        @Override
                        public void onSure() {
                            runOnUiThread(() -> GlobalDialogManager.getInstance().show(getFragmentManager(), "正在认证中，请稍等..."));
                            p.uploadRealNameInfoNew("", "", mFontPath, mBackPath);
                        }
                    });
            mPopWindow.setOutSideDismiss(false);
        }
        mPopWindow.setContent(ssb, Gravity.START)
                .showPopupWindow();

    }
}
