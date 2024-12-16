package com.eminsasmaz.otoworldd.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint

data class TowModel (
    val towFirmId:String,
    val towAddress: String,
    val towContact: String,
    val towFirmName: String,
    val towImageUrl: String,
    val location: GeoPoint,
    val towLatitude: Double,
    val towLongitude: Double,
    val towPriceList: String,
    val towStatus: Boolean,
    val towWorkingHours: String,
    val towMail: String,
    val towPassword: String,
    val parkType: String,
    val userType:String
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        GeoPoint(parcel.readDouble(), parcel.readDouble()),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(towFirmId)
        parcel.writeString(towAddress)
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
        parcel.writeString(towMail)
        parcel.writeString(towPassword)
        parcel.writeString(parkType)
        parcel.writeString(userType)
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