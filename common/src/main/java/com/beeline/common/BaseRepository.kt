package com.beeline.common

open class BaseRepository : INoProguard {

    protected val defaultPageSize = 20

    protected fun <T> handResult(response: BaseResponse<T>): UIResult<T> {
        if (response.isSuccess() && response.data != null)
            return UIResult.content(response.data)
        if (response.code == CODE_TOKEN_EXP)
            throw ApiException(response.code, response.msg ?: "token exp")
        return UIResult.error(response.code, response.msg)
    }


    protected fun createListParams(page: Int, pageSize: Int = defaultPageSize): Map<String, Int> {
        val params = hashMapOf<String, Int>()
        params["pageNum"] = page
        params["pageSize"] = pageSize
        return params
    }
}