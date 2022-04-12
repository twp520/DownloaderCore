package com.beeline.downloader.core.download.ins

import com.beeline.common.INoProguard

/**
 * create by colin
 * 2022/1/29
 */
class InsImageVersion2 : INoProguard {

    var candidates: List<InsResource> = mutableListOf()

    fun getDownloadResource(): InsResource? {
        return candidates.firstOrNull()
    }
}