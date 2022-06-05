package com.tftechsz.common.entity;

/**
 * 包 名 : com.tftechsz.common.entity
 * 描 述 : TODO
 */
public class SVGAPlayerDto {
    private String name;
    private String animation;

    public SVGAPlayerDto(String animation) {
        this.animation = animation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnimation() {
        return animation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }
}
