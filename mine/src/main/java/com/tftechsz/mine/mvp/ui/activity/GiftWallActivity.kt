package com.tftechsz.mine.mvp.ui.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ConvertUtils
import com.gyf.immersionbar.ImmersionBar
import com.netease.nim.uikit.common.UserInfo
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration
import com.tftechsz.common.ARouterApi
import com.tftechsz.common.base.BaseMvpActivity
import com.tftechsz.common.utils.ARouterUtils
import com.tftechsz.common.utils.GlideUtils
import com.tftechsz.mine.R
import com.tftechsz.mine.adapter.GiftWallAdapter
import com.tftechsz.mine.databinding.ActivityGiftWallBinding
import com.tftechsz.mine.entity.dto.GiftDto
import com.tftechsz.mine.mvp.IView.IGiftWallView
import com.tftechsz.mine.mvp.presenter.GiftWallPresenter
import com.tftechsz.mine.utils.UserManager

/**
 * 礼物墙
 */
@Route(path = ARouterApi.ACTIVITY_GIFT_WALL)
class GiftWallActivity : BaseMvpActivity<IGiftWallView, GiftWallPresenter>(), IGiftWallView {

    @JvmField
    @Autowired(name = "user_id")
    var userId: String? = null

    @JvmField
    @Autowired(name = "gifts")
    var gifts: ArrayList<GiftDto>? = null

    private val giftWallAdapter by lazy {
        val wallAdapter = GiftWallAdapter()
        binding.rvGift.apply {
            layoutManager = GridLayoutManager(this@GiftWallActivity, 4)
            adapter = wallAdapter
            addItemDecoration(SpacingDecoration(ConvertUtils.dp2px(20f), ConvertUtils.dp2px(10f), false))
        }
        wallAdapter
    }

    private lateinit var binding: ActivityGiftWallBinding

    override fun getLayout(): Int {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gift_wall)
        return 0
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBar(baseTitle)
                .transparentBar()
                .statusBarDarkFont(false)
                .navigationBarDarkIcon(false)
                .init()
        ToolBarBuilder().showBack(true)
                .setTitle(getString(R.string.gift_wall))
                .setTitleColor(R.color.white)
                .setBackTint(R.color.white)
                .setBackgroundColor(0)
                .build()

        if (userId.isNullOrBlank()) {
            binding.tvMyGiftWall.visibility = View.GONE
            binding.rvGift.setPadding(binding.rvGift.paddingStart, binding.rvGift.paddingTop, binding.rvGift.paddingEnd, binding.rvGift.paddingBottom)
        } else {
            binding.tvMyGiftWall.visibility = View.VISIBLE
            binding.rvGift.setPadding(binding.rvGift.paddingStart, binding.rvGift.paddingTop, binding.rvGift.paddingEnd, ConvertUtils.dp2px(90f))
            binding.tvMyGiftWall.setOnClickListener {
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_GIFT_WALL)
            }
        }
    }

    override fun initData() {
        super.initData()
        getData()
    }

    override fun initPresenter(): GiftWallPresenter = GiftWallPresenter()

    private fun getData() {
        if (userId.isNullOrBlank()) {
            p.getUserInfoDetail()
        } else {
            p.getUserInfoById(userId)
        }
        val uid = if (userId.isNullOrBlank()) {
            val id = UserManager.getInstance().userId
            id.toString()
        } else userId
        if (gifts.isNullOrEmpty()) {
            p.getGiftList(uid)
        } else {
            onGetGiftSuccess(gifts)
        }
    }

    override fun onGetUserInfoSuccess(userInfo: UserInfo?) {
        binding.tvUsername.text = userInfo?.nickname
        GlideUtils.loadImage(this, binding.civAvatar, userInfo?.icon)
    }

    override fun onGetGiftSuccess(data: MutableList<GiftDto>?) {
        giftWallAdapter.setList(data)
        val count = data?.count { it.number > 0 }
        binding.tvGiftCount.text = "$count/${data?.size ?: 0}"
    }
}