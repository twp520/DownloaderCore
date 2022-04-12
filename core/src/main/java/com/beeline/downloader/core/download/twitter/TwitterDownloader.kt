package com.beeline.downloader.core.download.twitter

import android.net.Uri
import com.beeline.common.CODE_NET_ERROR
import com.beeline.common.CODE_NOT_FOUNT_EXP
import com.beeline.common.UIResult
import com.beeline.downloader.core.download.EmptyDownloader
import okhttp3.Request
import org.json.JSONObject

/**
 * create by colin
 * 2022/3/10
 */
class TwitterDownloader : EmptyDownloader() {

    private val twitterApi = "https://api.twitter.com/2/timeline/conversation/%s.json"
    private val twitterAuth =
        "Bearer AAAAAAAAAAAAAAAAAAAAANRILgAAAAAAnNwIzUejRCOuH5E6I8xnZz4puTs%3D1Zv7ttfk8LF81IUq16cHjhLTvJu4FA33AGWWjCpTnA"

    override suspend fun prepareUrl(url: String): UIResult<String> {
        try {
            val result = prepare(url).firstOrNull()
                ?: return UIResult.error(CODE_NOT_FOUNT_EXP, "Not fount url")
            return UIResult.content(result)
        } catch (e: Exception) {
            return UIResult.error(CODE_NET_ERROR, "exception")
        }
    }

    override suspend fun prepareUrl2List(url: String): UIResult<List<String>> {
        try {
            val list = prepare(url)
            if (list.isEmpty())
                return UIResult.error(CODE_NOT_FOUNT_EXP, "Not fount url")
            return UIResult.content(list)
        } catch (e: Exception) {
            return UIResult.error(CODE_NET_ERROR, "empty implement")
        }
    }

    private fun prepare(url: String): List<String> {
        //https://twitter.com/kl15385514/status/1499748793368993792
        val uri = Uri.parse(url)
        val twitterId = uri.lastPathSegment ?: ""
        if (twitterId.isEmpty()) {
            return emptyList()
        }
        val requestUrl = twitterApi.format(twitterId)
        val request = Request.Builder().url(requestUrl)
            .get()
            .addHeader("authorization", twitterAuth)
            .build()
        val response = execute(request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val obj = JSONObject(body.string())
            val globalObjects = obj.optJSONObject("globalObjects")
            val tweets = globalObjects?.optJSONObject("tweets")
            val postUser = tweets?.optJSONObject(twitterId)
            val entities = postUser?.optJSONObject("extended_entities")
            val mediaArray = entities?.optJSONArray("media") ?: return emptyList()
            val result = mutableListOf<String>()
            for (i in 0 until mediaArray.length()) {
                val media = mediaArray.getJSONObject(i)
                val type = media.getString("type")
                if (type == "video") {
                    val videoInfo = media.optJSONObject("video_info")
                    val variants = videoInfo?.optJSONArray("variants")
                    val video = variants?.optJSONObject(0)
                    val videoUrl = video?.optString("url") ?: continue
                    result.add(videoUrl)
                } else {
                    result.add(media.getString("media_url_https"))
                }
            }
            return result
        }
        return emptyList()
    }
}