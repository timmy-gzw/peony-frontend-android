package com.tftechsz.im.adapter;



import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.IntimacyDto;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.im.model.dto.MultiIntmacyItem;

import java.util.Objects;

public class IntmacyLevelAdapter extends BaseMultiItemQuickAdapter<MultiIntmacyItem,BaseViewHolder> {

    private int mCurrentLevel;
    private MultiIntmacyItem mLastItemDto;

    public void setmCurrentLevel(int mCurrentLevel) {
        this.mCurrentLevel = mCurrentLevel;
    }

    public int getmCurrentLevel() {
        return mCurrentLevel;
    }

    public void setLastItemDto(MultiIntmacyItem multiIntmacyItem){
        this.mLastItemDto = multiIntmacyItem;
    }

    public IntmacyLevelAdapter() {
        addItemType(0,R.layout.item_intmacy_level);
        addItemType(1,R.layout.item_intmacy_level_current);
        addItemType(2,R.layout.item_intmacy_level_current);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MultiIntmacyItem multiIntmacyItem) {
        IntimacyDto.IntimacyConfigDTO dto = multiIntmacyItem.getDto();
        switch (helper.getItemViewType()) {
            case 0:
                    if(dto.level>mCurrentLevel+1){//未达成
                        ImageView iv_unlock = helper.getView(R.id.iv_unlock);
                        iv_unlock.setImageResource(R.mipmap.icon_level_lock);
                        ImageView imageView = helper.getView(R.id.iv_to_unlock);
                        //Lv.16 亲密度达到 2822.3/13140
                        TextView tv_level_name = helper.getView(R.id.tv_level_name);
                        //已获得“萍水相逢”标签
                        tv_level_name.setText("LV."+dto.level+" "+dto.tips.substring(dto.tips.indexOf("“")+1,dto.tips.indexOf("”")));
                        tv_level_name.setTextColor(Color.parseColor("#CD5C33"));
                        TextView tv_lock = helper.getView(R.id.tv_lock);
                        tv_lock.setTextColor(Color.parseColor("#CD5C33"));
                        String[] s = dto.title.split(" ");
                        tv_lock.setText(s[1]+(s[2].substring(s[2].indexOf("/")+1)).replace(".1","")+"解锁");
                        imageView.setVisibility(View.VISIBLE);
                        if(dto.level.equals(mLastItemDto.getDto().level) && dto.tips.equals(mLastItemDto.getDto().tips)) {
                            imageView.setVisibility(View.INVISIBLE);
                        }
                    }else{//已达成
                        ImageView iv_unlock = helper.getView(R.id.iv_unlock);
                        iv_unlock.setImageResource(R.mipmap.icon_level_unlock);
                        ImageView imageView = helper.getView(R.id.iv_to_unlock);
                        imageView.setVisibility(View.INVISIBLE);
                        TextView tv_level_name = helper.getView(R.id.tv_level_name);
                        //已获得“萍水相逢”标签
                        tv_level_name.setText("LV."+dto.level+" "+dto.tips.substring(dto.tips.indexOf("“")+1,dto.tips.indexOf("”")));
                        tv_level_name.setTextColor(Color.parseColor("#50CD5C33"));
                        TextView tv_lock = helper.getView(R.id.tv_lock);
                        tv_lock.setTextColor(Color.parseColor("#50CD5C33"));
                        tv_lock.setText("已解锁");
                        if(dto.level.equals(mLastItemDto.getDto().level) && dto.tips.equals(mLastItemDto.getDto().tips)) {
                            imageView.setVisibility(View.INVISIBLE);
                        }
                    }
                break;
            case 1:
                ImageView ivUnlock = helper.getView(R.id.iv_unlock);
                ivUnlock.setImageResource(R.mipmap.icon_level_current);
                ImageView ivToUnlock = helper.getView(R.id.iv_to_unlock);
                ivToUnlock.setImageResource(R.mipmap.icon_level_curret_next);
                ConstraintLayout clbg = helper.getView(R.id.cl_right);
                clbg.setBackgroundResource(R.drawable.shape_intmacy_level_current);
                ImageView ivTip = helper.getView(R.id.iv_tip);
                ivTip.setImageResource(R.mipmap.icon_intmacy_current);
                TextView tvLevel = helper.getView(R.id.tv_level);
                tvLevel.setText("LV."+dto.level);
                TextView textView = helper.getView(R.id.tv_level_name);
                textView.setText(" "+dto.tips.substring(dto.tips.indexOf("“")+1,dto.tips.indexOf("”")));
                TextView tvLevelTitle = helper.getView(R.id.tv_level_title);
                tvLevelTitle.setText(dto.tips);
                if(dto.level.equals(mLastItemDto.getDto().level) && dto.tips.equals(mLastItemDto.getDto().tips)) {
                    ivToUnlock.setVisibility(View.INVISIBLE);
                }
                break;
            case 2:
                ImageView ivUnlock1 = helper.getView(R.id.iv_unlock);
                ivUnlock1.setImageResource(R.mipmap.icon_level_next);
                ImageView ivToUnlock1 = helper.getView(R.id.iv_to_unlock);
                ivToUnlock1.setImageResource(R.mipmap.icon_level_next_lock);
                ConstraintLayout clbg1 = helper.getView(R.id.cl_right);
                clbg1.setBackgroundResource(R.drawable.shape_intmacy_level_next);
                ImageView ivTip1 = helper.getView(R.id.iv_tip);
                ivTip1.setImageResource(R.mipmap.icon_intmacy_next);
                TextView tvLevel1 = helper.getView(R.id.tv_level);
                tvLevel1.setText("LV."+dto.level);
                TextView textView1 = helper.getView(R.id.tv_level_name);
                textView1.setText(" "+dto.tips.substring(dto.tips.indexOf("“")+1,dto.tips.indexOf("”")));
                TextView tvLevelTitle1 = helper.getView(R.id.tv_level_title);
                tvLevelTitle1.setText(dto.tips);
                if(dto.level.equals(mLastItemDto.getDto().level) && dto.tips.equals(mLastItemDto.getDto().tips)) {
                    ivToUnlock1.setVisibility(View.INVISIBLE);
                }
                break;
        }
        if(dto.level.equals(mLastItemDto.getDto().level) && dto.tips.equals(mLastItemDto.getDto().tips)) {
            helper.getView(R.id.bottom).setVisibility(View.VISIBLE);
        }else {
            helper.getView(R.id.bottom).setVisibility(View.GONE);
        }
    }

}
