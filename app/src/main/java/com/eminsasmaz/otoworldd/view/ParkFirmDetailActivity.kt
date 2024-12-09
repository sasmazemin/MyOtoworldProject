package com.eminsasmaz.otoworldd.view

import CarparkModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            // MapsActivity'i başlatmak için intent oluşturuyoruz
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
        imageList.add(SlideModel(firm.parkImageUrl, ScaleTypes.FIT))
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
        val (openingTime, closingTime) = workingHours.split("-")
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = Calendar.getInstance()
        val openingDate = timeFormat.parse(openingTime)
        val closingDate = timeFormat.parse(closingTime)

        openingDate?.let { open ->
            closingDate?.let { close ->
                val openingCalendar = Calendar.getInstance().apply {
                    time = open
                    set(Calendar.YEAR, currentTime.get(Calendar.YEAR))
                    set(Calendar.MONTH, currentTime.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH))
                }

                val closingCalendar = Calendar.getInstance().apply {
                    time = close
                    set(Calendar.YEAR, currentTime.get(Calendar.YEAR))
                    set(Calendar.MONTH, currentTime.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH))
                }

                if (currentTime.after(openingCalendar) && currentTime.before(closingCalendar)) {
                    binding.imageView22.setImageResource(R.drawable.eclipse_green_firm_open)
                    binding.textView19.text = "OPEN"
                } else {
                    binding.imageView22.setImageResource(R.drawable.eclipse_red_firm_closed)
                    binding.textView19.setTextColor(getResources().getColor(R.color.mainColor))
                    binding.textView19.text = "CLOSED"
                }
            }
        }
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
                    onSuccess()  // Araç plakası alındıktan sonra kaydetme işlemini başlat
                } else {
                    Toast.makeText(this, "Seçili araç bulunamadı.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Araç bilgisi alınamadı: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveReservation() {
        if (userId == null) {
            Toast.makeText(this, "Kullanıcı giriş yapmamış", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedDateTime != null && selectedVehiclePlate != null) {
            val reservationData = hashMapOf(
                "appointmentStatus" to false,
                "selectedDateTime" to selectedDateTime,
                "selectedFirmName" to selectedFirmName,
                "selectedFirmPhoto" to selectedFirmPhoto,
                "selectedVehiclePlate" to selectedVehiclePlate
            )

            Log.d("Reservation", "Rezervasyon verileri: $reservationData")

            firestore.collection("Users").document(userId)
                .collection("Reservations")
                .add(reservationData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Rezervasyon başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Rezervasyon kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
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
        fetchSelectedVehiclePlate {
            saveReservation()  // Araç plakası alındıktan sonra kaydetme işlemini çağır
        }
    }
}


