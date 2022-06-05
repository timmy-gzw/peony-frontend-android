package com.tftechsz.common.adapter;

import android.animation.AnimatorSet;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.constant.IGiftVpOnItemClick;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

public class GiftAdapter extends BaseQuickAdapter<GiftDto, GiftAdapter.ViewHolder> {

    private int checkPosition;
    private final SparseArray<AnimatorSet> animationSetSparseArray;
    private final IGiftVpOnItemClick iGiftVpOnItemClick;
    private GradientDrawable mGradientDrawable;
    private int[] color;

    public GiftAdapter(@Nullable List<GiftDto> data, IGiftVpOnItemClick iGiftVpOnItemClick) {
        super(R.layout.item_gift, data);
        checkPosition = 0;
        animationSetSparseArray = new SparseArray<>();
        this.iGiftVpOnItemClick = iGiftVpOnItemClick;
    }

    public GiftAdapter(@Nullable List<GiftDto> data, IGiftVpOnItemClick iGiftVpOnItemClick, int checkPosition) {
        super(R.layout.item_gift, data);
        this.checkPosition = checkPosition;
        animationSetSparseArray = new SparseArray<>();
        this.iGiftVpOnItemClick = iGiftVpOnItemClick;
    }

    public void setCheckPosition(int p) {
        checkPosition = p;
    }

    public void setCheckPositions(int p) {
        checkPosition = p;
        notifyDataSetChanged();

    }

    public int getCheckPosition() {
        return checkPosition;
    }

    public GiftDto getGift() {
        if (checkPosition != -1 && getItemCount() > getCheckPosition()) {
            return getItem(checkPosition);
        }
        return null;
    }

    @Override
    protected void convert(@NonNull GiftAdapter.ViewHolder holder, GiftDto item) {
        holder.name.setText(item.name);
        holder.price.setText(item.coin + "金币");
        GlideUtils.loadRouteImage(getContext(), holder.iv, item.getImage());
        holder.canUseBg.setVisibility(item.can_use ? View.GONE : View.VISIBLE);
        if (!TextUtils.isEmpty(item.getTag())) {
            GlideUtils.loadRouteImage(getContext(), holder.tvGiftType, item.getTag());
            holder.tvGiftType.setVisibility(View.VISIBLE);
        } else {
            if (item.animationType == 2) { // 1.普通PNG 2.炫 3.动 4七夕
                holder.tvGiftType.setVisibility(View.VISIBLE);
                GlideUtils.loadImage(getContext(), holder.tvGiftType, R.mipmap.ic_bg_gift_dazzle, R.mipmap.ic_bg_gift_dazzle);
            } else if (item.animationType == 3) {
                holder.tvGiftType.setVisibility(View.VISIBLE);
                GlideUtils.loadImage(getContext(), holder.tvGiftType, R.mipmap.ic_bg_gift_move, R.mipmap.ic_bg_gift_move);
            } else if (item.animationType == 4) {
                holder.tvGiftType.setVisibility(View.VISIBLE);
                GlideUtils.loadImage(getContext(), holder.tvGiftType, R.mipmap.ic_bg_gift_qx, R.mipmap.ic_bg_gift_qx);
            } else {
                holder.tvGiftType.setVisibility(View.GONE);
            }
        }
        if (!TextUtils.isEmpty(item.getExpired_time())) { //如果过期时间不为空
            holder.expired_time.setVisibility(View.VISIBLE);
            holder.expired_time.setText(item.getExpired_time());
            holder.tvGiftType.setVisibility(View.GONE);
            holder.num.setVisibility(View.VISIBLE);
            holder.num.setText(String.format("x%s", item.getNumber()));
        } else {
            holder.num.setVisibility(View.GONE);
            holder.expired_time.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NotNull GiftAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (checkPosition >= getItemCount()) {
            checkPosition = 0;
        }
        if (checkPosition == position) {
            if (mGradientDrawable == null) {
                if (color == null) {
                    color = new int[]{Color.parseColor("#FFD400"), Color.parseColor("#F36A54"), Color.parseColor("#F74767"), Color.parseColor("#DA2AF9")};
                }
                mGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, color);
                mGradientDrawable.setCornerRadius(ConvertUtils.dp2px(8));
            }
            holder.bg.setVisibility(View.VISIBLE);
            holder.root.setBackground(mGradientDrawable);
        } else {
            holder.bg.setVisibility(View.GONE);
            holder.root.setBackground(Utils.getDrawable(R.drawable.bg_trans));
        }
        holder.root.setOnClickListener(view -> {
            if (checkPosition == position) {
                return;
            } else {
                animationSetSparseArray.get(position);
                AnimatorSet set = animationSetSparseArray.get(position);
                if (null != set)
                    set.cancel();
                checkPosition = position;
            }
            if (iGiftVpOnItemClick != null) {
                iGiftVpOnItemClick.onitemClickGiftInfoActivityGet(getGift());
            }
            notifyDataSetChanged();
        });
        if (checkPosition == position) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setRepeatMode(Animation.REVERSE);
            scaleAnimation.setRepeatCount(Animation.INFINITE);
            scaleAnimation.setDuration(400);
            holder.iv.startAnimation(scaleAnimation);
            if (checkPosition != 0 && iGiftVpOnItemClick != null) {
                iGiftVpOnItemClick.onitemClickGiftInfoActivityGet(getGift());
            }
        }
    }

    public static class ViewHolder extends BaseViewHolder {
        ConstraintLayout root;
        ImageView tvGiftType, iv;
        View canUseBg, bg;
        TextView name, price, expired_time;
        TextView num;

        ViewHolder(View itemView) {
            super(itemView);
            root = getView(R.id.root);
            tvGiftType = getView(R.id.tv_gift_type);
            canUseBg = getView(R.id.can_use_bg);
            bg = getView(R.id.bg);
            iv = getView(R.id.iv);
            name = getView(R.id.tv_name);
            price = getView(R.id.tv_price);
            expired_time = getView(R.id.expired_time);
            num = getView(R.id.num);
        }
    }
}
