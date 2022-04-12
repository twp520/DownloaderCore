package com.beeline.downloader.core.download.ins

import com.beeline.common.INoProguard
import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2022/1/29
 */
class InsResourceInfo : INoProguard {

    @SerializedName("media_type")
    var mediaType: Int = -1

    @SerializedName("image_versions2")
    var image: InsImageVersion2? = null

    @SerializedName("video_versions")
    var video: List<InsResource> = mutableListOf()

    @SerializedName("carousel_media")
    var multiResource: List<InsResourceInfo> = mutableListOf()

    fun getDownloadResource(): InsResource? {
        if (mediaType <= 0) return null
        return when (mediaType) {
            2 -> {
                video.firstOrNull()
            }
            1 -> {
                image?.getDownloadResource()
            }
            8 -> {
                //多张照片的情况
                multiResource.firstOrNull()?.getDownloadResource()
            }
            else -> null
        }
    }

    fun getDownloadResourceList(): List<InsResource> {
        if (mediaType <= 0) return mutableListOf()
        val result = mutableListOf<InsResource>()
        when (mediaType) {
            2 -> {
                video.firstOrNull()?.let {
                    result.add(it)
                }
            }
            1 -> {
                image?.getDownloadResource()?.let {
                    result.add(it)
                }
            }
            8 -> {
                //多张照片的情况
                multiResource.forEach {
                    it.getDownloadResource()?.let { res ->
                        result.add(res)
                    }
                }
            }
        }
        return result
    }
}