package com.tftechsz.mine.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.entity
 * 描 述 : TODO
 */
public class NobleBean implements Serializable {

    public List<GradeDTO> grade;
    public List<List<PriceDTO>> price;
    public List<PrivilegeDTO> privilege;
    public String privilege_icon;
    public String link;
    public String svga_link;
    public String window_privilege_icon; //  = "http://public-cdn1.peony125.com//nobility/window_privilege/{level}_{index}_{lock}.png"
    public String bottom_bg; //  = "http://public-cdn1.peony125.com/nobility/bottom_bg/v2/%d.png"
    public List<bgDTO> bg_color_code;
    public String nobility_icon; //http://public-cdn1.peony125.com/nobility/icon/v2/%d.png

    public static class bgDTO {
        public String id;
        public String bg_start_color;
        public String bg_end_color;
    }

    public static class GradeDTO implements Serializable {
        public int id;
        public String name; //子爵
        public String full_name; //贵族·子爵
        public List<Integer> privilege;
        public int is_active;
        public int used;  //0：未开通  1:开通
        public int has_svga;//是否有svga动画
        public String bottom_tips;
        public String expiration_time_tips;
        public int heat;

        public boolean isSel() {
            return is_active > 0;
        }

        public void setSel(boolean sel) {
            is_active = sel ? 1 : 0;
        }
    }

    public static class PriceDTO implements Serializable {
        public int coin;
        public int day;
        public int pay_id;
        public int price;
        private int is_active;
        private int is_active_temp;

        public boolean isSelTemp() {
            return is_active_temp == 1;
        }

        public void setSelTemp(boolean sel) {
            is_active_temp = sel ? 1 : 0;
        }

        public boolean isSel() {
            return is_active == 1;
        }

        public void setSel(boolean sel) {
            is_active = sel ? 1 : 0;
        }
    }

    public static class PrivilegeDTO implements Serializable {
        public int id;
        public String name;
        public String tips;
        public int is_heat;

    }
}
