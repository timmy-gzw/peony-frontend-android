package com.tftechsz.im.uikit;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.im.R;
import com.tftechsz.im.mvp.ui.fragment.PartyChatListFragment;
import com.tftechsz.im.mvp.ui.fragment.PartyChatOnLineFragment;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.widget.DepthPageTransformer;

import java.util.ArrayList;


/**
 * 派对列表
 */
@Route(path = ARouterApi.ACTIVITY_PARTY_MESSAGE)
public class PartyChatActivity extends BaseMvpActivity implements View.OnClickListener {
    private TextView mTvLine, mTvChat;
    private View mViewLine1, mViewLine2;
    private ViewPager mViewPager;
    private String roomId, roomName, roomIcon;
    private int mHeight;
    private PartyChatListFragment partyChatListFragment;
    private PartyChatOnLineFragment partyChatOnLineFragment;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mFlagIsPortrait = false;
        super.onCreate(savedInstanceState);
        //解决背景黑色
//        getWindow().setBackgroundDrawable(null);
    }

    @Override
    protected int getLayout() {
        return R.layout.layout_party_chat;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        roomId = getIntent().getStringExtra("roomId");
        roomName = getIntent().getStringExtra("roomName");
        roomIcon = getIntent().getStringExtra("roomIcon");
        mViewLine1 = findViewById(R.id.view_bottom_line1);
        mViewLine2 = findViewById(R.id.view_bottom_line2);
        mTvLine = findViewById(R.id.tv_tag_online);
        mTvLine.setOnClickListener(this);
        mTvChat = findViewById(R.id.tv_tag_chat);
        //解决点击头部空白也关闭问题
        findViewById(R.id.rootclick).setOnClickListener(this);
        mTvChat.setOnClickListener(this);
        mViewPager = findViewById(R.id.vp);
        ArrayList<Fragment> fragments = new ArrayList<>();
        partyChatListFragment = PartyChatListFragment.newInstance(2, mHeight);
        fragments.add(partyChatListFragment);
        partyChatOnLineFragment = PartyChatOnLineFragment.newInstance(2, roomId, mHeight);
        fragments.add(partyChatOnLineFragment);
        mViewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), fragments, null));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setBtn(position);
                visitPartyList(position == 0 ? 4 : 5);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 上传日志
     * 4\语音房私信页面曝光   5\语音房在线列表曝光
     */
    public void visitPartyList(int type) {
        UserProviderService serviceUser = ARouter.getInstance().navigation(UserProviderService.class);
        if (serviceUser == null) {
            return;
        }
        if (type == 4 || type == 5) {
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent(type == 4 ? "语音房私信页面曝光" : "语音房在线列表曝光", type == 4 ? "voice_room_msg_list_visit" : "voice_room_online_list_visit", "", JSON.toJSONString(new NavigationLogEntity(serviceUser.getUserId(), roomId, System.currentTimeMillis(), roomName, roomIcon, 1, CommonUtil.getOSName(), Constants.APP_NAME)), null);

        }

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onAttachedToWindow() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view
                .getLayoutParams();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        mHeight = DensityUtils.getDisplay(this).getHeight() - DensityUtils.dp2px(this, 150);
        lp.height = mHeight;
        if (partyChatListFragment != null) {
            partyChatListFragment.mHeight = mHeight;
        }
        if (partyChatOnLineFragment != null) {
            partyChatOnLineFragment.windowHeight = mHeight;
        }
        lp.gravity = Gravity.BOTTOM;
    /*    lp.x = 0;

        lp.y = 200;*/
        getWindowManager().updateViewLayout(view, lp);
        //下面两行代码的顺序不可以改变不然圆角背景就设置不上了

//        view.setBackgroundResource(R.drawable.bg_mine_white_top);//圆角背景

        // 点击窗口外部区域可消除
        view.setOnTouchListener((v, event) -> {


            int x = (int) event.getX();
            int y = (int) event.getY();
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);

            if (!rect.contains(x, DensityUtils.getDisplay(PartyChatActivity.this).getHeight() - DensityUtils.dp2px(PartyChatActivity.this, 150))) {
                finish();
            }

            return false;
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_tag_online) {
            setBtn(1);
        } else if (id == R.id.tv_tag_chat) {
            setBtn(0);
        }
    }

    private void setBtn(int position) {
        if (position == 1) {
            mTvLine.setTextColor(Color.parseColor("#333333"));
            mTvChat.setTextColor(Color.parseColor("#999999"));
            mViewLine1.setVisibility(View.GONE);
            mViewLine2.setVisibility(View.VISIBLE);
            mViewPager.setCurrentItem(1);
        } else {
            mTvChat.setTextColor(Color.parseColor("#333333"));
            mTvLine.setTextColor(Color.parseColor("#999999"));
            mViewLine2.setVisibility(View.GONE);
            mViewLine1.setVisibility(View.VISIBLE);
            mViewPager.setCurrentItem(0);
        }
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);
    }
}
