package com.eminsasmaz.otoworldd.model

import com.google.firebase.Timestamp

data class ReservationModel (
    val selectedFirmPhoto: String? = null,
    val selectedFirmName: String? = null,
    val selectedVehiclePlate: String? = null,
    val selectedDateTime: String? = null,
    var appointmentStatus: String = "waiting for approval" // Varsayılan durum
    )