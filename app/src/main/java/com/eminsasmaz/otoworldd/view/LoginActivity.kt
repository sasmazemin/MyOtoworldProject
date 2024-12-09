
package com.eminsasmaz.otoworldd.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var showPassword = false // Şifre görünürlüğünü takip eden değişken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        // Şifre görünürlüğü için listener
        binding.passwordLoginText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // Sağdaki drawable
                if (event.rawX >= (binding.passwordLoginText.right - binding.passwordLoginText.compoundDrawables[drawableEnd].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Login butonuna tıklama işlemi
        binding.signInClicked.setOnClickListener {
            val email = binding.emailLoginText.text.toString().trim()
            val password = binding.passwordLoginText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun togglePasswordVisibility() {
        showPassword = !showPassword
        if (showPassword) {
            binding.passwordLoginText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordLoginText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.open_eye_see_watch_observe_svgrepo_com_1, 0)
        } else {
            binding.passwordLoginText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordLoginText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_svgrepo_com_1, 0)
        }
        binding.passwordLoginText.setSelection(binding.passwordLoginText.text.length)
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Giriş başarılı, userType kontrolü yapılacak
                    checkUserType(auth.currentUser!!.uid)
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserType(userId: String) {
        firestore.collection("Users") // Kullanıcıların olduğu Firestore koleksiyonu
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userType = document.getString("userType") // "userType" alanını kontrol ediyoruz
                    if (userType == "user") {
                        // Kullanıcı türü doğru, ana ekrana yönlendirme
                        navigateToUserHome()
                    } else {
                        Toast.makeText(this, "You must log in as a normal user.", Toast.LENGTH_SHORT).show()
                        auth.signOut() // Yanlış giriş yapan kullanıcıyı çıkış yaptırıyoruz
                    }
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
                auth.signOut()
            }
    }

    private fun navigateToUserHome() {
        val intent = Intent(this, HomeScreenActivity::class.java) // Kullanıcı ana ekranına yönlendirme
        startActivity(intent)
        finish()
    }

    fun dontHaveClicked(view: View) {
        val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun firmLoginClicked(view: View) {
        val intent = Intent(this@LoginActivity, FirmLoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

