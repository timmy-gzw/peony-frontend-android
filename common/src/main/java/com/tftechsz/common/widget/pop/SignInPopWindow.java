package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.tftechsz.common.R;
import com.tftechsz.common.adapter.SignInAdapter;
import com.tftechsz.common.entity.SignInBean;
import com.tftechsz.common.entity.SignInSuccessBean;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.iservice.MainService;
import com.tftechsz.common.other.SpaceItemDecoration;

/**
 * 包 名 : com.tftechsz.common.widget
 * 描 述 : 签到pop
 */
public class SignInPopWindow extends BaseCenterPop {

    private final MainService mainService;
    private OnSignInListener mSignInListener;
    private TextView tvSignIn;
    private TextView tvDesc;
    private RecyclerView recyclerView;

    public SignInPopWindow(Context context) {
        super(context);
        mainService = ARouter.getInstance().navigation(MainService.class);
        initUI();
    }

    public void setSignInListener(OnSignInListener listener) {
        this.mSignInListener = listener;
    }

    private void initUI() {
        setOutSideDismiss(false);

        tvSignIn = findViewById(R.id.tv_sign_in);
        tvDesc = findViewById(R.id.tv_sign_in_c);
        recyclerView = findViewById(R.id.sign_recy);
        findViewById(R.id.ic_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSignInListener != null) {
                    mSignInListener.cancelSign();
                }
                dismiss();
            }
        });
        tvSignIn.setOnClickListener(v -> signIn());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
        recyclerView.addItemDecoration(new SpaceItemDecoration(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f), false));
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    public void setData(String signInJson) {
        if (TextUtils.isEmpty(signInJson)) return;
        SignInBean signInBean = GsonUtils.fromJson(signInJson, SignInBean.class);
        setData(signInBean);
    }

    public void setData(SignInBean data) {
        if (data == null) return;
        if (tvDesc != null) tvDesc.setText(data.desc);
        if (tvSignIn != null) tvSignIn.setEnabled(!data.is_complete_today);
        if (tvSignIn != null) tvSignIn.setText(data.is_complete_today ? getContext().getString(R.string.sign_in_already) : getContext().getString(R.string.sign_in));
        SignInAdapter adapter = new SignInAdapter(data.start_day);
        if (recyclerView != null) recyclerView.setAdapter(adapter);
        adapter.setList(data.list);
    }

    private void signIn() {
        if (mainService == null) return;
        mainService.signIn(new ResponseObserver<BaseResponse<SignInSuccessBean>>() {
            @Override
            public void onSuccess(BaseResponse<SignInSuccessBean> response) {
                if (mSignInListener != null) {
                    mSignInListener.onSignInResult(SignInPopWindow.this, response.getData());
                }
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
                if (mSignInListener != null) {
                    mSignInListener.onSignInResult(SignInPopWindow.this, null);
                }
            }
        });
    }

    public interface OnSignInListener {
        void onSignInResult(SignInPopWindow popup, SignInSuccessBean bean);

        void cancelSign();
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_sign_in);
    }
}
