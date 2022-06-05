package com.tftechsz.common.entity;

import java.util.List;

public class ShareConfigDto {

    public HomeTopNav home_top_nav;

    /**
     * 首页顶部参数
     */
    public static class HomeTopNav {

        public Nav nav_1;
        public Nav nav_2;
        public Nav nav_3;
        public Nav nav_4;

    }


    public static class Nav {
        public String title_1;
        public String title_2;
        public List<String> img_list;
        public String json;
        public String link;
    }

}
