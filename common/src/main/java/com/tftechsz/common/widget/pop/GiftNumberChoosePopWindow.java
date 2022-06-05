package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.R;
import com.tftechsz.common.adapter.KeyBordNumberAdapter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.KeyBordDto;
import com.tftechsz.common.event.KeyBordEvent;
import com.tftechsz.common.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GiftNumberChoosePopWindow extends BaseBottomPop implements View.OnClickListener {

    private final String DEL_KEY = "del";

    private TextView tvNumber;
    RecyclerView mRvKeyBord;

    public GiftNumberChoosePopWindow(Context context) {
        super(context);
        mContext = context;
        setPopupGravity(Gravity.BOTTOM | Gravity.END);
        initUI();
        initData();
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_gift_number_choose);
    }


    private void initUI() {

        tvNumber = findViewById(R.id.tv_number);
        findViewById(R.id.tv_sure).setOnClickListener(this);
        mRvKeyBord = findViewById(R.id.rv_keyBord);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        mRvKeyBord.setLayoutManager(layoutManager);


    }

    /**
     * 初始化加载数据
     */
    private void initData() {
        List<KeyBordDto> list = new ArrayList<>();
        list.add(new KeyBordDto("1", ""));
        list.add(new KeyBordDto("2", "ABC"));
        list.add(new KeyBordDto("3", "DEF"));
        list.add(new KeyBordDto("4", "GHI"));
        list.add(new KeyBordDto("5", "JKL"));
        list.add(new KeyBordDto("6", "MNO"));
        list.add(new KeyBordDto("7", "PQRS"));
        list.add(new KeyBordDto("8", "TUV"));
        list.add(new KeyBordDto("9", "WXYZ"));
        list.add(new KeyBordDto("", ""));
        list.add(new KeyBordDto("0", ""));
        list.add(new KeyBordDto(DEL_KEY, ""));
        KeyBordNumberAdapter adapter = new KeyBordNumberAdapter(list);
        mRvKeyBord.setAdapter(adapter);

        adapter.setOnItemClickListener((adapter1, view, position) -> {
            String key = adapter.getData().get(position).number;
            String num = tvNumber.getText().toString().trim();
            if (TextUtils.equals(DEL_KEY, key)) {
                if (num.length() > 0) {
                    tvNumber.setText(num.substring(0, num.length() - 1));
                }
            } else {
                if (!TextUtils.isEmpty(key)) {
                    num += adapter.getData().get(position).number;
                    tvNumber.setText(num);
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_sure) {
            String num = tvNumber.getText().toString();
            if (TextUtils.isEmpty(num) || num.startsWith("0")) {
                ToastUtil.showToast(mContext, "请输入正确礼物数量");
            } else {
                RxBus.getDefault().post(new KeyBordEvent(num));
                dismiss();
            }
        }

    }


}
