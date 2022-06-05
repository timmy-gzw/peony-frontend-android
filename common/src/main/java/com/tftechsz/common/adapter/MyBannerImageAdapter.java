package com.tftechsz.common.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.tftechsz.common.widget.MyBannerImageHolder;
import com.tftechsz.common.widget.MyImageView;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * 默认实现的图片适配器，图片加载需要自己实现
 */
public abstract class MyBannerImageAdapter<T> extends BannerAdapter<T, MyBannerImageHolder> {

    public MyBannerImageAdapter(List<T> mData) {
        super(mData);
    }

    @Override
    public MyBannerImageHolder onCreateHolder(ViewGroup parent, int viewType) {
        MyImageView imageView = new MyImageView(parent.getContext());
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new MyBannerImageHolder(imageView);
    }

}
