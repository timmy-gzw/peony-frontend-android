package com.tftechsz.family.widget.pop;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.family.entity.dto.FamilySettingDto;
import com.tftechsz.family.mvp.ui.activity.ApplyVerifyActivity;
import com.tftechsz.family.mvp.ui.activity.ChangeJoinConditionActivity;
import com.tftechsz.family.mvp.ui.activity.EditFamilyInfoActivity;
import com.tftechsz.family.mvp.ui.activity.FamilyBlackListActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.widget.pop.BaseBottomPop;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 管理家族弹窗
 */
public class FamilySettingPopWindow extends BaseBottomPop implements View.OnClickListener {
    private FamilyInfoDto mData;
    private final Activity mContext;
    private RecyclerView mRvFamilySetting;
    private int isMute;
    protected CompositeDisposable mCompositeDisposable;
    @Autowired
    UserProviderService service;

    public FamilySettingPopWindow(Activity context) {
        super(context);
        mContext = context;
        setPopupGravity(Gravity.BOTTOM);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        mRvFamilySetting = findViewById(R.id.rv_family_setting);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRvFamilySetting.setLayoutManager(layoutManager);
        mCompositeDisposable = new CompositeDisposable();
    }


    public void setData(FamilyInfoDto data, int isMute) {
        mData = data;
        this.isMute = isMute;
        initData();
    }


    /**
     * 加载数据
     */
    private void initData() {
        List<FamilySettingDto> data = new ArrayList<>();
        //id" => 4, "title" => "长老" ],[ "id" => 32, "title" => "副族长" ],[ "id" => 64, "title" => "族长" ],
        //权限管理：
        //   副族长：申请审核、编辑家族宣言、更改家族加入条件、退出家族、举报、取消
        //   长老：退出家族、举报、取消
        //   普通成员：退出家族、举报、取消
        if (mData.my_role_id == 64) {  //族长
            data.add(new FamilySettingDto(1, "申请审核"));
            data.add(new FamilySettingDto(2, "编辑家族宣言"));
            data.add(new FamilySettingDto(3, "更改家族图章"));
            data.add(new FamilySettingDto(4, "更改家族名称"));
            data.add(new FamilySettingDto(5, "家族公告"));
            data.add(new FamilySettingDto(6, "更改家族加入条件"));
            data.add(new FamilySettingDto(7, "家族黑名单"));
            data.add(new FamilySettingDto(8, isMute == 1 ? "取消全员禁言" : "全员禁言"));
            data.add(new FamilySettingDto(9, "解散家族"));
        } else if (mData.my_role_id == 32) {   //副族长
            data.add(new FamilySettingDto(1, "申请审核"));
            data.add(new FamilySettingDto(2, "编辑家族宣言"));
            data.add(new FamilySettingDto(5, "家族公告"));
            data.add(new FamilySettingDto(6, "更改家族加入条件"));
            data.add(new FamilySettingDto(7, "家族黑名单"));
            data.add(new FamilySettingDto(9, "退出家族"));
        } else if (mData.my_role_id == 4) {  //长老
            data.add(new FamilySettingDto(1, "申请审核"));
            data.add(new FamilySettingDto(9, "退出家族"));
        } else {
            data.add(new FamilySettingDto(9, "退出家族"));
        }
//        data.add(new FamilySettingDto(7, "举报"));
        FamilySettingAdapter adapter = new FamilySettingAdapter(data);
        mRvFamilySetting.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            if (adapter.getData().get(position).id == 1) {
                Intent intent = new Intent(mContext, ApplyVerifyActivity.class);
                intent.putExtra("familyId", mData.family_id);
                mContext.startActivity(intent);
            } else if (adapter.getData().get(position).id == 2) {  //编辑家族宣言
                EditFamilyInfoActivity.startForSign(mContext, mData.intro);
            } else if (adapter.getData().get(position).id == 3) {  //更改家族图章
                if (null != listener)
                    listener.changePhoto();
            } else if (adapter.getData().get(position).id == 4) {  // 更改家族名称
                EditFamilyInfoActivity.startForName(mContext, mData.family_name);
            } else if (adapter.getData().get(position).id == 5) {  // 编辑家族公告
                EditFamilyInfoActivity.startForAnnouncement(mContext, mData.announcement, mData.my_role_id);
            } else if (adapter.getData().get(position).id == 6) { //更改家族加入条件
                Intent intent = new Intent(mContext, ChangeJoinConditionActivity.class);
                mContext.startActivity(intent);
            } else if (adapter.getData().get(position).id == 7) { //家族黑名单
                Intent intent = new Intent(mContext, FamilyBlackListActivity.class);
                intent.putExtra(Interfaces.EXTRA_ID, mData.family_id);
                mContext.startActivity(intent);
            } else if (adapter.getData().get(position).id == 8) { //禁言
                if (listener != null)
                    listener.muteAll(isMute == 1 ? 0 : 1);
            } else if (adapter.getData().get(position).id == 9) {  //解散
                if (mData.my_role_id == 64) {
                    EditFamilyInfoActivity.startForDissolution(mContext);
                } else if (mData.my_role_id == 32) {  //副族长
                    EditFamilyInfoActivity.startForExit(mContext);
                } else {  // 管理员和成员
                    if (listener != null)
                        listener.exitFamily();
                }
            }
            dismiss();
        });

    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_family_setting);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_video) {

            dismiss();
        } else if (id == R.id.tv_audio) {

            dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }


    class FamilySettingAdapter extends BaseQuickAdapter<FamilySettingDto, BaseViewHolder> {


        public FamilySettingAdapter(@Nullable List<FamilySettingDto> data) {
            super(R.layout.item_family_setting, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, FamilySettingDto item) {
            helper.setText(R.id.tv_content, item.content);
            helper.setGone(R.id.view, helper.getLayoutPosition() == getData().size() - 1);
            TextView tvApplyNum = helper.getView(R.id.tv_apply_num);
            if (item.id == 1) {
                if (CommonUtil.isShowApplyNum(service.getUserId(),tvApplyNum, 0)) {
                    tvApplyNum.setVisibility(View.VISIBLE);
                } else {
                    tvApplyNum.setVisibility(View.GONE);
                }
            } else {
                tvApplyNum.setVisibility(View.GONE);
            }

        }
    }


    public interface OnSelectListener {
        void changePhoto();

        void exitFamily();

        void muteAll(int isMute);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
