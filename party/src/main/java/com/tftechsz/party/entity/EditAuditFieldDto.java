package com.tftechsz.party.entity;

public class EditAuditFieldDto {
    //提交公告
    public String title;
    public String announcement;

    public EditAuditFieldDto(String title, String announcement) {
        this.title = title;
        this.announcement = announcement;
    }

    // 修改名称
    public static class EditAuditFieldDtoInner {
        public String room_name;

        public EditAuditFieldDtoInner(String room_name) {
            this.room_name = room_name;
        }
    }

    // 修改图片
    public static class EditAuditFieldImgDtoInner {
        public String icon;

        public EditAuditFieldImgDtoInner(String icon) {
            this.icon = icon;
        }
    }
}
