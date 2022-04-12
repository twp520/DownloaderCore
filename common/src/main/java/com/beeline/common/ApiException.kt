package com.beeline.common


class ApiException(val code: Int, val msg: String) : Exception(msg)