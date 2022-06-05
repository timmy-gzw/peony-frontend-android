package com.tftechsz.common.event;

/**
 * 包 名 : com.tftechsz.common.event
 * 描 述 : 埋点dto
 */
public class BuriedPointExtendDto {
    public FamilyExtendDto family; //家族埋点信息
    public RechargeExtendDto recharge; //充值埋点信息
    public FamilyActivityDto family_activity; //家族活动埋点信息
    public String desc;

    public static class FamilyExtendDto {
        public String family_id;

        public FamilyExtendDto(String family_id) {
            this.family_id = family_id;
        }
    }

    public static class RechargeExtendDto {
        public String pay_type; //充值类型
        public String title; //商品信息

        public RechargeExtendDto(String pay_type, String title) {
            this.pay_type = pay_type;
            this.title = title;
        }
    }

    public BuriedPointExtendDto() {
    }

    public BuriedPointExtendDto(FamilyActivityDto family_activity) {
        this.family_activity = family_activity;
    }

    public BuriedPointExtendDto(FamilyExtendDto family) {
        this.family = family;
    }

    public BuriedPointExtendDto(RechargeExtendDto recharge) {
        this.recharge = recharge;
    }

    public BuriedPointExtendDto(String desc) {
        this.desc = desc;
    }
}
