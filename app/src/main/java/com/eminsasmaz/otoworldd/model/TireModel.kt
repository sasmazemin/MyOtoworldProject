package com.eminsasmaz.otoworldd.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint

data class TireModel (
    val tireAdress: String,
    val tireContact: String,
    val tireFirmName: String,
    val tireImageUrl: String,
    val location: GeoPoint,
    val tireLatitude: Double,
    val tireLongitude: Double,
    val tirePriceList: String,
    val tireStatus: Boolean,
    val tireWorkingHours: String
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
        parcel.writeString(tireAdress)
        parcel.writeString(tireContact)
        parcel.writeString(tireFirmName)
        parcel.writeString(tireImageUrl)
        parcel.writeDouble(location.latitude)
        parcel.writeDouble(location.longitude)
        parcel.writeDouble(tireLatitude)
        parcel.writeDouble(tireLongitude)
        parcel.writeString(tirePriceList)
        parcel.writeByte(if (tireStatus) 1 else 0)
        parcel.writeString(tireWorkingHours)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<TireModel> {
        override fun createFromParcel(parcel: Parcel): TireModel {
            return TireModel(parcel)
        }

        override fun newArray(size: Int): Array<TireModel?> {
            return arrayOfNulls(size)
        }
    }
}