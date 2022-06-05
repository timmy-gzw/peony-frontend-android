package com.tftechsz.home.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.home.R;
import com.tftechsz.common.utils.GlideUtils;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.home.adapter
 * 描 述 : TODO
 */
public class HomePictureAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private Context context;

    public HomePictureAdapter(Context context) {
        super(R.layout.item_home_picture);
        this.context = context;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        ImageView pic = helper.getView(R.id.pic);
        GlideUtils.loadRouteImage(context, pic, item);
    }
}
