package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.PriceEditInputFilter;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.LuckyStoneResultAdapter;
import com.tftechsz.party.entity.TurntableStartBeforeBean;

import java.util.List;

/**
 * 幸运转盘 -购买幸运石
 */
public class BuyLuckyStonePopWindow extends BaseBottomPop implements View.OnClickListener {

    private RecyclerView recyclerView;
    private LuckyStoneResultAdapter adapter;
    private EditText mTvContent;
    private TextView mTvNumberText, mTvBtn;
    //luckyStone:默认金额
    private int max;
    private int type, mLuckyStone;
    private ImageView mImgText;
    private ImageView mImgSu, mImgAdd;
    private boolean isCostThan1000;//大于1000

    public BuyLuckyStonePopWindow(Context context) {
        super(context);
        mContext = context;

    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_buy_lucky_stone);
    }

    public void setAdapter(List<TurntableStartBeforeBean.BuyBean> list, String cost, int luckyStone, int type, ILBuyLuckPopCallBack callBack) {
        this.type = type;
        this.ilBuyLuckPopCallBack = callBack;
        mLuckyStone = luckyStone;
        initUI(list);
        if ((Double.parseDouble(cost) - luckyStone) >= luckyStone && luckyStone != 0) {
            isCostThan1000 = Double.parseDouble(cost) / luckyStone > 1000;
            max = Integer.parseInt(CommonUtil.replace(String.valueOf((Double.parseDouble(cost) / luckyStone))));
        } else if (Double.parseDouble(cost) == luckyStone && luckyStone != 0) {
            max = 1;
        } else {
            max = 0;
            setControlBtn(3);
        }
        mTvContent.setText("1");
        mTvBtn.setText(luckyStone + "金币");
        mTvBtn.setBackgroundResource(type == 1 ? R.drawable.party_btn_bg_1 : R.drawable.party_btn_bg_2);
        mTvNumberText.setText("当前金币余额：" + cost);
        mImgText.setImageResource(type == 1 ? R.drawable.pop_buy_title1 : R.drawable.pop_buy_title1_gj);
    }

    private void initUI(List<TurntableStartBeforeBean.BuyBean> list) {
        mTvBtn = findViewById(R.id.tv_btn);
        mImgAdd = findViewById(R.id.img_click_add);
        mImgAdd.setOnClickListener(this);
        mImgText = findViewById(R.id.img_text);
        mImgSu = findViewById(R.id.img_click_su);
        mImgSu.setOnClickListener(this);
        mTvBtn.setOnClickListener(this);
        mTvContent = findViewById(R.id.tv_content);
        InputFilter[] filters1 = {new PriceEditInputFilter(1000, mTvContent, 3)};
        mTvContent.setFilters(filters1);
        mTvContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(s.toString())) {
                    setControlBtn(2);
                    mTvContent.setCursorVisible(true);
                    mTvContent.requestFocus();
                    mTvContent.setHint(" ");
                    mTvContent.findFocus();
                } else if (max == 0) {
                    if (!s.toString().equals("1")) {
                        mTvContent.setText("1");
                    }
                    setControlBtn(3);
                } else if ((Double.parseDouble(s.toString()) > max)) {
                    mTvContent.setText(max > 1000 ? "1000" : max + "");
//                    mTvContent.setText(s.toString().substring(0, s.length() - 1));
                } else {
                    int content = Integer.parseInt(s.toString());
                    if (s.toString().equals("1000")) {
                        setControlBtn(1);
                    } else if (content == max && max != 1) {
                        setControlBtn(1);
                    } else if (content == max) {
                        setControlBtn(3);
                    } else if (content < max && content > 1) {
                        setControlBtn(0);
                    } else if (content < max && content == 1) {
                        setControlBtn(2);
                    }
                    //计算数额
                    mTvBtn.setText((Integer.parseInt(s.toString()) * mLuckyStone) + "金币");
                }
                try {
                    mTvContent.setSelection(s.toString().length());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        recyclerView = findViewById(R.id.rel_party_lucky_result_recyclerview);
        if (adapter == null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new LuckyStoneResultAdapter();
            recyclerView.setAdapter(adapter);
        }
        adapter.setList(list);
        findViewById(R.id.img_click_max).setOnClickListener(this);
        mTvNumberText = findViewById(R.id.tv_coin);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_click_max) {
//最大
            if (isCostThan1000) {
                setControlBtn(1);
                mTvContent.setText("1000");
                return;
            }
            if (max > 0) {
                mTvContent.setText(String.valueOf(max));
                if (max != 1) {
                    setControlBtn(1);
                } else {
                    setControlBtn(3);
                }
            }
        } else if (id == R.id.img_click_add) {
            if (mTvContent.getText() == null)
                return;
            String number = mTvContent.getText().toString();
            if (TextUtils.isEmpty(number) || number.equals("1000")) {
                return;
            }
            //增加数量
            if (Integer.parseInt(number) > 0 && Integer.parseInt(number) < max) {
                String curNumber = String.valueOf(Integer.parseInt(number) + 1);
                mTvContent.setText(curNumber);
                setControlBtn((Integer.parseInt(curNumber) < max) ? 0 : 1);
                if (mTvContent.getText().toString().equals("1000")) {
                    setControlBtn(1);
                }
            } else if (max == Integer.parseInt(mTvContent.getText().toString()) && max > 1) {
                setControlBtn(1);
            } else {
                setControlBtn(max > 1 ? 2 : 3);
            }
        } else if (id == R.id.img_click_su) {
            if (mTvContent.getText() == null || TextUtils.isEmpty(mTvContent.getText().toString())) {
                return;
            }
            //减少数量
            if (Integer.parseInt(mTvContent.getText().toString()) > 1) {
                mTvContent.setText(String.valueOf(Integer.parseInt(mTvContent.getText().toString()) - 1));
                setControlBtn(Integer.parseInt(mTvContent.getText().toString()) > 1 ? 0 : 2);
            } else {
                setControlBtn(max > 1 ? 2 : 3);
            }
        } else if (id == R.id.tv_btn) {
            if (mTvContent.getText() == null || TextUtils.isEmpty(mTvContent.getText().toString())) {
                ToastUtil.showToast(mContext, "请输入数量");
                return;
            }
            //确认
            if (ilBuyLuckPopCallBack != null) {
                ilBuyLuckPopCallBack.ok(type, Integer.parseInt(mTvContent.getText().toString()));
            }
            dismiss();

        }

    }

    /**
     * 可增可见
     *
     * @param selectedSub 0 : 可减法 可增加
     *                    3:不能减不能加
     */
    private void setControlBtn(int selectedSub) {
        if (max == 0) {
            selectedSub = 3;
        }
        mImgSu.setImageResource(selectedSub == 0 || selectedSub == 1 ? R.drawable.pop_lucky_stone_subtraction_selected : R.drawable.pop_lucky_stone_subtraction);
        mImgAdd.setImageResource(selectedSub == 0 || selectedSub == 2 ? R.drawable.pop_lucky_stone_add : R.drawable.pop_lucky_stone_add_nor);
    }

    ILBuyLuckPopCallBack ilBuyLuckPopCallBack;

    interface ILBuyLuckPopCallBack {
        void ok(int type, int number);
    }
}
