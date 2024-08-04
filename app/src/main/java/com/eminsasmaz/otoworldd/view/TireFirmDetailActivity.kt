package com.eminsasmaz.otoworldd.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ActivityTireFirmDetailBinding
import com.eminsasmaz.otoworldd.model.InspectionModel
import com.eminsasmaz.otoworldd.model.TireModel

class TireFirmDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTireFirmDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTireFirmDetailBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        val firm = intent.getParcelableExtra<TireModel>("FIRM")
        firm?.let {
            binding.firmName.text = it.tireFirmName
            binding.firmName2.text=it.tireFirmName
            binding.firmAddress.text = it.tireAdress
            binding.firmContact.text = it.tireContact
            binding.firmWorkingHours.text = it.tireWorkingHours
            binding.firmPriceList.text = it.tirePriceList


        } ?: run {
            Toast.makeText(this, "Firm details not found", Toast.LENGTH_SHORT).show()
        }
    }

}