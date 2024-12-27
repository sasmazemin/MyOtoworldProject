package com.eminsasmaz.otoworldd.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.adapter.FirmProfileItemAdapter
import com.eminsasmaz.otoworldd.databinding.ActivityFirmProfileBinding
import com.eminsasmaz.otoworldd.model.FirmProfileItemModel

class FirmProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirmProfileBinding
    private lateinit var firmProfileItemAdapter: FirmProfileItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirmProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val firmProfileItems = listOf(
            FirmProfileItemModel(R.drawable.userred, "My Account", R.drawable.next),
            FirmProfileItemModel(R.drawable.reservationred, "Reservation", R.drawable.next),
            FirmProfileItemModel(R.drawable.helpandsupportred, "Help and Support", R.drawable.next),
            FirmProfileItemModel(R.drawable.signoutred, "Logout", R.drawable.next)
        )

        firmProfileItemAdapter = FirmProfileItemAdapter(firmProfileItems) { position ->
            when (position) {
                0 -> { // My Account
                    val sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
                    val firmId = sharedPreferences.getString("firmId", null)
                    val firmType = sharedPreferences.getString("firmType", null)

                    if (!firmId.isNullOrEmpty() && !firmType.isNullOrEmpty()) {
                        val intent = Intent(this, UpdateFirmDetailActivity::class.java)
                        intent.putExtra("firmId", firmId)
                        intent.putExtra("firmType", firmType)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Firm information is missing!", Toast.LENGTH_SHORT).show()
                    }
                }
                1 -> { // Reservation
                    val intent = Intent(this, FirmReservationActivity::class.java)
                    startActivity(intent)
                }
                2 -> { // Help and Support
                    // Şu anda devre dışı bırakılmış durumda
                    // val intent = Intent(this, HelpAndSupportActivity::class.java)
                    // startActivity(intent)
                }
                3 -> { // Logout
                    performLogout()
                }
            }
        }

        binding.firmProfileItemRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.firmProfileItemRecyclerView.adapter = firmProfileItemAdapter
    }

    private fun performLogout() {
        // Kullanıcı oturum kapatma işlemleri
        // Örnek: SharedPreferences temizleme veya Firebase oturumu kapatma
        val sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // SharedPreferences'taki bilgileri temizle
        editor.apply()

        // Giriş ekranına yönlendirme
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}