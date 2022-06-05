package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.NetworkUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ActVipDressUpBinding;
import com.tftechsz.mine.entity.DressListDto;
import com.tftechsz.mine.mvp.IView.IDressUpView;
import com.tftechsz.mine.mvp.presenter.IDressUpPresenter;
import com.tftechsz.mine.mvp.ui.fragment.DressUpFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 会员装扮
 */
@Route(path = ARouterApi.ACTIVITY_VIP_DRESS_UP)
public class VipDressUpActivity extends BaseMvpActivity<IDressUpView, IDressUpPresenter> implements IDressUpView {
    private UserProviderService service;
    private ActVipDressUpBinding mBind;
    private int mDefaultPage;
    private PageStateManager mPageManager;

    @Override
    public IDressUpPresenter initPresenter() {
        return new IDressUpPresenter();
    }

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_vip_dress_up);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mDefaultPage = getIntent().getIntExtra(Interfaces.EXTRA_PAGE, 0);
        boolean isMyDressUp = getIntent().getBooleanExtra(Interfaces.EXTRA_DATA, true);
        ImmersionBar.setTitleBar(this, mBind.include);
        TextView title = mBind.include.findViewById(R.id.toolbar_title);
        title.setText(isMyDressUp ? "我的装扮" : "会员装扮");
        mBind.include.findViewById(R.id.toolbar_back_all).setOnClickListener(v -> finish());

        mPageManager = PageStateManager.initWhenUse(mBind.llContent, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showLoading();
                p.getCategoryList();
            }

            @Override
            public int customErrorLayoutId() {
                return R.layout.pager_error_exit;
            }

            @Override
            public void onExit() {
                finish();
            }
        });
        if (!NetworkUtils.isConnected()) {
            mPageManager.showError(null);
        } else {
            mPageManager.showLoading();
            p.getCategoryList();
        }
    }

    @Override
    public void getCategoryListSuccess(List<DressListDto> data) {
        mPageManager.showContent();
        int defaultPage = 0;
        if (data != null && data.size() > 0) {
            List<String> titles = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();
            ArrayList<Fragment> fragments = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                DressListDto dto = data.get(i);
                if (dto.status == 1) { //1显示 0不显示
                    titles.add(dto.name);
                    ids.add(dto.id);
                    DressUpFragment df = new DressUpFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Interfaces.EXTRA_TYPE, dto.id);
                    df.setArguments(bundle);
                    fragments.add(df);
                    if (dto.id == mDefaultPage) {
                        defaultPage = i;
                    }
                }
            }
            if (titles.size() > 0) {
                mBind.viewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), fragments, titles));
                mBind.tabLayout.setViewPager(mBind.viewPager);
                mBind.tabLayout.setSnapOnTabClick(true);
                mBind.viewPager.setCurrentItem(defaultPage, false);
                mBind.viewPager.setOffscreenPageLimit(fragments.size());
                mBind.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                        ARouter.getInstance()
                                .navigation(MineService.class)
                                .trackEvent("我的装扮页面曝光", "outfit_visit", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), ids.get(position), System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME)), null);

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
    }

    @Override
    public void getCategoryListError() {
        mPageManager.showError("出错了，请重试");
    }
}
