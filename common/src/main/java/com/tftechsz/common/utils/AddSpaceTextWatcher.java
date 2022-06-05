package com.tftechsz.common.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

/**
 * 包 名 : com.tftechsz.common.utils
 * 描 述 : TODO
 */
public class AddSpaceTextWatcher implements TextWatcher {

    /**
     * text改变之前的长度
     */
    private int beforeTextLength = 0;
    private int onTextLength = 0;
    private boolean isChanged = false;
    private StringBuffer buffer = new StringBuffer();
    /**
     * 改变之前text空格数量
     */
    int spaceNumberA = 0;
    private EditText editText;
    /**
     * text最大长度限制
     */
    private int maxLenght;
    private SpaceType spaceType;
    /**
     * 记录光标的位置
     */
    private int location = 0;
    /**
     * 是否是主动设置text
     */
    private boolean isSetText = false;

    public AddSpaceTextWatcher(EditText editText, SpaceType spaceType) {
        if (editText == null) {
            throw new NullPointerException("editText is null");
        }
        this.editText = editText;
        switch (spaceType) {
            case bankCardNumberType:
                this.maxLenght = 48;
                break;
            case mobilePhoneNumberType:
                this.maxLenght = 13;
                break;
            case IDCardNumberType:
                this.maxLenght = 21;
                break;

            default:
                this.maxLenght = 1000;
                break;
        }
        setSpaceType(spaceType);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLenght)});
        editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        beforeTextLength = s.length();
        if (buffer.length() > 0) {
            buffer.delete(0, buffer.length());
        }
        spaceNumberA = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                spaceNumberA++;
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextLength = s.length();
        buffer.append(s.toString());
        if (onTextLength == beforeTextLength || onTextLength > maxLenght || isChanged) {
            isChanged = false;
            return;
        }
        isChanged = true;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isChanged) {
            location = editText.getSelectionEnd();
            int index = 0;
            while (index < buffer.length()) { // 删掉所有空格
                if (buffer.charAt(index) == ' ') {
                    buffer.deleteCharAt(index);
                } else {
                    index++;
                }
            }

            index = 0;
            int spaceNumberB = 0;
            while (index < buffer.length()) { // 插入所有空格
                spaceNumberB = insertSpace(index, spaceNumberB);
                index++;
            }

            String str = buffer.toString();

            // 下面是计算光位置的
            if (spaceNumberB > spaceNumberA) {
                location += (spaceNumberB - spaceNumberA);
                spaceNumberA = spaceNumberB;
            }
            if (isSetText) {
                location = str.length();
                isSetText = false;
            } else if (location > str.length()) {
                location = str.length();
            } else if (location < 0) {
                location = 0;
            }
            updateContext(s, str);
            isChanged = false;
        }
    }

    /**
     * 更新编辑框中的内容
     *
     * @param editable
     * @param values
     */
    private void updateContext(Editable editable, String values) {
        if (spaceType == SpaceType.IDCardNumberType) {
            editable.replace(0, editable.length(), values);
        } else {
            editText.setText(values);
            try {
                editText.setSelection(location);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据类型插入空格
     *
     * @param index
     * @param spaceNumberAfter
     * @return
     * @see [类、类#方法、类#成员]
     */
    private int insertSpace(int index, int spaceNumberAfter) {
        switch (spaceType) {
            // 相隔四位空格
            case mobilePhoneNumberType:
                if (index == 3
                        || ((index > 7) && ((index - 3) % (4 * spaceNumberAfter) == spaceNumberAfter))) {
                    buffer.insert(index, ' ');
                    spaceNumberAfter++;
                }
                break;
            case IDCardNumberType:
                if (index == 6
                        || ((index > 10) && ((index - 6) % (4 * spaceNumberAfter) == spaceNumberAfter))) {
                    buffer.insert(index, ' ');
                    spaceNumberAfter++;
                }
                break;
            default:
                if (index > 3
                        && (index % (4 * (spaceNumberAfter + 1)) == spaceNumberAfter)) {
                    buffer.insert(index, ' ');
                    spaceNumberAfter++;
                }
                break;
        }
        return spaceNumberAfter;
    }

    /***
     * 计算需要的空格数
     *
     * @return 返回添加空格后的字符串长度
     * @see [类、类#方法、类#成员]
     */
    private int computeSpaceCount(CharSequence charSequence) {
        buffer.delete(0, buffer.length());
        buffer.append(charSequence.toString());
        int index = 0;
        int spaceNumberB = 0;
        while (index < buffer.length()) { // 插入所有空格
            spaceNumberB = insertSpace(index, spaceNumberB);
            index++;
        }
        buffer.delete(0, buffer.length());
        return index;
    }

    /**
     * 设置空格类型
     *
     * @param spaceType
     * @see [类、类#方法、类#成员]
     */
    public void setSpaceType(SpaceType spaceType) {
        this.spaceType = spaceType;
        if (this.spaceType == SpaceType.IDCardNumberType) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);

            //此处添加输入法的限制
            String digits = "0123456789Xx ";
            if (!TextUtils.isEmpty(digits)) {
                editText.setKeyListener(DigitsKeyListener.getInstance(digits));
            }
        }
    }

    /**
     * 设置输入字符
     *
     * @param charSequence
     * @return 返回设置成功失败
     * @see [类、类#方法、类#成员]
     */
    public boolean setText(CharSequence charSequence) {
        if (editText != null && !TextUtils.isEmpty(charSequence) && computeSpaceCount(charSequence) <= maxLenght) {
            isSetText = true;
            editText.removeTextChangedListener(this);
            editText.setText(charSequence);
            editText.addTextChangedListener(this);
            return true;
        }
        return false;
    }

    /**
     * 得到输入的字符串去空格后的字符串
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public String getTextNotSpace() {
        if (editText != null) {
            return delSpace(editText.getText().toString());
        }
        return null;
    }

    /**
     * 得到输入的字符串去空格后的长度
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public int getLenghtNotSpace() {
        if (editText != null) {
            return getTextNotSpace().length();
        }
        return 0;
    }

    /**
     * 得到空格数量
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public int getSpaceCount() {
        return spaceNumberA;
    }

    /**
     * 去掉字符空格，换行符等
     *
     * @param str
     * @return
     * @see [类、类#方法、类#成员]
     */
    private String delSpace(String str) {
        if (str != null) {
            str = str.replaceAll("\r", "");
            str = str.replaceAll("\n", "");
            str = str.replace(" ", "");
        }
        return str;
    }

    /**
     * 空格类型
     */
    public enum SpaceType {
        /**
         * 默认类型
         */
        defaultType,
        /**
         * 银行卡类型
         */
        bankCardNumberType,
        /**
         * 手机号类型
         */
        mobilePhoneNumberType,
        /**
         * 身份证类型
         */
        IDCardNumberType;
    }
}
