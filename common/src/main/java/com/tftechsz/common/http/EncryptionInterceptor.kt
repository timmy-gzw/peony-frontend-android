package com.tftechsz.common.http

import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.tftechsz.common.iservice.UserProviderService
import com.tftechsz.common.utils.enc.XXTEA
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException
import java.nio.charset.StandardCharsets

class EncryptionInterceptor : Interceptor {

    private val dstPath = "/qq"// 加密转发的路径

    /**
     * 服务器下发是否加密：是否转发接口到 [dstPath]
     */
    private val key: String? = ARouter.getInstance().navigation(UserProviderService::class.java)?.configInfo?.sys?.encryptKey

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val srcRequest: Request = chain.request()
        val srcHttpUrl = srcRequest.url
        val srcPath = srcHttpUrl.encodedPath

        if (!key.isNullOrBlank() && !(srcPath == "/launch" || srcPath == dstPath || srcPath.contains("/jsonConfig/"))) {
            val srcRequestBody = srcRequest.body
            val srcContentType = srcRequestBody?.contentType()
            val srcQuery = srcHttpUrl.encodedQuery
            // 拼接目标路径GET/POST /xx/yy?aa=bb&cc=dd
            val srcPathWithQuery = if (srcQuery.isNullOrBlank()) srcPath else "$srcPath?${srcQuery}"
            val srcHeadersMap = HashMap<String, String>()
            var dstParamsBody: String? = null
            //读取 request body 内容
            if (srcRequestBody != null) {
                val buffer = Buffer()
                srcRequestBody.writeTo(buffer)
                val charset = srcContentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
                dstParamsBody = buffer.readString(charset)
//                LogUtils.e("--- req: body ---\n$dstParamsBody\n-----------------------------")
            }
            val srcHeaderIterator = srcRequest.headers.iterator()
            while (srcHeaderIterator.hasNext()) {
                val next = srcHeaderIterator.next()
                srcHeadersMap[next.first] = next.second
            }
            val dstReqBodyParamsMap = HashMap<String, Any>()
            dstReqBodyParamsMap["path"] = srcPathWithQuery
            dstReqBodyParamsMap["method"] = srcRequest.method
            dstReqBodyParamsMap["headers"] = srcHeadersMap
            dstReqBodyParamsMap["ct"] = srcContentType?.toString() ?: ""
            if (!dstParamsBody.isNullOrBlank()) {
                dstReqBodyParamsMap["body"] = dstParamsBody
            }

            val dstReqBodyParams = GsonUtils.toJson(dstReqBodyParamsMap)
            val dstReqBodyParamsEnc = XXTEA.encryptToBase64String(dstReqBodyParams, key)
//            LogUtils.e("--- req: original params ---\n$dstReqBodyParams")
//            LogUtils.e("--- req: params with tea ---\n$dstReqBodyParamsEnc\n-----------------------------")

            // 构造请求体
            val dstHttpUrl = srcHttpUrl.newBuilder().encodedPath(dstPath).query(null).build()
//            LogUtils.e("--- HttpUrl ---\nsrcHttpUrl: $srcHttpUrl\ndstHttpUrl: $dstHttpUrl\n-----------------------------")
            val dstReqBody = dstReqBodyParamsEnc.toRequestBody("application/json".toMediaType())
            val dstRequest = srcRequest.newBuilder()
                .removeHeader("x-auth-token")
                .removeHeader("api-ua")
                .removeHeader("x-user-code")
                .removeHeader("x-anti-cheat-token")
                .removeHeader("dit")
                .removeHeader("is-emulator")
                .url(dstHttpUrl)
                .method("POST", dstReqBody)
                .build()

//            LogUtils.e("--- targetRequest ---\n$dstRequest\n-----------------------------")
            val dstResp = chain.proceed(dstRequest)
            val dstBody = dstResp.body
            val dstRespBodyString = XXTEA.decryptBase64StringToString(dstBody?.string(), key)
//            LogUtils.e("--- resp: decrypted response ---\n$dstRespBodyString\n-----------------------------")
            val dstRespBody = dstRespBodyString?.toResponseBody(dstBody?.contentType())
            return dstResp.newBuilder()
                .request(srcRequest)// 转回源请求接口参数
                .body(dstRespBody)
                .build()
        } else {
            return chain.proceed(srcRequest)
        }
    }
}