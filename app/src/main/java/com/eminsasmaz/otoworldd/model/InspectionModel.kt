package com.eminsasmaz.otoworldd.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint

data class InspectionModel (
    val inspectionFirmId:String,
    val inspectionAddress: String,
    val inspectionContact: String,
    val inspectionFirmName: String,
    val inspectionImageUrl: String,
    val location: GeoPoint,
    val inspectionLatitude: Double,
    val inspectionLongitude: Double,
    val inspectionPriceList: String,
    val inspectionStatus: Boolean,
    val inspectionWorkingHours: String,
    val inspectionMail: String,
    val inspectionPassword: String,
    val parkType: String,
    val userType:String
) : Parcelable{
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
        parcel.writeString(inspectionFirmId)
        parcel.writeString(inspectionAddress)
        parcel.writeString(inspectionContact)
        parcel.writeString(inspectionFirmName)
        parcel.writeString(inspectionImageUrl)
        parcel.writeDouble(location.latitude)
        parcel.writeDouble(location.longitude)
        parcel.writeDouble(inspectionLatitude)
        parcel.writeDouble(inspectionLongitude)
        parcel.writeString(inspectionPriceList)
        parcel.writeByte(if (inspectionStatus) 1 else 0)
        parcel.writeString(inspectionWorkingHours)
        parcel.writeString(inspectionMail)
        parcel.writeString(inspectionPassword)
        parcel.writeString(parkType)
        parcel.writeString(userType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InspectionModel> {
        override fun createFromParcel(parcel: Parcel): InspectionModel {
            return InspectionModel(parcel)
        }

        override fun newArray(size: Int): Array<InspectionModel?> {
            return arrayOfNulls(size)
        }
    }
}