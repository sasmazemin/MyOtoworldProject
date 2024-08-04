package com.eminsasmaz.otoworldd.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.eminsasmaz.otoworldd.databinding.ActivityInspectionFirmDetailBinding
import com.eminsasmaz.otoworldd.model.InspectionModel

class InspectionFirmDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityInspectionFirmDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityInspectionFirmDetailBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        val firm = intent.getParcelableExtra<InspectionModel>("FIRM")
        firm?.let {
            binding.firmName.text = it.inspectionFirmName
            binding.firmName2.text=it.inspectionFirmName
            binding.firmAddress.text = it.inspectionAdress
            binding.firmContact.text = it.inspectionContact
            binding.firmWorkingHours.text = it.inspectionWorkingHours
            binding.firmPriceList.text = it.inspectionPriceList


        } ?: run {
            Toast.makeText(this, "Firm details not found", Toast.LENGTH_SHORT).show()
        }
    }
}