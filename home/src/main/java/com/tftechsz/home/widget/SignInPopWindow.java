package com.tftechsz.home.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.tftechsz.common.other.SpaceItemDecoration;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.home.R;
import com.tftechsz.home.adapter.SignInAdapter;
import com.tftechsz.home.entity.SignInBean;

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

        TextView tvSignIn = findViewById(R.id.tv_sign_in);
        TextView tvDesc = findViewById(R.id.tv_sign_in_c);
        tvDesc.setText(data.desc);
        findViewById(R.id.ic_close).setOnClickListener(v -> dismiss());
        tvSignIn.setOnClickListener(v -> listener.signInPopClick());
        tvSignIn.setEnabled(!data.is_complete_today);
        tvSignIn.setText(data.is_complete_today ? getContext().getString(R.string.sign_in_already) : getContext().getString(R.string.sign_in));
        RecyclerView recy = findViewById(R.id.sign_recy);
        SignInAdapter adapter = new SignInAdapter(data.start_day);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
        recy.addItemDecoration(new SpaceItemDecoration(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f), false));
        recy.setAdapter(adapter);
        recy.setLayoutManager(gridLayoutManager);
        adapter.setList(data.list);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_sign_in);
    }
}
