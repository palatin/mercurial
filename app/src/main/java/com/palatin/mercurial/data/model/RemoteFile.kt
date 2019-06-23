package com.palatin.mercurial.data.model

import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateFormat
import com.palatin.mercurial.util.fileSize
import java.util.*

//Not used kotlin @Parcelable annotation because of bug with aidl
data class RemoteFile(val name: String, val isFolder: Boolean, val date: Long, val size: Long) : Parcelable {

    private var formattedDate: String? = null

    fun getFormattedDate(): String {
        return formattedDate ?: with(Calendar.getInstance()) {
            timeInMillis = date
            DateFormat.format("dd/MM", this).toString().also { formattedDate = it }
        }
    }

    private var formattedSize: String? = null

    fun getFormattedSize(): String {
        return formattedSize ?: size.fileSize().also { formattedSize = it }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeByte(if (isFolder) 1 else 0)
        parcel.writeLong(date)
        parcel.writeLong(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RemoteFile> {
        override fun createFromParcel(parcel: Parcel): RemoteFile {
            return RemoteFile(parcel)
        }

        override fun newArray(size: Int): Array<RemoteFile?> {
            return arrayOfNulls(size)
        }
    }

}