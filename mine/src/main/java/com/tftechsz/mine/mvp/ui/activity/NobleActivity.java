package com.tftechsz.mine.mvp.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.NoblePrivilegeAdapter;
import com.tftechsz.mine.databinding.ActMyNobleBinding;
import com.tftechsz.mine.entity.NobleBean;
import com.tftechsz.mine.entity.NobleItemClickEvent;
import com.tftechsz.mine.entity.dto.NoblePrivilegeDto;
import com.tftechsz.mine.mvp.IView.IMyNobleView;
import com.tftechsz.mine.mvp.presenter.MyNoblePresenter;
import com.tftechsz.mine.widget.pop.NobleItemDetailPop;
import com.tftechsz.mine.widget.pop.NoblePayPop;
import com.youth.banner.transformer.AlphaPageTransformer;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 贵族中心
 */
@Route(path = ARouterApi.ACTIVITY_MY_NOBLE)
public class NobleActivity extends BaseMvpActivity<IMyNobleView, MyNoblePresenter> implements View.OnClickListener, IMyNobleView {

    private ActMyNobleBinding mBind;
    private int pageSel = -1;
    private final List<List<NoblePrivilegeDto>> adapterDto = new ArrayList<>();
    private NoblePayPop mNoblePayPop;
    private PageStateManager mPageManager;
    private NobleItemDetailPop mPop;
    private SVGAParser svgaParser;
    private UserProviderService service;
    private StringBuilder svgaZipPath;
    private final ArrayList<GradientDrawable> gradientList = new ArrayList<>();
    private final SVGAParser.ParseCompletion mParseCompletionCallback = new SVGAParser.ParseCompletion() {
        @Override
        public void onComplete(@NotNull SVGAVideoEntity videoItem) {
            if (mBind != null) {
                mBind.svgImage.setVisibility(View.VISIBLE);
                mBind.lottieBg.setVisibility(View.VISIBLE);
                mBind.icon.setVisibility(View.GONE);
                if (!mBind.lottieBg.isAnimating()) {
                    mBind.lottieBg.playAnimation();
                }

                mBind.svgImage.setVideoItem(videoItem);
                mBind.svgImage.stepToFrame(0, true);

            }
        }

        @Override
        public void onError() {

        }
    };
    private NoblePrivilegeAdapter mNoblePrivilegeAdapter;

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_my_noble);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(mActivity).transparentStatusBar().navigationBarDarkIcon(false).navigationBarColor(R.color.black).statusBarDarkFont(false, 0.2f).init();
        svgaZipPath = new StringBuilder();
        svgaZipPath.append(DownloadHelper.FILE_PATH).append(File.separator);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        svgaParser = new SVGAParser(BaseApplication.getInstance());
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mBind.back.getLayoutParams();
        lp.topMargin = ImmersionBar.getStatusBarHeight(this);
        mBind.back.setLayoutParams(lp);
        mBind.back.setOnClickListener(this);
        mBind.tips.setOnClickListener(this);
        mNoblePrivilegeAdapter = new NoblePrivilegeAdapter();
        mBind.vp.setAdapter(mNoblePrivilegeAdapter);
        mBind.vp.setPageTransformer(new AlphaPageTransformer());
        initRxBus();
        mPageManager = PageStateManager.initWhenUse(this, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showLoading();
                p.getNobilityList();
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
            ImmersionBar.with(mActivity).statusBarColor(R.color.transparent).statusBarDarkFont(true).navigationBarColor(R.color.white).navigationBarDarkIcon(true).init();
            mPageManager.showError(null);
        } else {
            mPageManager.showContent();
            p.getNobilityList();
        }
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (/*event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS || */event.type == Constants.NOTIFY_NOBILITY_NOTICE_TO_UID) {
                                if (mNoblePayPop != null && mNoblePayPop.isShowing()) {
                                    mNoblePayPop.dismiss();
                                }
                                p.getNobilityList();
                            }
                        }
                ));
        mCompositeDisposable.add(RxBus.getDefault().toObservable(NobleItemClickEvent.class)
                .subscribe(event -> {
                            if (mPop == null) {
                                mPop = new NobleItemDetailPop(mActivity);
                                mPop.setOnPopupWindowShowListener(() -> {
                                    mBind.svgImage.postDelayed(() -> loadSvga(pageSel), 100);
                                });
                            }
                            loadPic(pageSel);
                            mPop.setData(mBean);
                            mPop.updateIndex(pageSel, event.position, true);
                            mPop.showPopupWindow();
                        }
                ));
    }

    private int currentPos = -1;

    private void performPage(int position) {
        if (currentPos == position) return;
        currentPos = position;
        NobleBean.GradeDTO gradeDTO = mBean.grade.get(position);
        mBind.topTitle.setText(String.format("%s/%s", gradeDTO.privilege.size(), mBean.privilege.size()));
        if (TextUtils.isEmpty(gradeDTO.expiration_time_tips)) {
            mBind.leftData.setVisibility(View.INVISIBLE);
        } else {
            mBind.leftData.setVisibility(View.VISIBLE);
            mBind.leftData.setText(gradeDTO.expiration_time_tips);
        }
        mBind.ngct.setText(gradeDTO.bottom_tips);
        mBind.rootBg.setBackground(gradientList.get(position));
        mBind.botBg.setBackground(null);
        if (!isDestroyed())
            Glide.with(this).asDrawable().load(String.format(mBean.bottom_bg, gradeDTO.id)).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    mBind.botBg.setBackground(resource);
                    return false;
                }
            }).submit();
        loadPic(position);
        loadSvga(position);
        pageSel = position;

        ARouter.getInstance()
                .navigation(MineService.class)
                .trackEvent("我的贵族页面曝光", "mynoble_visit", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), gradeDTO.id, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME)), null);

    }

    private void loadSvga(int position) {
        NobleBean.GradeDTO gradeDTO = mBean.grade.get(position);

        //gradeDTO.id = 12; //test测试
        String svgaPath = svgaZipPath.toString() + String.format("nobility_%s", gradeDTO.id);
        if (FileUtils.isFileExists(svgaPath)) {
            playAirdrop(new File(svgaPath));
            //Utils.logE("文件存在, 直接读取~~~");
        } else {
            if (TextUtils.isEmpty(mBean.svga_link)) {
                loadPic(position);
                return;
            }
            //Utils.logE("文件不存在, 下载中:" + String.format(mBean.svga_link, mBean.grade.get(position).id).replace(".svga", ""));
            DownloadHelper.download(String.format(mBean.svga_link, gradeDTO.id), new DownloadHelper.DownloadListener() {
                @Override
                public void completed() {
                    //Utils.logE("下载完成:" + svgaPath);
                    playAirdrop(new File(svgaPath));
                }

                @Override
                public void failed() {
                    //Utils.logE("下载失败,加载静态图");
                    loadPic(position);
                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }
    }

    private void loadPic(int position) {
        mBind.icon.setVisibility(View.VISIBLE);
        mBind.svgImage.setVisibility(View.GONE);
        mBind.lottieBg.setVisibility(View.VISIBLE);
        if (!mBind.lottieBg.isAnimating()) {
            mBind.lottieBg.playAnimation();
        }
        if (mBind.svgImage.isAnimating()) {
            mBind.svgImage.stopAnimation();
            mBind.svgImage.clearAnimation();
        }

        if (!isDestroyed())
            Glide.with(mContext)
                    .load(String.format(mBean.nobility_icon, mBean.grade.get(position).id))
                    .error(mBind.icon.getDrawable() == null ? Utils.getDrawable(R.drawable.bg_trans) : mBind.icon.getDrawable())
                    .placeholder(mBind.icon.getDrawable() == null ? Utils.getDrawable(R.drawable.bg_trans) : mBind.icon.getDrawable())
                    .into(mBind.icon);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public MyNoblePresenter initPresenter() {
        return new MyNoblePresenter();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.tips) {
            if (mBean != null && !TextUtils.isEmpty(mBean.link)) {
                BaseWebViewActivity.start(this, "", mBean.link, true, 0, 0);
            }
        }
    }

    private NobleBean mBean;
    int netPageSel = 0;

    @Override
    public void getDataSuccess(NobleBean data) {
        ImmersionBar.with(mActivity).transparentStatusBar().navigationBarDarkIcon(false).navigationBarColor(R.color.black).statusBarDarkFont(false, 0.2f).init();
        netPageSel = 0;
        mPageManager.showContent();
        mBean = data;
        mBind.setIsShow(true);
        adapterDto.clear();
        gradientList.clear();
        mBind.tips.setVisibility(!TextUtils.isEmpty(mBean.link) ? View.VISIBLE : View.GONE);

        ArrayList<String> titles = new ArrayList<>();
        for (int i = 0; i < data.grade.size(); i++) {
            NobleBean.GradeDTO gd = data.grade.get(i);
            titles.add(gd.name);
            ArrayList<NoblePrivilegeDto> list = new ArrayList<>();
            for (int j = 0; j < data.privilege.size(); j++) {
                list.add(new NoblePrivilegeDto(data.privilege, gd.privilege, data.privilege_icon, gd.heat));
            }
            if (gd.isSel()) {
                mBind.leftData.setText(gd.expiration_time_tips);
                netPageSel = i;
            }
            NobleBean.bgDTO bgDTO = data.bg_color_code.get(i);
            int[] color = {Color.parseColor(bgDTO.bg_start_color), Color.parseColor(bgDTO.bg_end_color)};
            gradientList.add(new GradientDrawable(GradientDrawable.Orientation.TL_BR, color));
            adapterDto.add(list);
        }
        mNoblePrivilegeAdapter.setList(adapterDto);
        mBind.vp.setOffscreenPageLimit(adapterDto.size());
        if (mBind.vp.getChildCount() > 0) {
            mBind.vp.getChildAt(0).setNestedScrollingEnabled(false);
        }
        bind(mBind.vp);
        if (!titles.isEmpty()) {
            mBind.tabLayout.setViewPager(mBind.vp, titles.toArray(new String[0]));
            //mBind.tabLayout.setSnapOnTabClick(true);
            mBind.tabLayout.postDelayed(() -> {
                performPage(netPageSel);
                mBind.tabLayout.setScrollListener(this::performPage);
                mBind.tabLayout.setCurrentTab(netPageSel, false);
            }, 100);
        }
    }

    private void playAirdrop(File file) {
        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            svgaParser.decodeFromInputStream(bis, file.getAbsolutePath(), mParseCompletionCallback, true, null, null);
        } catch (FileNotFoundException e) {
            loadPic(pageSel);
            e.printStackTrace();
        }
    }

    public void bind(ViewPager2 viewPager) {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

            }
        });
    }
}
