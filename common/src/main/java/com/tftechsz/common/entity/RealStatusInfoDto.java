package com.tftechsz.common.entity;

import java.io.Serializable;

public class RealStatusInfoDto implements Serializable {

    public String icon;   ////用户发起时候的头像
    public String image_self;  //用户真人认证的图片
    public String content;  //用户审核返回信息
    public int icon_or_self;  //0.未认证，1.头像未通过，2.真人图片未通过
    public int status;  //用户审核状态 -1.未认证,0.等待审核，1.审核完成，2.审核驳回


}
