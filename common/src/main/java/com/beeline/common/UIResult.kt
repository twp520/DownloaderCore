package com.beeline.common


class UIResult<T>(
    var code: Int = CODE_SUCCESS,
    var status: Int = UI_STATUS_LOADING,
    var content: T? = null,
    var errorMsg: String? = "",
    var progress: Int = 0
) : INoProguard {


    fun isContent() = status == UI_STATUS_CONTENT && content != null

    fun isError() = status == UI_STATUS_ERROR

    fun isLoading() = status == UI_STATUS_LOADING

    companion object {

        fun <T> loading(): UIResult<T> {
            return UIResult(status = UI_STATUS_LOADING)
        }

        fun <T> progress(progress: Int): UIResult<T> {
            return UIResult(status = UI_STATUS_PROGRESS, progress = progress)
        }

        fun <T> error(code: Int, msg: String?): UIResult<T> {
            return UIResult(code, UI_STATUS_ERROR, errorMsg = msg)
        }

        fun <T> content(content: T): UIResult<T> {
            return UIResult(CODE_SUCCESS, UI_STATUS_CONTENT, content)
        }
    }

}