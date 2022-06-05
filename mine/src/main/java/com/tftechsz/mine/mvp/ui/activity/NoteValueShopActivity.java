package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import com.blankj.utilcode.util.KeyboardUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.PriceEditInputFilter;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ActNoteValueShopBinding;
import com.tftechsz.mine.entity.NoteValueConfirmDto;
import com.tftechsz.mine.mvp.IView.INoteValueShopView;
import com.tftechsz.mine.mvp.presenter.INoteValueShopPresenter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 音符商城
 */
public class NoteValueShopActivity extends BaseMvpActivity<INoteValueShopView, INoteValueShopPresenter> implements View.OnClickListener, INoteValueShopView {

    private ActNoteValueShopBinding mBind;
    private IntegralDto mIntegralDto;

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_note_value_shop);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(this).keyboardEnable(true).init();
        new ToolBarBuilder().showBack(true).setTitle("音符商城").setRightText("兑换记录", this).build();
        mBind.btn.setOnClickListener(this);

        mIntegralDto = (IntegralDto) getIntent().getSerializableExtra(Interfaces.EXTRA_DATA);
        if (mIntegralDto != null) {
            setData(mIntegralDto);
        } else {
            p.getNoteValue();
        }
        //mBind.edtNum.setOnFocusChangeListener((v, hasFocus) -> mBind.edtNum.setHint(null));
    }

    private void setData(IntegralDto dto) {
        mBind.noteValue.setText(new SpannableStringUtils.Builder().append("音符余额：").append(dto.note_value).setForegroundColor(Utils.getColor(R.color.color_8C4BE1)).create());
        mBind.edtNum.setHint(new SpannableStringUtils.Builder().append("请输入金币数量").setFontSize(14, true).create());
        mBind.maxCoin.setText(new SpannableStringUtils.Builder().append(String.format("最多可兑换金币：%s，", dto.coin))
                .append("全部兑换").setForegroundColor(Utils.getColor(R.color.color_89A5FF))
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        int i = Integer.parseInt(dto.coin);
                        if (i == 0) {
                            Utils.toast("没有可兑换的音符");
                        } else {
                            mBind.edtNum.setText(String.valueOf(Math.min(dto.note_rules.maximum_exchange, Integer.parseInt(dto.coin))));
                            Utils.setFocus(mBind.edtNum);
                        }
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                    }
                }).create());
        mBind.maxCoin.setMovementMethod(LinkMovementMethod.getInstance());
        mBind.noteRules.setText(new SpannableStringUtils.Builder()
                .append(dto.note_rules.title).setBold()
                .append(dto.note_rules.content).create());
        InputFilter[] filters1 = {new PriceEditInputFilter(dto.note_rules.maximum_exchange, mBind.edtNum, 2)};
        mBind.edtNum.setFilters(filters1);
    }

    @Override
    public INoteValueShopPresenter initPresenter() {
        return new INoteValueShopPresenter();
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canOperate()) return;
        int id = v.getId();
        if (id == R.id.toolbar_tv_menu) { //收支记录
            ARouterUtils.toIntegralDetailedActivity(5);
        } else if (id == R.id.btn) {//立即兑换
            String num = Utils.getText(mBind.edtNum);
            if (Utils.checkNull(mBind.edtNum, "请输入要兑换的金币数")) {
                return;
            }
            if (mIntegralDto == null || mIntegralDto.note_rules == null)
                return;
            int v1 = (int) Double.parseDouble(num);
            if (v1 < mIntegralDto.note_rules.minimum_exchange) {
                toastTip(String.format("单次最少兑换%s金币", mIntegralDto.note_rules.minimum_exchange));
                return;
            }
            if (v1 > Integer.parseInt(mIntegralDto.coin)) {
                toastTip("超出最多可兑换金币数量");
                return;
            }
            KeyboardUtils.hideSoftInput(this);
            p.convertConfirm(v1);
        }
    }

    @Override
    public void getNoteValueSuccess(IntegralDto data) {
        mIntegralDto = data;
        setData(mIntegralDto);
    }

    @Override
    public void exchangeNoteValueSuccess() {
        toastTip("兑换金币成功");
        mBind.edtNum.setText(null);
        p.getNoteValue();
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
    }

    @Override
    public void convertConfirmSuccess(NoteValueConfirmDto data) {
        new CustomPopWindow(this, 1)
                .setContent(new SpannableStringUtils.Builder().append(String.format("消耗音符: %s\n兑换金币: %s", data.cost, data.value)).setBold().create())
                .setRightButton("确定兑换")
                .setLeftButton("取消")
                .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onSure() {
                        p.exchangeNoteValue(data.value);

                    }
                }).showPopupWindow();
    }
}
