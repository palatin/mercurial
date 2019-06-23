package com.palatin.mercurial.data

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep


data class Resource<out T>(val status: Status, val data: T?, val message: String?, var isReceived: Boolean = false) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String?, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> noInternet(data: T?, message: String? = null): Resource<T> {
            return Resource(Status.NO_INTERNET, data, message)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

        fun <T> unit(data: T? = null, message: String? = null): Resource<T> {
            return Resource(Status.UNIT, data, message)
        }
    }

    enum class Status {
        SUCCESS,
        ERROR,
        NO_INTERNET,
        LOADING,
        UNIT
    }


}