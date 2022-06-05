package com.tftechsz.im.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tftechsz.im.R;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.OrangeStrokeTextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DoubleGiftView extends ConstraintLayout {
    private OrangeStrokeTextView mGiftNum;
    private ImageView mIvGift;
    private GiftDto mData;
    private List<String> mToMember;

    public DoubleGiftView(@NonNull @NotNull Context context) {
        super(context);
        init(context);
    }

    public DoubleGiftView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DoubleGiftView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        View.inflate(context, R.layout.nim_chat_double_gift, this);
        mIvGift = findViewById(R.id.iv_gift);
        mGiftNum = findViewById(R.id.gift_num);
        ImageView ivSend = findViewById(R.id.iv_send);
        ivSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData != null && listener != null) {
                    mGiftNum.setText("x " + 1);
                    listener.sendGift(mData, 1, mToMember, "");
                }
            }
        });

    }

    public void setData(GiftDto data, List<String> toMember) {
        mData = data;
        mToMember = toMember;
        GlideUtils.loadRouteImage(getContext(), mIvGift, data.image);

    }


    public interface OnDoubleGiftListener {
        void sendGift(GiftDto data, int num, List<String> userId, String name);
    }

    public OnDoubleGiftListener listener;

    public void addOnClickListener(OnDoubleGiftListener listener) {
        this.listener = listener;
    }


}
