package com.tftechsz.family.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.family.R;
import com.tftechsz.family.mvp.IView.IEditFamilyInfoView;
import com.tftechsz.family.mvp.presenter.EditFamilyInfoPresenter;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.StringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;

/**
 * 编辑家族信息
 */
@Route(path = ARouterApi.ACTIVITY_EDIT_FAMILY)
public class EditFamilyInfoActivity extends BaseMvpActivity<IEditFamilyInfoView, EditFamilyInfoPresenter> implements View.OnClickListener, IEditFamilyInfoView {

    private final static int TYPE_EDIT_SIGN = 1;  //编辑家族宣言
    private final static int TYPE_EDIT_NAME = 2;  //编辑家族名称
    private final static int TYPE_DISSOLUTION = 3;   //解散家族
    private final static int TYPE_EXIT = 4;   //退出家族
    public final static int TYPE_ANNOUNCEMENT = 5;  //编辑家族公告
    private final static int TYPE_VOICE_NAME = 6;  //编辑语音房公告
    private final static int TYPE_EDIT_PARTY_NAME = 7;  //编辑派对名称

    private EditText mEtContent;
    private TextView mTvContent, mTvCommit;
    private TextView mTvTitle;
    private TextView mTvNumber;
    private int roleId;
    private int type;
    private int length;
    private String value;

    @Autowired
    UserProviderService service;


    public static void startForSign(Activity context, String desc) {
        Intent i = new Intent(context, EditFamilyInfoActivity.class);
        i.putExtra("type", TYPE_EDIT_SIGN);
        i.putExtra("desc", desc);
        context.startActivity(i);
    }

    public static void startForAnnouncement(Activity context, String desc, int role) {
        Intent i = new Intent(context, EditFamilyInfoActivity.class);
        i.putExtra("type", TYPE_ANNOUNCEMENT);
        i.putExtra("desc", desc);
        i.putExtra("role", role);
        context.startActivity(i);
    }

    public static void startForName(Activity context, String name) {
        Intent i = new Intent(context, EditFamilyInfoActivity.class);
        i.putExtra("type", TYPE_EDIT_NAME);
        i.putExtra("name", name);
        context.startActivity(i);
    }


    public static void startForDissolution(Activity context) {
        Intent i = new Intent(context, EditFamilyInfoActivity.class);
        i.putExtra("type", TYPE_DISSOLUTION);
        context.startActivity(i);
    }

    public static void startForExit(Activity context) {
        Intent i = new Intent(context, EditFamilyInfoActivity.class);
        i.putExtra("type", TYPE_EXIT);
        context.startActivity(i);
    }

    @Override
    public EditFamilyInfoPresenter initPresenter() {
        return new EditFamilyInfoPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mEtContent = findViewById(R.id.et_content);
        mTvContent = findViewById(R.id.tv_content);
        mTvTitle = findViewById(R.id.toolbar_title);
        mTvCommit = findViewById(R.id.toolbar_tv_menu);
        mTvNumber = findViewById(R.id.tv_number);
        initListener();

    }

    private void initListener() {
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mTvCommit.setOnClickListener(this);
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_edit_family_info;
    }


    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getIntExtra("type", -1);
        roleId = getIntent().getIntExtra("role", -1);
        String name = getIntent().getStringExtra("name");
        String desc = getIntent().getStringExtra("desc");
        value = getIntent().getStringExtra("value");
        if (type == TYPE_EDIT_SIGN) {
            mTvTitle.setText("编辑家族宣言");
            mEtContent.setHint("请输入家族宣言");
            mEtContent.setText(desc);
            length = 200;
            p.setLength(mEtContent, length);
        }
        if (type == TYPE_ANNOUNCEMENT) {
            mTvTitle.setText("编辑家族公告");
            mEtContent.setHint("请输入家族公告");
            mTvCommit.setText("发布");
            if (roleId != 64 && roleId != 32) {
                mTvCommit.setVisibility(View.GONE);
                mEtContent.setVisibility(View.GONE);
                mTvNumber.setVisibility(View.GONE);
                mTvContent.setVisibility(View.VISIBLE);
                mTvContent.setText(desc);
                mTvTitle.setText("家族公告");
                return;
            }
            mEtContent.setText(desc);
            length = 200;
            p.setLength(mEtContent, length);
        } else if (type == TYPE_EDIT_NAME) {
            mTvTitle.setText("更改家族名称");
            mEtContent.setHint("请输入家族名称");
            mEtContent.setText(name);
            length = 20;
            p.setLength(mEtContent, length, true);
        } else if (type == TYPE_EDIT_PARTY_NAME) {
            mTvTitle.setText("更改派对名称");
            mEtContent.setHint("请输入派对名称");
            mEtContent.setText(desc);
            length = 30;
            p.setLength(mEtContent, length, true);
        } else if (type == TYPE_DISSOLUTION) {
            mTvTitle.setText("解散家族");
            mEtContent.setHint("请输入解散理由");
            length = 30;
            p.setLength(mEtContent, length);
        } else if (type == TYPE_EXIT) {
            mTvTitle.setText("退出家族");
            mEtContent.setHint("请输入退出理由");
            length = 30;
            p.setLength(mEtContent, length);
        } else if (type == TYPE_VOICE_NAME) {
            mTvTitle.setText("请编辑语音房公告");
            mEtContent.setHint("请输入语音房公告");
            mEtContent.setText(desc);
            length = 200;
            p.setLength(mEtContent, length);
        }
        int size = Math.min(StringUtils.judgeTextLength(mEtContent.getText().toString()), length);
        mTvNumber.setText(size + "/" + length);
        mEtContent.setSelection(mEtContent.getText().length());
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start1, int before, int count) {
                int size = Math.min(StringUtils.judgeTextLength(mEtContent.getText().toString()), length);
                mTvNumber.setText(size + "/" + length);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.toolbar_tv_menu) {
            String content = mEtContent.getText().toString();
            if (TextUtils.isEmpty(content.trim())) {
                toastTip("内容不能为空");
                return;
            }
            if (type == TYPE_EDIT_SIGN) {  //宣言
                p.editFamilyInfo("intro", content);
            } else if (type == TYPE_EDIT_NAME) {   //宣言家族名称
                p.editFamilyInfo("tname", content);
            } else if (type == TYPE_EDIT_PARTY_NAME) {   //派对名称
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ENTER_PARTY_UPDATE_NAME, content));
                finish();
            } else if (type == TYPE_ANNOUNCEMENT) {   //家族公告
                CustomPopWindow popWindow = new CustomPopWindow(this);
                popWindow.setContent("该公告后台审核通过后，将向家族全员展示，确认编辑吗？");
                popWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onSure() {
                        getP().editFamilyInfo("announcement", content);
                    }
                });
                popWindow.showPopupWindow();
            } else if (type == TYPE_DISSOLUTION) {   //解散家族
                p.dissolutionFamily(content);
            } else if (type == TYPE_EXIT) {  //退出家族
                p.leave(content);
            } else if (type == TYPE_VOICE_NAME) {  //语音房公告
                p.editFile("announcement", content);
            }
        }
    }

    @Override
    public void editFamilyInfoSuccess(String data) {
//        toastTip(data);
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
        finish();
    }

    @Override
    public void dissolutionFamilySuccess(Boolean data) {
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_DISSOLUTION_SUCCESS));
        Utils.logE("家族解散成功");
        finish();
    }

    @Override
    public void exitFamilySuccess(Boolean data) {
        toastTip("退出申请已通知族长尽快处理");
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
        finish();
    }

    @Override
    public void closeVoice(String name) {
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_VOICE_INFO, name));
        finish();
    }
}
