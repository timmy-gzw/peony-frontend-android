package com.tftechsz.common.entity;

public class ComingEffectDto {

    /**
     * id : 1
     * effectName : 初级连击效果
     * giftValue : 100000
     * combo : http://fwres.oss-cn-hangzhou.aliyuncs.com/upload/gift/effect/1.png
     * bubbleColor : #000000
     * borderColor :
     * opacity : 40
     * status : 1
     */

    private int id;
    private String effectName;
    private int giftValue;
    private int combo;
    private String bubbleColor;
    private String borderColor;
    private int opacity;
    private int status;
    private String comingTxtColor;

    public String getComingTxtColor() {
        return comingTxtColor;
    }

    public void setComingTxtColor(String comingTxtColor) {
        this.comingTxtColor = comingTxtColor;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEffectName() {
        return effectName;
    }

    public void setEffectName(String effectName) {
        this.effectName = effectName;
    }

    public int getGiftValue() {
        return giftValue;
    }

    public void setGiftValue(int giftValue) {
        this.giftValue = giftValue;
    }

    public int getCombo() {
        return combo;
    }

    public void setCombo(int combo) {
        this.combo = combo;
    }

    public String getBubbleColor() {
        return bubbleColor;
    }

    public void setBubbleColor(String bubbleColor) {
        this.bubbleColor = bubbleColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
