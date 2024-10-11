package com.eminsasmaz.otoworldd.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.eminsasmaz.otoworldd.databinding.FragmentDetailMyCarsBinding
import com.eminsasmaz.otoworldd.model.Vehicle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailMyCarsFragment : Fragment() {
    private lateinit var binding: FragmentDetailMyCarsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var selectedCarId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        arguments?.let {
            selectedCarId = it.getString("selectedCar") // selectedCar argümanını al
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailMyCarsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (selectedCarId.isNullOrEmpty()) {
            // Eğer yeni araç ekleniyorsa, butonları SAVE ve CANCEL olarak ayarla
            binding.saveButton.text = "SAVE"
            binding.cancelButton.text = "CANCEL"
            binding.saveButton.setOnClickListener { saveVehicleData() }
            binding.cancelButton.setOnClickListener { findNavController().popBackStack() } // CANCEL butonuna geri gitme işlevi
        } else {
            // Eğer bir araç güncelleniyorsa, butonları UPDATE ve DELETE olarak ayarla
            binding.saveButton.text = "UPDATE"
            binding.cancelButton.text = "DELETE"
            loadVehicleData(selectedCarId!!) // Araç bilgilerini yükle
            binding.saveButton.setOnClickListener { updateVehicleData(selectedCarId!!) }
            binding.cancelButton.setOnClickListener { deleteVehicleData(selectedCarId!!) } // DELETE işlevi
        }

        // Butonların her zaman görünür olduğundan emin olun
        binding.saveButton.visibility = View.VISIBLE
        binding.cancelButton.visibility = View.VISIBLE
    }

    private fun saveVehicleData() {
        val licensePlate = binding.licensePlateEditText.text.toString()
        val brand = binding.brandEditText.text.toString()
        val model = binding.modelEditText.text.toString()

        if (licensePlate.isNotEmpty() && brand.isNotEmpty() && model.isNotEmpty()) {
            val vehicle = Vehicle(licensePlate, brand, model)
            val userId = auth.currentUser?.uid

            if (userId != null) {
                val vehiclesCollection = firestore.collection("Users")
                    .document(userId)
                    .collection("Vehicles")

                vehiclesCollection.add(vehicle)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Araç kaydedildi!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack() // Ekleme sonrası geri git
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(requireContext(), "Lütfen tüm bilgileri doldurun!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadVehicleData(vehicleId: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val vehicleRef = firestore.collection("Users")
                .document(userId)
                .collection("Vehicles")
                .document(vehicleId)

            vehicleRef.get()
                .addOnSuccessListener { document ->
                    val vehicle = document.toObject(Vehicle::class.java)
                    if (vehicle != null) {
                        binding.licensePlateEditText.setText(vehicle.licensePlate)
                        binding.brandEditText.setText(vehicle.brand)
                        binding.modelEditText.setText(vehicle.model)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateVehicleData(vehicleId: String) {
        val licensePlate = binding.licensePlateEditText.text.toString()
        val brand = binding.brandEditText.text.toString()
        val model = binding.modelEditText.text.toString()

        if (licensePlate.isNotEmpty() && brand.isNotEmpty() && model.isNotEmpty()) {
            val updatedVehicle = Vehicle(licensePlate, brand, model)
            val userId = auth.currentUser?.uid

            if (userId != null) {
                val vehicleRef = firestore.collection("Users")
                    .document(userId)
                    .collection("Vehicles")
                    .document(vehicleId)

                vehicleRef.set(updatedVehicle)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Araç güncellendi!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(requireContext(), "Lütfen tüm bilgileri doldurun!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteVehicleData(vehicleId: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val vehicleRef = firestore.collection("Users")
                .document(userId)
                .collection("Vehicles")
                .document(vehicleId)

            vehicleRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Araç silindi!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
