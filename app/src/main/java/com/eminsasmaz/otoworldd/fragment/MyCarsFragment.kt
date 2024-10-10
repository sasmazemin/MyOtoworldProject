
package com.eminsasmaz.otoworldd.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.adapter.MyCarsAdapter
import com.eminsasmaz.otoworldd.databinding.FragmentHomeBinding
import com.eminsasmaz.otoworldd.databinding.FragmentMyCarsBinding
import androidx.navigation.fragment.findNavController
import com.eminsasmaz.otoworldd.model.Vehicle
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class MyCarsFragment : Fragment() {
    private lateinit var binding:FragmentMyCarsBinding
    private lateinit var myCarsAdapter:MyCarsAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var selectedCarId: String? = null  // Seçili aracın id'sini saklar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth=Firebase.auth
        firestore=Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentMyCarsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView için LayoutManager ayarla
        binding.mycarsRecyclerView.layoutManager = LinearLayoutManager(context)

        if (auth.currentUser != null) {
            val userId = auth.currentUser?.uid

            if (userId != null) {
                // Firestore'dan Vehicles koleksiyonundaki araçları çek
                firestore.collection("Users").document(userId).collection("Vehicles")
                    .get()
                    .addOnSuccessListener { documents ->
                        val vehicleList = mutableListOf<Vehicle>() // Boş bir liste oluştur

                        // Her belgeyi gez ve Vehicle objesine dönüştür
                        for (document in documents) {
                            val vehicle = document.toObject(Vehicle::class.java)
                            vehicle.documentId = document.id // documentId'yi ekle
                            vehicleList.add(vehicle) // Araç listesine ekle
                        }

                        // Adapter'ı kur ve RecyclerView'a bağla
                        myCarsAdapter = MyCarsAdapter(vehicleList) { selectedCar ->
                            val action = MyCarsFragmentDirections.actionMyCarsFragment2ToDetailMyCarsFragment2(selectedCar.documentId)
                            findNavController().navigate(action)
                        }
                        binding.mycarsRecyclerView.adapter = myCarsAdapter
                    }
                    .addOnFailureListener { exception ->
                        // Hata durumunda
                        Toast.makeText(context, "Hata: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        binding.addVehicleButton.setOnClickListener{
            // Yeni araç eklemek için DetailMyCarsFragment'a yönlendir
            val action = MyCarsFragmentDirections.actionMyCarsFragment2ToDetailMyCarsFragment2("")
            findNavController().navigate(action)
        }



    }


}
