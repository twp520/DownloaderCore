package com.beeline.common


class BaseResponse<T> {

    var code: Int = 0
    var msg: String? = ""
    val data: T? = null


    fun isSuccess(): Boolean {
        return code == CODE_SUCCESS && data != null
    }
}