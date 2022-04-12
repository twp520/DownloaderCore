package com.beeline.downloader.core.download

import androidx.annotation.IntDef

/**
 * create by colin
 * 2022/3/22
 */
@IntDef(DownloaderType.INS, DownloaderType.TWITTER)
annotation class DownloaderType {

    companion object {
        const val INS = 1
        const val TWITTER = 2
    }
}
