package com.eminsasmaz.otoworldd.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eminsasmaz.otoworldd.databinding.MycarsItemBinding
import com.eminsasmaz.otoworldd.model.Vehicle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyCarsAdapter(
    private var items: MutableList<Vehicle>,  // Veriler mutable list olarak gelir
    private val onItemClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<MyCarsAdapter.MyCarsViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    inner class MyCarsViewHolder(val binding: MycarsItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.radioButton.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val selectedCar = items[currentPosition]

                    // Diğer tüm araçların 'selected' özelliğini false yap
                    items.forEach { it.selected = false }

                    // Seçilen aracın 'selected' özelliğini true yap
                    selectedCar.selected = true

                    // Firestore'da güncelle
                    updateSelectionInFirestore(selectedCar)

                    // Görselde güncelleme
                    notifyDataSetChanged() // Tüm öğeleri yeniden güncelle
                }
            }

            // Item tıklanırsa, detay sayfasına yönlendirme
            binding.root.setOnClickListener {
                val item = items[adapterPosition]
                onItemClick(item)
            }
        }

        fun bind(vehicle: Vehicle) {
            binding.carBrand.text = vehicle.brand
            binding.carLicensePlate.text = vehicle.licensePlate
            binding.carModel.text = vehicle.model

            // Seçilen aracın 'selected' bilgisine göre RadioButton'ı işaretle
            binding.radioButton.isChecked = vehicle.selected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCarsViewHolder {
        val binding = MycarsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyCarsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyCarsViewHolder, position: Int) {
        val vehicle = items[position]
        holder.bind(vehicle)
    }

    // Firestore'da 'selected' özelliğini güncelleyen metot
    private fun updateSelectionInFirestore(selectedCar: Vehicle) {
        val vehiclesRef = firestore.collection("Users").document(userId!!)
            .collection("Vehicles")

        // Tüm araçların 'selected' özelliğini false yap ve sadece seçilen aracı true yap
        items.forEach { vehicle ->
            val vehicleRef = vehiclesRef.document(vehicle.documentId!!)
            val isSelected = vehicle == selectedCar
            vehicleRef.update("selected", isSelected)
        }
    }

    // Firestore'dan veriyi dinlemek için kullanılır
    fun listenToVehicleData() {
        val vehiclesRef = firestore.collection("Users").document(userId!!)
            .collection("Vehicles")

        vehiclesRef.get().addOnSuccessListener { snapshot ->
            if (snapshot != null) {
                val updatedItems = snapshot.toObjects(Vehicle::class.java)
                items.clear()
                items.addAll(updatedItems)  // Firestore'dan gelen verilerle güncelle
                notifyDataSetChanged() // Adapter'ı yenile
            }
        }
    }
}
