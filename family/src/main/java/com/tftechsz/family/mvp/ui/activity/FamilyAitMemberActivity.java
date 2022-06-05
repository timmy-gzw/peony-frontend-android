package com.tftechsz.family.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.family.R;
import com.tftechsz.family.adapter.FamilyAitMemberAdapter;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.mvp.IView.IFamilyAitMemberView;
import com.tftechsz.family.mvp.presenter.FamilyAitMemberPresenter;
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

@Route(path = ARouterApi.ACTIVITY_FAMILY_AIT_MEMBER)
public class FamilyAitMemberActivity extends BaseMvpActivity<IFamilyAitMemberView, FamilyAitMemberPresenter> implements IFamilyAitMemberView {
    private final String mAllName = "*#*#*#*#";
    private String mTid;
    private FamilyAitMemberAdapter mAdapter;
    private PinyinComparator pinyinComparator;
    private RecyclerView mRvMember;
    private List<FamilyMemberDto> mData;
    private EditText mEtSearch;
    private SideBar sideBar;

    @Override
    public FamilyAitMemberPresenter initPresenter() {
        return new FamilyAitMemberPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_family_ait_member;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("选择提醒的人")
                .build();
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
        mTid = getIntent().getStringExtra("tid");
        mData = new ArrayList<>();
        p.getFamilyId(mTid);
        pinyinComparator = new PinyinComparator();
        mAdapter = new FamilyAitMemberAdapter(this, mData);
        mRvMember.setAdapter(mAdapter);
        sideBar.setOnTouchingLetterChangedListener(s -> {
            //该字母首次出现的位置
            int position = mAdapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                layoutManager.scrollToPositionWithOffset(position, 0);
            }
        });
        mAdapter.setOnItemClickListener((view, position) -> {
            FamilyMemberDto data = (FamilyMemberDto) mAdapter.getItem(position);
            if (data != null) {
                Intent intent = new Intent();
                if (TextUtils.equals(data.nickname, mAllName)) {
                    intent.putExtra("userId", -9999);
                    intent.putExtra("nickName", "所有人");
                } else {
                    intent.putExtra("userId", data.user_id);
                    intent.putExtra("nickName", data.nickname);
                }
                setResult(RESULT_OK, intent);
                finish();
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
    }

    @Override
    public void getMemberSuccess(List<FamilyMemberDto> data, int roleId) {
        if (data == null || data.size() <= 0) return;
        if (roleId == 64 || roleId == 32) {
            FamilyMemberDto familyMemberDto = new FamilyMemberDto();
            familyMemberDto.nickname = mAllName;
            mData.add(familyMemberDto);
        }
        mData.addAll(data);
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            FamilyMemberDto familyMemberDto = mData.get(i);
            String pinyin = PinyinUtils.getPingYin(familyMemberDto.nickname);
            String sortString = pinyin.substring(0, 1).toUpperCase();
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
}
