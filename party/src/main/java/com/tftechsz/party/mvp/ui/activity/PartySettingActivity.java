package com.tftechsz.party.mvp.ui.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.blankj.utilcode.util.ToastUtils;
import com.flyco.tablayout.SegmentTabLayout;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PartySettingAdapter;
import com.tftechsz.party.entity.EditAuditFieldDto;
import com.tftechsz.party.entity.PartyEditBean;
import com.tftechsz.party.entity.PartyManagerBgBean;
import com.tftechsz.party.mvp.IView.IPartySettingView;
import com.tftechsz.party.mvp.presenter.PartySettingPresenter;
import com.tftechsz.party.mvp.ui.fragment.AnnouncementDialog;
import com.tftechsz.party.widget.pop.MacScenePopWindow;

import java.util.List;

public class PartySettingActivity extends BaseMvpActivity<IPartySettingView, PartySettingPresenter> implements View.OnClickListener, IPartySettingView, MacScenePopWindow.IModeScene, AnnouncementDialog.IAnnouncementSetListener {
    @Autowired
    UserProviderService service;
    //派对名称、连麦场景
    TextView mTvPartyName, mTvPartySettingEvenTheWheatScenario;
    SegmentTabLayout segmentTabLayout;
    private final String[] mTitles_2 = {"自由模式", "麦序模式"};

    private PartySettingAdapter mAdapter;
    private ImageView mImgCover;
    private TextView mTvComText;
    private SwitchCompat switchCompat;
    private String mCover;
    private String mCoverValue;
    private String mBgIconCurrentValue;
    private final int REQUEST_PHOTO = 10000;
    private MacScenePopWindow macScenePopWindow;
    private String announcementTitle, announcementContent;
    private AnnouncementDialog confirmDialogFragment;
    private int partyRoomId;
    private CustomPopWindow closePop;
    //关闭按钮
    private TextView mTvBtnClose;
    //请求加载中
    private boolean toNetLoading;

    @Override
    public PartySettingPresenter initPresenter() {
        return new PartySettingPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("派对设置")
                .setRightText("保存", v -> commitData())
                .build();
        mTvBtnClose = findViewById(R.id.tv_exit_party);
        partyRoomId = getIntent().getIntExtra(Constants.PARAM_ROOM_ID, 0);
        initRxBus();
        mTvComText = findViewById(R.id.tv_right4);
        switchCompat = findViewById(R.id.my_switch);
        mImgCover = findViewById(R.id.iv_right_img);
        findViewById(R.id.rel_item_1).setOnClickListener(this);
        findViewById(R.id.rel_click_scene).setOnClickListener(this);
        findViewById(R.id.rel_click_announcement).setOnClickListener(this);
        findViewById(R.id.rel_btn_name).setOnClickListener(this);
        mTvBtnClose.setOnClickListener(this);
        mTvPartyName = findViewById(R.id.tv_right);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_setting);
        mTvPartySettingEvenTheWheatScenario = findViewById(R.id.tv_party_setting_even_the_wheat_scenario);
        segmentTabLayout = findViewById(R.id.tl_2);
        segmentTabLayout.setTabData(mTitles_2);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        mAdapter = new PartySettingAdapter(this);
        mAdapter.onAttachedToRecyclerView(recyclerView);
        mAdapter.setOnItemClickListener((adapter, view, position) -> mAdapter.setSel(position));
        recyclerView.setAdapter(mAdapter);
//        mAdapter.setEmptyView(View.inflate(mContext, R.layout.base_empty_view, null));

        initListener();
        p.getEditParty(partyRoomId);
        //获得当前图片之后在请求 所有背景列表
        p.getEditPartyBg();
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            //修改派对名字
                            if (event.type == Constants.NOTIFY_ENTER_PARTY_UPDATE_NAME) {
                                p.updatePartyName(null, partyRoomId, PartySettingActivity.this, new EditAuditFieldDto.EditAuditFieldDtoInner(event.code), null);
                                mTvPartyName.setText(event.code);
                            }
                        }
                ));
    }


    private void commitData() {
        if (!ClickUtil.canOperate()) {
            return;
        }
      /*  if (TextUtils.isEmpty(mCover)) {
            ToastUtils.showShort("请上传封面");
            return;
        } else if (TextUtils.isEmpty(mTvPartyName.getText().toString())) {
            ToastUtils.showShort("请输入派对名称");
            return;
        } else */if (mTvPartySettingEvenTheWheatScenario.getTag() == null) {
            ToastUtils.showShort("请选择连麦场景");
            return;
        } /*else if (TextUtils.isEmpty(mTvComText.getText().toString()) || mTvComText.getText().toString().equals("编辑")) {
            ToastUtils.showShort("请编辑公告信息");
            return;
        }*/ else if (TextUtils.isEmpty(mAdapter.getSel())) {
            ToastUtils.showShort("请选择派对背景");
            return;
        }
        String bgIcon = mAdapter.getSel();
        p.commitEditParty(/*mCoverValue, mTvPartyName.getText().toString(),*/ (int) mTvPartySettingEvenTheWheatScenario.getTag(), switchCompat.isChecked() ? 1 : 0, segmentTabLayout.getCurrentTab() == 0 ? 1 : 2, /*announcementContent, announcementTitle,*/ bgIcon, partyRoomId);
    }

    private void initListener() {
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_party_setting;
    }


    @Override
    protected void initData() {
        super.initData();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rel_click_announcement) {
            checkStatus(3);
        } else if (id == R.id.rel_item_1) {
            //派对封面
            checkStatus(1);
        } else if (id == R.id.rel_click_scene) {
            //连麦场景
            if (null == macScenePopWindow) {
                macScenePopWindow = new MacScenePopWindow(this, this);
            }
            macScenePopWindow.showPopupWindow();
        } else if (id == R.id.rel_btn_name) {
            checkStatus(2);
        } else if (id == R.id.tv_exit_party) {
            showEndPkPop(this);
        }

    }

    /**
     * 封面 、名称、公告    检查状态
     *
     * @param i 1 :封面 icon  2:名称 room_name  3：公告 announcement
     */
    public void checkStatus(int i) {
        if (!toNetLoading) {
            p.checkAuditing(partyRoomId, i == 1 ? "icon" : i == 2 ? "room_name" : "announcement", i);
        }
        toNetLoading = true;
    }

    /**
     * 是否关闭派对
     */
    public void showEndPkPop(Activity activity) {
        if (closePop == null)
            closePop = new CustomPopWindow(activity);
        closePop.setContent("是否关闭当前派对？");
        closePop.setLeftButton("点错了");
        closePop.setRightButton("关闭");
        closePop.setLeftColor(R.color.color_normal);
        closePop.setRightColor(R.color.red);
        closePop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
                closePop.dismiss();
            }

            @Override
            public void onSure() {
                //关闭派对
                p.closeParty(partyRoomId);
            }
        });
        closePop.showPopupWindow();
    }

    @Override
    public void editData(PartyEditBean data) {
        if (mCover == null) {
            mCover = data.icon;
            mCoverValue = data.icon_value;
        }
        mBgIconCurrentValue = data.bg_icon_value;
        //设置派对数据
        GlideUtils.loadRoundImage(this, mImgCover, data.icon);
        mTvPartyName.setText(data.room_name);
        mTvPartySettingEvenTheWheatScenario.setText(data.fight_pattern == 1 ? "普通模式" : "PK模式");
        mTvPartySettingEvenTheWheatScenario.setTag(data.fight_pattern);
        announcementTitle = data.title;
        announcementContent = data.announcement;
        switchCompat.setChecked(data.is_mute == 1);
        if (data.microphone_pattern == 1) {
            segmentTabLayout.setCurrentTab(0);
        } else
            segmentTabLayout.setCurrentTab(1);
        mTvComText.setText(data.title);
        //重新设置选中item
        if (mAdapter != null && mAdapter.getData().size() > 0) {
            setSelectImg(mAdapter.getData());
        }
        if (data.show_close_btn == 1) {
            mTvBtnClose.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void addBgs(List<PartyManagerBgBean> data) {
        if (!TextUtils.isEmpty(mBgIconCurrentValue)) {
            setSelectImg(data);
        }
        //派对背景图
        mAdapter.setList(data);

    }

    /**
     * 设置默认选中背景图
     */
    private void setSelectImg(List<PartyManagerBgBean> data) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).isSelected = data.get(i).value.contains(mBgIconCurrentValue);
        }
        mAdapter.notifyDataSetChanged();
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

    @Override
    public void setImgCover(String data, String value) {
        mCover = data;
        mCoverValue = value;
        p.updatePartyName(null, partyRoomId, PartySettingActivity.this, null, new EditAuditFieldDto.EditAuditFieldImgDtoInner(mCoverValue));

        runOnUiThread(() -> GlideUtils.loadRoundImage(PartySettingActivity.this, mImgCover, data));

    }

    @Override
    public void commit() {
//        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_PARTY_UPDATE));
        finish();
    }

    @Override
    public void closePartySuccess() {
        //关闭派对
        finish();
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_CLOSE_PARTY));
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void checkStatusCallBack(int status) {

        switch (status) {
            case 1:
                ChoosePicUtils.picSingle(this, REQUEST_PHOTO);
                break;
            case 2:
                ARouterUtils.toEditFamilyfVoice("", 7, mTvPartyName.getText().toString());
                break;
            case 3:
                if (confirmDialogFragment == null) {
                    confirmDialogFragment = new AnnouncementDialog(this, announcementTitle, announcementContent);
                }
                confirmDialogFragment.show(this.getSupportFragmentManager(), "confirmDialogFragment");
                break;
        }

        toNetLoading = false;
    }


    @Override
    public void freedom() {
        mTvPartySettingEvenTheWheatScenario.setText("普通模式");
        mTvPartySettingEvenTheWheatScenario.setTag(1);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void scene() {
        mTvPartySettingEvenTheWheatScenario.setText("PK模式");
        mTvPartySettingEvenTheWheatScenario.setTag(2);
    }

    @Override
    public void saveAnnouncement(String title, String content) {
        //保存的修改公告信息
        announcementTitle = title;
        announcementContent = content;
        p.updatePartyName(new EditAuditFieldDto(title, content), partyRoomId, PartySettingActivity.this, null, null);

        mTvComText.setText(title);
    }
}
