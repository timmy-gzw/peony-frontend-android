package com.tftechsz.mine.mvp.ui.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.gyf.immersionbar.ImmersionBar
import com.tftechsz.common.ARouterApi
import com.tftechsz.common.base.BaseMvpActivity
import com.tftechsz.mine.R
import com.tftechsz.mine.adapter.CareerAdapter
import com.tftechsz.mine.databinding.ActivityChooseCareerBinding
import com.tftechsz.mine.entity.dto.CareerBean
import com.tftechsz.mine.mvp.IView.IChooseCareerView
import com.tftechsz.mine.mvp.presenter.ChooseCareerPresenter

/**
 *  TODO 选择职业
 */
@Route(path = ARouterApi.ACTIVITY_CHOOSE_CAREER)
class ChooseCareerActivity : BaseMvpActivity<IChooseCareerView, ChooseCareerPresenter>(), IChooseCareerView {

    private lateinit var binding: ActivityChooseCareerBinding
    private val careerAdapter: CareerAdapter by lazy {
        val ada = CareerAdapter(1)
        binding.rvCareerParent.apply {
            layoutManager = LinearLayoutManager(this@ChooseCareerActivity)
            adapter = ada
        }
        ada.setOnItemClickListener { _, _, position ->
            chooseParent(position)
        }
        ada
    }
    private val careerChildAdapter: CareerAdapter by lazy {
        val ada = CareerAdapter(2)
        binding.rvCareerChild.apply {
            layoutManager = LinearLayoutManager(this@ChooseCareerActivity)
            adapter = ada
        }
        ada.setOnItemClickListener { _, _, position ->
            chooseChild(position)
        }
        ada
    }

    override fun getLayout(): Int {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_career)
        return 0
    }

    override fun initPresenter(): ChooseCareerPresenter = ChooseCareerPresenter()

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).titleBar(binding.clTitleBar).init()
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivDone.setOnClickListener {
            val position = careerChildAdapter.selectedIndex
            if (position > 0 && position < careerChildAdapter.itemCount) {
                val name = careerChildAdapter.getItem(position).name
                setResult(RESULT_OK, intent.putExtra("type", name))
            }
            finish()
        }
    }

    override fun initData() {
        super.initData()
        getP().getCareers()
    }

    override fun onGetCareer(career: CareerBean?) {
        careerAdapter.setList(career?.job_list)
        chooseParent(0)
    }

    private fun chooseParent(position: Int) {
        if (careerAdapter.selectedIndex == position) return
        if (careerAdapter.selectedIndex >= 0) {
            careerAdapter.getItem(careerAdapter.selectedIndex).isSelected = false
            careerAdapter.notifyItemChanged(careerAdapter.selectedIndex)
        }
        careerAdapter.getItem(position).isSelected = true
        careerAdapter.notifyItemChanged(position)
        careerAdapter.selectedIndex = position

        if (careerChildAdapter.selectedIndex >= 0) {
            careerChildAdapter.getItem(careerChildAdapter.selectedIndex).isSelected = false
            careerChildAdapter.selectedIndex = -1
        }
        careerChildAdapter.setList(careerAdapter.getItem(position).child_list)
    }

    private fun chooseChild(position: Int) {
        if (careerChildAdapter.selectedIndex == position) return
        if (careerChildAdapter.selectedIndex >= 0) {
            careerChildAdapter.getItem(careerChildAdapter.selectedIndex).isSelected = false
            careerChildAdapter.notifyItemChanged(careerChildAdapter.selectedIndex)
        }
        careerChildAdapter.getItem(position).isSelected = true
        careerChildAdapter.notifyItemChanged(position)
        careerChildAdapter.selectedIndex = position
    }

}