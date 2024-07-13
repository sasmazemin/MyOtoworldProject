package com.eminsasmaz.otoworldd.model

import com.google.firebase.firestore.GeoPoint

data class CarparkModel(
    val parkAddress: String,
    val parkContact: String,
    val parkFirmName: String,
    val parkImageUrl: String,
    val location:GeoPoint,
    val parkLatitude: Double,
    val parkLongitude: Double,
    val parkPriceList: String,
    val parkStatus: Boolean,
    val parkWorkingHours: String
)