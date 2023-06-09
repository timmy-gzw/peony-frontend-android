package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.SlidingScaleTabLayout;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.mine.R;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.mvp.ui.fragment.FriendFragment;
import com.tftechsz.mine.utils.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * 我的好友，关注，粉丝
 */
public class MineFriendActivity extends BaseMvpActivity implements View.OnClickListener {

    private UserProviderService service;
    private MineApiService userApi;
    private List<String> titles;
    private  FragmentVpAdapter adapter;
    private SlidingScaleTabLayout mTabLayout;
    public static final String TYPE_MINE_FRIEND = "friend_type";
    public static final int TYPE_FRIEND = 0;
    public static final int TYPE_WATCH = 1;
    public static final int TYPE_FANS = 2;

    @Override
    protected void initView(Bundle savedInstanceState) {
        service = ARouter.getInstance().navigation(UserProviderService.class);
        userApi = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        UserInfo userInfo = service.getUserInfo();
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mTabLayout = findViewById(R.id.tabLayout);
        ViewPager mViewPager = findViewById(R.id.vp);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(FriendFragment.newInstance(FriendFragment.TYPE_FRIEND));
        fragments.add(FriendFragment.newInstance(FriendFragment.TYPE_WATCH));
        fragments.add(FriendFragment.newInstance(FriendFragment.TYPE_FANS));
        titles = new ArrayList<>();
        titles.add("好友 "+userInfo.getFriend_number());
        titles.add("关注 "+userInfo.getWatch_number());
        titles.add("粉丝 "+userInfo.getFans_number());
        adapter = new FragmentVpAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(adapter);
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(getIntent().getIntExtra(TYPE_MINE_FRIEND, 0));
    }

    @Override
    protected void initData() {
        super.initData();
        ((TextView)findViewById(R.id.toolbar_title)).setText(getIntent().getStringExtra("username"));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_mine_friend;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all)
            finish();
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    public void onRefresh(String type){
        mCompositeDisposable.add(userApi.getUserInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        UserInfo userInfo = response.getData();
                        if(type.equals("watch")){
                            Collections.replaceAll(titles,titles.get(1),"关注 "+userInfo.getWatch_number());
                        }else if(type.equals("fans")){
                            Collections.replaceAll(titles,titles.get(2),"粉丝 "+userInfo.getFans_number());
                        }else{
                            Collections.replaceAll(titles,titles.get(0),"好友 "+userInfo.getFriend_number());
                        }
                        adapter.notifyDataSetChanged();
                        mTabLayout.notifyDataSetChanged();
                    }
                }));
    }
}
