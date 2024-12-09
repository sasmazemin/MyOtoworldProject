package com.eminsasmaz.otoworldd.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ActivityFirmSignUpBinding
import com.eminsasmaz.otoworldd.databinding.ActivitySignUpBinding
import com.eminsasmaz.otoworldd.fragment.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage

class FirmSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirmSignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirmSignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Firebase başlatma
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Spinner tanımlama
        val spinner: Spinner = binding.firmTypeSpinner
        val firmTypes = arrayOf("CarparkFirms", "InspectionFirms", "TireFirms", "TowFirms")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, firmTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Resim seçme işlemi
        binding.imageView48.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestPermission()
            }
        }

        // Sign Up Button'a tıklama işlemi
        binding.firmSignUpClicked.setOnClickListener {
            signUpUser()
        }
    }

    private fun checkPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data
            binding.imageView48.setImageURI(imageUri)
        }
    }

    private fun signUpUser() {
        val email = binding.firmMailText.text.toString().trim()
        val password = binding.firmPasswordText.text.toString().trim()
        val confirmPassword = binding.firmConfirmPasswordText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val firmName = binding.firmNameText.text.toString().trim()
                    val firmType = binding.firmTypeSpinner.selectedItem.toString()
                    val firmAddress = binding.firmAdressText.text.toString().trim()
                    val firmPhone = binding.firmPhoneText.text.toString().trim()
                    val firmWorkingHours = binding.firmWorkingHourText.text.toString().trim()
                    val firmPriceList = binding.firmPriceListText.text.toString().trim()
                    val latitude = binding.firmLatitudeText.text.toString().toDoubleOrNull()
                    val longitude = binding.firmLongitudeText.text.toString().toDoubleOrNull()

                    val location = if (latitude != null && longitude != null) {
                        GeoPoint(latitude, longitude)
                    } else null

                    val firmData = hashMapOf(
                        "userType" to "Firm",
                        "parkFirmName" to firmName,
                        "parkType" to firmType,
                        "parkAddress" to firmAddress,
                        "parkContact" to firmPhone,
                        "parkWorkingHours" to firmWorkingHours,
                        "parkPriceList" to firmPriceList,
                        "location" to location,
                        "parkMail" to email,
                        "parkPassword" to password,
                        "parkStatus" to true
                    )

                    val firmRef = firestore.collection(firmType).document(user!!.uid)

                    firmRef.set(firmData)
                        .addOnSuccessListener {
                            uploadImageToFirebase(firmType, firmName, user.uid)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadImageToFirebase(firmType: String, firmName: String, userId: String) {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase Storage'a doğru klasöre yükleme yapıyoruz.
        // "CarparkFirmsImages" gibi bir klasöre görseli yüklüyoruz
        val storageRef = storage.reference.child("$firmType" + "Images/$firmName/$userId.jpg")

        // Görseli Firebase Storage'a yüklüyoruz
        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                // Yükleme başarılı olursa, image URL'sini alıp Firestore'a kaydediyoruz
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    firestore.collection(firmType).document(userId)
                        .update("parkImageUrl", imageUrl)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Onboarding1Activity::class.java)
                            startActivity(intent)
                            finish()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}


