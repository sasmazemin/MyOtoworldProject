
package com.eminsasmaz.otoworldd.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.fragment.HomeFragment
import com.eminsasmaz.otoworldd.databinding.ActivityHomeScreenBinding
import com.eminsasmaz.otoworldd.fragment.MyCarsFragment
import com.eminsasmaz.otoworldd.fragment.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        if (savedInstanceState == null) {
            val navController = findNavController(R.id.fragmentContainerView)
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

            // BottomNavigationView ile NavController'ı eşleştir
            bottomNav.setupWithNavController(navController)
        }






    }

}
