package com.tftechsz.im.adapter;

import android.text.Html;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.im.R;
import com.tftechsz.common.Constants;
import com.tftechsz.common.utils.Utils;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.im.adapter
 * 描 述 : TODO
 */
public class FamilyBoxDescAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public FamilyBoxDescAdapter(List<String> data) {
        super(R.layout.item_family_box_desc, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String s) {
        TextView desc = helper.getView(R.id.desc);
        desc.setText(Html.fromHtml(s.replace(Constants.WEB_PARAMS_APP_NAME, Utils.getString(R.string.app_name))));
    }
}
