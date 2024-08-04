
package com.eminsasmaz.otoworldd.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eminsasmaz.otoworldd.databinding.ActivityOnboarding1Binding

class Onboarding1Activity : AppCompatActivity() {
    private val binding:ActivityOnboarding1Binding by lazy {
        ActivityOnboarding1Binding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.skip1Button.setOnClickListener {
            val intent=Intent(this, Onboarding2Activity::class.java)
            startActivity(intent)
        }
    }

}
