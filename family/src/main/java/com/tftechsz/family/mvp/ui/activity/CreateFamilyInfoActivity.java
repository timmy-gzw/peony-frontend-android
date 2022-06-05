package com.tftechsz.family.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.family.mvp.IView.ICreateFamilyView;
import com.tftechsz.family.mvp.presenter.CreateFamilyPresenter;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.SoftHideKeyBoardUtil;
import com.tftechsz.common.utils.StringUtils;
import com.tftechsz.common.widget.CustomFilter;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * 创建家族
 */
@Route(path = ARouterApi.ACTIVITY_CREATE_FAMILY)
public class CreateFamilyInfoActivity extends BaseMvpActivity<ICreateFamilyView, CreateFamilyPresenter> implements View.OnClickListener, ICreateFamilyView {
    private final int REQUEST_PHOTO = 10000;
    private ImageView mIvAvatar;
    private TextView mTvCommit;
    private TextView mTvNumber;
    private EditText mEtFamilyName;  //家族名称
    private TextView mTvReUpload;
    private String mPath;
    private final int length = 20;

    @Override
    public CreateFamilyPresenter initPresenter() {
        return new CreateFamilyPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        SoftHideKeyBoardUtil.assistActivity(this);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvCommit = findViewById(R.id.tv_commit);
        mTvNumber = findViewById(R.id.tv_number);
        mEtFamilyName = findViewById(R.id.tv_family_name);
        mTvReUpload = findViewById(R.id.toolbar_tv_menu);  //重新上传图片
        setListener();
    }


    private void setListener() {
        mIvAvatar.setOnClickListener(this);
        mTvCommit.setOnClickListener(this);
        mTvReUpload.setOnClickListener(this);
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mEtFamilyName.setFilters(new InputFilter[]{new CustomFilter(length)});
        int size = Math.min(StringUtils.judgeTextLength(mEtFamilyName.getText().toString()), length);
        mTvNumber.setText(size + "/" + length);
        mEtFamilyName.addTextChangedListener(new TextWatcher() {
            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start1, int before, int count) {
                int size = Math.min(StringUtils.judgeTextLength(s.toString()), length);
                mTvNumber.setText(size + "/" + length);
                if (!TextUtils.isEmpty(mPath) && size != 0) {
                    mTvCommit.setBackgroundResource(R.drawable.bg_orange_enable);
                    mTvCommit.setTextColor(ContextCompat.getColor(CreateFamilyInfoActivity.this, R.color.color_normal));
                    mTvCommit.setEnabled(true);
                } else {
                    mTvCommit.setBackgroundResource(R.drawable.bg_gray_ee_radius25);
                    mTvCommit.setTextColor(ContextCompat.getColor(CreateFamilyInfoActivity.this, R.color.color_light_font));
                    mTvCommit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_create_family_info;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.iv_avatar || id == R.id.toolbar_tv_menu) {   //头像上传
            ChoosePicUtils.picSingle(this, REQUEST_PHOTO);
        } else if (id == R.id.tv_commit) {
            String name = mEtFamilyName.getText().toString();
            p.uploadAvatar(name, mPath);

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
                mTvReUpload.setVisibility(View.VISIBLE);
                GlideUtils.loadRoundImage(this, mIvAvatar, mPath, 15);
                if (!TextUtils.isEmpty(mPath) && !TextUtils.isEmpty(mEtFamilyName.getText().toString())) {
                    mTvCommit.setBackgroundResource(R.drawable.bg_orange_enable);
                    mTvCommit.setTextColor(ContextCompat.getColor(CreateFamilyInfoActivity.this, R.color.color_normal));
                    mTvCommit.setEnabled(true);
                } else {
                    mTvCommit.setBackgroundResource(R.drawable.bg_gray_ee_radius25);
                    mTvCommit.setTextColor(ContextCompat.getColor(CreateFamilyInfoActivity.this, R.color.color_light_font));
                    mTvCommit.setEnabled(false);
                }
            }

        }
    }


    @Override
    public void createFamilySuccess(FamilyInfoDto data) {
        if (data != null) {
//            Intent intent = new Intent(CreateFamilyInfoActivity.this,FamilyDetailActivity.class);
//            intent.putExtra("familyId",data.family_id);
//            startActivity(intent);
//            ARouterUtils.toChatTeamActivity(data.tid + "", NimUIKit.getCommonTeamSessionCustomization(), null);
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_CREATE_FAMILY));
            finish();
        }

    }

    @Override
    public void uploadAvatarSuccess(String path) {

    }
}
