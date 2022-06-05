package com.tftechsz.common.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.arcns.glide.grogress.GlideProgressExtensionKt;
import com.arcns.glide.grogress.GlideProgressListener;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gyf.immersionbar.ImmersionBar;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseFragment;
import com.tftechsz.common.constant.Interfaces;

import net.mikaelzero.mojito.impl.DefaultPercentProgress;

/**
 * 包 名 : com.tftechsz.home.mvp.ui.fragment
 * 描 述 : TODO
 */
public class PicLoadFragment extends BaseFragment {

    private PhotoView mPhotoView;
    private String url;
    private FrameLayout mLoadingLayout;
    private int pos;

    public static PicLoadFragment newInstance(int pos, String uri) {
        Bundle args = new Bundle();
        args.putString(Interfaces.EXTRA_IMAGE_URL, uri);
        args.putInt(Interfaces.EXTRA_IMAGE_POS, pos);
        PicLoadFragment fragment = new PicLoadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_pic_load;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        if (getArguments() != null) {

            url = getArguments().getString(Interfaces.EXTRA_IMAGE_URL);
            pos = getArguments().getInt(Interfaces.EXTRA_IMAGE_POS);
        }
        mLoadingLayout = getView(R.id.loadingLayout);
        mPhotoView = getView(R.id.photoView);
    }

    @Override
    protected void initData() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.black)
                .navigationBarColor(R.color.black)
                .statusBarDarkFont(false)
                .navigationBarDarkIcon(false)
                .init();

        mPhotoView.enable();
        mPhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mPhotoView.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        DefaultPercentProgress progress = new DefaultPercentProgress();
        progress.attach(pos, mLoadingLayout);
        mLoadingLayout.setVisibility(View.VISIBLE);
        GlideProgressExtensionKt.loadWithProgress(
                Glide.with(mContext), // Glide RequestManager或RequestBuilder
                mContext, // 上下文
                url, // 图片网络地址
                new GlideProgressListener<Drawable>() {
                    @Override
                    public void onProgress(long current, long total, float percent) {
                        mLoadingLayout.post(() -> mLoadingLayout.setVisibility((int) percent < 100 ? View.VISIBLE : View.GONE));
                        progress.onProgress(pos, (int) percent);
                    }
                }, // 进度监听，可为空
                this, // 生命周期感知，生命周期结束时自动解除监听，可为空
                null, // 进度条，可为空
                null // 进度文本(x%)，可为空
        ).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                mPhotoView.post(() -> mLoadingLayout.setVisibility(View.GONE));
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                mPhotoView.post(() -> {
                    mLoadingLayout.setVisibility(View.GONE);
                    mPhotoView.setImageDrawable(resource);
                    mPhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                });
                return false;
            }
        }).submit();
    }
}
