package com.eminsasmaz.otoworldd.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ActivityFirmLoginBinding
import com.eminsasmaz.otoworldd.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FirmLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirmLoginBinding
    private lateinit var auth: FirebaseAuth
    private var showPassword = false // Şifre görünürlüğünü takip eden değişken
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirmLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        // Butonun tıklama olayını bağlama
        binding.FirmSignInClicked.setOnClickListener {
            signInClicked()
        }

        // Drawable'ı tıklanabilir hale getirmek için OnTouchListener ekliyoruz
        binding.firmPasswordLoginText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // Sağdaki drawable
                if (event.rawX >= (binding.firmPasswordLoginText.right - binding.firmPasswordLoginText.compoundDrawables[drawableEnd].bounds.width())) {
                    togglePasswordVisibility() // Şifre görünürlüğünü değiştir
                    return@setOnTouchListener true // Olayın işlendiğini belirt
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility() {
        showPassword = !showPassword // Görünürlük durumunu tersine çevir

        if (showPassword) {
            binding.firmPasswordLoginText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.firmPasswordLoginText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.open_eye_see_watch_observe_svgrepo_com_1, 0) // Şifreyi gösteren ikon
        } else {
            binding.firmPasswordLoginText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.firmPasswordLoginText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_svgrepo_com_1, 0) // Şifreyi gizleyen ikon
        }

        // Metni seçili duruma getir
        binding.firmPasswordLoginText.setSelection(binding.firmPasswordLoginText.text.length)
    }

    fun dontHaveFirmClicked(view: View) {
        val intent = Intent(this@FirmLoginActivity, FirmSignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun signInClicked() {
        val email = binding.firmEmailLoginText.text.toString()
        val password = binding.firmPasswordLoginText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_LONG).show()
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                // Kullanıcı girişi başarılıysa Firestore'dan userType'ı kontrol et
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val userRefCarpark = firestore.collection("CarparkFirms").document(currentUser.uid)
                    val userRefInspection = firestore.collection("InspectionFirms").document(currentUser.uid)
                    val userRefTire = firestore.collection("TireFirms").document(currentUser.uid)
                    val userRefTow = firestore.collection("TowFirms").document(currentUser.uid)

                    // Bu dört koleksiyonu kontrol edelim
                    checkUserInCollections(userRefCarpark, userRefInspection, userRefTire, userRefTow)
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkUserInCollections(vararg userRefs: DocumentReference) {
        val tasks = userRefs.map { userRef ->
            userRef.get()
        }

        Tasks.whenAllSuccess<DocumentSnapshot>(*tasks.toTypedArray()).addOnSuccessListener { results ->
            // Eğer kullanıcı herhangi bir koleksiyonda varsa ve userType "Firm" ise
            val currentUser = auth.currentUser
            for (result in results) {
                if (result.exists()) {
                    val userType = result.getString("userType")
                    if (userType == "Firm") {
                        // "Firm" kullanıcı tipi ise ana ekrana yönlendir
                        val intent = Intent(this, Onboarding1Activity::class.java)
                        startActivity(intent)
                        finish()
                        return@addOnSuccessListener
                    }
                }
            }

            // Eğer "Firm" tipi olmayan kullanıcı varsa ya da hiçbir koleksiyonda bulunamazsa
            Toast.makeText(this, "Access denied. User is not a firm.", Toast.LENGTH_LONG).show()
            auth.signOut() // Kullanıcıyı çıkart
        }.addOnFailureListener {
            Toast.makeText(this, "Error: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            auth.signOut()
        }
    }
}


