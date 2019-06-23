package com.palatin.mercurial.data.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//Not used kotlin @Parcelable annotation because of bug with aidl
data class FTPRemoteConfig(val hostName: String, val link: String, val username: String, val password: String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(hostName)
        parcel.writeString(link)
        parcel.writeString(username)
        parcel.writeString(password)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FTPRemoteConfig> {
        override fun createFromParcel(parcel: Parcel): FTPRemoteConfig {
            return FTPRemoteConfig(parcel)
        }

        override fun newArray(size: Int): Array<FTPRemoteConfig?> {
            return arrayOfNulls(size)
        }
    }

}