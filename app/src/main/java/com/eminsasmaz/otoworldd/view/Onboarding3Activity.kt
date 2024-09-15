
package com.eminsasmaz.otoworldd.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eminsasmaz.otoworldd.databinding.ActivityOnboarding3Binding

class Onboarding3Activity : AppCompatActivity() {
    private val binding :ActivityOnboarding3Binding by lazy {
        ActivityOnboarding3Binding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.getStartedButton.setOnClickListener {
            val intent=Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.back2Button.setOnClickListener {
            val intent=Intent(this, Onboarding2Activity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
