package com.tftechsz.mine.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.luck.picture.lib.entity.LocalMedia;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;

import androidx.annotation.NonNull;

/**
 * 相册
 */
public class MinePhotoAdapter extends BaseQuickAdapter<LocalMedia, BaseViewHolder> {

    public MinePhotoAdapter() {
        super(R.layout.item_trend);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Interfaces.TYPE_CAMERA;
        }
        return Interfaces.TYPE_PICTURE;
    }

    private boolean isShowDelete;

    @Override
    protected void convert(@NonNull BaseViewHolder helper, LocalMedia item) {
        ImageView ivClose = helper.getView(R.id.iv_close);
        if (getItemViewType(helper.getLayoutPosition()) == Interfaces.TYPE_CAMERA) {
            helper.setImageResource(R.id.iv_trend, R.mipmap.ic_add);
            ivClose.setVisibility(View.GONE);
        } else {
            ivClose.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);
            GlideUtils.loadRoundImage(getContext(), helper.getView(R.id.iv_trend), !TextUtils.isEmpty(item.getCutPath()) ? item.getCutPath() : item.getPath());
        }
    }

    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        notifyDataSetChanged();
    }
}
