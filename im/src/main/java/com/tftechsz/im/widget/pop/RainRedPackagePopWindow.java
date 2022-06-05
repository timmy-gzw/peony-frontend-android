package com.tftechsz.im.widget.pop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.tftechsz.im.R;
import com.tftechsz.common.entity.BoxInfo;
import com.tftechsz.common.widget.rain.RedPacketViewHelper;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class RainRedPackagePopWindow extends BasePopupWindow {

    private LottieAnimationView lottieAnimationView;
    RedPacketViewHelper mRedPacketViewHelper;
    private Activity context;

    public RainRedPackagePopWindow(Activity context) {
        super(context);
        this.context = context;
        lottieAnimationView = findViewById(R.id.rain_red_package);
        initData();
    }


    private void initData() {
        mRedPacketViewHelper = new RedPacketViewHelper(getContext());
        lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(listener !=null){
                    lottieAnimationView.removeAllAnimatorListeners();
                    lottieAnimationView.clearAnimation();
                    listener.onEndAnimation();
                    dismiss();
                }
            }
        });
    }

    public void rain() {
        mRedPacketViewHelper.endGiftRain();
        context.getWindow().getDecorView().postDelayed(() -> {
            List<BoxInfo> boxInfos = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                BoxInfo boxInfo = new BoxInfo();
                boxInfo.setAwardId(i);
                boxInfos.add(boxInfo);
            }
            mRedPacketViewHelper.launchGiftRainRocket(0, boxInfos, new RedPacketViewHelper.GiftRainListener() {
                @Override
                public void startLaunch() {

                }

                @Override
                public void openRedPacket(int id) {

                }

                @Override
                public void startRain() {

                }

                @Override
                public void endRain() {
                }
            });
        }, 500);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_rain_red_package);
    }


    public interface OnSelectListener {
        void onEndAnimation();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

}
