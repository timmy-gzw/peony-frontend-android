package com.tftechsz.common.event;

import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.entity.SystemAccostDto;
import com.tftechsz.common.entity.UpdateInfo;

public class UpdateEvent {

    public UpdateInfo updateInfo;
    public SystemAccostDto alert_accost;
    public ConfigInfo.HomeTopNav homeTopNav;
    public int type;

    public UpdateEvent(UpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
    }

    public UpdateEvent(SystemAccostDto alert_accost) {
        this.alert_accost = alert_accost;
    }

    public UpdateEvent(ConfigInfo.HomeTopNav shareConfig) {
        this.homeTopNav = shareConfig;
    }

    public UpdateEvent(int type) {
        this.type = type;
    }

}
