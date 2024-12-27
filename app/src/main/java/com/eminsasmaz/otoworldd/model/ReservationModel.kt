package com.eminsasmaz.otoworldd.model

import com.google.firebase.Timestamp

data class ReservationModel (
    val selectedFirmPhoto: String? = null,
    val selectedFirmName: String? = null,
    val selectedVehiclePlate: String? = null,
    val selectedDateTime: String? = null,
    val userId: String? = null,
    val firmId: String? = null,
    var reservationId: String? = null,
    var appointmentStatus: String = STATUS_WAITING // Varsayılan durum
) {
    companion object {
        const val STATUS_WAITING = "waiting for approval"
        const val STATUS_APPROVED = "approved"
        const val STATUS_REJECTED = "rejected"
    }
}
