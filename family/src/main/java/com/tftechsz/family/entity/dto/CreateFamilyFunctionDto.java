package com.tftechsz.family.entity.dto;

import androidx.annotation.DrawableRes;

/**
 * 包 名 : com.tftechsz.family.entity.dto
 * 描 述 : TODO
 */
public class CreateFamilyFunctionDto {
    public CreateFamilyFunctionDto(int imgRes, String name) {
        this.imgRes = imgRes;
        this.name = name;
    }

    @DrawableRes
    public int imgRes;
    public String name;
}
