package com.eminsasmaz.otoworldd.model

import com.google.firebase.firestore.GeoPoint

data class InspectionModel (
    val inspectionAdress: String,
    val inspectionContact: String,
    val inspectionFirmName: String,
    val inspectionImageUrl: String,
    val location: GeoPoint,
    val inspectionLatitude: Double,
    val inspectionLongitude: Double,
    val inspectionPriceList: String,
    val inspectionStatus: Boolean,
    val inspectionWorkingHours: String
)