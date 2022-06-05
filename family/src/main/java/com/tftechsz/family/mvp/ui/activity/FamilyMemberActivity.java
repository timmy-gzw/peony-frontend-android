package com.tftechsz.family.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.KeyboardUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.family.R;
import com.tftechsz.family.adapter.FamilyMemberGroupAdapter;
import com.tftechsz.family.adapter.FamilyMemberSearchAdapter;
import com.tftechsz.family.databinding.ActivityFamilyMemberBinding;
import com.tftechsz.family.entity.dto.ChangeMasterDto;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.entity.dto.FamilyMemberGroupDto;
import com.tftechsz.family.mvp.IView.IFamilyMemberView;
import com.tftechsz.family.mvp.presenter.FamilyMemberPresenter;
import com.tftechsz.family.widget.pop.FamilyChangeMasterWindow;
import com.tftechsz.family.widget.pop.FamilyClearPopWindow;
import com.tftechsz.family.widget.pop.FamilyContributePopWindow;
import com.tftechsz.family.widget.pop.FamilyInactivePopWindow;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.FamilyInactiveDto;
import com.tftechsz.common.entity.FamilyRoleDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.FamilyMemberEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.FamilyManagerPopWindow;
import com.tftechsz.common.widget.pop.FamilyMutePopWindow;
import com.tftechsz.common.widget.pop.ReportPopWindow;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 家族成员
 */
@Route(path = ARouterApi.ACTIVITY_FAMILY_MEMBER)
public class FamilyMemberActivity extends BaseMvpActivity<IFamilyMemberView, FamilyMemberPresenter> implements IFamilyMemberView, View.OnClickListener {
    private int mFamilyId;
    private FamilyMemberGroupAdapter mAdapter;
    private RecyclerView mRvMember;
    private ImageView mIvMenu, mIvChoose;
    protected SmartRefreshLayout smartRefreshLayout;
    private List<FamilyMemberGroupDto> mData;
    private FamilyManagerPopWindow familyManagerWindow;
    private FamilyInactivePopWindow familyInactivePopWindow;
    private FamilyChangeMasterWindow familyChangeMasterWindow;
    private FamilyMutePopWindow familyMutePopWindow;
    private TextView mTvTitle;
    private int mFrom;  // 1 ：礼物选择进入  2：聊天室进入  3：获取可以转让的成员列表
    private String mTid;
    private int mOrderBy;   //默认 0 1-日 2-周 3-总
    private int mRoleId;
    private final TextWatcher editWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String s1 = s.toString();
            if (TextUtils.isEmpty(s1)) {
                mSearchAdapter.setList(null);
            } else {
                p.member_list_search(mFamilyId, s1.trim());
            }
        }
    };

    @Autowired
    UserProviderService service;
    private ActivityFamilyMemberBinding mBind;
    private FamilyMemberSearchAdapter mSearchAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public FamilyMemberPresenter initPresenter() {
        return new FamilyMemberPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(this).titleBar(mBind.clSearch).init();
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mRvMember = findViewById(R.id.recycleview);
        mIvMenu = findViewById(R.id.toolbar_iv_menu);
        mIvChoose = findViewById(R.id.toolbar_iv_choose);
        mTvTitle = findViewById(R.id.toolbar_title);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRvMember.setLayoutManager(mLinearLayoutManager);
        mBind.searchRecy.setLayoutManager(new LinearLayoutManager(this));
        smartRefreshLayout = findViewById(R.id.smartrefreshlayout);
        mLlEmpty = findViewById(com.tftechsz.common.R.id.ll_empty);
        mTvEmpty = findViewById(com.tftechsz.common.R.id.tv_empty);
        findViewById(R.id.iv_search_member).setOnClickListener(this);
        mIvMenu.setOnClickListener(this);
        mBind.backAll.setOnClickListener(this);
        mBind.toolbarTvMenu.setOnClickListener(this);
        mBind.selBtn.setOnClickListener(this);
        mIvChoose.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mFamilyId = getIntent().getIntExtra("familyId", 0);
        mFrom = getIntent().getIntExtra("from", 0);
        //群组id
        mTid = getIntent().getStringExtra("tid");
        mData = new ArrayList<>();
        mAdapter = new FamilyMemberGroupAdapter(this, mData, mFamilyId, mFrom);
        mRvMember.setAdapter(mAdapter);
        mSearchAdapter = new FamilyMemberSearchAdapter();
        mSearchAdapter.setEmptyView(View.inflate(this, R.layout.base_empty_view, null));
        mSearchAdapter.onAttachedToRecyclerView(mBind.searchRecy);
        mBind.searchRecy.setAdapter(mSearchAdapter);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            if (mFrom == 3) {
                getP().getChangeMaster();
            } else {
                mPage = 1;
                p.getFamilyMember(mPage, mFamilyId, mOrderBy, false);
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mPage += 1;
            p.getFamilyMember(mPage, mFamilyId, mOrderBy, false);
        });
        //聊天页面进入
        if (mFrom == 2) {
            p.getFamilyId(mTid);
        } else if (mFrom == 3) {  //获取可以转让的成员
            mTvTitle.setText("转让族长");
            mIvMenu.setVisibility(View.GONE);
            mIvChoose.setVisibility(View.GONE);
            mBind.ivSearchMember.setVisibility(View.GONE);
            getP().getChangeMaster();
            smartRefreshLayout.setEnableAutoLoadMore(false);
        } else {
            p.getFamilyMember(mPage, mFamilyId, mOrderBy, true);
        }
        getP().getFamilyRole();
        mAdapter.addOnClickListener(new FamilyMemberGroupAdapter.OnSelectListener() {
            @Override
            public void clickMore(int groupPosition, int childPosition, int userId, String nickname, int roleId) {
                p.getRoleSet(groupPosition, childPosition, userId, nickname, roleId);
            }

            @Override
            public void changeMaster(int groupPosition, int childPosition, int userId) {
                FamilyMemberDto data = mData.get(groupPosition).getData().get(childPosition);
                if (data != null) {
                    if (familyChangeMasterWindow == null)
                        familyChangeMasterWindow = new FamilyChangeMasterWindow(FamilyMemberActivity.this);
                    familyChangeMasterWindow.setData(data);
                    familyChangeMasterWindow.addOnClickListener(userId1 -> getP().changeMaster(userId1));
                    familyChangeMasterWindow.showPopupWindow();

                }

            }
        });
        mAdapter.setOnChildClickListener((adapter1, holder, groupPosition, childPosition) -> {
            if (mBind.getSelectedMode()) {
                if (null != mData && mData.size() > 0) {
                    FamilyMemberDto data = mData.get(groupPosition).getData().get(childPosition);
                    if (service.getUserId() == data.user_id) {
                        toastTip("不能选择自己哦～");
                        return;
                    }
                    data.isSelected = data.isSelected == 0 ? 1 : 0;
                    View view = mLinearLayoutManager.findViewByPosition(performIndex(groupPosition - 1, childPosition));
                    if (view != null) {
                        CheckBox checkbox = view.findViewById(R.id.checkbox);
                        if (checkbox != null) {
                            checkbox.setChecked(data.isSelected == 1);
                        }
                    }
                    ArrayList<FamilyMemberDto> selectedData = getSelectedData();
                    setSelTxt(selectedData.size());
                }
            } else {
                if (mFrom == 3) return;
                if (null != mData && mData.size() > 0) {
                    FamilyMemberDto data = mData.get(groupPosition).getData().get(childPosition);
                    if (mFrom == 0) {
                        ARouterUtils.toMineDetailActivity(data.user_id == service.getUserId() ? "" : ("" + data.user_id));
                    } else if (mFrom == 1) {
                        if (service.getUserId() == data.user_id) {
                            toastTip("不能给自己发礼物哦～");
                            return;
                        }
                        RxBus.getDefault().post(new FamilyMemberEvent(data.user_id, data.nickname, data.icon));
                        finish();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("userId", data.user_id);
                        intent.putExtra("nickName", data.nickname);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });

        mSearchAdapter.addChildClickViewIds(R.id.cl_leader, R.id.iv_arrow);
        mSearchAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            int id = view.getId();
            FamilyMemberDto data = mSearchAdapter.getItem(position);
            if (id == R.id.cl_leader) {
                if (mFrom == 3) return;
                if (mFrom == 0) {
                    ARouterUtils.toMineDetailActivity(data.user_id == service.getUserId() ? "" : ("" + data.user_id));
                } else if (mFrom == 1) {
                    if (service.getUserId() == data.user_id) {
                        toastTip("不能给自己发礼物哦～");
                        return;
                    }
                    RxBus.getDefault().post(new FamilyMemberEvent(data.user_id, data.nickname, data.icon));
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("userId", data.user_id);
                    intent.putExtra("nickName", data.nickname);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else if (id == R.id.iv_arrow) {
                KeyboardUtils.hideSoftInput(mActivity);
                p.getRoleSet(0, 0, data.user_id, data.nickname, data.role_id);
            }
        });

        //搜索列表滑动时隐藏软键盘
        mBind.searchRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (KeyboardUtils.isSoftInputVisible(mActivity)) {
                        KeyboardUtils.hideSoftInput(mActivity);
                    }
                }
            }
        });
    }

    private ArrayList<FamilyMemberDto> getSelectedData() {
        ArrayList<FamilyMemberDto> list = new ArrayList<>();
        if (mData == null || mData.isEmpty()) {
            return list;
        }
        for (int i = 0, j = mData.size(); i < j; i++) {
            ArrayList<FamilyMemberDto> data = mData.get(i).getData();
            for (int x = 0, y = data.size(); x < y; x++) {
                FamilyMemberDto familyMemberDto = data.get(x);
                if (familyMemberDto.isSelected == 1) {
                    list.add(familyMemberDto);
                }
            }
        }
        return list;
    }

    private int performIndex(int groupPosition, int childPosition) {
        int pos = childPosition + 1;
        for (int i = groupPosition; i >= 0; i--) {
            pos += (mData.get(i).getData().size() + 1);
        }
        return pos;
    }


    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_family_member);
        return 0;
    }

    /**
     * 获取成员列表
     *
     * @param data
     */

    @Override
    public void getMemberSuccess(List<FamilyMemberGroupDto> data, int orderBy) {
        if (null == data) {
            if (mPage == 1) {
                mLlEmpty.setVisibility(View.VISIBLE);
            }
            smartRefreshLayout.setEnableLoadMore(false);
        } else {
            if (mPage == 1) {
                mData.clear();
                smartRefreshLayout.setEnableLoadMore(true);
            }
            if (mPage != 1) {
                for (int i = 0; i < data.size(); i++) {
                    for (int j = 0; j < mData.size(); j++) {
                        if (mData.get(j).getTitle().equals(data.get(i).getTitle())) {
                            data.get(i).setTitle("");
                            mData.get(j).getData().addAll(data.get(i).getData());
                            break;
                        }
                    }
                }
            } else {
                mData.addAll(data);
            }
            if (mData.size() <= 0)
                mLlEmpty.setVisibility(View.VISIBLE);
            else
                mLlEmpty.setVisibility(View.GONE);
        }
        mAdapter.setOrderType(orderBy);
        mAdapter.notifyDataChanged();
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }

    /**
     * 转让家族族长成功
     */
    @Override
    public void getChangeMasterSuccess(ChangeMasterDto data) {
        if (data.status) {
            //MMKVUtils.getInstance().removeKey(service.getUserId() + Constants.FAMILY_APPLY);
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
            finish();
            CommonUtil.startTeamChatActivity(this, mTid);
        } else {
            new CustomPopWindow(mContext).setContent(data.desc).setLeftButton("暂不邀请").setRightButton("马上邀请")
                    .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onSure() {
                            p.changeMasterNotice(data.user_id);
                        }
                    }).showPopupWindow();
        }
    }

    @Override
    public void getChangeMasterNoticeSuccess(boolean data) {

    }

    /**
     * 获取可以操作的权限成功
     */
    @Override
    public void getRoleSuccess(int groupPosition, int childPosition, List<FamilyRoleDto> data, int userId, String name, int roleId) {
        if (null == familyManagerWindow)
            familyManagerWindow = new FamilyManagerPopWindow(FamilyMemberActivity.this, mRoleId);
        familyManagerWindow.initData(roleId, name, data);
        familyManagerWindow.addOnClickListener(new FamilyManagerPopWindow.OnSelectListener() {
            @Override
            public void setRole(int roleId) {
                getP().setUserRole(userId, roleId);
            }

            @Override
            public void lookUser() {
                ARouterUtils.toMineDetailActivity(userId == service.getUserId() ? "" : ("" + userId));
            }

            @Override
            public void muteUser(int mute) {
                if (mute == 1) {
                    getP().getMuteMap(userId == service.getUserId() ? 0 : userId);
                } else {
                    getP().muteUser(mFamilyId, userId == service.getUserId() ? 0 : userId, 0, 0);
                }
            }

            @Override
            public void blockUser(int block) {
                getP().blackUser(groupPosition, childPosition, mFamilyId, userId == service.getUserId() ? 0 : userId, block);

            }

            @Override
            public void reportUser() {
                ReportPopWindow pop = new ReportPopWindow(FamilyMemberActivity.this, userId == service.getUserId() ? 0 : userId, 1);
                pop.showPopupWindow();
            }

            @Override
            public void removeUser() {
                p.removeUser(groupPosition, childPosition, userId);
            }
        });
        familyManagerWindow.showPopupWindow();
    }

    @Override
    public void setRoleSuccess(boolean data) {
        toastTip("设置成功");
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
        mPage = 1;
        p.getFamilyMember(mPage, mFamilyId, mOrderBy, false);
        if (mBind.getSearchMode() && !TextUtils.isEmpty(Utils.getText(mBind.etSearch))) { //如果是搜索模式 && 输入框非空
            p.member_list_search(mFamilyId, Utils.getText(mBind.etSearch));
        }
    }

    @Override
    public void removeRoleSuccess(int groupPosition, int childPosition, int userId, boolean data) {
        toastTip("踢出成功");
        mData.get(groupPosition).getData().remove(childPosition);
        mAdapter.notifyDataSetChanged();
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
        if (mBind.getSearchMode() && !TextUtils.isEmpty(Utils.getText(mBind.etSearch))) { //如果是搜索模式 && 输入框非空
            p.member_list_search(mFamilyId, Utils.getText(mBind.etSearch));
        }
    }

    @Override
    public void getInactiveSuccess(List<FamilyInactiveDto> data) {
        if (data == null || data.size() <= 0) return;
        if (null == familyInactivePopWindow)
            familyInactivePopWindow = new FamilyInactivePopWindow(FamilyMemberActivity.this);
        familyInactivePopWindow.initData(data);
        familyInactivePopWindow.addOnClickListener(type -> {
            getP().clearInactiveUser(type);
        });
        familyInactivePopWindow.showPopupWindow();
    }

    @Override
    public void getMuteMapSuccess(List<FamilyInactiveDto> data, int userId) {
        if (data == null || data.size() <= 0) return;
        if (null == familyMutePopWindow)
            familyMutePopWindow = new FamilyMutePopWindow(FamilyMemberActivity.this);
        familyMutePopWindow.initData(data);
        familyMutePopWindow.addOnClickListener(type -> {
            getP().muteUser(mFamilyId, userId == service.getUserId() ? 0 : userId, type, 1);
        });
        familyMutePopWindow.showPopupWindow();
    }

    @Override
    public void getMuteUserSuccess(boolean data) {

    }

    @Override
    public void clearInactiveUserSuccess(boolean data) {
        if (mFrom == 2) {
            p.getFamilyId(mTid);
        } else {
            p.getFamilyMember(mPage, mFamilyId, mOrderBy, true);
        }
    }


    @Override
    public void blackUserSuccess(int groupPosition, int childPosition, int userId, boolean data) {
        mData.get(groupPosition).getData().remove(childPosition);
        mAdapter.notifyDataSetChanged();
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
    }

    @Override
    public void getFamilyRoleSuccess(FamilyIdDto data) {
        mRoleId = data.role_id;
        if (data.role_id != 64) {
            mIvMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public void searchMemberSuccess(List<FamilyMemberDto> data) {
        mSearchAdapter.setList(data);
    }

    @Override
    public void batchOutSuccess() {//踢出成员成功
        setSelTxt(0);
        for (int i = 0, j = mData.size(); i < j; i++) {
            ArrayList<FamilyMemberDto> data = mData.get(i).getData();
            for (int x = data.size() - 1; x >= 0; x--) {
                FamilyMemberDto familyMemberDto = data.get(x);
                if (familyMemberDto.isSelected == 1) {
                    mAdapter.notifyChildRemoved(i, x);
                    mData.get(i).getData().remove(x);
                    if (data.size() == 0) {
                        mAdapter.notifyGroupRemoved(i);
                        mData.remove(i);
                    }
                }
            }
        }
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
    }

    private void setSelTxt(int count) {
        mBind.selNum.setText(new SpannableStringUtils.Builder()
                .append("已选")
                .append(String.valueOf(count))
                .setForegroundColor(Utils.getColor(R.color.colorPrimary))
                .append("人")
                .create());
        mBind.selBtn.setText(String.format("移除 (%s)", count));
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canOperate()) return;
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.toolbar_iv_menu) { //t人
            FamilyClearPopWindow familyClearPopWindow = new FamilyClearPopWindow(this);
            familyClearPopWindow.addOnClickListener(position -> {
                switch (position) {
                    case 0: //批量踢出
                        getP().getInactiveUser();
                        break;

                    case 1: //选择踢出
                        setSelTxt(getSelectedData().size());
                        mBind.setSelectedMode(true);
                        smartRefreshLayout.setEnableRefresh(false);
                        mAdapter.setSelectedMode(true);
                        break;
                }
            });
            familyClearPopWindow.showPopupWindow(mIvMenu);

        } else if (id == R.id.toolbar_iv_choose) { //周贡献 筛选
            FamilyContributePopWindow popWindow = new FamilyContributePopWindow(this);
            popWindow.addOnClickListener(type -> {
                mOrderBy = type;
                mPage = 1;
                getP().getFamilyMember(mPage, mFamilyId, mOrderBy, false);
            });
            popWindow.showPopupWindow(mIvChoose);
        } else if (id == R.id.iv_search_member) { //搜索按钮
            mBind.setSearchMode(true);
            Utils.setFocus(mBind.etSearch);
            KeyboardUtils.showSoftInput();
            mBind.etSearch.addTextChangedListener(editWatcher);
        } else if (id == R.id.back_all) { //搜索返回
            mBind.setSearchMode(false);
            mBind.etSearch.setText(null);
            KeyboardUtils.hideSoftInput(this);
        } else if (id == R.id.toolbar_tv_menu) { //搜索
            String text = Utils.getText(mBind.etSearch);
            if (TextUtils.isEmpty(text)) {
                return;
            }
            p.member_list_search(mFamilyId, Utils.getText(mBind.etSearch));
        } else if (id == R.id.sel_btn) { //移除成员
            ArrayList<FamilyMemberDto> selectedData = getSelectedData();
            if (selectedData.size() == 0) {
                toastTip("请选择要移除的成员");
                return;

            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0, j = selectedData.size(); i < j; i++) {
                FamilyMemberDto familyMemberDto = selectedData.get(i);
                sb.append(familyMemberDto.user_id).append(",");
            }
            p.batchOut(mFamilyId, sb.substring(0, sb.length() - 1));
        }
    }

    @Override
    public void onBackPressed() {
        if (mBind.getSearchMode()) { //如果是搜索模式
            mBind.setSearchMode(false);
            mBind.etSearch.setText(null);
            KeyboardUtils.hideSoftInput(this);
            return;
        }
        if (mBind.getSelectedMode()) {
            mBind.setSelectedMode(false);
            mAdapter.setSelectedMode(false);
            smartRefreshLayout.setEnableRefresh(true);
            return;
        }
        super.onBackPressed();
    }
}
