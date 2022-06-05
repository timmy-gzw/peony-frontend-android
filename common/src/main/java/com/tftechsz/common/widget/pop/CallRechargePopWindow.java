package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.R;


/**
 *  充值提示弹窗
 */
public class CallRechargePopWindow extends BaseCenterPop implements View.OnClickListener {

    public CallRechargePopWindow(Context context) {
        super(context);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        //todo   后面改配置
//        TextView tvContent = findViewById(R.id.tv_content);
//        String res = tvContent.getText().toString();
//        SpannableStringBuilder builder = new SpannableStringBuilder(res);
//        int stat = res.indexOf("去充值");
//        builder.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View view) {
//                if (listener != null) {
//                    listener.onSure();
//                }
//                dismiss();
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//            }
//        }, stat, stat + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#7F89F3")), stat, stat + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvContent.setText(builder);
//        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        TextView tvRecharge = findViewById(R.id.tv_recharge);
        tvRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onRecharge();
                dismiss();
            }
        });

    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_call_recharge);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

    }

    public interface OnSelectListener {

        void onRecharge();

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
