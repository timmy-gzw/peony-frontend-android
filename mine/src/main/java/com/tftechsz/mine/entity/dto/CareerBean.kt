package com.tftechsz.mine.entity.dto

data class CareerBean(val id: Int, val name: String, var isSelected: Boolean, val subCareers: List<CareerBean>?)