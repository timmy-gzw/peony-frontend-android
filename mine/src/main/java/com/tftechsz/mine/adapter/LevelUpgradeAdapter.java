package com.tftechsz.mine.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.mine.R;

import java.lang.reflect.Type;

public class LevelUpgradeAdapter extends BaseQuickAdapter<ConfigInfo.LevelIntroduction, BaseViewHolder> {

    private Context mContext;
    public LevelUpgradeAdapter(Context context) {
        super(R.layout.item_level_upgrade);
        this.mContext = context;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ConfigInfo.LevelIntroduction lv) {
        ImageView imageView = baseViewHolder.getView(R.id.iv_image);
        TextView tvTitle = baseViewHolder.getView(R.id.tv_title);
        TextView tvTip = baseViewHolder.getView(R.id.tv_tip);
        tvTitle.setText(lv.title);
        tvTip.setText(lv.description);
        ConfigInfo config = getConfig(mContext);
        Glide.with(mContext).load(config.api.oss.cdn_scheme + config.api.oss.cdn.pl+lv.icon).into(imageView);
    }

    public ConfigInfo getConfig(Context context) {
        SharedPreferences sp = context.getSharedPreferences("tfpeony-pref",
                Context.MODE_PRIVATE);
        String config = sp.getString("configInfo", "");
        Gson gson = new Gson();
        ConfigInfo configInfo = gson.fromJson(config, (Type) ConfigInfo.class);
        return configInfo;
    }
}
