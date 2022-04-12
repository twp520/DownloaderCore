package com.beeline.downloader.core.download.ins

import android.text.TextUtils
import com.beeline.common.*
import com.beeline.downloader.core.download.EmptyDownloader
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Request
import java.util.regex.Pattern
import kotlin.coroutines.resume

/**
 * create by colin
 * 2022/1/29
 *
 * Instagram下载器
 */
class InsDownloader : EmptyDownloader() {


    override suspend fun prepareUrl(url: String): UIResult<String> {
        return prepare(url) {
            val resources = it.getDownloadResource()
            if (resources != null) {
                UIResult.content(resources.url)
            } else {
                UIResult.error(
                    CODE_NOT_FOUNT_EXP,
                    "Not fount resource"
                )
            }
        }
    }

    override suspend fun prepareUrl2List(url: String): UIResult<List<String>> {
        return prepare(url) {
            val resource = it.getDownloadResourceList()
            if (resource.isEmpty()) {
                UIResult.error(
                    CODE_NOT_FOUNT_EXP,
                    "Not fount resource"
                )
            } else {
                UIResult.content(resource.map { insResource -> insResource.url })
            }
        }
    }

    private suspend fun <T> prepare(
        url: String,
        block: (resources: InsResourceInfo) -> UIResult<T>
    ): UIResult<T> {
        return suspendCancellableCoroutine { coroutine ->
            val matches = url.matches(Regex("https://www.instagram.com/(.*)"))
            if (matches) {
                val data = url.split(Pattern.quote("?").toRegex())
                val requestUrl = "${data[0]}?__a=1"
                LogUtils.d("处理前的url = $url", "处理后的 url = $requestUrl")
                val builder = Request.Builder().url(requestUrl)
                    .get()
                builder.removeHeader("Cookie")
                if (InsCookieManager.isLogin()) {
                    builder.addHeader("Cookie", InsCookieManager.getInsCookie())
                }
                val response = execute(builder.build())
                if (response.isSuccessful) {
                    val contentType = response.body()?.contentType()?.type()
                    val subType = response.body()?.contentType()?.subtype()
                    if (TextUtils.equals(contentType, "application") && TextUtils.equals(
                            subType,
                            "json"
                        )
                    ) {
                        //解析成功，
                        val json = response.body()?.string()
                        val insPrepareInfo =
                            GsonProvider.gson.fromJson(json, InsPrepareInfo::class.java)
                        val resourceInfo = insPrepareInfo.items.firstOrNull()
                        if (resourceInfo != null) {
                            coroutine.resume(block.invoke(resourceInfo))
                        } else {
                            //解析失败
                            coroutine.resume(
                                UIResult.error(
                                    CODE_NOT_FOUNT_EXP,
                                    "Not fount resource"
                                )
                            )
                        }
                    } else {
                        //表示没登录
                        coroutine.resume(UIResult.error(CODE_TOKEN_EXP, "Not Login"))
                    }
                } else {
                    //网络失败
                    coroutine.resume(UIResult.error(CODE_NET_ERROR, "IO Exception"))
                }
            }
        }
    }

}