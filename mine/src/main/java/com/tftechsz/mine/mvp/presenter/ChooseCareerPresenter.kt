package com.tftechsz.mine.mvp.presenter

import com.tftechsz.common.base.BasePresenter
import com.tftechsz.common.http.BaseResponse
import com.tftechsz.common.http.ResponseObserver
import com.tftechsz.common.http.RetrofitManager
import com.tftechsz.mine.api.MineApiService
import com.tftechsz.mine.entity.dto.CareerBean
import com.tftechsz.mine.mvp.IView.IChooseCareerView

class ChooseCareerPresenter : BasePresenter<IChooseCareerView>() {

    private val service: MineApiService by lazy { RetrofitManager.getInstance().createUserApi(MineApiService::class.java) }

    fun getCareers() {
        // TODO: 选择职业接口
        addNet(
            service.careers.compose(applySchedulers())
                .subscribeWith(object : ResponseObserver<BaseResponse<CareerBean>>() {
                    override fun onSuccess(t: BaseResponse<CareerBean>?) {
                        view?.onGetCareer(t?.data)
                    }
                })
        )
    }
}