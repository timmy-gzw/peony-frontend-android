package com.tftechsz.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.home.R;
import com.tftechsz.home.adapter.SignInAdapter;
import com.tftechsz.home.adapter.TopSinAdapter;
import com.tftechsz.home.entity.SignInBean;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import java.util.ArrayList;
import java.util.List;

/**
 * 包 名 : com.tftechsz.home.widget
 * 描 述 : 签到pop
 */
public class SignInPopWindow extends BaseCenterPop {

    public SignInPopWindow(Context context, SignInBean data, SignInPopClickListener listener) {
        super(context);
        initUI(data, listener);
    }

    public interface SignInPopClickListener {
        void signInPopClick();
    }

    private void initUI(SignInBean data, SignInPopClickListener listener) {
        setOutSideDismiss(false);
        View view = findViewById(R.id.iv_yy);
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(5 * 1000);
        rotate.setRepeatCount(-1);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        view.setAnimation(rotate);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                view.clearAnimation();
            }
        });

        findViewById(R.id.del).setOnClickListener(v -> dismiss());
       /* findViewById(R.id.tv_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.signInPopClick();
            }
        });*/
        RecyclerView recy = findViewById(R.id.sign_recy);
        SignInAdapter adapter = new SignInAdapter(data.start_day);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
        recy.setAdapter(adapter);
        recy.setLayoutManager(gridLayoutManager);
        adapter.setList(data.list);


       /* List<SignInBean.SignTopBean> list = new ArrayList<>();
        list.add(new SignInBean.SignTopBean("coin", "金币+20", ""));
        list.add(new SignInBean.SignTopBean("vip", "VIP体验", "2小时"));
        data.top_list = list;*/

        List<SignInBean.SignTopBean> signTopBeans = new ArrayList<>();
        for (SignInBean.SignTopBean signTopBean : data.top_list) {
            if (TextUtils.equals(signTopBean.type, "coin") || TextUtils.equals(signTopBean.type, "vip") || TextUtils.equals(signTopBean.type, "chat_card")) {
                signTopBeans.add(signTopBean);
            }
        }
        if (signTopBeans.size() == 0) {
            return;
        }
        RecyclerView topRecy = findViewById(R.id.top_recy);
        topRecy.setLayoutManager(new GridLayoutManager(mContext, data.top_list.size()));
        TopSinAdapter topSinAdapter = new TopSinAdapter(data.top_list);
        topRecy.setAdapter(topSinAdapter);


    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_sign_in);
    }
}
