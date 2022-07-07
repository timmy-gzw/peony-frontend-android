package com.tftechsz.mine.mvp.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.netease.nim.uikit.common.UserInfo
import com.tftechsz.common.ARouterApi
import com.tftechsz.common.base.BaseMvpActivity
import com.tftechsz.mine.R
import com.tftechsz.mine.entity.dto.GiftDto
import com.tftechsz.mine.mvp.IView.IGiftWallView
import com.tftechsz.mine.mvp.presenter.GiftWallPresenter

@Route(path = ARouterApi.ACTIVITY_GIFT_WALL)
class GiftWallActivity : BaseMvpActivity<IGiftWallView, GiftWallPresenter>(), IGiftWallView {

    override fun getLayout(): Int = R.layout.activity_gift_wall

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initPresenter(): GiftWallPresenter = GiftWallPresenter()

    override fun getUserInfoSuccess(userInfo: UserInfo?) {
    }

    override fun getGiftSuccess(data: MutableList<GiftDto>?) {
    }
}