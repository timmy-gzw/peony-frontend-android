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

public class LevelTitleAdapter extends BaseQuickAdapter<ConfigInfo.LevelLadder, BaseViewHolder> {


    private Context mContext;
    private String gendar;//用户性别：0.未知，1.男，2.女

    public LevelTitleAdapter(Context context,String gendar) {
        super(R.layout.item_level_title);
        this.mContext = context;
        this.gendar = gendar;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ConfigInfo.LevelLadder levelLadder) {
        TextView tvLevel = baseViewHolder.getView(R.id.tv_level);
        tvLevel.setText(levelLadder.min_level+"-"+levelLadder.max_level);
        TextView tvTitle = baseViewHolder.getView(R.id.tv_title);
        tvTitle.setText(levelLadder.boy_title != null ?(gendar.equals("1")?levelLadder.boy_title:levelLadder.girl_title):levelLadder.title);
        ImageView ivBg = baseViewHolder.getView(R.id.iv_bg);
        ConfigInfo config = getConfig(mContext);
        String url = config.api.oss.cdn_scheme + config.api.oss.cdn.pl + levelLadder.icon;
        Glide.with(mContext).load(url).into(ivBg);
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
