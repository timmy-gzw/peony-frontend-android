package com.tftechsz.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.Constants;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.home.R;
import com.tftechsz.home.widget.carrousellayout.CarrouselLayout;

public class HomeTopItemLayout extends LinearLayout {
    private Context mContext;
    private int oldTopSize;
    public CarrouselLayout mCarrousel;

    public HomeTopItemLayout(Context context) {
        super(context);
        init(context);
    }

    public HomeTopItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeTopItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mContext = context;

    }

    public void setData(ConfigInfo.Nav data, int topSize) {
        if (oldTopSize != topSize) {
            removeAllViews();
            oldTopSize = topSize;
        }
        View.inflate(mContext, R.layout.item_home_right, this);
        ImageView bg = findViewById(R.id.bg_frame);
        ImageView right_img = findViewById(R.id.right_img);
        LinearLayout llAvatar2 = findViewById(R.id.ll_avatar2);

        TextView tvContent = findViewById(R.id.tv_content);
        TextView tvDesc = findViewById(R.id.tv_desc);

        LottieAnimationView lottieAnimationView = findViewById(R.id.animation_view);
        lottieAnimationView.setImageAssetsFolder(Constants.ACCOST_GIFT);
        if (data == null) return;
        tvContent.setText(data.title_1);
        tvDesc.setText(data.title_2);
        GlideUtils.loadImageNew(mContext, bg, data.bg_img, data.radius == 0 ? 10 : data.radius);
        if (!TextUtils.isEmpty(data.link) && data.link.equals(Interfaces.LINK_PEONY + Interfaces.LINK_PEONY_JOIN_VIDEO_MATCH)) {
            if (data.img_list != null && data.img_list.size() >= 2) {
                Utils.runOnUiThread(() -> {
                    if (llAvatar2.getChildCount() == 0) {
                        View topLeft = LayoutInflater.from(mContext).inflate(R.layout.item_home_banner_ani, null);
                        mCarrousel = topLeft.findViewById(R.id.carrousel);
                        topLeft.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        ImageView img1 = topLeft.findViewById(R.id.img_carrousel1);
                        ImageView img2 = topLeft.findViewById(R.id.img_carrousel2);
                        ImageView img3 = topLeft.findViewById(R.id.img_carrousel3);
                        GlideUtils.loadRoundImage(mContext, img1, data.img_list.get(0));
                        GlideUtils.loadRoundImage(mContext, img2, data.img_list.get(1));
                        GlideUtils.loadRoundImage(mContext, img3, data.img_list.get(2));
                        llAvatar2.addView(topLeft);
                    }
                    llAvatar2.setVisibility(View.VISIBLE);
                });
            }
        } else {
            Utils.runOnUiThread(() -> {
                llAvatar2.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(data.json)) {
                    lottieAnimationView.setVisibility(VISIBLE);
                    lottieAnimationView.setImageAssetsFolder(Constants.ACCOST_GIFT);
                    lottieAnimationView.setAnimationFromUrl(data.json);
                    lottieAnimationView.setFailureListener(result -> result.printStackTrace());
                    lottieAnimationView.playAnimation();
                } else {
                    GlideUtils.loadRouteImage(mContext, right_img, data.right_img);
                }
            });
        }
    }

}
