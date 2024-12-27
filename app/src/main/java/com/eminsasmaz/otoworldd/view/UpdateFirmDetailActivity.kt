package com.eminsasmaz.otoworldd.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ActivityUpdateFirmDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class UpdateFirmDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateFirmDetailBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var firmId: String? = null  // Güncellemek istediğiniz firma ID'si burada tutulur
    private var firmType: String? = null
    private var oldFirmName: String? = null // Eski firma adı

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateFirmDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Firestore ve FirebaseStorage referanslarını alıyoruz
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Intent ile gelen firma ID'sini ve türünü al
        firmId = intent.getStringExtra("firmId")
        firmType = intent.getStringExtra("firmType")

        // firmId ve firmType kontrolü
        if (firmId.isNullOrEmpty() || firmType.isNullOrEmpty()) {
            Toast.makeText(this, "Firm ID or Type is missing!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Firma detaylarını Firestore'dan çek
        fetchFirmDetails(firmId!!)

        // Güncelleme butonu tıklama işlemi
        binding.firmSignUpClicked.setOnClickListener {
            updateFirmDetails()
        }

        // Ana sayfaya dönme butonu
        binding.goToHomePage.setOnClickListener {
            val intent = Intent(this, FirmProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Firma detaylarını Firestore'dan çekme işlemi
    private fun fetchFirmDetails(firmId: String) {
        firestore.collection(firmType!!).document(firmId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Firestore verilerini alıp UI'ye yerleştiriyoruz
                    val firmName = document.getString(getFirmField("FirmName"))
                    oldFirmName = firmName // Eski firma ismini sakla
                    binding.firmMailText.setText(document.getString(getFirmField("Mail")))
                    binding.firmPasswordText.setText(document.getString(getFirmField("Password")))
                    binding.firmConfirmPasswordText.setText(document.getString(getFirmField("Password")))
                    binding.firmNameText.setText(firmName)
                    binding.firmAdressText.setText(document.getString(getFirmField("Address")))
                    binding.firmPhoneText.setText(document.getString(getFirmField("Contact")))
                    binding.firmWorkingHourText.setText(document.getString(getFirmField("WorkingHours")))
                    binding.firmPriceListText.setText(document.getString(getFirmField("PriceList")))

                    // Firma görselini Firebase Storage'dan al
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        val firmImageRef = storage.reference.child("$firmType" + "Images/${firmName}/$userId.jpg")
                        firmImageRef.downloadUrl.addOnSuccessListener { uri ->
                            Picasso.get().load(uri).into(binding.imageView48) // Görseli ImageView'a yerleştir
                        }.addOnFailureListener { exception ->
                            Toast.makeText(this, "Error loading image: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "No such document in Firestore!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Firma detaylarını güncelleme işlemi
    private fun updateFirmDetails() {
        val mail = binding.firmMailText.text.toString()
        val password = binding.firmPasswordText.text.toString()
        val firmName = binding.firmNameText.text.toString()
        val address = binding.firmAdressText.text.toString()
        val phone = binding.firmPhoneText.text.toString()
        val workingHours = binding.firmWorkingHourText.text.toString()
        val priceList = binding.firmPriceListText.text.toString()

        if (firmId != null) {
            // Güncelleme verilerini hazırlama
            val updatedData: Map<String, Any> = when (firmType) {
                "CarparkFirms" -> hashMapOf(
                    "parkMail" to mail,
                    "parkPassword" to password,
                    "parkFirmName" to firmName,
                    "parkAddress" to address,
                    "parkContact" to phone,
                    "parkWorkingHours" to workingHours,
                    "parkPriceList" to priceList
                )
                "InspectionFirms" -> hashMapOf(
                    "inspectionMail" to mail,
                    "inspectionPassword" to password,
                    "inspectionFirmName" to firmName,
                    "inspectionAddress" to address,
                    "inspectionContact" to phone,
                    "inspectionWorkingHours" to workingHours,
                    "inspectionPriceList" to priceList
                )
                "TireFirms" -> hashMapOf(
                    "tireMail" to mail,
                    "tirePassword" to password,
                    "tireFirmName" to firmName,
                    "tireAddress" to address,
                    "tireContact" to phone,
                    "tireWorkingHours" to workingHours,
                    "tirePriceList" to priceList
                )
                "TowFirms" -> hashMapOf(
                    "towMail" to mail,
                    "towPassword" to password,
                    "towFirmName" to firmName,
                    "towAddress" to address,
                    "towContact" to phone,
                    "towWorkingHours" to workingHours,
                    "towPriceList" to priceList
                )
                else -> return
            }

            // Firestore'a güncellenmiş veriyi gönder
            firestore.collection(firmType!!).document(firmId!!)
                .update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Firm details updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Invalid Firm ID", Toast.LENGTH_SHORT).show()
        }
    }

    // Firma türüne göre ilgili alan adlarını döndüren fonksiyon
    private fun getFirmField(field: String): String {
        return when (firmType) {
            "CarparkFirms" -> {
                when (field) {
                    "FirmName" -> "parkFirmName"
                    "Mail" -> "parkMail"
                    "Password" -> "parkPassword"
                    "Address" -> "parkAddress"
                    "Contact" -> "parkContact"
                    "WorkingHours" -> "parkWorkingHours"
                    "PriceList" -> "parkPriceList"
                    else -> ""
                }
            }
            "InspectionFirms" -> {
                when (field) {
                    "FirmName" -> "inspectionFirmName"
                    "Mail" -> "inspectionMail"
                    "Password" -> "inspectionPassword"
                    "Address" -> "inspectionAddress"
                    "Contact" -> "inspectionContact"
                    "WorkingHours" -> "inspectionWorkingHours"
                    "PriceList" -> "inspectionPriceList"
                    else -> ""
                }
            }
            "TireFirms" -> {
                when (field) {
                    "FirmName" -> "tireFirmName"
                    "Mail" -> "tireMail"
                    "Password" -> "tirePassword"
                    "Address" -> "tireAddress"
                    "Contact" -> "tireContact"
                    "WorkingHours" -> "tireWorkingHours"
                    "PriceList" -> "tirePriceList"
                    else -> ""
                }
            }
            "TowFirms" -> {
                when (field) {
                    "FirmName" -> "towFirmName"
                    "Mail" -> "towMail"
                    "Password" -> "towPassword"
                    "Address" -> "towAddress"
                    "Contact" -> "towContact"
                    "WorkingHours" -> "towWorkingHours"
                    "PriceList" -> "towPriceList"
                    else -> ""
                }
            }
            else -> ""
        }
    }
}
