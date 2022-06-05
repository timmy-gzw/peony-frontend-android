package com.tftechsz.mine.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.AccostSettingListBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class AccostCustomizeAdapter extends BaseQuickAdapter<AccostSettingListBean, BaseViewHolder> {
    private boolean isEditor;
    private List<Integer> checkedIds = new ArrayList<>();

    public AccostCustomizeAdapter(boolean isEditor) {
        super(R.layout.item_accost_customize);
        this.isEditor = isEditor;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, AccostSettingListBean item) {
        CheckBox checkbox = helper.getView(R.id.checkbox);
        TextView content = helper.getView(R.id.content);
        ImageView editor = helper.getView(R.id.editor);
        content.setText(item.text);
        checkbox.setChecked(false);
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkedIds.add(item.id);
            } else {
                for (int i = 0; i < checkedIds.size(); i++) {
                    if (checkedIds.get(i) == item.id) {
                        checkedIds.remove(i);
                        break;
                    }
                }
            }
        });
        checkbox.setVisibility(isEditor ? View.VISIBLE : View.GONE);
        editor.setVisibility(isEditor ? View.VISIBLE : View.GONE);
        if (isEditor && item.is_show == 0) {
            editor.setVisibility(View.INVISIBLE);
            checkbox.setVisibility(View.INVISIBLE);
        }
        helper.setGone(R.id.tv_under_review, item.is_show == 1);
    }

    public List<Integer> getCheckedIds() {
        return checkedIds;
    }

    public void refresh(List<AccostSettingListBean> data) {
        checkedIds.clear();
        setList(data);
    }
}
