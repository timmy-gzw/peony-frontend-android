package com.tftechsz.common.widget;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyBannerImageHolder extends RecyclerView.ViewHolder {
    public MyImageView imageView;

    public MyBannerImageHolder(@NonNull View view) {
        super(view);
        this.imageView = (MyImageView) view;
    }
}
