package com.tftechsz.mine.mvp.presenter

import com.tftechsz.common.base.BasePresenter
import com.tftechsz.common.http.RetrofitManager
import com.tftechsz.mine.api.MineApiService
import com.tftechsz.mine.entity.dto.CareerBean
import com.tftechsz.mine.mvp.IView.IChooseCareerView

class ChooseCareerPresenter : BasePresenter<IChooseCareerView>() {

    private val service: MineApiService by lazy { RetrofitManager.getInstance().createConfigApi(MineApiService::class.java) }

    fun getCareers() {
        // TODO: 选择职业接口
//        addNet(
//            service.careers.compose(applySchedulers())
//                .subscribeWith(object : ResponseObserver<BaseResponse<List<CareerBean>>>() {
//                    override fun onSuccess(t: BaseResponse<List<CareerBean>>?) {
//                        view?.onGetCareer(t?.data)
//                    }
//
//                })
//        )
        val d = mutableListOf<CareerBean>()
        for (i in 0 until 20) {
            val c = mutableListOf<CareerBean>()
            for (j in 0 until 8) {
                c.add(j, CareerBean(j, "跟单${i + j}", false, null))
            }
            d.add(CareerBean(i, "销售/业务$i", false, c))
        }
        view?.onGetCareer(d)
    }
}