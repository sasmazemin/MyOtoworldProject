package com.eminsasmaz.otoworldd.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        // Intent ile gelen firma ID'sini al
        firmId = intent.getStringExtra("firmId") // Firma ID'si başka bir ekrandan alınır.
        firmType = intent.getStringExtra("firmType")

        if (firmId != null) {
            fetchFirmDetails(firmId!!)
        }

        // Firma detaylarını güncelleme butonuna tıklama işlemi
        binding.firmSignUpClicked.setOnClickListener {
            updateFirmDetails()
        }
    }

    // Firma detaylarını Firestore'dan çekme işlemi
    private fun fetchFirmDetails(firmId: String) {
        if (firmType != null) {
            firestore.collection(firmType!!).document(firmId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
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

                        // Firebase Authentication ile kullanıcı ID'sini alıyoruz
                        val userId = FirebaseAuth.getInstance().currentUser?.uid

                        if (userId != null) {
                            // Firebase Storage'dan firma görselini alıp ImageView'a yerleştiriyoruz
                            val firmImageRef = storage.reference.child("$firmType" + "Images/${binding.firmNameText.text}/$userId.jpg")
                            firmImageRef.downloadUrl.addOnSuccessListener { uri ->
                                Picasso.get().load(uri).into(binding.imageView48) // imageView48'e resmi yerleştiriyoruz
                            }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Firma türüne göre ilgili field isimlerini döndüren fonksiyon
    private fun getFirmField(field: String): String {
        return when (firmType) {
            "CarparkFirms" ->{
                when(field){
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
            "InspectionFirms" ->{
                when(field){
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
            else -> {
                "" // Diğer firma türleri için uygun field'ları ekleyebilirsiniz.
            }

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
            // updatedData'yı Map<String, Any> olarak tanımlıyoruz
            val updatedData: Map<String, Any> = when (firmType) {
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
                else -> return // Eğer geçerli bir firma türü yoksa çık
            }

            // Firestore'a güncellenmiş veriyi gönderiyoruz
            firestore.collection("$firmType").document(firmId!!)
                .update(updatedData)
                .addOnSuccessListener {
                    // Eski firma klasörünü sil ve yeni klasörü oluştur
                    oldFirmName?.let { oldName ->
                        val oldFirmImageRef = storage.reference.child("$firmType" + "Images/$oldName")
                        renameStorageFolder(oldFirmImageRef, firmName)
                    }
                    Toast.makeText(this, "Firm details updated successfully!", Toast.LENGTH_SHORT).show()
                    finish() // İşlem başarılıysa aktivitiyi kapat
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Invalid Firm ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun renameStorageFolder(oldFirmImageRef: StorageReference, newFirmName: String) {
        oldFirmImageRef.listAll().addOnSuccessListener { result ->
            for (item in result.items) {
                // Yeni klasöre taşıma
                val newFirmImageRef = storage.reference.child("$firmType" + "Images/$newFirmName/${item.name}")
                item.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                    newFirmImageRef.putBytes(bytes).addOnSuccessListener {
                        // Yeni dosya URL'sini al
                        newFirmImageRef.downloadUrl.addOnSuccessListener { newUri ->
                            // Firestore'da URL'yi güncelle
                            firmId?.let { id ->
                                val imageFieldName = when (firmType) {
                                    "CarparkFirms" -> "parkImageUrl"
                                    "InspectionFirms" -> "inspectionImageUrl"
                                    "TireFirms" -> "tireImageUrl"
                                    "TowFirms" -> "towImageUrl"
                                    else -> null
                                }
                                imageFieldName?.let { field ->
                                    firestore.collection(firmType!!).document(id)
                                        .update(field, newUri.toString())
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Firestore image URL updated successfully!", Toast.LENGTH_SHORT).show()
                                        }.addOnFailureListener { exception ->
                                            Toast.makeText(this, "Failed to update Firestore: ${exception.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }

                            // Eski dosyayı silme
                            item.delete().addOnSuccessListener {
                                // Tüm süreç başarıyla tamamlandı
                                oldFirmImageRef.delete().addOnSuccessListener {
                                    Toast.makeText(this, "Folder renamed and updated", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener { exception ->
                                Toast.makeText(this, "Failed to delete old file: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(this, "Failed to get new file URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to move file: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to download file: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to list items: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
