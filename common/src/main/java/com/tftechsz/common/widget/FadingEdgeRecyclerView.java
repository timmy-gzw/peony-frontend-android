package com.tftechsz.common.widget;


import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class FadingEdgeRecyclerView extends RecyclerView {

    public FadingEdgeRecyclerView(@NonNull @NotNull Context context) {
        super(context);
    }

    public FadingEdgeRecyclerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FadingEdgeRecyclerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 去掉下面阴影
     * @return
     */
    @Override
    protected float getBottomFadingEdgeStrength() {
        return 0;
    }
}
