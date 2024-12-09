package com.eminsasmaz.otoworldd.model

import java.sql.Timestamp

data class UserModel (
    val createdAt: com.google.firebase.Timestamp,
    val photoUrl: String = "",
    val userEmail: String,
    val userLocation: String = "",
    val userName: String,
    val userPhone: String = "",
    val vehicleCount: Int = 0,
    val userType: String = ""
    )