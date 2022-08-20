package com.tftechsz.mine.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.ActionListener;
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.StatusBarUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.AutoPollRecyclerView;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.LoginImageAdapter;
import com.tftechsz.mine.widget.pop.PrivacyPopWindow;


public class ConfigUtils {
    /**
     * 闪验三网运营商授权页配置类
     *
     * @param context
     * @return
     */
    //沉浸式竖屏样式
    public static ShanYanUIConfig getCJSConfig(final Activity context, LoginWay loginWay) {
        StatusBarUtil.fullScreen(context);
        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        ConfigInfo configInfo = service.getConfigInfo();
        String link = "";
        String link1 = "";
        if (null != configInfo) {
            if (null != configInfo.api.about_bot && configInfo.api.about_bot.size() >= 2) {
                String webView = "webview://";
                link = configInfo.api.about_bot.get(0).link.substring(webView.length());
                link1 = configInfo.api.about_bot.get(1).link.substring(webView.length());
            }
        }
        /************************************************自定义控件**************************************************************/
        Drawable logBtnImgPath = context.getResources().getDrawable(R.drawable.shape_login_btn);
        Drawable background = context.getResources().getDrawable(R.drawable.bg_trans);

        Drawable check = context.getResources().getDrawable(R.mipmap.icon_protocol_check);
        Drawable unCheck = context.getResources().getDrawable(R.mipmap.icon_protocol_normal);
        //loading自定义加载框
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout view_dialog = (LinearLayout) inflater.inflate(R.layout.dialog_loading, null);
        RelativeLayout.LayoutParams matchParentsLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        view_dialog.setLayoutParams(matchParentsLayoutParams);
        view_dialog.setVisibility(View.GONE);
//        //号码栏背景
//        LayoutInflater numberinflater = LayoutInflater.from(context);
//        RelativeLayout numberLayout = (RelativeLayout) numberinflater.inflate(R.layout.shanyan_demo_phobackground, null);
//        RelativeLayout.LayoutParams numberParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        numberParams.setMargins(0, 0, 0, AbScreenUtils.dp2px(context, 250));
//        numberParams.width = AbScreenUtils.getScreenWidth(context, false) - AbScreenUtils.dp2px(context, 50);
//        numberParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        numberParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        numberLayout.setLayoutParams(numberParams);
        RelativeLayout autoLoginLayout = (RelativeLayout) inflater.inflate(R.layout.activity_auto_login, null);
        RelativeLayout.LayoutParams customLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        autoLoginLayout.setLayoutParams(customLayout);
        AutoPollRecyclerView mRvLogin = autoLoginLayout.findViewById(R.id.rv_login);
        mRvLogin.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        LoginImageAdapter adapter = new LoginImageAdapter(BaseApplication.getInstance());
        mRvLogin.setAdapter(adapter);
        mRvLogin.start();
        RelativeLayout alternativeLoginsLayout = (RelativeLayout) inflater.inflate(R.layout.layout_alternative_login, null);
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.setMargins(0, DensityUtils.dp2px(context, 30), 0, DensityUtils.dp2px(context, 60));
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsOther.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        alternativeLoginsLayout.setLayoutParams(layoutParamsOther);
        alternativeLogins(context, alternativeLoginsLayout, loginWay);
        OneKeyLoginManager.getInstance().setActionListener(new ActionListener() {
            @Override
            public void ActionListner(int type, int code, String message) {
                //type=2 ，checkbox点击事件，code分为0,1（0为未选中，1为选中
                if (type == 2) {
                    MMKVUtils.getInstance().encode(Constants.AGREED_TO_TOS, code != 0);
                }
            }
        });
        ShanYanUIConfig uiConfig = new ShanYanUIConfig.Builder()
                .setActivityTranslateAnim("shanyan_fade_in_anim", "shanyan_fade_out_anim")
                //授权页导航栏：
                .setNavColor(Color.parseColor("#00000000"))  //设置导航栏颜色
                .setNavText("")  //设置导航栏标题文字
                .setNavReturnBtnWidth(35)
                .setNavReturnBtnHeight(35)
                .setAuthBGImgPath(background)
                .setLogoHidden(true)   //是否隐藏logo
                .setDialogDimAmount(0f)
                .setSloganHidden(true)
                .setAuthNavTransparent(true)
                .setNavReturnImgHidden(true)
                .setAuthNavHidden(true)
                .setNavReturnImgHidden(true)
                //授权页号码栏：
                .setNumberColor(context.getResources().getColor(R.color.white))  //设置手机号码字体颜色
                .setNumFieldOffsetBottomY(325)    //设置号码栏相对于标题栏下边缘y偏移
                .setNumberSize(34)
                .setNumFieldHeight(50)
                //授权页登录按钮：
                .setLogBtnText("本机号码一键登录")  //设置登录按钮文字
                .setLogBtnTextColor(context.getResources().getColor(R.color.white))   //设置登录按钮文字颜色
                .setLogBtnImgPath(logBtnImgPath)   //设置登录按钮图片
                .setLogBtnTextSize(18)
                .setLogBtnHeight(60)
                .setLogBtnWidth(300)
                .setLogBtnOffsetBottomY(260)
                //授权页隐私栏：
                .setAppPrivacyOne("用户协议", link)  //设置开发者隐私条款1名称和URL(名称，url)
                .setAppPrivacyTwo("隐私政策", link1)  //设置开发者隐私条款1名称和URL(名称，url)
                .setPrivacyText("同意", "", "", "", "")
                .setAppPrivacyColor(Color.parseColor("#999999"), Color.parseColor("#ffffff"))    //	设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
                .setPrivacyCustomToastText("请阅读并勾选协议")
                .setPrivacySmhHidden(false)
                .setUncheckedImgPath(unCheck)
                .setCheckedImgPath(check)
                .setPrivacyOffsetBottomY(20) //设置隐私条款相对于屏幕下边缘y偏
                .setcheckBoxOffsetXY(0, 1)
                .setPrivacyState(false)
                .setPrivacyTextSize(12)
                .setPrivacyOffsetX(5)
                .setSloganHidden(true)
                .setCheckBoxHidden(false)
                .setCheckBoxWH(16, 16)
                .setcheckBoxOffsetXY(0, 4)
                .setPrivacyGravityHorizontalCenter(true)
                .setShanYanSloganTextColor(Color.parseColor("#ffffff"))
//                .addCustomView(numberLayout, false, false, null)
                .setLoadingView(view_dialog)
//                // 添加自定义控件:
                .addCustomView(alternativeLoginsLayout, false, false, null)
                .addCustomView(autoLoginLayout, false, false, null)
                //标题栏下划线，可以不写
                .build();
        return uiConfig;
    }


    private static void alternativeLogins(Activity context, RelativeLayout relativeLayout, LoginWay loginWay) {
        TextView wxLoginView = relativeLayout.findViewById(R.id.tv_wx_login);
        TextView qqLoginView = relativeLayout.findViewById(R.id.tv_qq_login);
        TextView otherPhoneView = relativeLayout.findViewById(R.id.tv_other_phone);
        wxLoginView.setOnClickListener(v -> {
            showLogin(loginWay, context, 0);    //微信登陆
        });
        qqLoginView.setOnClickListener(v -> {
            showLogin(loginWay, context, 1);    //qq登陆
        });
        otherPhoneView.setOnClickListener(v -> {   //其他手机号码
            showLogin(loginWay, context, 2);   //其他手机号码
        });
    }


    private static void showLogin(LoginWay loginWay, Activity context, int type) {
        if (showPop(context))
            return;
        CheckBox checkBox = OneKeyLoginManager.getInstance().getPrivacyCheckBox();
        if (checkBox != null && !checkBox.isChecked()) {
            Utils.toast("请阅读并勾选协议");
            return;
        }
        loginWay.otherLoginWay(type);   //其他手机号码
    }


    public interface LoginWay {
        void otherLoginWay(int type);
    }

    /**
     * 未显示弹出隐私协议弹窗
     */
    public static boolean showPop(Activity context) {
        boolean isReturn = false;
        int isAgree = MMKVUtils.getInstance().decodeInt(Constants.IS_AGREE_AGREEMENT);
        if (isAgree == 0) {
            PrivacyPopWindow popWindow = new PrivacyPopWindow(context);
            popWindow.showPopupWindow();
            isReturn = true;
        }
        return isReturn;
    }

}
