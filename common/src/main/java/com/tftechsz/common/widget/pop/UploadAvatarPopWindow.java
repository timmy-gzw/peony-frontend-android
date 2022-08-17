package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tftechsz.common.R;
import com.tftechsz.common.iservice.UserProviderService;

/**
 *  上传头像
 */
public class UploadAvatarPopWindow extends BaseBottomPop implements View.OnClickListener {


    UserProviderService service;

    public UploadAvatarPopWindow(Context context) {
        super(context);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        initUI();
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_upload_avatar);
    }

    private void initUI() {
        TextView mTvTip = findViewById(R.id.tv_tip);
        ImageView ivErrorAvater = findViewById(R.id.iv_error_avatar);
        if(service.getUserInfo().isGirl()){
            ivErrorAvater.setImageResource(R.mipmap.upload_avater_rule_girl);
        }else {
            ivErrorAvater.setImageResource(R.mipmap.upload_avater_rule_boy);
        }
        findViewById(R.id.tv_upload).setOnClickListener(this);   // 知道了
//        RoundedImageView ivAvatar = findViewById(R.id.iv_true_avatar);
//        ivAvatar.setBackgroundResource(service.getUserInfo().getSex() == 1 ? R.mipmap.ic_boy_true_avatar : R.mipmap.ic_true_avatar);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_upload) {
            if (listener != null)
                listener.onUpload();
            dismiss();
        }
    }


    public interface OnSelectListener {
        void onUpload();

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
