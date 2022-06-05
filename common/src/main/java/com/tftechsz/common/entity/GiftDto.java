package com.tftechsz.common.entity;

import androidx.annotation.IdRes;

import java.io.Serializable;
import java.util.List;

public class GiftDto implements Serializable, Comparable<GiftDto> {


    public int id;
    public String name;
    public String image;
    public int coin;
    public int animationType;  //动效类型:1.普通PNG 2.炫 3.动
    public int combo;  //1可以连击
    public int tag_value; // 4 幸运礼物
    public int cate;
    public String animation;
    public List<String> animations;
    public String tag;   //右上角图标
    public String headUrl;
    public String userName;
    public String toUserName;
    public int group;
    public int num;
    public int number;
    public long sortNum;
    public int groupNum;
    public int weight = 1;
    public String expired_time;
    public boolean can_use;
    public String cannot_use_msg;
    public int is_choose_num; //是否可选择多个数量发送
    public int blink_type; //是否是盲盒类型
    @IdRes
    private int bgResId;

    public int getBgResId() {
        return bgResId;
    }

    public void setBgResId(int bgResId) {
        this.bgResId = bgResId;
    }

    public String getExpired_time() {
        return expired_time;
    }

    public void setExpired_time(String expired_time) {
        this.expired_time = expired_time;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getAnimationType() {
        return animationType;
    }

    public void setAnimationType(int animationType) {
        this.animationType = animationType;
    }

    public int getCate() {
        return cate;
    }

    public void setCate(int cate) {
        this.cate = cate;
    }

    public String getAnimation() {
        return animation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public long getSortNum() {
        return sortNum;
    }

    public void setSortNum(long sortNum) {
        this.sortNum = sortNum;
    }

    public int getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(int groupNum) {
        this.groupNum = groupNum;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int compareTo(GiftDto bean) {
        return this.weight - bean.weight;
    }
}
