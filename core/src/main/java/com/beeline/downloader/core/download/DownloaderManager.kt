package com.beeline.downloader.core.download

import com.beeline.downloader.core.download.ins.InsDownloader
import com.beeline.downloader.core.download.twitter.TwitterDownloader

/**
 * create by colin
 * 2022/1/29
 */
object DownloaderManager {

    fun getDownloader(@DownloaderType type: Int): IDownloader {
        return when (type) {
            DownloaderType.INS -> InsDownloader()
            DownloaderType.TWITTER -> TwitterDownloader()
            else -> EmptyDownloader()
        }
    }


    fun checkUrl(url: String, @DownloaderType downloaderType: Int): Boolean {
        return when (downloaderType) {
            DownloaderType.INS -> {
                url.startsWith("https://www.instagram.com")
            }
            DownloaderType.TWITTER -> {
                url.startsWith("https://twitter.com")
            }
            else -> {
                false
            }
        }
    }
}