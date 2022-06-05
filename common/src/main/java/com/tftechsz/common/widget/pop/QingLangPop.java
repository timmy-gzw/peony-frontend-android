package com.tftechsz.common.widget.pop;

import android.view.View;
import android.widget.ImageView;

import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;

/**
 * 包 名 : com.tftechsz.common.widget.pop
 * 描 述 : TODO
 */
public class QingLangPop extends BaseCenterPop {
    public QingLangPop(String imgUrl, String link, ConfigInfo.Option option) {
        super(BaseApplication.getInstance());
        ImageView img = findViewById(R.id.img);
        ImageView del = findViewById(R.id.del);

        GlideUtils.loadRouteImage(getContext(), img, imgUrl);
        img.setOnClickListener(v -> CommonUtil.performLink(getContext(), new ConfigInfo.MineInfo(link, option)));
        del.setOnClickListener(v -> dismiss());

    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_qing_lang);
    }
}
