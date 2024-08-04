package com.eminsasmaz.otoworldd.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint

data class TowModel (
    val towAdress: String,
    val towContact: String,
    val towFirmName: String,
    val towImageUrl: String,
    val location: GeoPoint,
    val towLatitude: Double,
    val towLongitude: Double,
    val towPriceList: String,
    val towStatus: Boolean,
    val towWorkingHours: String
):Parcelable{
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
        parcel.writeString(towAdress)
        parcel.writeString(towContact)
        parcel.writeString(towFirmName)
        parcel.writeString(towImageUrl)
        parcel.writeDouble(location.latitude)
        parcel.writeDouble(location.longitude)
        parcel.writeDouble(towLatitude)
        parcel.writeDouble(towLongitude)
        parcel.writeString(towPriceList)
        parcel.writeByte(if (towStatus) 1 else 0)
        parcel.writeString(towWorkingHours)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<TowModel> {
        override fun createFromParcel(parcel: Parcel): TowModel {
            return TowModel(parcel)
        }

        override fun newArray(size: Int): Array<TowModel?> {
            return arrayOfNulls(size)
        }
    }
}