package com.eminsasmaz.otoworldd.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ActivityTowFirmDetailBinding
import com.eminsasmaz.otoworldd.model.TireModel
import com.eminsasmaz.otoworldd.model.TowModel

class TowFirmDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTowFirmDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTowFirmDetailBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        val firm = intent.getParcelableExtra<TowModel>("FIRM")
        firm?.let {
            binding.firmName.text = it.towFirmName
            binding.firmName2.text=it.towFirmName
            binding.firmAddress.text = it.towAdress
            binding.firmContact.text = it.towContact
            binding.firmWorkingHours.text = it.towWorkingHours
            binding.firmPriceList.text = it.towPriceList


        } ?: run {
            Toast.makeText(this, "Firm details not found", Toast.LENGTH_SHORT).show()
        }
    }
}