package com.tftechsz.common.music.base;

import com.blankj.utilcode.util.SPUtils;
import com.tftechsz.common.music.constant.Constant;

/**
 * 包 名 : com.tftechsz.common.music.base
 * 描 述 : 所有的配置
 */
public enum BaseConfig {

    //对象
    INSTANCE;

    private boolean isLogin;
    private boolean isNight;
    private long position;
    private boolean isLocked;

    public void initConfig(){
        //1.是否是登录状态
        isLogin = SPUtils.getInstance(Constant.SP_NAME).getBoolean(Constant.KEY_IS_LOGIN, false);
        //.初始化夜间模式
        isNight = SPUtils.getInstance(Constant.SP_NAME).getBoolean(Constant.KEY_NIGHT_STATE);
        //3.音视频播放的位置
        position = SPUtils.getInstance(Constant.SP_NAME).getLong(Constant.PLAY_POSITION,0);
        //4.是否锁屏
        isLocked = SPUtils.getInstance(Constant.SP_NAME).getBoolean(Constant.IS_SCREEN_LOCK,true);
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        SPUtils.getInstance(Constant.SP_NAME).put(Constant.KEY_IS_LOGIN,login);
        this.isLogin = login;
    }

    public boolean isNight() {
        return isNight;
    }

    public void setNight(boolean night) {
        SPUtils.getInstance(Constant.SP_NAME).put(Constant.KEY_NIGHT_STATE,night);
        this.isNight = night;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        SPUtils.getInstance(Constant.SP_NAME).put(Constant.IS_SCREEN_LOCK,locked);
        this.isLocked = locked;
    }
}
