package com.tftechsz.family.entity.dto;

/**
 * 包 名 : com.tftechsz.family.entity.dto
 * 描 述 : TODO
 */
public class FamilyRecruitDto {

    public int coin;
    public String des;
    public String expiration_time;
    public FamilyLevelDTO family_level;
    public String icon;
    public int id;
    public String prestige;
    public String tname;
    public int user_count;
    public String intro;
    public int family_id;
    public long tid;

    public static class FamilyLevelDTO {
        public int diff;
        public String icon;
        public int level;
        public int total;
    }
}
