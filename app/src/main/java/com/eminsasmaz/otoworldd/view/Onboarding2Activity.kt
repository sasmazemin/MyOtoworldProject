
package com.eminsasmaz.otoworldd.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eminsasmaz.otoworldd.databinding.ActivityOnboarding2Binding

class Onboarding2Activity : AppCompatActivity() {
    private val binding:ActivityOnboarding2Binding by lazy {
        ActivityOnboarding2Binding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.nextButton.setOnClickListener {
            val intent=Intent(this, Onboarding3Activity::class.java)
            startActivity(intent)
            finish()
        }
        binding.skip2Button.setOnClickListener {
            val intent=Intent(this, Onboarding3Activity::class.java)
            startActivity(intent)
            finish()
        }
        binding.back1Button.setOnClickListener {
            val intent=Intent(this, Onboarding1Activity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
