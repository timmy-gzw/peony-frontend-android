package com.faceunity.nama.ui;

import com.faceunity.nama.R;
import com.faceunity.nama.entity.Filter;
import com.faceunity.nama.entity.FilterEnum;
import com.faceunity.nama.param.BeautificationParam;
import com.faceunity.nama.utils.DecimalUtils;
import com.faceunity.nama.utils.PreferenceUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 美颜参数SharedPreferences记录,目前仅以保存数据，可改造为以SharedPreferences保存数据
 * Created by tujh on 2018/3/7.
 */
public final class BeautyParameterModel {
    public static final String TAG = BeautyParameterModel.class.getSimpleName();
    /**
     * 滤镜默认强度 0.4
     */
    public static final float DEFAULT_FILTER_LEVEL = 0.4f;
    public static final String STR_FILTER_LEVEL = "FilterLevel_";
    /**
     * 每个滤镜强度值。key: name, value: level
     */
    public static Map<String, Float> sFilterLevel = new HashMap<>(16);
    /**
     * 默认滤镜 自然 1
     */
    public static Filter sFilter;
    // 美型默认参数
    private static final Map<Integer, Float> FACE_SHAPE_DEFAULT_PARAMS = new HashMap<>(16);

    private static float sColorLevel;// 美白
    private static float sBlurLevel; // 精细磨皮程度
    private static float sRedLevel;// 红润
    private static float sEyeBright;// 亮眼
    private static float sToothWhiten;// 美牙
    // 美肤默认参数
    private static final Map<Integer, Float> FACE_SKIN_DEFAULT_PARAMS = new HashMap<>(16);

    private static float sMicroPouch; // 去黑眼圈
    private static float sMicroNasolabialFolds; // 去法令纹
    private static float sMicroSmile; // 微笑嘴角
    private static float sMicroCanthus; // 眼角
    private static float sMicroPhiltrum; // 人中
    private static float sMicroLongNose; // 鼻子长度
    private static float sMicroEyeSpace; // 眼睛间距
    private static float sMicroEyeRotate; // 眼睛角度

    private static float sCheekThinning;//瘦脸
    private static float sCheekV;//V脸
    private static float sCheekNarrow;//窄脸
    private static float sCheekSmall;//小脸
    private static float sEyeEnlarging;//大眼
    private static float sIntensityChin;//下巴
    private static float sIntensityForehead;//额头
    private static float sIntensityNose;//瘦鼻
    private static float sIntensityMouth;//嘴形


    private static float mFilterLevel = 0.4f;// 滤镜程度
    private static final float mBlurLevel = 0.6f;// 磨皮程度
    private static final float mColorLevel = 0.4f;// 美白
    private static final float mRedLevel = 0.2f;// 红润
    private static final float mEyeBright = 0.5f;// 亮眼
    private static final float mToothWhiten = 0.4f;// 美牙
    private static final float mCheekThinning = 0.3f;// 瘦脸
    private static final float mCheekV = 0.08f;// V脸
    private static final float mCheekNarrow = 0.1f;// 窄脸
    private static final float mCheekSmall = 0f;// 小脸
    private static final float mEyeEnlarging = 0.25f;// 大眼
    private static final float mIntensityChin = 0.3f;// 下巴
    private static final float mIntensityForehead = 0.3f;// 额头
    private static final float mIntensityMouth = 0.4f;// 嘴形
    private static final float mIntensityNose = 0.5f;// 瘦鼻
    private static final float mRemovePouchStrength = 0f; // 去黑眼圈
    private static final float mRemoveNasolabialFoldsStrength = 0f; // 去法令纹
    private static final float mIntensitySmile = 0f; // 微笑嘴角
    private static final float mIntensityCanthus = 0f; // 开眼角
    private static final float mIntensityPhiltrum = 0.5f; // 调节人中
    private static final float mIntensityLongNose = 0.5f; // 鼻子长度
    private static final float mIntensityEyeSpace = 0.5f; // 眼睛间距
    private static final float mIntensityEyeRotate = 0.5f; // 眼睛角度




    public static void init() {
        //从 PreferenceUtil 中读取参数到内存
        sFilterLevel = PreferenceUtil.getFilterParams();
        sFilter = PreferenceUtil.getFilter(FilterEnum.ziran_1.create());

        sColorLevel = PreferenceUtil.getFloatParam(BeautificationParam.COLOR_LEVEL, mColorLevel);
        sBlurLevel = PreferenceUtil.getFloatParam(BeautificationParam.BLUR_LEVEL, mBlurLevel);
        sRedLevel = PreferenceUtil.getFloatParam(BeautificationParam.RED_LEVEL, mRedLevel);
        sEyeBright = PreferenceUtil.getFloatParam(BeautificationParam.EYE_BRIGHT, mEyeBright);
        sToothWhiten = PreferenceUtil.getFloatParam(BeautificationParam.TOOTH_WHITEN, mToothWhiten);

        sMicroPouch = PreferenceUtil.getFloatParam(BeautificationParam.REMOVE_POUCH_STRENGTH, mRemovePouchStrength);
        sMicroNasolabialFolds = PreferenceUtil.getFloatParam(BeautificationParam.REMOVE_NASOLABIAL_FOLDS_STRENGTH, mRemoveNasolabialFoldsStrength);
        sMicroSmile = PreferenceUtil.getFloatParam(BeautificationParam.INTENSITY_SMILE, mIntensitySmile);
        sMicroCanthus = PreferenceUtil.getFloatParam(BeautificationParam.INTENSITY_CANTHUS, mIntensityCanthus);
        sMicroPhiltrum = PreferenceUtil.getFloatParam(BeautificationParam.INTENSITY_PHILTRUM, mIntensityPhiltrum);
        sMicroLongNose = PreferenceUtil.getFloatParam(BeautificationParam.INTENSITY_LONG_NOSE, mIntensityLongNose);
        sMicroEyeSpace = PreferenceUtil.getFloatParam(BeautificationParam.INTENSITY_EYE_SPACE, mIntensityEyeSpace);
        sMicroEyeRotate = PreferenceUtil.getFloatParam(BeautificationParam.INTENSITY_EYE_ROTATE, mIntensityEyeRotate);

        sCheekThinning = PreferenceUtil.getFloatParam(BeautificationParam.CHEEK_THINNING, mCheekThinning);
        sCheekV = PreferenceUtil.getFloatParam(BeautificationParam.CHEEK_V, mCheekV);
        sCheekNarrow = PreferenceUtil.getFloatParam(BeautificationParam.CHEEK_NARROW, mCheekNarrow);
        sCheekSmall = PreferenceUtil.getFloatParam(BeautificationParam.CHEEK_SMALL, mCheekSmall);
        sEyeEnlarging = PreferenceUtil.getFloatParam(BeautificationParam.EYE_ENLARGING, mEyeEnlarging);
        sIntensityChin = PreferenceUtil.getFloatParam(BeautificationParam.INTENSITY_CHIN, mIntensityChin);
        sIntensityForehead = PreferenceUtil.getFloatParam(BeautificationParam.INTENSITY_FOREHEAD, mIntensityForehead);
        sIntensityNose = PreferenceUtil.getFloatParam(BeautificationParam.INTENSITY_NOSE, mIntensityNose);
        sIntensityMouth = PreferenceUtil.getFloatParam(BeautificationParam.INTENSITY_MOUTH, mIntensityMouth);

        bindParams();
    }

    /**
     * 美颜效果是否打开
     *
     * @param checkId
     * @return
     */
    public static boolean isOpen(int checkId) {
        if (checkId == R.id.beauty_box_blur_level) {
            return sBlurLevel > 0;
        } else if (checkId == R.id.beauty_box_color_level) {
            return sColorLevel > 0;
        } else if (checkId == R.id.beauty_box_red_level) {
            return sRedLevel > 0;
        } else if (checkId == R.id.beauty_box_pouch) {
            return sMicroPouch > 0;
        } else if (checkId == R.id.beauty_box_nasolabial) {
            return sMicroNasolabialFolds > 0;
        } else if (checkId == R.id.beauty_box_eye_bright) {
            return sEyeBright > 0;
        } else if (checkId == R.id.beauty_box_tooth_whiten) {
            return sToothWhiten != 0;
        } else if (checkId == R.id.beauty_box_eye_enlarge) {
            return sEyeEnlarging > 0;
        } else if (checkId == R.id.beauty_box_cheek_thinning) {
            return sCheekThinning > 0;
        } else if (checkId == R.id.beauty_box_cheek_narrow) {
            return sCheekNarrow > 0;
        } else if (checkId == R.id.beauty_box_cheek_v) {
            return sCheekV > 0;
        } else if (checkId == R.id.beauty_box_cheek_small) {
            return sCheekSmall > 0;
        } else if (checkId == R.id.beauty_box_intensity_chin) {
            return !DecimalUtils.floatEquals(sIntensityChin, 0.5f);
        } else if (checkId == R.id.beauty_box_intensity_forehead) {
            return !DecimalUtils.floatEquals(sIntensityForehead, 0.5f);
        } else if (checkId == R.id.beauty_box_intensity_nose) {
            return sIntensityNose > 0;
        } else if (checkId == R.id.beauty_box_intensity_mouth) {
            return !DecimalUtils.floatEquals(sIntensityMouth, 0.5f);
        } else if (checkId == R.id.beauty_box_smile) {
            return sMicroSmile > 0;
        } else if (checkId == R.id.beauty_box_canthus) {
            return sMicroCanthus > 0;
        } else if (checkId == R.id.beauty_box_philtrum) {
            return !DecimalUtils.floatEquals(sMicroPhiltrum, 0.5f);
        } else if (checkId == R.id.beauty_box_long_nose) {
            return !DecimalUtils.floatEquals(sMicroLongNose, 0.5f);
        } else if (checkId == R.id.beauty_box_eye_space) {
            return !DecimalUtils.floatEquals(sMicroEyeSpace, 0.5f);
        } else if (checkId == R.id.beauty_box_eye_rotate) {
            return !DecimalUtils.floatEquals(sMicroEyeRotate, 0.5f);
        }
        return true;
    }

    /**
     * 获取美颜的参数值
     *
     * @param checkId
     * @return
     */
    public static float getValue(int checkId) {
        if (checkId == R.id.beauty_box_blur_level) {
            return sBlurLevel;
        } else if (checkId == R.id.beauty_box_color_level) {
            return sColorLevel;
        } else if (checkId == R.id.beauty_box_red_level) {
            return sRedLevel;
        } else if (checkId == R.id.beauty_box_pouch) {
            return sMicroPouch;
        } else if (checkId == R.id.beauty_box_nasolabial) {
            return sMicroNasolabialFolds;
        } else if (checkId == R.id.beauty_box_eye_bright) {
            return sEyeBright;
        } else if (checkId == R.id.beauty_box_tooth_whiten) {
            return sToothWhiten;
        } else if (checkId == R.id.beauty_box_eye_enlarge) {
            return sEyeEnlarging;
        } else if (checkId == R.id.beauty_box_cheek_thinning) {
            return sCheekThinning;
        } else if (checkId == R.id.beauty_box_cheek_narrow) {
            return sCheekNarrow;
        } else if (checkId == R.id.beauty_box_cheek_v) {
            return sCheekV;
        } else if (checkId == R.id.beauty_box_cheek_small) {
            return sCheekSmall;
        } else if (checkId == R.id.beauty_box_intensity_chin) {
            return sIntensityChin;
        } else if (checkId == R.id.beauty_box_intensity_forehead) {
            return sIntensityForehead;
        } else if (checkId == R.id.beauty_box_intensity_nose) {
            return sIntensityNose;
        } else if (checkId == R.id.beauty_box_intensity_mouth) {
            return sIntensityMouth;
        } else if (checkId == R.id.beauty_box_smile) {
            return sMicroSmile;
        } else if (checkId == R.id.beauty_box_canthus) {
            return sMicroCanthus;
        } else if (checkId == R.id.beauty_box_philtrum) {
            return sMicroPhiltrum;
        } else if (checkId == R.id.beauty_box_long_nose) {
            return sMicroLongNose;
        } else if (checkId == R.id.beauty_box_eye_space) {
            return sMicroEyeSpace;
        } else if (checkId == R.id.beauty_box_eye_rotate) {
            return sMicroEyeRotate;
        }
        return 0;
    }

    /**
     * 设置美颜的参数值
     *
     * @param checkId
     * @param value
     */
    public static void setValue(int checkId, float value) {
        if (checkId == R.id.beauty_box_blur_level) {
            sBlurLevel = value;
        } else if (checkId == R.id.beauty_box_color_level) {
            sColorLevel = value;
        } else if (checkId == R.id.beauty_box_red_level) {
            sRedLevel = value;
        } else if (checkId == R.id.beauty_box_pouch) {
            sMicroPouch = value;
        } else if (checkId == R.id.beauty_box_nasolabial) {
            sMicroNasolabialFolds = value;
        } else if (checkId == R.id.beauty_box_eye_bright) {
            sEyeBright = value;
        } else if (checkId == R.id.beauty_box_tooth_whiten) {
            sToothWhiten = value;
        } else if (checkId == R.id.beauty_box_eye_enlarge) {
            sEyeEnlarging = value;
        } else if (checkId == R.id.beauty_box_cheek_thinning) {
            sCheekThinning = value;
        } else if (checkId == R.id.beauty_box_cheek_v) {
            sCheekV = value;
        } else if (checkId == R.id.beauty_box_cheek_narrow) {
            sCheekNarrow = value;
        } else if (checkId == R.id.beauty_box_cheek_small) {
            sCheekSmall = value;
        } else if (checkId == R.id.beauty_box_intensity_chin) {
            sIntensityChin = value;
        } else if (checkId == R.id.beauty_box_intensity_forehead) {
            sIntensityForehead = value;
        } else if (checkId == R.id.beauty_box_intensity_nose) {
            sIntensityNose = value;
        } else if (checkId == R.id.beauty_box_intensity_mouth) {
            sIntensityMouth = value;
        } else if (checkId == R.id.beauty_box_smile) {
            sMicroSmile = value;

            sMicroCanthus = value;
        } else if (checkId == R.id.beauty_box_canthus) {
            sMicroCanthus = value;
        } else if (checkId == R.id.beauty_box_philtrum) {
            sMicroPhiltrum = value;
        } else if (checkId == R.id.beauty_box_long_nose) {
            sMicroLongNose = value;
        } else if (checkId == R.id.beauty_box_eye_space) {
            sMicroEyeSpace = value;
        } else if (checkId == R.id.beauty_box_eye_rotate) {
            sMicroEyeRotate = value;
        }
    }

    /**
     * 默认的美型参数是否被修改过
     *
     * @return
     */
    public static boolean checkIfFaceShapeChanged() {
        if (Float.compare(sCheekNarrow, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_narrow)) != 0) {
            return true;
        }
        if (Float.compare(sCheekSmall, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_small)) != 0) {
            return true;
        }
        if (Float.compare(sCheekV, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_v)) != 0) {
            return true;
        }
        if (Float.compare(sCheekThinning, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_thinning)) != 0) {
            return true;
        }
        if (Float.compare(sEyeEnlarging, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_enlarge)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityNose, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_nose)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityChin, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_chin)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityMouth, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_mouth)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityForehead, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_forehead)) != 0) {
            return true;
        }
        if (Float.compare(sMicroCanthus, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_canthus)) != 0) {
            return true;
        }
        if (Float.compare(sMicroEyeSpace, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_space)) != 0) {
            return true;
        }
        if (Float.compare(sMicroEyeRotate, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_rotate)) != 0) {
            return true;
        }
        if (Float.compare(sMicroLongNose, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_long_nose)) != 0) {
            return true;
        }
        if (Float.compare(sMicroPhiltrum, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_philtrum)) != 0) {
            return true;
        }
        if (Float.compare(sMicroSmile, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_smile)) != 0) {
            return true;
        }
        return false;
    }

    /**
     * 默认的美肤参数是否被修改过
     *
     * @return
     */
    public static boolean checkIfFaceSkinChanged() {
        if (Float.compare(sColorLevel, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_color_level)) != 0) {
            return true;
        }
        if (Float.compare(sRedLevel, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_red_level)) != 0) {
            return true;
        }
        if (Float.compare(sMicroPouch, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_pouch)) != 0) {
            return true;
        }
        if (Float.compare(sMicroNasolabialFolds, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_nasolabial)) != 0) {
            return true;
        }
        if (Float.compare(sEyeBright, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_eye_bright)) != 0) {
            return true;
        }
        if (Float.compare(sToothWhiten, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_tooth_whiten)) != 0) {
            return true;
        }
        if (Float.compare(sBlurLevel, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_blur_level)) != 0) {
            return true;
        }
        return false;
    }

    /**
     * 恢复美型的默认值
     */
    public static void recoverFaceShapeToDefValue() {
        sCheekNarrow = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_narrow);
        sCheekSmall = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_small);
        sCheekV = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_v);
        sCheekThinning = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_thinning);
        sEyeEnlarging = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_enlarge);
        sIntensityNose = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_nose);
        sIntensityMouth = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_mouth);
        sIntensityForehead = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_forehead);
        sIntensityChin = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_chin);
        sMicroCanthus = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_canthus);
        sMicroEyeSpace = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_space);
        sMicroEyeRotate = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_rotate);
        sMicroLongNose = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_long_nose);
        sMicroPhiltrum = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_philtrum);
        sMicroSmile = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_smile);
    }

    /**
     * 恢复美肤的默认值
     */
    public static void recoverFaceSkinToDefValue() {
        sBlurLevel = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_blur_level);
        sColorLevel = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_color_level);
        sRedLevel = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_red_level);
        sMicroPouch = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_pouch);
        sMicroNasolabialFolds = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_nasolabial);
        sEyeBright = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_eye_bright);
        sToothWhiten = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_tooth_whiten);
    }

    public static void saveParams() {
        //从内存中读取参数保存到PreferenceUtil中
        PreferenceUtil.setFilterParams(sFilterLevel);
        PreferenceUtil.setFilter(sFilter);

        PreferenceUtil.setParam(BeautificationParam.COLOR_LEVEL, sColorLevel);
        PreferenceUtil.setParam(BeautificationParam.BLUR_LEVEL, sBlurLevel);
        PreferenceUtil.setParam(BeautificationParam.RED_LEVEL, sRedLevel);
        PreferenceUtil.setParam(BeautificationParam.EYE_BRIGHT, sEyeBright);
        PreferenceUtil.setParam(BeautificationParam.TOOTH_WHITEN, sToothWhiten);

        PreferenceUtil.setParam(BeautificationParam.REMOVE_POUCH_STRENGTH, sMicroPouch);
        PreferenceUtil.setParam(BeautificationParam.REMOVE_NASOLABIAL_FOLDS_STRENGTH, sMicroNasolabialFolds);
        PreferenceUtil.setParam(BeautificationParam.INTENSITY_SMILE, sMicroSmile);
        PreferenceUtil.setParam(BeautificationParam.INTENSITY_CANTHUS, sMicroCanthus);
        PreferenceUtil.setParam(BeautificationParam.INTENSITY_PHILTRUM, sMicroPhiltrum);
        PreferenceUtil.setParam(BeautificationParam.INTENSITY_LONG_NOSE, sMicroLongNose);
        PreferenceUtil.setParam(BeautificationParam.INTENSITY_EYE_SPACE, sMicroEyeSpace);
        PreferenceUtil.setParam(BeautificationParam.INTENSITY_EYE_ROTATE, sMicroEyeRotate);

        PreferenceUtil.setParam(BeautificationParam.CHEEK_THINNING, sCheekThinning);
        PreferenceUtil.setParam(BeautificationParam.CHEEK_V, sCheekV);
        PreferenceUtil.setParam(BeautificationParam.CHEEK_NARROW, sCheekNarrow);
        PreferenceUtil.setParam(BeautificationParam.CHEEK_SMALL, sCheekSmall);
        PreferenceUtil.setParam(BeautificationParam.EYE_ENLARGING, sEyeEnlarging);
        PreferenceUtil.setParam(BeautificationParam.INTENSITY_CHIN, sIntensityChin);
        PreferenceUtil.setParam(BeautificationParam.INTENSITY_FOREHEAD, sIntensityForehead);
        PreferenceUtil.setParam(BeautificationParam.INTENSITY_NOSE, sIntensityNose);
        PreferenceUtil.setParam(BeautificationParam.INTENSITY_MOUTH, sIntensityMouth);

    }

    private static void bindParams() {
        // 美型
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_thinning, mCheekThinning);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_narrow, mCheekNarrow);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_small, mCheekSmall);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_v, mCheekV);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_eye_enlarge, mEyeEnlarging);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_chin, mIntensityChin);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_forehead, mIntensityForehead);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_nose, mIntensityNose);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_mouth, mIntensityMouth);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_canthus, sMicroCanthus);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_eye_space, mIntensityEyeSpace);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_eye_rotate, mIntensityEyeRotate);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_long_nose, mIntensityLongNose);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_philtrum, mIntensityPhiltrum);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_smile, mIntensitySmile);

        // 美肤
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_blur_level, mBlurLevel);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_color_level, mColorLevel);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_red_level, mRedLevel);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_pouch, mRemovePouchStrength);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_nasolabial, mRemoveNasolabialFoldsStrength);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_eye_bright, mEyeBright);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_tooth_whiten, mToothWhiten);


    }

}
