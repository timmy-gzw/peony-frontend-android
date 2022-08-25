package com.tftechsz.mine.mvp.presenter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.AttentionService;
import com.tftechsz.common.utils.TimeUtils;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.mvp.IView.IMineInfoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MineInfoPresenter extends BasePresenter<IMineInfoView> {

    public MineApiService service;

    private TimePickerView pvTime;
    private OptionsPickerView pvHeight;  //
    private OptionsPickerView pvWeight;  //体重
    private OptionsPickerView pvIncome;  //体重
    private OptionsPickerView pvOptions;

    //  省份
    private final ArrayList<String> provinceBeanList = new ArrayList<>();
    private final ArrayList<List<String>> cityList = new ArrayList<>();
    private final ArrayList<List<List<String>>> districtList = new ArrayList<>();

    @Autowired
    AttentionService attentionService;

    public MineInfoPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }

    public void initJsonData(Context context) {
        //解析数据
        String JsonData = getJson(context, "province.json");//获取assets目录下的json文件数据
        parseJson(JsonData);
    }

    /**
     * 从asset目录下读取fileName文件内容
     *
     * @param fileName 待读取asset下的文件名
     * @return 得到省市县的String
     */
    private String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    /**
     * 解析json填充集合
     *
     * @param str 待解析的json，获取省市县
     */
    public void parseJson(String str) {
        try {
            //  获取json中的数组
            JSONArray jsonArray = new JSONArray(str);
            //  遍历数据组
            for (int i = 0; i < jsonArray.length(); i++) {
                //  获取省份的对象
                JSONObject provinceObject = jsonArray.optJSONObject(i);
                //  获取省份名称放入集合
                String provinceName = provinceObject.getString("name");
//                provinceBeanList.add(new ProvinceBean(provinceName));
                provinceBeanList.add(provinceName);
                //  获取城市数组
                JSONArray cityArray = provinceObject.optJSONArray("city");
                //  城市
                ArrayList<String> cities = new ArrayList<>();
                //   声明存放城市的集合
                ArrayList<List<String>> districts = new ArrayList<>();
                //声明存放区县集合的集合
                //  遍历城市数组
                for (int j = 0; j < cityArray.length(); j++) {
                    //  获取城市对象
                    JSONObject cityObject = cityArray.optJSONObject(j);
                    //  将城市放入集合
                    String cityName = cityObject.optString("name");
                    cities.add(cityName);
                    //  区/县
                    ArrayList<String> district = new ArrayList<>();
                    // 声明存放区县的集合
                    //  获取区县的数组
                    JSONArray areaArray = cityObject.optJSONArray("area");
                    //  遍历区县数组，获取到区县名称并放入集合
                    for (int k = 0; k < areaArray.length(); k++) {
                        String areaName = areaArray.getString(k);
                        district.add(areaName);
                    }
                    //  将区县的集合放入集合
                    districts.add(district);
                }
                //  将存放区县集合的集合放入集合
                districtList.add(districts);
                //  将存放城市的集合放入集合
                cityList.add(cities);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void showPicker(Context context, RelativeLayout relativeLayout, String[] str) {
        if (pvOptions == null) {
            pvOptions = new OptionsPickerBuilder(context, (options1, options2, options3, v) -> {
                String city = provinceBeanList.get(options1);
                String address; //  如果是直辖市或者特别行政区只设置市和区/县
                if ("北京市".equals(city) || "上海市".equals(city) || "天津市".equals(city) || "重庆市".equals(city) || "澳门".equals(city) || "香港".equals(city)) {
                    address = provinceBeanList.get(options1);
                } else {
                    address = provinceBeanList.get(options1) + " " + cityList.get(options1).get(options2);
                }
                if (null == getView()) return;
                getView().getChooseAddress(address);

            }).setOptionsSelectChangeListener((options1, options2, options3) -> {

                    })
                    .setLayoutRes(R.layout.layout_pickerview_custom, new CustomListener() {
                        @Override
                        public void customLayout(View v) {
                            TextView title = v.findViewById(R.id.pick_c_title);
                            title.setText("请选择你的家乡");
                            v.findViewById(R.id.tv_finish).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pvOptions.returnData();
                                    pvOptions.dismiss();
                                }
                            });
                        }
                    })
                    .setContentTextSize(18) //内容文字大小
                    .setLineSpacingMultiplier(2.1f) //行高倍数
                    .setTextColorCenter(ContextCompat.getColor(context, R.color.colorPrimary))//选中颜色
                    .setSubmitText("确定")//确定按钮文字
                    .setCancelText("取消")//取消按钮文字
                    .setSubCalSize(18)//确定和取消文字大小
                    .setSubmitColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setCancelColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setTitleSize(20)//标题文字大小
                    .setContentTextSize(18)//滚轮文字大小
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setCyclic(false, false, false)//循环与否
                    .setSelectOptions(0, 0, 0)  //设置默认选中项
                    .setDecorView(relativeLayout)
                    .build();
            pvOptions.setPicker(provinceBeanList, cityList);//添加数据源
        }
        int selectProvince = 0;
        int selectCity = 0;
        //设置默认选中item
        if (str != null && str.length > 1 && provinceBeanList.size() > 0) {
            String province = str[0];
            String city = str[1];
            int size = provinceBeanList.size();
            for (int i = 0; i < size; i++) {
                if (provinceBeanList.get(i).contains(province)) {
                    //省份
                    selectProvince = i;
                    int citySize = cityList.get(i).size();
                    for (int j = 0; j < citySize; j++) {
                        if (cityList.get(i).get(j).contains(city)) {
                            selectCity = j;
                            pvOptions.setSelectOptions(selectProvince, selectCity);
                            pvOptions.show();
                            return;
                        }
                    }
                }
            }
        }
        pvOptions.setSelectOptions(selectProvince, selectCity);
        pvOptions.show();
    }


    /**
     * 选择生日
     *
     * @param context
     */
    public void timeChoose(Context context, RelativeLayout relativeLayout, int year, int moth, int day) {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        selectedDate.set(year, moth - 1, day);
        //正确设置方式 原因：注意事项有说明
        startDate.set(1900, 0, 1);
//        endDate.set(TimeUtils.getCurrentYear(), TimeUtils.getCurrentMonth() - 1, TimeUtils.getCurrentDay2());
        String[] split = Utils.getOldYearDate(-18).split("-");
        endDate.set(Integer.parseInt(split[0]), TimeUtils.getCurrentMonth() - 1, TimeUtils.getCurrentDay2());
        if (pvTime == null) {
            pvTime = new TimePickerBuilder(context, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {//选中事件回调
                    int year = Integer.parseInt(Objects.requireNonNull(TimeUtils.date2Str(date, "yyyy")));
                    int moth = Integer.parseInt(Objects.requireNonNull(TimeUtils.date2Str(date, "MM")));
                    int day = Integer.parseInt(Objects.requireNonNull(TimeUtils.date2Str(date, "dd")));
                    selectedDate.set(year, moth - 1, day);
                    if (null == getView()) return;
                    getView().getChooseBirthday(TimeUtils.dateToYYYYMMdd(date), TimeUtils.getAstro(moth, day));
                }
            })
                    .setLayoutRes(R.layout.layout_pickerview_custom, new CustomListener() {
                        @Override
                        public void customLayout(View v) {
                            TextView title = v.findViewById(R.id.pick_c_title);
                            title.setText("请选择你的出生日期");
                            v.findViewById(R.id.tv_finish).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pvTime.returnData();
                                    pvTime.dismiss();
                                }
                            });
                        }
                    })
                    .setContentTextSize(18) //内容文字大小
                    .setLineSpacingMultiplier(2.1f) //行高倍数
                    .setTextColorCenter(ContextCompat.getColor(context, R.color.colorPrimary))//选中颜色
                    .setDecorView(relativeLayout)
                    .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                    .setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(20)//标题文字大小
                    .setTitleText("日期")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(context, R.color.colorPrimary))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(context, R.color.colorPrimary))//取消按钮文字颜色
                    .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, endDate)//起始终止年月日设定
                    .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .build();
        }
        pvTime.show();
    }


    /**
     * 身高
     */
    public void chooseHeight(Context context, RelativeLayout relativeLayout, String height) {
        int position = 0;
        List<String> list = new ArrayList<>();
        for (int i = 150; i <= 200; i++) {
            list.add(i + "cm");
        }
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.equals(height, list.get(i))) {
                position = i;
            }
        }
        if (pvHeight == null) {
            pvHeight = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3, View v) {
                    if (null == getView()) return;
                    getView().getChooseHeight(list.get(options1));
                }
            })
                    .setLayoutRes(R.layout.layout_pickerview_custom, new CustomListener() {
                        @Override
                        public void customLayout(View v) {
                            TextView title = v.findViewById(R.id.pick_c_title);
                            title.setText("请选择你的身高（cm）");
                            v.findViewById(R.id.tv_finish).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pvHeight.returnData();
                                    pvHeight.dismiss();
                                }
                            });
                        }
                    })
                    .setContentTextSize(18) //内容文字大小
                    .setLineSpacingMultiplier(2.1f) //行高倍数
                    .setTextColorCenter(ContextCompat.getColor(context, R.color.colorPrimary))//选中颜色
                    .setDecorView(relativeLayout)
                    .setSubmitColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setCancelColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .build();
        }
        pvHeight.setSelectOptions(position);
        pvHeight.setPicker(list);
        pvHeight.show();
    }

    /**
     * 体重
     */
    public void chooseWeight(Context context, RelativeLayout relativeLayout, String weight) {
        int position = 11;
        List<String> list = new ArrayList<>();
        list.add("30kg以下");
        for (int i = 30; i <= 100; i++) {
            list.add(i + "kg");
        }
        list.add("100kg以上");
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.equals(weight, list.get(i))) {
                position = i;
            }
        }
        if (pvWeight == null) {
            pvWeight = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3, View v) {
                    if (null == getView()) return;
                    getView().getChooseWeight(list.get(options1));
                }
            })
                    .setLayoutRes(R.layout.layout_pickerview_custom, new CustomListener() {
                        @Override
                        public void customLayout(View v) {
                            TextView title = v.findViewById(R.id.pick_c_title);
                            title.setText("请选择你的体重（kg）");
                            v.findViewById(R.id.tv_finish).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pvWeight.returnData();
                                    pvWeight.dismiss();
                                }
                            });
                        }
                    })
                    .setContentTextSize(18) //内容文字大小
                    .setLineSpacingMultiplier(2.1f) //行高倍数
                    .setTextColorCenter(ContextCompat.getColor(context, R.color.colorPrimary))//选中颜色
                    .setDecorView(relativeLayout)
                    .setSubmitColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setCancelColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .build();
        }
        pvWeight.setSelectOptions(position);
        pvWeight.setPicker(list);
        pvWeight.show();
    }

    /**
     * 年收入
     */
    public void chooseIncome(Context context, RelativeLayout relativeLayout, String income) {
        int position = 0;
        List<String> list = new ArrayList<>();
        list.add("5W以下");
        list.add("5~10W");
        list.add("10~20W");
        list.add("20~30W");
        list.add("30~50W");
        list.add("50~100W");
        list.add("100W以上");
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.equals(income, list.get(i))) {
                position = i;
            }
        }
        if (pvIncome == null) {
            pvIncome = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3, View v) {
                    if (null == getView()) return;
                    getView().getChooseIncome(list.get(options1));

                }
            })
                    .setLayoutRes(R.layout.layout_pickerview_custom, new CustomListener() {
                        @Override
                        public void customLayout(View v) {
                            TextView title = v.findViewById(R.id.pick_c_title);
                            title.setText("当前年收入情况（元）");
                            v.findViewById(R.id.tv_finish).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pvWeight.returnData();
                                    pvWeight.dismiss();
                                }
                            });
                        }
                    })
                    .setContentTextSize(18) //内容文字大小
                    .setLineSpacingMultiplier(2.1f) //行高倍数
                    .setTextColorCenter(ContextCompat.getColor(context, R.color.colorPrimary))//选中颜色
                    .setDecorView(relativeLayout)
                    .setSubmitColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setCancelColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .build();
        }
        pvIncome.setSelectOptions(position);
        pvIncome.setPicker(list);
        pvIncome.show();
    }


    /**
     * 更新用户信息
     */
    public void updateUserInfo(String birthday, String hometown, String height, String weight, String income) {
        HashMap<String, String> map = new HashMap<>();
        map.put("birthday", birthday);
        map.put("hometown", hometown);
        map.put("height", height);
        map.put("weight", weight);
        map.put("income", income);
        addNet(service.updateUserInfo(createRequestBody(map)).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView()) return;
                        getView().updateUserInfoSuccess(response.getData());
                    }
                }));
    }


    /**
     * 上传头像
     *
     * @param url
     */
    public void updateAvatar(String url) {
        addNet(service.uploadAvatar(url).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView()) return;
                        if (response.cmd_data == null) {
                            Utils.toast("上传成功");
                        }
                        getView().uploadAvatarSuccess(response.getData());
                        getView().hideLoadingDialog();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().uploadAvatarFail(msg);
                        getView().hideLoadingDialog();
                    }
                }));
    }


    public void uploadAvatar(String path) {
        if (getView() != null)
            getView().showLoadingDialog();
        File file = new File(path);
        UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_USER_TYPE, UploadHelper.PATH_USER_AVATAR, UploadHelper.TYPE_IMAGE, file, new UploadHelper.OnUploadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onLoading(long cur, long total) {

            }

            @Override
            public void onSuccess(String url) {
                updateAvatar(url);
            }

            @Override
            public void onError() {
                if (null == getView()) return;
                getView().hideLoadingDialog();
            }
        });

    }

    /**
     * 获取用户信息
     */
    public void getUserInfoDetail() {
        addNet(service.getUserInfoDetail().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (getView() == null) return;
                        getView().getUserInfoSuccess(response.getData());
                    }
                }));
    }

    /**
     * 获取自己相册
     */
    public void getSelfPhoto(int pageSize) {
        addNet(service.getSelfPhoto(pageSize).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<String>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> response) {
                        if (getView() == null) return;
                        getView().getPhotoSuccess(response.getData());
                    }
                }));
    }


}
