package com.tftechsz.home.entity.req;

import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.entity.SystemAccostDto;
import com.tftechsz.common.entity.UpdateInfo;
import com.netease.nim.uikit.common.UserInfo;

import java.util.List;

public class RecommendReq {
    public List<UserInfo> data;
    public UpdateInfo update_info;
    public SystemAccostDto alert_accost;
    public ConfigInfo.ShareConfig share_config;

}
