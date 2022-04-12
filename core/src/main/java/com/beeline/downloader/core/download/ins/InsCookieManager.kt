package com.beeline.downloader.core.download.ins

import com.blankj.utilcode.util.SPUtils

/**
 * create by colin
 * 2022/1/29
 */
object InsCookieManager {

    private const val testCookie =
        "mid=YfSZAQABAAGevzOo-z41vCZyN3UO; ig_did=06E397F3-488A-4B2A-BD79-1D185B0459AB; ig_nrcb=1; csrftoken=1x1f72YNzbzdc2kEM6OLtWCW8wmihedv; ds_user_id=51341858433; sessionid=51341858433%3AagwNxsdz9gOSyT%3A3;"

    fun isLogin(): Boolean {
        return getInsCookie().isNotEmpty()
    }

    fun getInsCookie(): String {
        return SPUtils.getInstance().getString("insCookie", "")
        // return testCookie
    }

    fun setInsCookie(cookie: String) {
        SPUtils.getInstance().put("insCookie", cookie)
    }
}