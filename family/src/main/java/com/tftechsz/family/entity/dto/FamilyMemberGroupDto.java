package com.tftechsz.family.entity.dto;

import java.util.ArrayList;

public class FamilyMemberGroupDto {

    private String title;
    private ArrayList<FamilyMemberDto> data;

    public FamilyMemberGroupDto(String title, ArrayList<FamilyMemberDto> data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<FamilyMemberDto> getData() {
        return data;
    }

    public void setData(ArrayList<FamilyMemberDto> data) {
        this.data = data;
    }
}
