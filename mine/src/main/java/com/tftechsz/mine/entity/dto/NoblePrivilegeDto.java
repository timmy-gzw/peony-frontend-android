package com.tftechsz.mine.entity.dto;

import com.tftechsz.mine.entity.NobleBean;

import java.io.Serializable;
import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.entity.dto
 * 描 述 : TODO
 */
public class NoblePrivilegeDto implements Serializable {
    public List<NobleBean.PrivilegeDTO> privilege;
    public List<Integer> gradeDTO_privilege;
    public String privilege_icon;
    public int heat;

    public NoblePrivilegeDto(List<NobleBean.PrivilegeDTO> privilege, List<Integer> gradeDTO_privilege, String privilege_icon, int heat) {
        this.privilege = privilege;
        this.gradeDTO_privilege = gradeDTO_privilege;
        this.privilege_icon = privilege_icon;
        this.heat = heat;
    }
}
