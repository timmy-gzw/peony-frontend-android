/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.tftechsz.common.location;

import com.baidu.location.BDLocation;

/**
 * @author by liuhongjian01 on 16/8/17.
 */
public interface LocationListener {
    void onLocationChanged(BDLocation location);

}
