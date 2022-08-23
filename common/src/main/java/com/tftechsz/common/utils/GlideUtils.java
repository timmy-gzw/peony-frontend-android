package com.tftechsz.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.other.GlideRoundTransform;
import com.tftechsz.common.other.GlideRoundTransform2;
import com.tftechsz.common.widget.GlideCircleTransformWithBorderUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class GlideUtils {

    private Context mContext;

    public static int getChannelLevel(int level) {
        return getResId("" + level, R.drawable.class);
    }

    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return R.drawable.ic_level_0_9;
        }
    }

    /**
     * 获取用户土豪等级
     */
    public static String getUserLevel(int level) {
        if (level <= 9) {
            return "ic_level_0_9";
        } else if (level <= 20) {
            return "ic_level_10_20";
        } else if (level <= 30) {
            return "ic_level_21_30";
        } else if (level <= 40) {
            return "ic_level_31_40";
        } else if (level <= 44) {
            return "ic_level_41_44";
        } else if (level <= 48) {
            return "ic_level_45_48";
        } else if (level <= 52) {
            return "ic_level_49_52";
        } else if (level <= 56) {
            return "ic_level_53_56";
        } else {
            return "ic_level_57";
        }
    }

    /**
     * @return true 表示正常
     */
    private static boolean checkContext(ImageView iv) {
        Context context = iv.getContext();
        if (context instanceof Activity) {
            if (((Activity) context).isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true 表示正常
     */
    private static boolean checkContext(Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return !activity.isDestroyed();
        }
        return true;
    }


    /**
     * 加载图片
     *
     * @param context
     * @param iv
     * @param url
     * @param placeImage
     */
    public static void loadImage(Context context, ImageView iv, String url, int placeImage) {
        if (!checkContext(context)) {
            return;
        }
        RequestOptions options = RequestOptions
                .noTransformation()
                .error(placeImage)
                .placeholder(placeImage)
                //.fitCenter()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(iv);

    }

    public static void loadImage(Context context, ImageView iv, int url) {
        if (!checkContext(context)) return;
        RequestOptions options = RequestOptions
                .noTransformation()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(iv);
    }

    public static void loadImage(Context context, ImageView iv, int url, int place) {
        if (!checkContext(context)) return;
        RequestOptions options = RequestOptions
                .noTransformation()
                .placeholder(place)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(iv);
    }


    public static void loadImageNew(Context context, ImageView iv, String url) {
        if (!checkContext(context)) return;
        RequestOptions options = RequestOptions
                .noTransformation()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(iv);
    }


    public static void loadImageNew(Context context, ImageView iv, String url, int radius) {
        if (!checkContext(context)) return;
        RequestOptions options = RequestOptions
                .noTransformation()
                .transform(new GlideRoundTransform(context, radius))
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(iv);
    }

    public static void loadImage(Context context, ImageView iv, String url) {
        if (!checkContext(context)) return;
        RequestOptions options = RequestOptions
                .noTransformation()
                .error(R.mipmap.ic_default_avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(iv);

    }

    public static void loadPicImage(Context context, ImageView iv, String url) {
        if (!checkContext(context)) return;
        RequestOptions options = RequestOptions
                .noTransformation()
                .error(R.mipmap.ic_default_avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(context).load(url).apply(options).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NotNull Drawable drawable, Transition<? super Drawable> transition) {
                iv.setImageDrawable(drawable);
            }
        });
    }

    /**
     * 加载圆角图片
     *
     * @param context
     * @param iv
     * @param url
     * @param placeImage
     */
    public static void loadRoundImage(Context context, ImageView iv, String url, int placeImage, int errorImage, int radius) {
        if (!checkContext(context)) return;
        RequestOptions options = new RequestOptions()
                .transform(new GlideRoundTransform(context, radius))
                .error(errorImage == 0 ? R.mipmap.ic_default_avatar : errorImage)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(placeImage == 0 ? R.mipmap.ic_default_avatar : placeImage);
        Glide.with(context).load(url)
                .apply(options)
//                .thumbnail(loadTransform(context, placeImage, radius))
                .into(iv);
    }


    /**
     * 加载圆形图片
     */
    public static void loadCircleImage(Context context, ImageView iv, String url) {
        loadCircleImage(context, iv, url, R.drawable.party_ic_seat);
    }

    /**
     * 加载圆形图片
     */
    public static void loadCircleImage(Context context, ImageView iv, String url, int placeImage) {
        if (!checkContext(context)) return;
        RequestOptions options = new RequestOptions()
                .transform(new CircleCrop())
                .error(placeImage)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(placeImage);
        Glide.with(context).load(url)
                .apply(options)
                .into(iv);
    }


    /**
     * 加载圆角图片
     *
     * @param context
     * @param iv
     * @param url
     */
    public static void loadTopRoundImage(Context context, ImageView iv, String url) {
        if (!checkContext(context)) return;
        RequestOptions options = new RequestOptions()
                .transform(new GlideRoundTransform2(context, 10))
                .error(R.mipmap.ic_default_avatar)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.mipmap.ic_default_avatar);
        Glide.with(context).load(url)
                .apply(options)
//                .thumbnail(loadTransform(context, placeImage, radius))
                .into(iv);
    }

    /**
     * 加载圆角图片
     *
     * @param context
     * @param iv
     * @param url
     */
    public static void loadTopRoundImageError(Context context, ImageView iv, String url) {
        if (!checkContext(context)) return;
        RequestOptions options = new RequestOptions()
                .transform(new GlideRoundTransform2(context, 10))
                .error(R.drawable.party_bg_default)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.party_bg_default);
        Glide.with(context).load(url)
                .apply(options)
                .thumbnail(loadTransform(context, R.drawable.party_bg_default, 10))
                .into(iv);
    }

    private static RequestBuilder<Drawable> loadTransform(Context context, @DrawableRes int placeholderId, int radius) {
        return Glide.with(context)
                .load(placeholderId)
                .apply(new RequestOptions().centerCrop()
                        .transform(new GlideRoundTransform(context, radius)));
    }


    public static void loadRoundImage(Context context, ImageView iv, String url) {
        loadRoundImage(context, iv, url, 0, 0, 10);
    }

    public static void loadRoundImageRadius(Context context, ImageView iv, String url) {
        loadRoundImage(context, iv, url, 0, 0, 6);
    }


    public static void loadRoundImage(Context context, ImageView iv, String url, int radius) {
        loadRoundImage(context, iv, url, 0, 0, radius);
    }

    public static void loadRoundAvatarImage(Context context, ImageView iv, String url, int pic) {
        loadRoundImage(context, iv, url, pic, 0, 16);
    }

    public static void loadRoundAvatarImage(Context context, ImageView iv, String url, int pic, int errorPic) {
        loadRoundImage(context, iv, url, pic, errorPic, 10);
    }

    /**
     * 加载高斯模糊图片
     *
     * @param i
     * @param url
     * @param placeImage
     */
    public static void loadImageGaussian(Context context, ImageView i, String url, int placeImage) {
        if (!checkContext(context)) return;
        Glide.with(context)
                .load(url)
                .apply(RequestOptions
                        .bitmapTransform(new BlurTransformation(12, 3)))
                .placeholder(placeImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(i);
    }

    /**
     * 加载高斯模糊图片
     *
     * @param i
     * @param url
     */
    public static void loadImageGaussian(Context context, ImageView i, String url) {
        if (!checkContext(context)) return;
        MultiTransformation multi = new MultiTransformation(
                new BlurTransformation(12, 3),
                new GlideRoundTransform(context, 6)
        );
        Glide.with(i.getContext())
                .load(url)
                .apply(RequestOptions.bitmapTransform(multi))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .dontAnimate()
                .placeholder(R.mipmap.ic_default_avatar)
                .into(i);
    }


    public static void loadGif(Context context, ImageView i, int url) {
        if (!checkContext(context)) return;
        RequestOptions requestOptions = new RequestOptions().centerCrop();
        Glide.with(context)
                .asGif()
                .load(url)
                .apply(requestOptions)
                .into(i);
    }


    public static void loadRouteImage(Context context, ImageView iv, String url) {
        loadImage(context, iv, url, R.mipmap.ic_default_avatar);
    }

    public static void loadRouteImage(Context context, ImageView iv, String url, int placeImage) {
        loadImage(context, iv, url, placeImage);
    }

    public static void loadUserIcon(ImageView iv, String url) {
        loadImage(BaseApplication.getInstance(), iv, url, R.mipmap.ic_default_avatar);
    }

    /**
     * 图片下载
     *
     * @param context
     * @param url
     * @param listener
     */
    public static void downloadImage(Context context, String url, DownloadBitmapSuccessListener listener) {
        RequestOptions options = new RequestOptions()
                .fitCenter()
                .error(R.mipmap.ic_default_avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .dontAnimate();
        Glide.with(context).asBitmap().load(url).apply(options)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        listener.success(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public static void downloadImage(Context context, String url, int width, int height, DownloadBitmapSuccessListener listener) {
        RequestOptions requestOptions = new RequestOptions().centerCrop()
                .error(R.mipmap.ic_default_avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .transform(new FitCenter())
                .format(DecodeFormat.PREFER_RGB_565)
                .priority(Priority.LOW)
                .override(width, height)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(context).asBitmap().load(url).apply(requestOptions)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        listener.success(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }


    public static void loadBackGround(Context context, String url, View view) {
        if (!checkContext(context)) return;
        Glide.with(BaseApplication.getInstance())
                .load(url)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        view.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }


    public interface DownloadBitmapSuccessListener {
        void success(Bitmap bp);
    }

    public static void loadBorderImg(Context context, String path, ImageView avatarImageView) {
        if (!checkContext(context)) return;
        //设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(16);
        //通过RequestOptions扩展功能
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);

        Glide.with(context)
                .load(path)
                .apply(options.placeholder(R.drawable.ic_default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new GlideCircleTransformWithBorderUtils(context, 2, Color.parseColor("#D49D1F"))))
                .into(avatarImageView);
//        GlideCircleTransformWithBorderUtils(context,边框,颜色)
    }
}
