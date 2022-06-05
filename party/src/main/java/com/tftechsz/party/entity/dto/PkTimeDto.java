package com.tftechsz.party.entity.dto;

import java.util.List;

public class PkTimeDto {

    private List<PkTimeInfo> pk_duration;

    public List<PkTimeInfo> getPk_duration() {
        return pk_duration;
    }

    public void setPk_duration(List<PkTimeInfo> pk_duration) {
        this.pk_duration = pk_duration;
    }

    public static class PkTimeInfo{
        private int minute;
        private String text;
        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
