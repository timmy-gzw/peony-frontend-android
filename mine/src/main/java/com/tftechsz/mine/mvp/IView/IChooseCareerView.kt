package com.tftechsz.mine.mvp.IView

import com.tftechsz.common.base.MvpView
import com.tftechsz.mine.entity.dto.CareerBean

interface IChooseCareerView : MvpView {

    fun onGetCareer(career: List<CareerBean>?)

}