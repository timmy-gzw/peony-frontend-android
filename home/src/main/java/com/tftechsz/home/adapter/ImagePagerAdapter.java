package com.tftechsz.home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.utils.GlideUtils;

import java.util.List;

public class ImagePagerAdapter extends CarouselPagerAdapter<CarouselViewPager> {

    private final List<String> images;
    private final Context context;

    public ImagePagerAdapter(Context context, List<String> images, CarouselViewPager viewPager) {
        super(viewPager);
        this.images = images;
        this.context = context;
    }


    @Override
    public Object instantiateRealItem(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.CENTER);
        view.setAdjustViewBounds(true);
        GlideUtils.loadRouteImage(context, view, images.get(position));
        view.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dp2px(context, 29), DensityUtils.dp2px(context, 29)));
        container.addView(view);
        return view;
    }

    @Override
    public int getRealDataCount() {
        return images != null ? images.size() : 0;
    }
}
