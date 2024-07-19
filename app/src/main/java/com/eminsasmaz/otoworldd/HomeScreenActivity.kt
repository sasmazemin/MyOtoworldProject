
package com.eminsasmaz.otoworldd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.eminsasmaz.otoworldd.fragment.HomeFragment
import com.eminsasmaz.otoworldd.databinding.ActivityHomeScreenBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, HomeFragment())
                .commit()
        }
        var navController=findNavController(R.id.fragmentContainerView)
        var bottomNav=findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)



    }

}
