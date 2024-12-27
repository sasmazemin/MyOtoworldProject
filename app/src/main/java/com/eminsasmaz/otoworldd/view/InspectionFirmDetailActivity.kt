package com.eminsasmaz.otoworldd.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.eminsasmaz.otoworldd.fragment.DateTimePickerFragment
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ActivityInspectionFirmDetailBinding
import com.eminsasmaz.otoworldd.model.InspectionModel
import com.eminsasmaz.otoworldd.model.Vehicle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InspectionFirmDetailActivity : AppCompatActivity(), DateTimePickerFragment.DateTimePickerListener {
    private lateinit var binding: ActivityInspectionFirmDetailBinding
    private var selectedFirmName: String? = null
    private var selectedFirmPhoto: String? = null
    private var selectedVehiclePlate: String? = null
    private var selectedDateTime: String? = null
    private var firmId: String? = null // Intent'ten alınacak firmId
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInspectionFirmDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Intent'ten firmId ve FIRM nesnesini al
        firmId = intent.getStringExtra("firmId")
        val firm = intent.getParcelableExtra<InspectionModel>("FIRM")

        if (firmId == null || firm == null) {
            Toast.makeText(this, "Firma bilgisi eksik!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupFirmDetails(firm)

        binding.textAppointment.setOnClickListener {
            val dialog = DateTimePickerFragment()
            dialog.show(supportFragmentManager, "DateTimePicker")
        }
    }

    private fun setupFirmDetails(firm: InspectionModel) {
        selectedFirmName = firm.inspectionFirmName
        selectedFirmPhoto = firm.inspectionImageUrl

        binding.firmName.text = firm.inspectionFirmName
        binding.firmName2.text = firm.inspectionFirmName
        binding.firmAddress.text = firm.inspectionAddress
        binding.firmContact.text = firm.inspectionContact
        binding.firmWorkingHours.text = firm.inspectionWorkingHours
        binding.firmPriceList.text = firm.inspectionPriceList

        checkWorkingHours(firm.inspectionWorkingHours)

        val imageSlider = binding.imageSliderInspectionDetail
        val imageList = ArrayList<SlideModel>()

        if (firm.inspectionImageUrl.isNotEmpty()) {
            imageList.add(SlideModel(firm.inspectionImageUrl, ScaleTypes.FIT))
        } else {
            imageList.add(SlideModel(R.drawable.times_svgrepo_com_1_red, ScaleTypes.FIT))
        }

        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {}
            override fun onItemSelected(position: Int) {
                val itemMessage = "Selected Image $position"
                Toast.makeText(this@InspectionFirmDetailActivity, itemMessage, Toast.LENGTH_SHORT).show()
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
                    binding.imageView29.setImageResource(R.drawable.eclipse_green_firm_open)
                    binding.textView20.text = "OPEN"
                } else {
                    binding.imageView29.setImageResource(R.drawable.eclipse_red_firm_closed)
                    binding.textView20.setTextColor(resources.getColor(R.color.mainColor))
                    binding.textView20.text = "CLOSED"
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
                    onSuccess()
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
            val reservationId = firestore.collection("Reservations").document().id
            val reservationData = hashMapOf(
                "reservationId" to reservationId,
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
                    Toast.makeText(this, "Rezervasyon kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            firestore.collection("InspectionFirms").document(firmId!!)
                .collection("Reservations").document(reservationId)
                .set(reservationData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Firma rezervasyonu kaydedildi!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Firma rezervasyonu kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Eksik bilgiler: tarih, saat veya araç plakası.", Toast.LENGTH_SHORT).show()
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
            saveReservation()
        }
    }
}