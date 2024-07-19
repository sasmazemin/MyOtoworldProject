package com.eminsasmaz.otoworldd.model

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
)