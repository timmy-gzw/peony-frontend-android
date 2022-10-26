package com.tftechsz.mine.adapter

import android.graphics.Typeface
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tftechsz.mine.R
import com.tftechsz.mine.entity.dto.CareerBean
import com.tftechsz.mine.entity.dto.CareerInfoDto

/**
 * @param type 1: 父级职业; 2: 子级职业
 */
class CareerAdapter(val type: Int) : BaseQuickAdapter<CareerInfoDto, BaseViewHolder>(
    if (type == 1)
        R.layout.item_career
    else
        R.layout.item_career_child
) {

    var selectedIndex: Int = -1

    override fun convert(holder: BaseViewHolder, item: CareerInfoDto) {
        val textView = holder.getView<TextView>(R.id.tv_name)
        textView.text = item.name
        textView.isSelected = item.isSelected
        textView.typeface = if (item.isSelected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    }

}