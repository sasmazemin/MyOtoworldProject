package com.eminsasmaz.otoworldd.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.eminsasmaz.otoworldd.R

class WelcomeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        // SharedPreferences'ı kontrol et
        val sharedPreferences: SharedPreferences = getSharedPreferences("onboarding_pref", MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true) // Varsayılan olarak true (ilk kez açıldığını varsayıyoruz)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (isFirstTime) {
                Intent(this, Onboarding1Activity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}
