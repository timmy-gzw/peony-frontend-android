package com.tftechsz.common.widget;

import android.view.View;

import com.flyco.tablayout.transformer.IViewPagerTransformer;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * 包 名 : com.tftechsz.common.widget
 * 描 述 : TODO
 */
public class MyExtendTransformer implements ViewPager2.PageTransformer {

    private final ArrayList<IViewPagerTransformer> transformers = new ArrayList<>();


    public MyExtendTransformer() {
    }

    public void addViewPagerTransformer(IViewPagerTransformer transformer) {
        if (!transformers.contains(transformer)) {
            transformers.add(transformer);
        }
    }

    public void removeViewPagerTransformer(IViewPagerTransformer transformer) {
        transformers.remove(transformer);
    }

    public List<IViewPagerTransformer> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<IViewPagerTransformer> transformers) {
        this.transformers.addAll(transformers);
    }

    @Override
    public void transformPage(@NonNull View view, final float position) {
        // 回调设置的页面切换效果设置
        if (transformers != null && transformers.size() > 0) {
            for (IViewPagerTransformer transformer : transformers) {
                transformer.transformPage(view, position);
            }
        }
    }


}
