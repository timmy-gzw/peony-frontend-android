package com.tftechsz.mine.mvp.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.ImageUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.SimpleImmersionOwner;
import com.hoko.blur.HokoBlur;
import com.hoko.blur.task.AsyncBlurTask;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.NobleItemDetailBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.fragment
 * 描 述 : TODO
 */
public class NobleItemDetailDialogFragment extends DialogFragment implements SimpleImmersionOwner {
    private NobleItemDetailBinding mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.noble_item_detail, null);
        mBind = DataBindingUtil.bind(view);
        View decorView = getActivity().getWindow().getDecorView();
        HokoBlur.with(getActivity()).radius(20)
                .asyncBlur(decorView, new AsyncBlurTask.Callback() {
                    @Override
                    public void onBlurSuccess(Bitmap bitmap) {
                        mBind.root.setBackground(ImageUtils.bitmap2Drawable(bitmap));
                    }

                    @Override
                    public void onBlurFailed(Throwable error) {

                    }
                });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar_Fullscreen);
    }

    @Override
    public void onStart() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置宽度为铺满
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        super.onStart();
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).transparentNavigationBar().init();
    }

    @Override
    public boolean immersionBarEnabled() {
        return true;
    }


   /* @Override
    public void onActivityCreated(Bundle savedInstanceState) {
                getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
                getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }*/
}
