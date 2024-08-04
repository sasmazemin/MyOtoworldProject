
package com.eminsasmaz.otoworldd.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.eminsasmaz.otoworldd.R

class WelcomeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent=Intent(this, Onboarding1Activity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}
