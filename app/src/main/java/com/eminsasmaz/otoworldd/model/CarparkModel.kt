package com.eminsasmaz.otoworldd.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint

data class CarparkModel(
    val parkAddress: String,
    val parkContact: String,
    val parkFirmName: String,
    val parkImageUrl: String,
    val location: GeoPoint,
    val parkLatitude: Double,
    val parkLongitude: Double,
    val parkPriceList: String,
    val parkStatus: Boolean,
    val parkWorkingHours: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        GeoPoint(parcel.readDouble(), parcel.readDouble()),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(parkAddress)
        parcel.writeString(parkContact)
        parcel.writeString(parkFirmName)
        parcel.writeString(parkImageUrl)
        parcel.writeDouble(location.latitude)
        parcel.writeDouble(location.longitude)
        parcel.writeDouble(parkLatitude)
        parcel.writeDouble(parkLongitude)
        parcel.writeString(parkPriceList)
        parcel.writeByte(if (parkStatus) 1 else 0)
        parcel.writeString(parkWorkingHours)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CarparkModel> {
        override fun createFromParcel(parcel: Parcel): CarparkModel {
            return CarparkModel(parcel)
        }

        override fun newArray(size: Int): Array<CarparkModel?> {
            return arrayOfNulls(size)
        }
    }
}