package com.beeline.common


class BaseListResponse<T> : INoProguard {

    var pageNum: Int = 0
    var pages: Int = 0
    var total: Long = 0
    var isLastPage: Boolean = false
    var list: MutableList<T> = mutableListOf()
}