package com.markel.passwordreminder.base.vo

import java.lang.Exception

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String? = null,
    val error: Throwable? = null
) {
    companion object {
        fun <T> success(data: T?) = Resource(Status.SUCCESS, data, null)

        fun <T> error(
            errorMsg: String? = null,
            error: Throwable? = null,
            data: T? = null
        ) = Resource(Status.ERROR, data, errorMsg, error ?: Exception(errorMsg))

        fun message(msg: String?) = Resource(com.markel.passwordreminder.base.vo.Status.MESSAGE, null, msg)

        fun <T> loading(data: T? = null) = Resource(com.markel.passwordreminder.base.vo.Status.LOADING, data)
    }
}