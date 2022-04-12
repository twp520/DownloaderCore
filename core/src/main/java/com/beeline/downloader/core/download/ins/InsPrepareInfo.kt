package com.beeline.downloader.core.download.ins

import com.beeline.common.INoProguard
import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2022/1/29
 */
class InsPrepareInfo : INoProguard {

    @SerializedName("num_results")
    var num: Int = 0

    var items: List<InsResourceInfo> = mutableListOf()
}