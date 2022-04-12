package com.beeline.downloader.core.download

/**
 * create by colin
 * 2022/1/30
 */
data class DownloadResult(
    val url: String,
    val mediaType: String,
    val saveFileName: String, val time: Long
)