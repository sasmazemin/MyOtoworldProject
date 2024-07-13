package com.eminsasmaz.otoworldd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ActionMode
import android.view.View
import com.eminsasmaz.otoworldd.databinding.ActivityOnboarding3Binding
import java.nio.channels.InterruptedByTimeoutException

class Onboarding3Activity : AppCompatActivity() {
    private val binding :ActivityOnboarding3Binding by lazy {
        ActivityOnboarding3Binding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.getStartedButton.setOnClickListener {
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        binding.back2Button.setOnClickListener {
            val intent=Intent(this,Onboarding2Activity::class.java)
            startActivity(intent)
        }
    }

}