
   -keepattributes EnclosingMethod
   #指定代码的压缩级别
    -optimizationpasses 5

    #包明不混合大小写
    -dontusemixedcaseclassnames

    #不去忽略非公共的库类
    -dontskipnonpubliclibraryclasses
     #优化  不优化输入的类文件
    -dontoptimize

     #预校验
    -dontpreverify

     #混淆时是否记录日志
    -verbose

     # 混淆时所采用的算法
    -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

    #保护注解
    -keepattributes *Annotation*
    # 保持哪些类不被混淆
    -keep public class * extends android.app.Fragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    -keep public class com.android.vending.licensing.ILicensingService
    #如果有引用v4包可以添加下面这行
    -keep public class * extends android.support.v4.app.Fragment

    #忽略警告
    -ignorewarnings

    ##记录生成的日志数据,gradle build时在本项目根目录输出##

    #apk 包内所有 class 的内部结构
    -dump class_files.txt
    #未混淆的类和成员
    -printseeds seeds.txt
    #列出从 apk 中删除的代码
    -printusage unused.txt
    #混淆前后的映射
    -printmapping mapping.txt
    ########记录生成的日志数据，gradle build时 在本项目根目录输出-end######
    #####混淆保护自己项目的部分代码以及引用的第三方jar包library#######

    #如果不想混淆 keep 掉
    -keep class com.lippi.recorder.iirfilterdesigner.** {*; }
    #友盟
    -keep class com.umeng.commonsdk.** {*;}
    -keepclassmembers class * {
     public <init> (org.json.JSONObject);
    }
    -keep,allowshrinking class org.android.agoo.service.* {
        public <fields>;
        public <methods>;
    }
    -keep,allowshrinking class com.umeng.message.* {
        public <fields>;
        public <methods>;
    }
    -keep public class com.xinda.loong.R$*{
       public static final int *;
    }

    -dontwarn com.umeng.**
    -dontwarn com.taobao.**
    -dontwarn anet.channel.**
    -dontwarn anetwork.channel.**
    -dontwarn org.android.**
    -dontwarn org.apache.thrift.**
    -dontwarn com.xiaomi.**
    -dontwarn com.huawei.**
    -dontwarn com.meizu.**

    -keepattributes *Annotation*

    -keep class com.taobao.** {*;}
    -keep class org.android.** {*;}
    -keep class anet.channel.** {*;}
    -keep class com.umeng.** {*;}
    -keep class com.xiaomi.** {*;}
    -keep class com.huawei.** {*;}
    -keep class com.meizu.** {*;}
    -keep class org.apache.thrift.** {*;}

    -keep class com.alibaba.sdk.android.**{*;}
    -keep class com.ut.**{*;}
    -keep class com.ta.**{*;}

    -keep public class **.R$*{
       public static final int *;
    }

   #（可选）避免Log打印输出
   -assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
   }

    #项目特殊处理代码

    #忽略警告
    -dontwarn com.lippi.recorder.utils**
    #保留一个完整的包
    -keep class com.lippi.recorder.utils.** {
        *;
     }
    -keep class  com.lippi.recorder.utils.AudioRecorder{*;}
    #如果引用了v4或者v7包
    -dontwarn android.support.**
    ####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####
    -keep public class * extends android.view.View {
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
        public void set*(...);
    }
    #保持 native 方法不被混淆
    -keepclasseswithmembernames class * {
        native <methods>;
    }
    #保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
    }
    #保持自定义控件类不被混淆
    -keepclassmembers class * extends android.app.Activity {
       public void *(android.view.View);
    }
    #保持 Parcelable 不被混淆
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }
    #保持 Serializable 不被混淆
    -keepnames class * implements java.io.Serializable

    -keep class com.google.gson.examples.android.model.** { *; }
    -keep class com.netease.nim.uikit.common.ConfigInfo
    -keep class com.netease.nim.uikit.common.ChatMsg
    -keep class com.tftechsz.common.base.**{*;}
    -keep class com.tftechsz.common.entity.**{*;}
    -keep class com.tftechsz.common.event.**{*;}
    -keep class com.tftechsz.common.http.**{*;}
    -keep class com.tftechsz.im.model.**{*;}
    -keep class com.tftechsz.main.entity.**{*;}
    -keep class com.tftechsz.home.entity.**{*;}
    -keep class com.tftechsz.family.entity.**{*;}
    -keep class com.tftechsz.party.entity.**{*;}
    -keep class com.tftechsz.mine.entity.**{*;}
    -keep class com.tftechsz.moment.entity.**{*;}
    -keep class com.tftechsz.moment.other.**{*;}
    -keep class com.tftechsz.moment.mvp.entity.**{*;}


    -keepnames class com.tftechsz.common.iservice.UserProviderService
    -keepnames class com.tftechsz.common.iservice.MineService
    -keep class com.tftechsz.mine.serviceImpl.**{*;}
    -keep class com.tftechsz.im.serviceimpl.**{*;}
    -keep class com.tftechsz.home.serviceimpl.**{*;}
    -keep class com.tftechsz.common.iservice.**{*;}




    #保持 Serializable 不被混淆并且enum 类也不被混淆
    -keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        !static !transient <fields>;
        !private <fields>;
        !private <methods>;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }

    -keepclassmembers class * {
        public void *ButtonClicked(android.view.View);
    }

    #不混淆资源类
    -keepclassmembers class **.R$* {
        public static <fields>;
    }

    #如果用用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。
    #gson
    -keepattributes Signature
    # Gson specific classes
    -keep class sun.misc.Unsafe { *; }
    # Application classes that will be serialized/deserialized over Gson

    #RxJava & RxAndroid
    -dontwarn sun.misc.**
    -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
       long producerIndex;
       long consumerIndex;
     }
    -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
        rx.internal.util.atomic.LinkedQueueNode producerNode;
    }
    -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
        rx.internal.util.atomic.LinkedQueueNode consumerNode;
    }
    # Retrofit
    -dontwarn retrofit2.**
    -keep class retrofit2.** { *; }
    -keepattributes Signature
    -keepattributes Exceptions
    # okhttp
    -dontwarn com.squareup.okhttp.**
    -keep class com.squareup.okhttp.** { *;}
    -dontwarn okhttp3.**
    -dontwarn okio.**
    -dontwarn javax.annotation.**
    #Glide
    -keep class com.bumptech.glide.** {*;}
    -keep public class * implements com.bumptech.glide.module.GlideModule
    -keep public class * extends com.bumptech.glide.module.AppGlideModule
    -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
      **[] $VALUES;
      public *;
     }
     #RecyclerView
     -keep class com.chad.library.adapter.** {
     *;
     }
     -keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
     -keep public class * extends com.chad.library.adapter.base.viewholder.BaseViewHolder{
      <init>(...);
     }
     -keepclassmembers  class **$** extends com.chad.library.adapter.base.viewholder.BaseViewHolder {
          <init>(...);
     }
      -keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
      }
      -keepattributes InnerClasses
      -keep class androidx.** {*;}
      -keep public class * extends androidx.**
      -keep interface androidx.** {*;}
      -dontwarn androidx.**

      -keepnames class * implements android.os.Parcelable {
          public static final ** CREATOR;
      }
      #==================gson && protobuf==========================
      -dontwarn com.google.**
      -keep class com.google.gson.** {*;}
      -keep class com.google.protobuf.** {*;}
      -keep class **$Properties

      #阿里云上传
      -keep class com.alibaba.sdk.android.oss.** { *; }
      -dontwarn okio.**
      -dontwarn org.apache.commons.codec.binary.**

      -keep class com.tftechsz.common.utils.** { *; }



      #支付宝
      -keep class com.alipay.android.app.IAlixPay{*;}
      -keep class com.alipay.android.app.IAlixPay$Stub{*;}
      -keep class com.alipay.android.app.IRemoteServiceCallback{*;}
      -keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
      -keep class com.alipay.sdk.app.PayTask{ public *;}
      -keep class com.alipay.sdk.app.AuthTask{ public *;}
      -keep class com.alipay.sdk.app.H5PayCallback {
          <fields>;
          <methods>;
      }
      -keep class com.alipay.android.phone.mrpc.core.** { *; }
      -keep class com.alipay.apmobilesecuritysdk.** { *; }
      -keep class com.alipay.mobile.framework.service.annotation.** { *; }
      -keep class com.alipay.mobilesecuritysdk.face.** { *; }
      -keep class com.alipay.tscenter.biz.rpc.** { *; }
      -keep class org.json.alipay.** { *; }
      -keep class com.alipay.tscenter.** { *; }
      -keep class com.ta.utdid2.** { *;}
      -keep class com.ut.device.** { *;}

     #沉浸式
     -keep class com.gyf.barlibrary.* {*;}

     #腾讯
     -dontwarn com.tencent.mm.**
     -keep class com.tencent.mm.**{*;}
     -keep class com.tencent.open.TDialog$*
     -keep class com.tencent.open.TDialog$* {*;}
     -keep class com.tencent.open.PKDialog$*
     -keep class com.tencent.open.PKDialog$* {*;}

     #KOTLIN
     -assumenosideeffects class kotlin.jvm.internal.Intrinsics {
         static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
     }
     -dontwarn kotlin.**

     #PictureSelector 2.0
     -keep class com.luck.picture.lib.** { *; }
     -dontwarn com.yalantis.ucrop**
     -keep class com.yalantis.ucrop** { *; }
     -keep interface com.yalantis.ucrop** { *; }

     #百度地图
     -keep class com.baidu.location.** {*;}
     -keep class org.json.** {*;}
     -keep class vi.com.gdi.bgl.android.** {
       *;
       }

    # banner 的混淆代码
    -keep class com.youth.banner.** {
     *;
    }

    # 友盟
    -keep class com.umeng.** {*;}
    -keep class com.uc.** {*;}
    -keepclassmembers class * {
       public <init> (org.json.JSONObject);
    }
    -keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
    }
    -keep public class com.tftechsz.peony.R$*{
    public static final int *;
    }

    -keepattributes *Annotation*
    -keepattributes Exceptions
    -keepattributes InnerClasses
    -keepattributes Signature
    -keepattributes SourceFile,LineNumberTable
    -keep class com.hianalytics.android.**{*;}
    -keep class com.huawei.updatesdk.**{*;}
    -keep class com.huawei.hms.**{*;}

    -keep class com.dueeeke.videoplayer.** { *; }
    -dontwarn com.dueeeke.videoplayer.**

    # IjkPlayer
    -keep class tv.danmaku.ijk.** { *; }
    -dontwarn tv.danmaku.ijk.**

    # ExoPlayer
    -keep class com.google.android.exoplayer2.** { *; }
    -dontwarn com.google.android.exoplayer2.**

    -keep class com.hjq.toast.** {*;}

    #aroute
    -keep public class com.alibaba.android.arouter.routes.**{*;}
    -keep public class com.alibaba.android.arouter.facade.**{*;}
    -keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

    # 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
    -keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

    # 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
    # -keep class * implements com.alibaba.android.arouter.facade.template.IProvider


    ### face unity
    -keep class com.faceunity.auth.AuthPack {*;}

    -dontwarn com.netease.**
    -keep class com.netease.** {*;}

    #如果你使用全文检索插件，需要加入
    -dontwarn org.apache.lucene.**
    -keep class org.apache.lucene.** {*;}
    #如果你开启数据库功能，需要加入
    -keep class net.sqlcipher.** {*;}

    -keep class com.netease.lava.** {*;}
    -keep class com.netease.yunxin.** {*;}
    -keep class com.netease.mobsec.**{*;}
    #云信点播
    -keep class com.netease.neliveplayer.**{*;}
    -keep class com.netease.gslb.** {*;}

    -dontwarn com.alibaba.**
    -keep class com.alibaba.fastjson.** {*;}

    #event
    -keepattributes *Annotation*
    -keepclassmembers class * {
        @org.greenrobot.eventbus.Subscribe <methods>;
    }
    -keep enum org.greenrobot.eventbus.ThreadMode { *; }
    # And if you use AsyncExecutor:
    -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
        <init>(java.lang.Throwable);
    }

    #oaid
    -keep class XI.CA.XI.**{*;}
    -keep class XI.K0.XI.**{*;}
    -keep class XI.XI.K0.**{*;}
    -keep class XI.xo.XI.XI.**{*;}
    -keep class com.zui.opendeviceidlibrary.**{*;}
    -keep class org.json.**{*;}
    -keep public class com.netease.nis.sdkwrapper.Utils {public <methods>;}

    #msa sdk 混淆配置
    -keep class com.bun.** {*;}
    -keep class com.asus.msa.** {*;}
    -keep class com.heytap.openid.** {*;}
    -keep class com.huawei.android.hms.pps.** {*;}
    -keepattributes *Annotation*
    -keep @android.support.annotation.Keep class **{
        @android.support.annotation.Keep <fields>;
        @android.support.annotation.Keep <methods>;
    }

    # freeme
    -keep class com.android.creator.** { *; }
    -keep class com.android.msasdk.** { *; }
    # huawei
    -keep class com.huawei.hms.ads.** { *; }
    -keep interface com.huawei.hms.ads.** {*; }
    # lenovo
    -keep class com.zui.deviceidservice.** { *; }
    -keep class com.zui.opendeviceidlibrary.** { *; }
    # meizu
    -keep class com.meizu.flyme.openidsdk.** { *; }
    # oppo
    -keep class com.heytap.openid.** { *; }
    # samsung
    -keep class com.samsung.android.deviceidservice.** { *; }
    # vivo
    -keep class com.vivo.identifier.** { *; }
    # coolpad
    -keep class com.coolpad.deviceidsupport.** { *; }



    #支付宝实名认证
    -keepclassmembers class ** {
         @com.squareup.otto.Subscribe public *;
         @com.squareup.otto.Produce public *;
    }

    -verbose
    -keep class com.alipay.face.network.model.** {*;}
    -keep class com.alipay.face.api.ZIMCallback {*;}
    -keep class com.alipay.face.api.ZIMFacade {*;}
    -keep class com.alipay.face.api.ZIMFacadeBuilder {*;}
    -keep class com.alipay.face.api.ZIMMetaInfo {*;}
    -keep class com.alipay.face.api.ZIMResponse {*;}
    -keep class com.alipay.face.api.ZIMSession {*;}
    -keep class com.alipay.face.config.**{*;}
    -keep class com.alipay.face.log.RecordBase {*;}
    -keep class com.alipay.face.ui.ToygerWebView {*;}
    -keep class com.alipay.zoloz.toyger.**{*;}
    -keep class com.alipay.deviceid.** {*;}
    -keep class com.alipay.rds.** {*;}
    -keep class com.alipay.android.fintech.log.** {*;}
    -keep class com.alipay.face.log.** {*;}
    -keep class com.alipay.bis.common.service.facade.gw.** {*;}

    -keep class com.alipay.rds.v2.face.RDSClient { *; }
    -keep class com.alipay.rds.constant.* { *; }

    #闪验
    -dontwarn com.cmic.gen.sdk.**
    -keep class com.cmic.gen.sdk.**{*;}
    -dontwarn com.sdk.**
    -keep class com.sdk.** { *;}
    -dontwarn com.unikuwei.mianmi.account.shield.**
    -keep class com.unikuwei.mianmi.account.shield.** {*;}
    -keep class cn.com.chinatelecom.account.api.**{*;}

    -dontwarn com.xiaomi.push.**
    -keep class com.xiaomi.** {*;}

    -ignorewarnings
    -keepattributes *Annotation*
    -keepattributes Exceptions
    -keepattributes InnerClasses
    -keepattributes Signature
    -keepattributes SourceFile,LineNumberTable
    -keep class com.hianalytics.android.**{*;}
    -keep class com.huawei.updatesdk.**{*;}
    -keep class com.huawei.hms.**{*;}


    -dontwarn com.vivo.push.**
    -keep class com.vivo.push.**{*; }
    -keep class com.vivo.vms.**{*; }
    -keep class com.netease.nimlib.mixpush.vivo.** {*;}
    -keep class com.netease.nimlib.mixpush.vivo.VivoPushReceiver{*;}

    -keep public class * extends android.app.Service
    -keep class com.heytap.msp.** { *;}

    -keep class com.tftechsz.common.push.**{*; }

    -keepclassmembers class ** {
        public void onEvent*(**);
        void onEvent*(**);
    }
    -keep class com.tbruyelle.rxpermissions2.* {
       *;
    }

    -keep class com.netease.nis.alivedetected.entity.**{*;}
    -keep class com.netease.nis.alivedetected.AliveDetector  {#不会混淆类名
        public <methods>;
    }
    -keep class com.netease.nis.alivedetected.DetectedEngine{
        native <methods>;
    }
    -keep class com.netease.nis.alivedetected.NISCameraPreview  {#不会混淆类名
        public <methods>;
    }
    -keep class com.netease.nis.alivedetected.DetectedListener{*;}
    -keep class com.netease.nis.alivedetected.ActionType  { *;}
    -keep class pl.droidsonroids.gif.**{*;}

    -keep class net.sourceforge.pinyin4j.** { *;}
    -keep class demo.** { *;}
    -keepattributesSourceFile,LineNumberTable

#    webview-https
    -keep public class android.net.http.SslError
    -dontwarn android.webkit.WebView
    -dontwarn android.net.http.SslError
    -dontwarn Android.webkit.WebViewClient


    #### ali hotfix
    #基线包使用，生成mapping.txt
    -printmapping mapping.txt
    #生成的mapping.txt在app/build/outputs/mapping/release路径下，移动到/app路径下
    #修复后的项目使用，保证混淆结果一致
    #-applymapping mapping.txt
    #hotfix
    -keep class com.taobao.sophix.**{*;}
    -keep class com.ta.utdid2.device.**{*;}
    #防止inline
    -dontoptimize
    -keepclassmembers class com.tftechsz.peony.App {
        public <init>();
    }
    -keep class com.tftechsz.peony.SophixStubApplication$RealApplicationStub

    #bugly
    -dontwarn com.tencent.bugly.**
    -keep public class com.tencent.bugly.**{*;}
