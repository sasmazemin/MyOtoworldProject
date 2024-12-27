package com.eminsasmaz.otoworldd.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.eminsasmaz.otoworldd.fragment.DateTimePickerFragment
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ActivityTireFirmDetailBinding
import com.eminsasmaz.otoworldd.model.TireModel
import com.eminsasmaz.otoworldd.model.Vehicle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TireFirmDetailActivity : AppCompatActivity(), DateTimePickerFragment.DateTimePickerListener {
    private lateinit var binding: ActivityTireFirmDetailBinding
    private var selectedFirmName: String? = null
    private var selectedFirmPhoto: String? = null
    private var selectedVehiclePlate: String? = null
    private var selectedDateTime: String? = null
    private var firmId: String? = null // Intent'ten alınacak firmId
    private var firmType: String? = null // Intent'ten alınacak firmType
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTireFirmDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Intent'ten firmId ve firmType al
        firmId = intent.getStringExtra("firmId")
        firmType = intent.getStringExtra("firmType")

        if (firmId == null || firmType == null) {
            Toast.makeText(this, "Firma bilgisi eksik!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val firm = intent.getParcelableExtra<TireModel>("FIRM")
        firm?.let {
            setupFirmDetails(it)
        } ?: run {
            Toast.makeText(this, "Firm details not found", Toast.LENGTH_SHORT).show()
        }

        binding.textAppointment.setOnClickListener {
            val dialog = DateTimePickerFragment()
            dialog.show(supportFragmentManager, "DateTimePicker")
        }
    }

    private fun setupFirmDetails(firm: TireModel) {
        selectedFirmName = firm.tireFirmName
        selectedFirmPhoto = firm.tireImageUrl

        binding.firmName.text = firm.tireFirmName
        binding.firmName2.text = firm.tireFirmName
        binding.firmAddress.text = firm.tireAddress
        binding.firmContact.text = firm.tireContact
        binding.firmWorkingHours.text = firm.tireWorkingHours
        binding.firmPriceList.text = firm.tirePriceList

        checkWorkingHours(firm.tireWorkingHours)

        val imageSlider = binding.imageSliderTireDetail
        val imageList = ArrayList<SlideModel>()

        if (firm.tireImageUrl.isNotEmpty()) {
            imageList.add(SlideModel(firm.tireImageUrl, ScaleTypes.FIT))
        } else {
            imageList.add(SlideModel(R.drawable.times_svgrepo_com_1_red, ScaleTypes.FIT))
        }

        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {}
            override fun onItemSelected(position: Int) {
                val itemMessage = "Selected Image $position"
                Toast.makeText(this@TireFirmDetailActivity, itemMessage, Toast.LENGTH_SHORT).show()
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
                    binding.imageView35.setImageResource(R.drawable.eclipse_green_firm_open)
                    binding.textView21.text = "OPEN"
                } else {
                    binding.imageView35.setImageResource(R.drawable.eclipse_red_firm_closed)
                    binding.textView21.setTextColor(resources.getColor(R.color.mainColor))
                    binding.textView21.text = "CLOSED"
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
                    Toast.makeText(this, "Selected vehicle not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "No vehicle information available: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveReservation() {
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDateTime != null && selectedVehiclePlate != null) {
            val reservationId = firestore.collection("Reservations").document().id // reservationId oluşturuluyor
            val reservationData = hashMapOf(
                "reservationId" to reservationId,
                "appointmentStatus" to "waiting for approval", // Status başta 'waiting for approval' olarak atanır
                "selectedDateTime" to selectedDateTime,
                "selectedFirmName" to selectedFirmName,
                "selectedFirmPhoto" to selectedFirmPhoto,
                "selectedVehiclePlate" to selectedVehiclePlate,
                "userId" to userId,
                "firmId" to firmId
            )

            // Kullanıcı rezervasyonuna kaydet
            firestore.collection("Users").document(userId!!)
                .collection("Reservations")
                .document(reservationId) // reservationId kullanılarak kaydediliyor
                .set(reservationData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Reservation successfully saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save reservation: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // Firma rezervasyonuna kaydet
            firestore.collection(firmType!!).document(firmId!!)
                .collection("Reservations")
                .document(reservationId) // reservationId kullanılarak kaydediliyor
                .set(reservationData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Reservation successfully saved for the firm!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save firm reservation: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Missing date, time or vehicle information!", Toast.LENGTH_SHORT).show()
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