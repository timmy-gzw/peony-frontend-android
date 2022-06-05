package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.RegexUtils;
import com.tftechsz.im.R;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CustomFilter;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 修改语音闲聊名称
 */
public class EditVoiceNamePopWindow extends BaseCenterPop implements View.OnClickListener {

    private EditText mEtContent;
    private ChatApiService chatApiService;
    private final CompositeDisposable mCompositeDisposable;

    public EditVoiceNamePopWindow(Context context) {
        super(context);
        chatApiService = RetrofitManager.getInstance().createFamilyApi(ChatApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        initUI();
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_edit_voice_name);
    }


    public void setName(String name) {
        mEtContent.setText(name);
//        if (!TextUtils.isEmpty(name)){
//            mEtContent.requestFocus();
//            mEtContent.setSelection(name.length());
//        }
    }


    private void initUI() {
        mEtContent = findViewById(R.id.et_content);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_sure).setOnClickListener(this);
        mEtContent.setFilters(new InputFilter[]{new CustomFilter(16)});
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }


    /**
     * 修改名称和公告
     */
    public void editFile(String file, String name) {
        mCompositeDisposable.add(chatApiService.editFile(file, name)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (response.getData() != null) {
                            dismiss();
                        }
                    }
                }));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.tv_sure) {
            String content = mEtContent.getText().toString();
            if (TextUtils.isEmpty(content)) {
                Utils.toast("玩法名称不能为空");
                return;
            }
            if (!RegexUtils.isMatch("^[\\u4e00-\\u9fa5]+$", content)) {
                Utils.toast("玩法名称仅支持中文");
                return;
            }
            editFile("name", content);
        }
    }

    public interface OnSelectListener {
        void deleteContact();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
