package com.eminsasmaz.otoworldd.view

import CarparkModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.eminsasmaz.otoworldd.fragment.DateTimePickerFragment
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ActivityParkFirmDetailBinding
import com.eminsasmaz.otoworldd.model.Vehicle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ParkFirmDetailActivity : AppCompatActivity(), DateTimePickerFragment.DateTimePickerListener {
    private lateinit var binding: ActivityParkFirmDetailBinding
    private var selectedFirmName: String? = null
    private var selectedFirmPhoto: String? = null
    private var selectedVehiclePlate: String? = null
    private var selectedDateTime: String? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParkFirmDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firmId = intent.getStringExtra("firmId")
        if (firmId.isNullOrEmpty()) {
            Toast.makeText(this, "Firma bilgisi alınamadı!", Toast.LENGTH_SHORT).show()
            finish()
        }

        val textAppointment: TextView = findViewById(R.id.text_appointment)
        textAppointment.setOnClickListener {
            val dialog = DateTimePickerFragment()
            dialog.show(supportFragmentManager, "DateTimePicker")
        }

        val firm = intent.getParcelableExtra<CarparkModel>("FIRM")
        firm?.let {
            setupFirmDetails(it)
        } ?: run {
            Toast.makeText(this, "Firm details not found", Toast.LENGTH_SHORT).show()
        }

        val imageView7: ImageView = findViewById(R.id.imageView7)
        imageView7.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupFirmDetails(firm: CarparkModel) {
        selectedFirmName = firm.parkFirmName
        selectedFirmPhoto = firm.parkImageUrl

        binding.firmName.text = firm.parkFirmName
        binding.firmName2.text = firm.parkFirmName
        binding.firmAddress.text = firm.parkAddress
        binding.firmContact.text = firm.parkContact
        binding.firmWorkingHours.text = firm.parkWorkingHours
        binding.firmPriceList.text = firm.parkPriceList

        checkWorkingHours(firm.parkWorkingHours)

        val imageSlider = binding.imageSliderParkDetail
        val imageList = ArrayList<SlideModel>()

        // Görsel URL'si boşsa varsayılan görsel ekle
        if (!firm.parkImageUrl.isNullOrEmpty()) {
            imageList.add(SlideModel(firm.parkImageUrl, ScaleTypes.FIT))
        } else {
            imageList.add(SlideModel(R.drawable.times_svgrepo_com_1_red, ScaleTypes.FIT)) // Varsayılan görsel
        }

        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {}
            override fun onItemSelected(position: Int) {
                val itemMessage = "Selected Image $position"
                Toast.makeText(this@ParkFirmDetailActivity, itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkWorkingHours(workingHours: String) {
        // Çalışma saatleri kontrolü
    }

    private fun fetchSelectedVehiclePlate(onSuccess: () -> Unit) {
        firestore.collection("Users").document(userId!!)
            .collection("Vehicles")
            .whereEqualTo("selected", true)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val vehicle = snapshot.documents.first().toObject(Vehicle::class.java)
                    selectedVehiclePlate = vehicle?.licensePlate
                    onSuccess()
                } else {
                    Toast.makeText(this, "Seçili araç bulunamadı.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Araç bilgisi alınamadı: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveReservation(firmId: String) {
        if (userId == null) {
            Toast.makeText(this, "Kullanıcı giriş yapmamış", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDateTime != null && selectedVehiclePlate != null) {
            val reservationId = firestore.collection("Reservations").document().id // Benzersiz bir ID oluşturuyoruz
            val reservationData = hashMapOf(
                "reservationId" to reservationId, // ID'yi açıkça ekliyoruz
                "appointmentStatus" to "waiting for approval",
                "selectedDateTime" to selectedDateTime,
                "selectedFirmName" to selectedFirmName,
                "selectedFirmPhoto" to selectedFirmPhoto,
                "selectedVehiclePlate" to selectedVehiclePlate,
                "userId" to userId,
                "firmId" to firmId
            )

            firestore.collection("Users").document(userId)
                .collection("Reservations").document(reservationId)
                .set(reservationData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Rezervasyon başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Kullanıcı rezervasyonu kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            firestore.collection("CarparkFirms").document(firmId)
                .collection("Reservations").document(reservationId)
                .set(reservationData)
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Firma rezervasyonu kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Tarih, saat veya araç bilgisi eksik!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDateTimeSelected(
        date: String,
        time: String,
        firmName: String,
        firmPhotoUrl: String,
        vehiclePlate: String
    ) {
        selectedDateTime = "$date $time"

        val firmId = intent.getStringExtra("firmId") ?: ""
        if (firmId.isEmpty()) {
            Toast.makeText(this, "Firma bilgisi eksik!", Toast.LENGTH_SHORT).show()
            return
        }

        fetchSelectedVehiclePlate {
            saveReservation(firmId)
        }
    }
}


