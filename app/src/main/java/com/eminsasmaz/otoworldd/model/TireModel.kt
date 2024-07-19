package com.eminsasmaz.otoworldd.model

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
)