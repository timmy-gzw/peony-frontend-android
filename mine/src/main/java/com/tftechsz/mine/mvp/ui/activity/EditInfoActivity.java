package com.tftechsz.mine.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.blankj.utilcode.util.RegexUtils;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.StringUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.IView.IEditInfoView;
import com.tftechsz.mine.mvp.presenter.EditInfoPresenter;

/**
 * 编辑个人信息
 */
public class EditInfoActivity extends BaseMvpActivity<IEditInfoView, EditInfoPresenter> implements View.OnClickListener, IEditInfoView {

    private final static int TYPE_NAME = 1;
    private final static int TYPE_JOB = 2;
    private final static int TYPE_SIGN = 3;
    private EditText mEtContent;
    private TextView mTvCommit;
    private TextView mTvTitle;
    private TextView mTvNumber;
    private int type;
    private int length;
    @Autowired
    UserProviderService service;


    public static void startForName(Activity context, int requestCode) {
        Intent i = new Intent(context, EditInfoActivity.class);
        i.putExtra("type", TYPE_NAME);
        context.startActivityForResult(i, requestCode);
    }

    public static void startForJob(Activity context, String job, int requestCode) {
        Intent i = new Intent(context, EditInfoActivity.class);
        i.putExtra("type", TYPE_JOB);
        i.putExtra("job", job);
        context.startActivityForResult(i, requestCode);
    }


    public static void startForSign(Activity context, String sign, int requestCode) {
        Intent i = new Intent(context, EditInfoActivity.class);
        i.putExtra("type", TYPE_SIGN);
        i.putExtra("sign", sign);
        context.startActivityForResult(i, requestCode);
    }

    @Override
    public EditInfoPresenter initPresenter() {
        return new EditInfoPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mEtContent = findViewById(R.id.et_content);
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
        return R.layout.activity_edit_info;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getIntExtra("type", -1);
        String mJob = getIntent().getStringExtra("job");
        String mSign = getIntent().getStringExtra("sign");

        if (type == TYPE_NAME) {
            mTvTitle.setText(getString(R.string.edit_nickname));
            mEtContent.setText(service.getUserInfo().getNickname());
            mEtContent.setHint("昵称仅支持中文");
            length = 6;
            p.setLength(mEtContent, length);
//            mEtContent.setFilters(new InputFilter[]{new ChineseFilter()});
        } else if (type == TYPE_JOB) {
            mTvTitle.setText("填写工作");
            if (!TextUtils.equals("待完善", mJob))
                mEtContent.setText(mJob);
            else
                mEtContent.setHint("请设置您的工作");
            length = 8;
            p.setLength(mEtContent, length);
        } else if (type == TYPE_SIGN) {
            mTvTitle.setText("设置交友宣言");
            if (!TextUtils.equals("待完善", mSign))
                mEtContent.setText(mSign);
            else
                mEtContent.setHint("一个好的宣言可以更吸引Ta哟～");
            length = 20;
            p.setLength(mEtContent, length);
        }
        mTvNumber.setText(StringUtils.judgeTextLength(mEtContent.getText().toString()) + "/" + length);
        mEtContent.setSelection(mEtContent.getText().length());
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start1, int before, int count) {
                int size = Math.min(StringUtils.judgeTextLength(s.toString()), 1000);
                mTvNumber.setText(size + "/" + length);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = mEtContent.getCompoundDrawables()[2];
                if(drawable == null){
                    return false;
                }
                if(event.getAction() != MotionEvent.ACTION_UP){
                    return false;
                }
                if(event.getX()>mEtContent.getWidth()-mEtContent.getPaddingRight()-drawable.getIntrinsicWidth()){
                    mEtContent.setText("");
                }
                return false;
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
            if (type == TYPE_NAME && !RegexUtils.isMatch("^[\\u4e00-\\u9fa5]+$", content)) {
                toastTip("昵称仅支持中文");
                return;
            }
            if (type == TYPE_JOB && !RegexUtils.isMatch("^[\\u4e00-\\u9fa5a-zA-Z]+$", content)) {
                toastTip("工作仅支持中英文");
                return;
            }
            getP().updateUserInfo(type, content);
        }
    }


    @Override
    public void updateUserInfoSuccess(int type, String data) {
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
        if (type == TYPE_NAME || type == TYPE_SIGN) {
            if (service.getUserInfo() != null && service.getUserInfo().getSex() == 1) {
                ToastUtil.showToast(this, "更改成功");
            } else {
                ToastUtil.showToast(this, "进入审核，通过后正常展示");
            }
        } else {
            ToastUtil.showToast(this, "更改成功");
        }
        Intent intent = new Intent();
        intent.putExtra("type", data);
        setResult(RESULT_OK, intent);
        finish();
    }
}
