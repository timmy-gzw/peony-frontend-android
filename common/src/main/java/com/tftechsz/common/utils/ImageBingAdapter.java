package com.tftechsz.common.utils;


import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tftechsz.common.R;
import com.tftechsz.common.other.GlideRoundTransform;

/**
 * 包 名 : com.tftechsz.mine.utils
 * 描 述 : TODO
 */
public class ImageBingAdapter {
    @BindingAdapter("imageUrl")
    public static void bindImageUrl(ImageView view, String imageUrl) {
        Glide.with(view)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .into(view);
    }

    @BindingAdapter("imageBackgroundUrl")
    public static void bindBackgroundUrl(ImageView view, String imageUrl) {
        Glide.with(view)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        view.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @BindingAdapter("imageUrlCri")
    public static void bindImageUrlCri(ImageView view, String imageUrl) {
        RequestOptions options2 = new RequestOptions()
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .transform(new GlideRoundTransform(view.getContext(), 10));

        Glide.with(view)
                .load(imageUrl)
                .apply(options2)
//                .transform(new GlideRoundTransform(view.getContext(), 50))
                .into(view);
    }


    @BindingAdapter("android:layout_marginStart")
    public static void setStartMargin(View view, int startMargin) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.setMargins(startMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin);
        view.setLayoutParams(lp);
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setTopMargin(View view, int topMargin) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.setMargins(lp.leftMargin, topMargin, lp.rightMargin, lp.bottomMargin);
        view.setLayoutParams(lp);
    }

    @BindingAdapter("android:layout_marginEnd")
    public static void setEndMargin(View view, int endMargin) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.setMargins(lp.leftMargin, lp.topMargin, endMargin, lp.bottomMargin);
        view.setLayoutParams(lp);
    }

    @BindingAdapter("android:layout_marginBottom")
    public static void setBottomMargin(View view, int botMargin) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, botMargin);
        view.setLayoutParams(lp);
    }
}
