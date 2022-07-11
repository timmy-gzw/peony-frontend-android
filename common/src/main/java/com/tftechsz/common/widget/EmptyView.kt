package com.tftechsz.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import com.tftechsz.common.R

class EmptyView : FrameLayout {
    private var resId = 0
    private var tips: String = ""
    var iconView: ImageView? = null
        private set
    var descView: TextView? = null
        private set
    var actionView: TextView? = null
        private set

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, tips: String) : super(context) {
        init(context, null)
        setTips(tips)
    }

    constructor(context: Context, imgRes: Int, tips: String) : super(context) {
        init(context, null)
        setRes(imgRes)
        setTips(tips)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun initAttributes(attrs: AttributeSet?) {
        if (attrs == null) return
        val attr = context.obtainStyledAttributes(attrs, R.styleable.EmptyView)
        try {
            tips = attr.getString(R.styleable.EmptyView_empty_str) ?: ""
            resId = attr.getResourceId(R.styleable.EmptyView_empty_resId, R.mipmap.ic_empty)
        } finally {
            attr.recycle()
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        initAttributes(attrs)
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_empty_view, this)
        iconView = view.findViewById(R.id.iv_empty_icon)
        descView = view.findViewById(R.id.tv_empty_desc)
        actionView = view.findViewById(R.id.tv_empty_action)
        if (tips.isNotBlank()) {
            descView?.text = tips
        }
        if (resId != 0) {
            iconView?.setImageResource(resId)
        }
    }

    fun setRes(resId: Int) {
        this.resId = resId
        iconView?.setImageResource(resId)
    }

    fun setTips(tips: String) {
        this.tips = tips
        descView?.text = tips
    }

    fun setAction(actionName: String?, onClickListener: OnClickListener?) {
        actionView?.visibility = VISIBLE
        actionView?.text = actionName
        if (onClickListener != null) actionView?.setOnClickListener(onClickListener)
    }
}