package com.tftechsz.mine.entity.dto

class CareerInfoDto {

    var id : Int = 0
    var name : String = ""
    var isSelected : Boolean = false
    var child_list : MutableList<CareerInfoDto>? = null


}