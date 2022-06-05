package com.tftechsz.family.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tftechsz.family.R;
import com.tftechsz.family.adapter.GroupCoupleAdapter;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.mvp.IView.IGroupCoupleView;
import com.tftechsz.family.mvp.presenter.GroupCouplePresenter;
import com.tftechsz.family.utils.tools.CharacterParserUtil;
import com.tftechsz.family.utils.tools.GetNameSort;
import com.tftechsz.family.utils.tools.PinyinComparator;
import com.tftechsz.family.utils.tools.PinyinUtils;
import com.tftechsz.family.utils.tools.SideBar;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(path = ARouterApi.ACTIVITY_GROUP_COUPLE)
public class GroupCoupleActivity extends BaseMvpActivity<IGroupCoupleView, GroupCouplePresenter> implements IGroupCoupleView, View.OnClickListener {
    private GroupCoupleAdapter mAdapter;
    private PinyinComparator pinyinComparator;
    private RecyclerView mRvMember;
    private List<FamilyMemberDto> mData;
    private EditText mEtSearch;
    private SideBar sideBar;

    @Override
    public GroupCouplePresenter initPresenter() {
        return new GroupCouplePresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_group_couple;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        findViewById(R.id.toolbar_tv_menu).setOnClickListener(this);
        mRvMember = findViewById(R.id.rv_member);
        sideBar = findViewById(R.id.sidebar);
        mEtSearch = findViewById(R.id.et_search);
    }

    @Override
    protected void initData() {
        super.initData();
        GetNameSort getNameSort = new GetNameSort();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvMember.setLayoutManager(layoutManager);
        mData = new ArrayList<>();
        pinyinComparator = new PinyinComparator();
        mAdapter = new GroupCoupleAdapter(this, mData);
        mRvMember.setAdapter(mAdapter);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if (!TextUtils.isEmpty(s)) {
                    //该字母首次出现的位置
                    int position = mAdapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        layoutManager.scrollToPositionWithOffset(position, 0);
                    }
                }
            }
        });
        mAdapter.setOnItemClickListener(new GroupCoupleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapter.setCheckPositions(position);
            }
        });
        mEtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable e) {
                String content = mEtSearch.getText().toString();
                if (content.length() > 0) {
                    ArrayList<FamilyMemberDto> fileList = (ArrayList<FamilyMemberDto>) getNameSort.search(content, mData);
                    mAdapter.updateList(fileList);
                } else {
                    mAdapter.updateList(mData);
                }
            }
        });
        getP().getGroupCouple();
    }

    @Override
    public void getGroupCoupleSuccess(List<FamilyMemberDto> data) {
        if (data == null || data.size() <= 0) return;
        mData.addAll(data);
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            FamilyMemberDto familyMemberDto = mData.get(i);
            String pinyin = PinyinUtils.getPingYin(familyMemberDto.nickname);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            LogUtil.e("=========", "");
            String sortKey = CharacterParserUtil.getInstance().getSelling(familyMemberDto.nickname);
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                familyMemberDto.setLetters(sortString.toUpperCase());
            } else if (sortString.equals("*")) {
                familyMemberDto.setLetters("*");
            } else {
                familyMemberDto.setLetters("#");
            }
            familyMemberDto.sortString = sortString;
            familyMemberDto.sortKey = sortKey;
        }
        //首字符排序，更新UI
        Collections.sort(mData, pinyinComparator);
        mAdapter.updateList(mData);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.toolbar_tv_menu) {  //提交
            if (mData == null || mData.size() <= 0) return;
            Intent intent = new Intent();
            intent.putExtra("userId", mData.get(mAdapter.getCheckPositions()).user_id);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mData != null) {
            mData.clear();
            mData = null;
        }
    }
}
