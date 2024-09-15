package com.eminsasmaz.otoworldd.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.eminsasmaz.otoworldd.DateTimePickerFragment
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ActivityTowFirmDetailBinding
import com.eminsasmaz.otoworldd.model.TireModel
import com.eminsasmaz.otoworldd.model.TowModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TowFirmDetailActivity : AppCompatActivity(), DateTimePickerFragment.DateTimePickerListener {
    private lateinit var binding:ActivityTowFirmDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTowFirmDetailBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        val textAppointment: TextView = findViewById(R.id.text_appointment)
        textAppointment.setOnClickListener {
            val dialog = DateTimePickerFragment()
            dialog.show(supportFragmentManager, "DateTimePicker")
        }

        val firm = intent.getParcelableExtra<TowModel>("FIRM")
        firm?.let {
            binding.firmName.text = it.towFirmName
            binding.firmName2.text=it.towFirmName
            binding.firmAddress.text = it.towAdress
            binding.firmContact.text = it.towContact
            binding.firmWorkingHours.text = it.towWorkingHours
            binding.firmPriceList.text = it.towPriceList

            val towWorkingHours = it.towWorkingHours.split("-")

            val openingTime = towWorkingHours[0] // "09:00"
            val closingTime = towWorkingHours[1] // "20:00"

// Saat formatı
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

// Şu anki zamanı al
            val currentTime = Calendar.getInstance()

// Opening ve Closing Time'ları Date objesine çevir
            val openingDate = timeFormat.parse(openingTime)
            val closingDate = timeFormat.parse(closingTime)

            if (openingDate != null && closingDate != null) {
                // Opening time için Calendar ayarla
                val openingCalendar = Calendar.getInstance()
                openingCalendar.time = openingDate
                openingCalendar.set(Calendar.YEAR, currentTime.get(Calendar.YEAR))
                openingCalendar.set(Calendar.MONTH, currentTime.get(Calendar.MONTH))
                openingCalendar.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH))

                // Closing time için Calendar ayarla
                val closingCalendar = Calendar.getInstance()
                closingCalendar.time = closingDate
                closingCalendar.set(Calendar.YEAR, currentTime.get(Calendar.YEAR))
                closingCalendar.set(Calendar.MONTH, currentTime.get(Calendar.MONTH))
                closingCalendar.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH))

                if (currentTime.after(openingCalendar) && currentTime.before(closingCalendar)) {
                    binding.imageView41.setImageResource(R.drawable.eclipse_green_firm_open)
                    binding.textView22.text="OPEN"// Çalışma saatleri içerisindeyse
                } else {
                    binding.imageView41.setImageResource(R.drawable.eclipse_red_firm_closed)
                    binding.textView22.setTextColor(getResources().getColor(R.color.mainColor))
                    binding.textView22.text="CLOSED"// Çalışma saatleri içerisinde değilse
                }
            }

            val imageSlider = binding.imageSliderTowDetail

            // SlideModel listesi oluşturma
            val imageList = ArrayList<SlideModel>()

            // Firebase'den gelen URL'yi SlideModel'e ekleme
            imageList.add(SlideModel(it.towImageUrl, ScaleTypes.FIT))

            // Image Slider'a ekleme
            imageSlider.setImageList(imageList, ScaleTypes.FIT)

            imageSlider.setItemClickListener(object : ItemClickListener {
                override fun doubleClick(position: Int) {
                    // İsteğe bağlı, çift tıklama için işlem yapılabilir
                }

                override fun onItemSelected(position: Int) {
                    val itemPosition = imageList[position]
                    val itemMessage = "Selected Image $position"
                    Toast.makeText(this@TowFirmDetailActivity, itemMessage, Toast.LENGTH_SHORT).show()
                }
            })


        } ?: run {
            Toast.makeText(this, "Firm details not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDateTimeSelected(date: String, time: String) {
        Toast.makeText(this, "Seçilen Tarih: $date, Seçilen Saat: $time", Toast.LENGTH_SHORT).show()
    }
}