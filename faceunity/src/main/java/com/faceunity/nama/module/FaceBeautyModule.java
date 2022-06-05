package com.faceunity.nama.module;

import android.content.Context;

import com.faceunity.nama.param.BeautificationParam;
import com.faceunity.nama.utils.BundleUtils;
import com.faceunity.nama.utils.LogUtils;
import com.faceunity.nama.utils.PreferenceUtil;
import com.faceunity.nama.utils.ThreadHelper;
import com.faceunity.wrapper.faceunity;

/**
 * 美颜模块
 *
 * @author Richie on 2020.07.07
 */
public class FaceBeautyModule extends AbstractEffectModule implements IFaceBeautyModule {
    private static final String TAG = "FaceBeautyModule";
    /* 美颜和滤镜的默认参数，具体的参数定义，请看 BeautificationParam 类 */
    private int mIsBeautyOn = 1; // 美颜开启
    private String mFilterName = BeautificationParam.ZIRAN_1;// 滤镜名称：自然 1
    private float mFilterLevel = 0.4f;// 滤镜程度
    private float mBlurLevel = 0.7f;// 磨皮程度
    private float mColorLevel = 0.3f;// 美白
    private float mRedLevel = 0.3f;// 红润
    private float mEyeBright = 0.0f;// 亮眼
    private float mToothWhiten = 0.0f;// 美牙
    private float mCheekThinning = 0f;// 瘦脸
    private float mCheekV = 0.5f;// V脸
    private float mCheekNarrow = 0f;// 窄脸
    private float mCheekSmall = 0f;// 小脸
    private float mEyeEnlarging = 0.4f;// 大眼
    private float mIntensityChin = 0.3f;// 下巴
    private float mIntensityForehead = 0.3f;// 额头
    private float mIntensityMouth = 0.4f;// 嘴形
    private float mIntensityNose = 0.5f;// 瘦鼻
    private float mRemovePouchStrength = 0f; // 去黑眼圈
    private float mRemoveNasolabialFoldsStrength = 0f; // 去法令纹
    private float mIntensitySmile = 0f; // 微笑嘴角
    private float mIntensityCanthus = 0f; // 开眼角
    private float mIntensityPhiltrum = 0.5f; // 调节人中
    private float mIntensityLongNose = 0.5f; // 鼻子长度
    private float mIntensityEyeSpace = 0.5f; // 眼睛间距
    private float mIntensityEyeRotate = 0.5f; // 眼睛角度

    @Override
    public void create(final Context context, final ModuleCallback moduleCallback) {
        if (mItemHandle > 0) {
            return;
        }
        mRenderEventQueue = new RenderEventQueue();
        ThreadHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final int itemFaceBeauty = BundleUtils.loadItem(context, "graphics/face_beautification.bundle");
                if (itemFaceBeauty <= 0) {
                    LogUtils.warn(TAG, "load face beauty item failed %d", itemFaceBeauty);
                    return;
                }
                mItemHandle = itemFaceBeauty;

                setIsBeautyOn(mIsBeautyOn);
                setFilterName(mFilterName);
                setFilterLevel(mFilterLevel);
                mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.HEAVY_BLUR, 0.0);
                mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.BLUR_TYPE, 2.0); // 精细磨皮
                setBlurLevel(mBlurLevel);
                setColorLevel(mColorLevel);
                setRedLevel(mRedLevel);
                setEyeBright(mEyeBright);
                setToothWhiten(mToothWhiten);
                mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.FACE_SHAPE, 4.0); // 精细变形
                mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.FACE_SHAPE_LEVEL, 1.0); // 精细变形程度
                setEyeEnlarging(mEyeEnlarging);
                setCheekThinning(mCheekThinning);
                setCheekNarrow(mCheekNarrow);
                setCheekSmall(mCheekSmall);
                setCheekV(mCheekV);
                setIntensityNose(mIntensityNose);
                setIntensityChin(mIntensityChin);
                setIntensityForehead(mIntensityForehead);
                setIntensityMouth(mIntensityMouth);
                setRemovePouchStrength(mRemovePouchStrength);
                setRemoveNasolabialFoldsStrength(mRemoveNasolabialFoldsStrength);
                setIntensitySmile(mIntensitySmile);
                setIntensityCanthus(mIntensityCanthus);
                setIntensityPhiltrum(mIntensityPhiltrum);
                setIntensityLongNose(mIntensityLongNose);
                setIntensityEyeSpace(mIntensityEyeSpace);
                setIntensityEyeRotate(mIntensityEyeRotate);

                LogUtils.debug(TAG, "face beauty param: isBeautyOn:" + mIsBeautyOn + ", filterName:"
                        + mFilterName + ", filterLevel:" + mFilterLevel + ", blurLevel:" + mBlurLevel + ", colorLevel:"
                        + mColorLevel + ", redLevel:" + mRedLevel + ", eyeBright:" + mEyeBright + ", toothWhiten:"
                        + mToothWhiten + ", eyeEnlarging:" + mEyeEnlarging + ", cheekThinning:" + mCheekThinning + ", cheekNarrow:"
                        + mCheekNarrow + ", cheekSmall:" + mCheekSmall + ", cheekV:" + mCheekV + ", intensityNose:"
                        + mIntensityNose + ", intensityChin:" + mIntensityChin + ", intensityForehead:"
                        + mIntensityForehead + ", intensityMouth:" + mIntensityMouth + ", removePouchStrength:"
                        + mRemovePouchStrength + ", removeNasolabialFoldsStrength:" + mRemoveNasolabialFoldsStrength + ", intensitySmile:"
                        + mIntensitySmile + ", intensityCanthus:" + mIntensityCanthus + ", intensityPhiltrum:"
                        + mIntensityPhiltrum + ", intensityLongNose:" + mIntensityLongNose + ", intensityEyeSpace:"
                        + mIntensityEyeSpace + ", eyeRotate:" + mIntensityEyeRotate);

                if (moduleCallback != null) {
                    moduleCallback.onBundleCreated(itemFaceBeauty);
                }
            }
        });
    }

    @Override
    public void setMaxFaces(final int maxFaces) {
        if (maxFaces <= 0 || mRenderEventQueue == null) {
            return;
        }
        mRenderEventQueue.add(new Runnable() {
            @Override
            public void run() {
                faceunity.fuSetMaxFaces(maxFaces);
                LogUtils.debug(TAG, "setMaxFaces : %d", maxFaces);
            }
        });
    }

    @Override
    public void setIsBeautyOn(int isBeautyOn) {
        if (mIsBeautyOn == isBeautyOn) {
            return;
        }
        mIsBeautyOn = isBeautyOn;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.IS_BEAUTY_ON, isBeautyOn);
        }
    }

    @Override
    public void setFilterName(String name) {
        mFilterName = name;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.FILTER_NAME, name);
        }
    }

    @Override
    public void setFilterLevel(float level) {
        mFilterLevel = level;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.FILTER_LEVEL, level);
        }
    }

    @Override
    public void setBlurLevel(float level) {
        mBlurLevel = level;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.BLUR_LEVEL, 6.0 * level);
            PreferenceUtil.setParam(BeautificationParam.BLUR_LEVEL, mBlurLevel);
        }
    }

    @Override
    public void setColorLevel(float level) {
        mColorLevel = level;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.COLOR_LEVEL, level);
            PreferenceUtil.setParam(BeautificationParam.COLOR_LEVEL, mColorLevel);
        }
    }

    @Override
    public void setRedLevel(float level) {
        mRedLevel = level;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.RED_LEVEL, level);
            PreferenceUtil.setParam(BeautificationParam.RED_LEVEL, mRedLevel);
        }
    }

    @Override
    public void setEyeBright(float level) {
        mEyeBright = level;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.EYE_BRIGHT, level);
            PreferenceUtil.setParam(BeautificationParam.EYE_BRIGHT, mEyeBright);
        }
    }

    @Override
    public void setToothWhiten(float level) {
        mToothWhiten = level;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.TOOTH_WHITEN, level);
            PreferenceUtil.setParam(BeautificationParam.TOOTH_WHITEN, mToothWhiten);
        }
    }

    @Override
    public void setEyeEnlarging(float level) {
        mEyeEnlarging = level;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.EYE_ENLARGING, level);
            PreferenceUtil.setParam(BeautificationParam.EYE_ENLARGING, mEyeEnlarging);
        }
    }

    @Override
    public void setCheekThinning(float level) {
        mCheekThinning = level;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.CHEEK_THINNING, level);
            PreferenceUtil.setParam(BeautificationParam.CHEEK_THINNING, mCheekThinning);
        }
    }

    @Override
    public void setCheekNarrow(float level) {
        mCheekNarrow = level;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.CHEEK_NARROW, level);
            PreferenceUtil.setParam(BeautificationParam.CHEEK_NARROW, mCheekNarrow);
        }
    }

    @Override
    public void setCheekSmall(float level) {
        mCheekSmall = level;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.CHEEK_SMALL, level);
            PreferenceUtil.setParam(BeautificationParam.CHEEK_SMALL, mCheekSmall);
        }
    }

    @Override
    public void setCheekV(float level) {
        mCheekV = level;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.CHEEK_V, level);
            PreferenceUtil.setParam(BeautificationParam.CHEEK_V, mCheekV);
        }
    }

    @Override
    public void setIntensityChin(float intensity) {
        mIntensityChin = intensity;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.INTENSITY_CHIN, intensity);
            PreferenceUtil.setParam(BeautificationParam.INTENSITY_CHIN, mIntensityChin);
        }
    }

    @Override
    public void setIntensityForehead(float intensity) {
        mIntensityForehead = intensity;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.INTENSITY_FOREHEAD, intensity);
            PreferenceUtil.setParam(BeautificationParam.INTENSITY_FOREHEAD, mIntensityForehead);
        }
    }

    @Override
    public void setIntensityNose(float intensity) {
        mIntensityNose = intensity;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.INTENSITY_NOSE, intensity);
            PreferenceUtil.setParam(BeautificationParam.INTENSITY_NOSE, mIntensityNose);
        }
    }

    @Override
    public void setIntensityMouth(float intensity) {
        mIntensityMouth = intensity;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.INTENSITY_MOUTH, intensity);
            PreferenceUtil.setParam(BeautificationParam.INTENSITY_MOUTH, mIntensityMouth);
        }
    }

    @Override
    public void setRemovePouchStrength(float strength) {
        mRemovePouchStrength = strength;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.REMOVE_POUCH_STRENGTH, strength);
            PreferenceUtil.setParam(BeautificationParam.REMOVE_POUCH_STRENGTH, mRemovePouchStrength);
        }
    }

    @Override
    public void setRemoveNasolabialFoldsStrength(float strength) {
        mRemoveNasolabialFoldsStrength = strength;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.REMOVE_NASOLABIAL_FOLDS_STRENGTH, strength);
            PreferenceUtil.setParam(BeautificationParam.REMOVE_NASOLABIAL_FOLDS_STRENGTH, mRemoveNasolabialFoldsStrength);
        }
    }

    @Override
    public void setIntensitySmile(float intensity) {
        mIntensitySmile = intensity;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.INTENSITY_SMILE, intensity);
            PreferenceUtil.setParam(BeautificationParam.INTENSITY_SMILE, mIntensitySmile);
        }
    }

    @Override
    public void setIntensityCanthus(float intensity) {
        mIntensityCanthus = intensity;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.INTENSITY_CANTHUS, intensity);
            PreferenceUtil.setParam(BeautificationParam.INTENSITY_CANTHUS, mIntensityCanthus);
        }
    }

    @Override
    public void setIntensityPhiltrum(float intensity) {
        mIntensityPhiltrum = intensity;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.INTENSITY_PHILTRUM, intensity);
            PreferenceUtil.setParam(BeautificationParam.INTENSITY_PHILTRUM, mIntensityPhiltrum);
        }
    }

    @Override
    public void setIntensityLongNose(float intensity) {
        mIntensityLongNose = intensity;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.INTENSITY_LONG_NOSE, intensity);
            PreferenceUtil.setParam(BeautificationParam.INTENSITY_LONG_NOSE, mIntensityLongNose);
        }
    }

    @Override
    public void setIntensityEyeSpace(float intensity) {
        mIntensityEyeSpace = intensity;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.INTENSITY_EYE_SPACE, intensity);
            PreferenceUtil.setParam(BeautificationParam.INTENSITY_EYE_SPACE, mIntensityEyeSpace);
        }
    }

    @Override
    public void setIntensityEyeRotate(float intensity) {
        mIntensityEyeRotate = intensity;
        if (mRenderEventQueue != null) {
            mRenderEventQueue.addItemSetParamEvent(mItemHandle, BeautificationParam.INTENSITY_EYE_ROTATE, intensity);
            PreferenceUtil.setParam(BeautificationParam.INTENSITY_EYE_ROTATE, mIntensityEyeRotate);
        }
    }

}