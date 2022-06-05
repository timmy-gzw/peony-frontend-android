package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.tftechsz.common.R;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.widget.CustomFilter;

import java.util.Random;

import io.reactivex.disposables.CompositeDisposable;

public class GoStoriesPopWindow extends BaseCenterPop implements View.OnClickListener {
    private final CompositeDisposable mCompositeDisposable;
    private final PublicService service;
    EditText etContent;

    public GoStoriesPopWindow(Context context) {
        super(context);
        mCompositeDisposable = new CompositeDisposable();
        service =  RetrofitManager.getInstance().createUserApi(PublicService.class);
        initUi();

    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_go_stories);
    }

    private void initUi() {
        TextView tvStoriesContent = findViewById(R.id.tv_stories_content);
         etContent = findViewById(R.id.et_content);
        Random random = new Random();
        int mMax = 60000;
        int mMin = 50000;
        int number = random.nextInt(mMax - mMin) + mMin + 1;
        String text1 = "你的头条将会在首页停留 ";
        String text2 = "30s\n";
        String text3 = "预计将被 ";
        String text4 = number + "";
        String text5 = " 人看到";
        SpannableStringBuilder spanString = new SpannableStringBuilder();
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(text1).append(text2).append(text3).append(text4).append(text5);
        spanString.append(text1).append(text2).append(text3).append(text4).append(text5);
        int start = stringBuffer.indexOf(text2);
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.red)), start, start + text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, start + text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int start1 = stringBuffer.indexOf(text4);
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.red)), start1, start1 + text4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvStoriesContent.setText(spanString);
        etContent.setFilters(new InputFilter[]{new CustomFilter(100)});
        findViewById(R.id.tv_send_stories).setOnClickListener(this);
    }





    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_send_stories) {
            sendStories();
        }
    }


    /**
     * 发头条
     */
    public void sendStories() {
        String text = etContent.getText().toString();
        mCompositeDisposable.add(service.sendStories(text)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                ToastUtil.showToast(mContext,response.getMessage());
            }
        }));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }




    public interface OnSelectListener {

        void alipay(String orderNum);

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
