
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

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var auth:FirebaseAuth
    private var showPassword = false // Şifre görünürlüğünü takip eden değişken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        auth=Firebase.auth

        // Drawable'ı tıklanabilir hale getirmek için OnTouchListener ekliyoruz
        binding.passwordLoginText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // Sağdaki drawable
                if (event.rawX >= (binding.passwordLoginText.right - binding.passwordLoginText.compoundDrawables[drawableEnd].bounds.width())) {
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
            binding.passwordLoginText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordLoginText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.open_eye_see_watch_observe_svgrepo_com_1, 0) // Şifreyi gösteren ikon
        } else {
            binding.passwordLoginText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordLoginText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_svgrepo_com_1, 0) // Şifreyi gizleyen ikon
        }

        // Metni seçili duruma getir
        binding.passwordLoginText.setSelection(binding.passwordLoginText.text.length)
    }
    fun dontHaveClicked(view: View){
        val intent=Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun signInClicked(view: View){
        val email=binding.emailLoginText.text.toString()
        val password=binding.passwordLoginText.text.toString()

        if(email.equals("") || password.equals("")){
            Toast.makeText(this,"Enter email and password",Toast.LENGTH_LONG).show()
        }else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent=Intent(this, HomeScreenActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

}
