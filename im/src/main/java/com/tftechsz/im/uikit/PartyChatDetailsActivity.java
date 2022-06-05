package com.tftechsz.im.uikit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.business.session.constant.Extras;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tftechsz.im.R;
import com.tftechsz.common.ARouterApi;

import static com.netease.nim.uikit.business.session.constant.Extras.EXTRA_TYPE_DIALOG_ACTIVITY;
import static com.netease.nim.uikit.business.session.constant.Extras.EXTRA_TYPE_DIALOG_ACTIVITY_HEIGHT;


/**
 * 派对私信聊天
 */
@Route(path = ARouterApi.ACTIVITY_PARTY_MESSAGE_DETAILS)
public class PartyChatDetailsActivity extends P2PMessageActivity {

    private int mHeight;

    public static void start(Context context, String contactId, SessionCustomization customization, IMMessage anchor, int height) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, contactId);
        intent.putExtra(EXTRA_TYPE_DIALOG_ACTIVITY_HEIGHT, height);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        if (anchor != null) {
            intent.putExtra(Extras.EXTRA_ANCHOR, anchor);
        }
        intent.setClass(context, PartyChatDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public boolean isDialogMenu() {
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onAttachedToWindow() {

        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view
                .getLayoutParams();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height = DensityUtils.getDisplay(PartyChatDetailsActivity.this).getHeight() - DensityUtils.dp2px(PartyChatDetailsActivity.this, 150);
        lp.gravity = Gravity.BOTTOM;
       /* lp.x = 0;

        lp.y = 200;*/

        getWindowManager().updateViewLayout(view, lp);
        //下面两行代码的顺序不可以改变不然圆角背景就设置不上了
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view.setBackgroundResource(R.drawable.bg_mine_white_top);//圆角背景
        // 点击窗口外部区域可消除
        view.setOnTouchListener((v, event) -> {

            int x = (int) event.getX();
            int y = (int) event.getY();
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);

            if (!rect.contains(x, DensityUtils.getDisplay(PartyChatDetailsActivity.this).getHeight() - DensityUtils.dp2px(PartyChatDetailsActivity.this, 150))) {
                finish();
            }

            return false;
        });
    }


    @Override
    protected MessageFragment fragment() {
        Bundle arguments = getIntent().getExtras();
        if (arguments == null) {
            arguments = new Bundle();
        }
        arguments.putSerializable(Extras.EXTRA_TYPE, SessionTypeEnum.P2P);
        arguments.putInt(EXTRA_TYPE_DIALOG_ACTIVITY, 1);
        Intent intent = getIntent();
        if (intent != null) {
            mHeight = intent.getIntExtra(Extras.EXTRA_TYPE_DIALOG_ACTIVITY_HEIGHT, 0);
        }
        arguments.putInt(EXTRA_TYPE_DIALOG_ACTIVITY_HEIGHT, mHeight);
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(arguments);
        fragment.setContainerId(R.id.message_fragment_container);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.frame_root).setOnClickListener(v -> {

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}
