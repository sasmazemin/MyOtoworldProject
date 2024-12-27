package com.eminsasmaz.otoworldd.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.eminsasmaz.otoworldd.adapter.FirmReservationAdapter
import com.eminsasmaz.otoworldd.databinding.ActivityFirmReservationBinding
import com.eminsasmaz.otoworldd.model.ReservationModel
import com.google.firebase.firestore.FirebaseFirestore

class FirmReservationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirmReservationBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: FirmReservationAdapter
    private var reservationsList = mutableListOf<ReservationModel>()
    private var firmType: String? = null // Firma türü dinamik olarak alınacak

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirmReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        val sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val firmId = sharedPreferences.getString("firmId", null)
        firmType = sharedPreferences.getString("firmType", null)

        if (firmId == null || firmType == null) {
            Toast.makeText(this, "Firma bilgisi eksik!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        fetchReservations(firmId)
    }

    private fun setupRecyclerView() {
        adapter = FirmReservationAdapter(
            reservationsList,
            onItemClick = { reservation ->
                Toast.makeText(this, "Rezervasyon: ${reservation.selectedVehiclePlate}", Toast.LENGTH_SHORT).show()
            },
            onApproveClick = { reservation ->
                updateReservationStatus(reservation, ReservationModel.STATUS_APPROVED)
            },
            onRejectClick = { reservation ->
                updateReservationStatus(reservation, ReservationModel.STATUS_REJECTED)
            }
        )
        binding.reservationsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reservationsRecyclerView.adapter = adapter
    }

    private fun fetchReservations(firmId: String) {
        firmType?.let { type ->
            firestore.collection(type)
                .document(firmId)
                .collection("Reservations")
                .get()
                .addOnSuccessListener { snapshot ->
                    reservationsList.clear()
                    for (document in snapshot.documents) {
                        val reservation = document.toObject(ReservationModel::class.java)
                        reservation?.reservationId = document.id // reservationId'yi güncelleyebilirsiniz
                        if (reservation != null) {
                            reservationsList.add(reservation)
                        }
                    }
                    adapter.updateList(reservationsList)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Rezervasyonlar yüklenemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateReservationStatus(reservation: ReservationModel, newStatus: String) {
        if (reservation.reservationId == null || reservation.userId == null || reservation.firmId == null) {
            val missingFields = mutableListOf<String>()
            if (reservation.reservationId == null) missingFields.add("reservationId")
            if (reservation.userId == null) missingFields.add("userId")
            if (reservation.firmId == null) missingFields.add("firmId")
            Toast.makeText(this, "Eksik Alanlar: ${missingFields.joinToString(", ")}", Toast.LENGTH_SHORT).show()
            return
        }

        firmType?.let { type ->
            firestore.collection(type)
                .document(reservation.firmId!!)
                .collection("Reservations")
                .document(reservation.reservationId!!)
                .update("appointmentStatus", newStatus)
                .addOnSuccessListener {
                    firestore.collection("Users")
                        .document(reservation.userId!!)
                        .collection("Reservations")
                        .document(reservation.reservationId!!)
                        .update("appointmentStatus", newStatus)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Rezervasyon güncellendi!", Toast.LENGTH_SHORT).show()
                            fetchReservations(reservation.firmId!!)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Kullanıcı rezervasyonu güncellenemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Firma rezervasyonu güncellenemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}