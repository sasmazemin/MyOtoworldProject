
package com.eminsasmaz.otoworldd.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ActivitySignUpBinding
import com.eminsasmaz.otoworldd.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivitySignUpBinding
    private var showPassword = false
    private var showConfirmedPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // Firestore örneğini oluştur

        binding.passwordText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // Sağdaki drawable
                if (event.rawX >= (binding.passwordText.right - binding.passwordText.compoundDrawables[drawableEnd].bounds.width())) {
                    togglePasswordVisibility(binding.passwordText) // Şifre görünürlüğünü değiştir
                    return@setOnTouchListener true // Olayın işlendiğini belirt
                }
            }
            false
        }

        binding.confirmPasswordText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // Sağdaki drawable
                if (event.rawX >= (binding.confirmPasswordText.right - binding.confirmPasswordText.compoundDrawables[drawableEnd].bounds.width())) {
                    toggleConfirmedPasswordVisibility(binding.confirmPasswordText) // Şifre görünürlüğünü değiştir
                    return@setOnTouchListener true // Olayın işlendiğini belirt
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility(editText: EditText) {
        showPassword = !showPassword // Görünürlük durumunu tersine çevir

        if (showPassword) {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.open_eye_see_watch_observe_svgrepo_com_1, 0) // Şifreyi gösteren ikon
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_svgrepo_com_1, 0) // Şifreyi gizleyen ikon
        }

        // Metni seçili duruma getir
        editText.setSelection(editText.text.length)
    }

    private fun toggleConfirmedPasswordVisibility(editText: EditText) {
        showConfirmedPassword = !showConfirmedPassword // Görünürlük durumunu tersine çevir

        if (showConfirmedPassword) {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.open_eye_see_watch_observe_svgrepo_com_1, 0) // Şifreyi gösteren ikon
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_svgrepo_com_1, 0) // Şifreyi gizleyen ikon
        }

        // Metni seçili duruma getir
        editText.setSelection(editText.text.length)
    }

    fun alreadyHaveClicked(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun signUpClicked(view: View) {
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()
        val userName = binding.userNameText.text.toString()
        val confirmPassword = binding.confirmPasswordText.text.toString()

        if (email.isEmpty() || password.isEmpty() || userName.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Enter email and password!", Toast.LENGTH_LONG).show()
        } else if (confirmPassword == password) {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid // Kullanıcı ID'sini al
                val createdAt = Timestamp.now() // Mevcut zaman

                // Kullanıcı bilgilerini içeren UserModel nesnesi oluştur
                val userModel = UserModel(
                    createdAt = createdAt,
                    userEmail = email,
                    userName = userName,
                    userType = "user" // Kullanıcı türünü ekle
                )

                // Firestore'da kullanıcı bilgilerini kaydet
                userId?.let {
                    firestore.collection("Users").document(it).set(userModel)
                        .addOnSuccessListener {
                            // Kullanıcı bilgileri kaydedildi, ana ekrana git
                            val intent = Intent(this, HomeScreenActivity::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "Error saving user data: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}





/*

 */
