package com.beeline.downloader.core.download

import android.content.Context
import com.beeline.common.UIResult

/**
 * create by colin
 * 2022/1/29
 */
interface IDownloader {

    /**
     * 解析链接
     *
     * @param url 从目标平台copy的链接
     * @return 真实内容地址
     */
    suspend fun prepareUrl(url: String): UIResult<String>

    /**
     * 解析链接
     *
     * @param url 从目标平台copy的链接
     * @return 真实内容地址集合
     */
    suspend fun prepareUrl2List(url: String): UIResult<List<String>>

    /**
     * 下载文件
     * @param url 资源地址
     *
     * @return 保存的文件路径
     */
    fun download(
        context: Context,
        url: String,
        progress: (progress: Int) -> Unit
    ): UIResult<DownloadResult>
}