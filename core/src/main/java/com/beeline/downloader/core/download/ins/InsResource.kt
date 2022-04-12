package com.beeline.downloader.core.download.ins

import com.beeline.common.INoProguard

/**
 * create by colin
 * 2022/1/29
 */
data class InsResource(
    val width: Int,
    val height: Int,
    val url: String
) : INoProguard