package com.tftechsz.moment.mvp.ui.activity;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.moment.R;

@Route(path = ARouterApi.ACTIVITY_BEFORE_REPORT)
public class BeforeReportActivity extends BaseMvpActivity implements View.OnClickListener {


    private TextView item,item1,item2,item3,item4,item5,temp;
    private int blogId;
    private int fromType;  // 1:个人  2：动态 3：帮助反馈

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_before_report;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle("请选择举报原因");
        if (getIntent() != null) {
            blogId = getIntent().getIntExtra("blogId", 0);
            fromType = getIntent().getIntExtra("fromType", 0);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        item =  findViewById(R.id.item);
        item1 =  findViewById(R.id.item1);
        item2 =  findViewById(R.id.item2);
        item3 =  findViewById(R.id.item3);
        item4 =  findViewById(R.id.item4);
        item5 =  findViewById(R.id.item5);
        item.setOnClickListener(this);
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        item4.setOnClickListener(this);
        item5.setOnClickListener(this);
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.submit){
            if(temp == null){
                toastTip("请选择举报原因");
                return;
            }
            String text = temp.getText().toString();
            int reportType = 0;
            switch (text){
                case "诈骗欺诈":
                    reportType = 0;
                    break;
                case "恶意骚扰":
                    reportType = 1;
                    break;
                case "色情信息":
                    reportType = 2;
                    break;
                case "性别不符":
                    reportType = 3;
                    break;
                case "垃圾广告":
                    reportType = 4;
                    break;
                case "其他":
                    reportType = 5;
                    break;
            }
            ARouterUtils.toReportActivity(reportType, blogId, fromType);
            finish();
        }else if(id == R.id.toolbar_back_all){
            finish();
        }else{
            Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.report_check_off);
            item.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            item1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            item2.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            item3.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            item4.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            item5.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            Drawable d = ContextCompat.getDrawable(this, R.mipmap.report_check_on);
            temp = (TextView) view;
            if(id == R.id.item){
                item.setCompoundDrawablesWithIntrinsicBounds(d,null,null,null);
            }
            if(id == R.id.item1){
                item1.setCompoundDrawablesWithIntrinsicBounds(d,null,null,null);
            }
            if(id == R.id.item2){
                item2.setCompoundDrawablesWithIntrinsicBounds(d,null,null,null);
            }
            if(id == R.id.item3){
                item3.setCompoundDrawablesWithIntrinsicBounds(d,null,null,null);
            }
            if(id == R.id.item4){
                item4.setCompoundDrawablesWithIntrinsicBounds(d,null,null,null);
            }
            if(id == R.id.item5){
                item5.setCompoundDrawablesWithIntrinsicBounds(d,null,null,null);
            }
        }

    }
}