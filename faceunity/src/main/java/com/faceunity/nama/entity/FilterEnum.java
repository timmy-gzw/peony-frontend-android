package com.faceunity.nama.entity;

import com.faceunity.nama.R;
import com.faceunity.nama.param.BeautificationParam;

import java.util.ArrayList;

/**
 * 美颜滤镜列表
 *
 * @author Richie on 2019.12.20
 */
public enum FilterEnum {
    /**
     * 滤镜
     */

    origin(BeautificationParam.ORIGIN, R.drawable.demo_icon_cancel, "原图"),
    ziran_1(BeautificationParam.ZIRAN_1, R.drawable.demo_icon_natural_1,"自然 1"),
    ziran_2(BeautificationParam.ZIRAN_2, R.drawable.demo_icon_natural_2, "自然 2"),
    ziran_3(BeautificationParam.ZIRAN_3, R.drawable.demo_icon_natural_3, "自然 3"),
    ziran_4(BeautificationParam.ZIRAN_4, R.drawable.demo_icon_natural_4, "自然 4"),
    ziran_5(BeautificationParam.ZIRAN_5, R.drawable.demo_icon_natural_5, "自然 5"),
    ziran_6(BeautificationParam.ZIRAN_6, R.drawable.demo_icon_natural_6,"自然 6"),
    ziran_7(BeautificationParam.ZIRAN_7, R.drawable.demo_icon_natural_7, "自然 7"),
    ziran_8(BeautificationParam.ZIRAN_8, R.drawable.demo_icon_natural_8, "自然 8"),
    zhiganhui_1(BeautificationParam.ZHIGANHUI_1, R.drawable.demo_icon_texture_gray1, "质感灰 1"),
    zhiganhui_2(BeautificationParam.ZHIGANHUI_2, R.drawable.demo_icon_texture_gray2, "质感灰 2"),
    zhiganhui_3(BeautificationParam.ZHIGANHUI_3, R.drawable.demo_icon_texture_gray3, "质感灰 3"),
    zhiganhui_4(BeautificationParam.ZHIGANHUI_4, R.drawable.demo_icon_texture_gray4, "质感灰 4"),
    zhiganhui_5(BeautificationParam.ZHIGANHUI_5, R.drawable.demo_icon_texture_gray5, "质感灰 5"),
    zhiganhui_6(BeautificationParam.ZHIGANHUI_6, R.drawable.demo_icon_texture_gray6, "质感灰 6"),
    zhiganhui_7(BeautificationParam.ZHIGANHUI_7, R.drawable.demo_icon_texture_gray7, "质感灰 7"),
    zhiganhui_8(BeautificationParam.ZHIGANHUI_8, R.drawable.demo_icon_texture_gray8, "质感灰 8"),

    mitao_1(BeautificationParam.MITAO_1, R.drawable.demo_icon_peach1, "蜜桃 1"),
    mitao_2(BeautificationParam.MITAO_2, R.drawable.demo_icon_peach2, "蜜桃 2"),
    mitao_3(BeautificationParam.MITAO_3, R.drawable.demo_icon_peach3, "蜜桃 3"),
    mitao_4(BeautificationParam.MITAO_4, R.drawable.demo_icon_peach4, "蜜桃 4"),
    mitao_5(BeautificationParam.MITAO_5, R.drawable.demo_icon_peach5, "蜜桃 5"),
    mitao_6(BeautificationParam.MITAO_6, R.drawable.demo_icon_peach6, "蜜桃 6"),
    mitao_7(BeautificationParam.MITAO_7, R.drawable.demo_icon_peach7, "蜜桃 7"),
    mitao_8(BeautificationParam.MITAO_8, R.drawable.demo_icon_peach8, "蜜桃 8"),

    bailiang_1(BeautificationParam.BAILIANG_1, R.drawable.demo_icon_bailiang1, "白亮 1"),
    bailiang_2(BeautificationParam.BAILIANG_2, R.drawable.demo_icon_bailiang2, "白亮 2"),
    bailiang_3(BeautificationParam.BAILIANG_3, R.drawable.demo_icon_bailiang3, "白亮 3"),
    bailiang_4(BeautificationParam.BAILIANG_4, R.drawable.demo_icon_bailiang4, "白亮 4"),
    bailiang_5(BeautificationParam.BAILIANG_5, R.drawable.demo_icon_bailiang5, "白亮 5"),
    bailiang_6(BeautificationParam.BAILIANG_6, R.drawable.demo_icon_bailiang6, "白亮 6"),
    bailiang_7(BeautificationParam.BAILIANG_7, R.drawable.demo_icon_bailiang7, "白亮 7"),

    fennen_1(BeautificationParam.FENNEN_1, R.drawable.demo_icon_fennen1, "粉嫩 1"),
    fennen_2(BeautificationParam.FENNEN_2, R.drawable.demo_icon_fennen2, "粉嫩 2"),
    fennen_3(BeautificationParam.FENNEN_3, R.drawable.demo_icon_fennen3,"粉嫩 3"),
    //    fennen_4(BeautificationParam.FENNEN_4, R.drawable.demo_icon_fennen4, R.string.fennen_4),
    fennen_5(BeautificationParam.FENNEN_5, R.drawable.demo_icon_fennen5,"粉嫩 4"),
    fennen_6(BeautificationParam.FENNEN_6, R.drawable.demo_icon_fennen6, "粉嫩 5"),
    fennen_7(BeautificationParam.FENNEN_7, R.drawable.demo_icon_fennen7, "粉嫩 6"),
    fennen_8(BeautificationParam.FENNEN_8, R.drawable.demo_icon_fennen8, "粉嫩 7"),

    lengsediao_1(BeautificationParam.LENGSEDIAO_1, R.drawable.demo_icon_lengsediao1, "冷色调 1"),
    lengsediao_2(BeautificationParam.LENGSEDIAO_2, R.drawable.demo_icon_lengsediao2, "冷色调 2"),
    lengsediao_3(BeautificationParam.LENGSEDIAO_3, R.drawable.demo_icon_lengsediao3, "冷色调 3"),
    lengsediao_4(BeautificationParam.LENGSEDIAO_4, R.drawable.demo_icon_lengsediao4, "冷色调 4"),
    //    lengsediao_5(BeautificationParam.LENGSEDIAO_5, R.drawable.demo_icon_lengsediao5, R.string.lengsediao_5),
    //    lengsediao_6(BeautificationParam.LENGSEDIAO_6, R.drawable.demo_icon_lengsediao6, R.string.lengsediao_6),
    lengsediao_7(BeautificationParam.LENGSEDIAO_7, R.drawable.demo_icon_lengsediao7, "冷色调 5"),
    lengsediao_8(BeautificationParam.LENGSEDIAO_8, R.drawable.demo_icon_lengsediao8, "冷色调 6"),
    //    lengsediao_9(BeautificationParam.LENGSEDIAO_9, R.drawable.demo_icon_lengsediao9, R.string.lengsediao_9),
    //    lengsediao_10(BeautificationParam.LENGSEDIAO_10, R.drawable.demo_icon_lengsediao10, R.string.lengsediao_10),
    lengsediao_11(BeautificationParam.LENGSEDIAO_11, R.drawable.demo_icon_lengsediao11, "冷色调 7"),

    nuansediao_1(BeautificationParam.NUANSEDIAO_1, R.drawable.demo_icon_nuansediao1, "暖色调 1"),
    nuansediao_2(BeautificationParam.NUANSEDIAO_2, R.drawable.demo_icon_nuansediao2, "暖色调 2"),
    //    nuansediao_3(BeautificationParam.NUANSEDIAO_3, R.drawable.demo_icon_nuansediao3, R.string.nuansediao_3),

    gexing_1(BeautificationParam.GEXING_1, R.drawable.demo_icon_gexing1, "个性 1"),
    gexing_2(BeautificationParam.GEXING_2, R.drawable.demo_icon_gexing2, "个性 2"),
    gexing_3(BeautificationParam.GEXING_3, R.drawable.demo_icon_gexing3, "个性 3"),
    gexing_4(BeautificationParam.GEXING_4, R.drawable.demo_icon_gexing4, "个性 4"),
    gexing_5(BeautificationParam.GEXING_5, R.drawable.demo_icon_gexing5, "个性 5"),
    //    gexing_6(BeautificationParam.GEXING_6, R.drawable.demo_icon_gexing6, R.string.gexing_6),
    gexing_7(BeautificationParam.GEXING_7, R.drawable.demo_icon_gexing7, "个性 6"),
    //    gexing_8(BeautificationParam.GEXING_8, R.drawable.demo_icon_gexing8, R.string.gexing_8),
    //    gexing_9(BeautificationParam.GEXING_9, R.drawable.demo_icon_gexing9, R.string.gexing_9),
    gexing_10(BeautificationParam.GEXING_10, R.drawable.demo_icon_gexing10, "个性 7"),
    gexing_11(BeautificationParam.GEXING_11, R.drawable.demo_icon_gexing11, "个性 8"),

    xiaoqingxin_1(BeautificationParam.XIAOQINGXIN_1, R.drawable.demo_icon_xiaoqingxin1, "小清新 1"),
    //    xiaoqingxin_2(BeautificationParam.XIAOQINGXIN_2, R.drawable.demo_icon_xiaoqingxin2, R.string.xiaoqingxin_2),
    xiaoqingxin_3(BeautificationParam.XIAOQINGXIN_3, R.drawable.demo_icon_xiaoqingxin3, "小清新 2"),
    xiaoqingxin_4(BeautificationParam.XIAOQINGXIN_4, R.drawable.demo_icon_xiaoqingxin4, "小清新 3"),
    //    xiaoqingxin_5(BeautificationParam.XIAOQINGXIN_5, R.drawable.demo_icon_xiaoqingxin5, R.string.xiaoqingxin_5),
    xiaoqingxin_6(BeautificationParam.XIAOQINGXIN_6, R.drawable.demo_icon_xiaoqingxin6, "小清新 4"),

    heibai_1(BeautificationParam.HEIBAI_1, R.drawable.demo_icon_heibai1,"黑白 1"),
    heibai_2(BeautificationParam.HEIBAI_2, R.drawable.demo_icon_heibai2, "黑白 2"),
    heibai_3(BeautificationParam.HEIBAI_3, R.drawable.demo_icon_heibai3, "黑白 3"),
    heibai_4(BeautificationParam.HEIBAI_4, R.drawable.demo_icon_heibai4, "黑白 4");




    private String name;
    private int iconId;
    private String description;

    FilterEnum(String name, int iconId, String description) {
        this.name = name;
        this.iconId = iconId;
        this.description = description;
    }

    public Filter create() {
        return new Filter(name, iconId, description);
    }

    public static ArrayList<Filter> getFilters() {
        FilterEnum[] filterEnums = FilterEnum.values();
        ArrayList<Filter> filters = new ArrayList<>(filterEnums.length);
        for (FilterEnum f : filterEnums) {
            filters.add(f.create());
        }
        return filters;
    }
}
