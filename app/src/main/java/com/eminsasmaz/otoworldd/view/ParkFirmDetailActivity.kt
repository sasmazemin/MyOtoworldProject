package com.eminsasmaz.otoworldd.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.eminsasmaz.otoworldd.databinding.ActivityParkFirmDetailBinding
import com.eminsasmaz.otoworldd.model.CarparkModel

class ParkFirmDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityParkFirmDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityParkFirmDetailBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)


        val firm = intent.getParcelableExtra<CarparkModel>("FIRM")
        firm?.let {
            binding.firmName.text = it.parkFirmName
            binding.firmName2.text=it.parkFirmName
            binding.firmAddress.text = it.parkAddress
            binding.firmContact.text = it.parkContact
            binding.firmWorkingHours.text = it.parkWorkingHours
            binding.firmPriceList.text = it.parkPriceList


        } ?: run {
            Toast.makeText(this, "Firm details not found", Toast.LENGTH_SHORT).show()
        }
}}