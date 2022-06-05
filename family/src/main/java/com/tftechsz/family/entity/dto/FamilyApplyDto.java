package com.tftechsz.family.entity.dto;

public class FamilyApplyDto {

    public int apply_id;
    public int status; // 申请状态（0:等待通过，1:已经通过，2:已经忽略）
    public String icon;
    public String nickname;
    public int user_id;

}
