package com.tftechsz.mine.mvp.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.AccostSettingListBean;
import com.tftechsz.mine.entity.AccostType;
import com.tftechsz.mine.entity.dto.AccostCustomizeEvent;
import com.tftechsz.mine.mvp.IView.IAccostSettingView;
import com.tftechsz.mine.mvp.presenter.IAccostSettingPresenter;

import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 新建/编辑 招呼
 */
public class AccostCustomizeEditActivity extends BaseMvpActivity<IAccostSettingView, IAccostSettingPresenter> implements IAccostSettingView {

    private int mEditType; //0编辑  1新建
    private EditText mEdit;
    private TextView mEdtCount;
    private String mExtraContent;
    private int mId;

    @Override
    public IAccostSettingPresenter initPresenter() {
        return new IAccostSettingPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.act_accost_customize_edit;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mEditType = getIntent().getIntExtra(Interfaces.EXTRA_CUSTOMIZE_EDIT_TYPE, 1);
        mId = getIntent().getIntExtra(Interfaces.EXTRA_CUSTOMIZE_EDIT_ID, 0);
        mExtraContent = getIntent().getStringExtra(Interfaces.EXTRA_CUSTOMIZE_EDIT_CONTENT);
        new ToolBarBuilder().showBack(true)
                .setTitle(mEditType == 0 ? "编辑内容" : "新建内容")
                .setRightTextColor(R.color.white)
                .setToolbarMenuBackground(R.drawable.bg_red)
                .setRightText("提交", v -> commit())
                .build();
        mEdit = findViewById(R.id.edit_accost);
        mEdtCount = findViewById(R.id.edt_count);
    }

    @Override
    protected void initData() {
        Utils.setEditCardTextChangedListener(mEdit, mEdtCount, 60, true);
        Utils.setFocus(mEdit);
        if (!TextUtils.isEmpty(mExtraContent)) {
            mEdit.setText(mExtraContent);
//            mEdit.setSelection(mExtraContent.length());
        }
        KeyboardUtils.showSoftInput();
    }

    //提交
    private void commit() {
        if (!ClickUtil.canOperate()) return;

        String text = Utils.getText(mEdit);
        if (TextUtils.isEmpty(text)) {
            toastTip("请输入打招呼文字~");
            return;
        }

        KeyboardUtils.hideSoftInput(mActivity);
        if (text.equals(mExtraContent)) { //未改变
            finish();
            return;
        }
        switch (mEditType) {
            case 0:
                p.updateAccostSetting(mId, 0, AccostType.TEXT, new AccostSettingListBean(text));
                break;

            case 1:
                p.addAccostSetting(AccostType.TEXT, new AccostSettingListBean(text));
                break;
        }
    }

    @Override
    public void getAccostListSuccess(List<AccostSettingListBean> data) {

    }

    @Override
    public void addAccostSettingSuccess() {
        RxBus.getDefault().post(new AccostCustomizeEvent(0));
        finish();
    }


    @Override
    public void addAccostSettingError() {

    }

    @Override
    public void updateAccostSettingSuccess(int position) {
        toastTip("更新成功，请等待审核!");
        RxBus.getDefault().post(new AccostCustomizeEvent(1, mId, Utils.getText(mEdit)));
        finish();
    }

    @Override
    public void delAccostSettingSuccess(int position) {

    }
}
