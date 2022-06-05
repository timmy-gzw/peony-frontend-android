package com.tftechsz.peony.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tftechsz.common.Constants;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {


    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            if (!api.handleIntent(getIntent(), this)) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
            String extraData = launchMiniProResp.extMsg; //对应小程序组件 <button open-type="launchApp"> 中的 app-parameter 属性
            Utils.logE("extraData=" + extraData + " code=" + resp.errCode);
        }

        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            switch (resp.getType()) {
                case ConstantsAPI.COMMAND_SENDAUTH:
                    String code = ((SendAuth.Resp) resp).code;
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_WX_LOGIN_SUCCESS, code));
                    break;
                case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                    //微信分享回调
                    ToastUtil.showToast(this, "分享成功");
                    break;

                case ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM:
                    WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
                    String extraData = launchMiniProResp.extMsg;
                    if (TextUtils.equals(extraData, "paySuccess")) {
                        Utils.toast("支付成功");
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
                    } else {
                        Utils.toast("支付失败");
                    }
                    break;
            }
        } else {
            switch (resp.getType()) {
                case ConstantsAPI.COMMAND_SENDAUTH:
                    break;
                case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                    ToastUtil.showToast(this, "分享失败");
                    //微信分享回调
                    break;
            }
        }
        finish();
    }

}
