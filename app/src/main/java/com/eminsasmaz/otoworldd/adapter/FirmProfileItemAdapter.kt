package com.eminsasmaz.otoworldd.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eminsasmaz.otoworldd.databinding.FirmProfileItemBinding
import com.eminsasmaz.otoworldd.model.FirmProfileItemModel

class FirmProfileItemAdapter(
    private val firmProfileItemList: List<FirmProfileItemModel>,
    private val onItemClick: (Int) -> Unit // Tıklama olayını dinlemek için lambda ekledik
):RecyclerView.Adapter<FirmProfileItemAdapter.FirmProfileViewHolder>(){

    class FirmProfileViewHolder(val binding:FirmProfileItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirmProfileViewHolder {
        val binding = FirmProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FirmProfileViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return firmProfileItemList.size
    }

    override fun onBindViewHolder(holder: FirmProfileViewHolder, position: Int) {
        val currentItem = firmProfileItemList[position]

        // Görselleri ve metinleri ilgili alanlara bağlama
        holder.binding.imageView24.setImageResource(currentItem.imageResId) // Resmi set etme
        holder.binding.serviceName.text = currentItem.name // İsmi set etme
        holder.binding.imageView45.setImageResource(currentItem.nextIconResId) // İleri ikonunu set etme

        // Item tıklama olayını dinleme
        holder.itemView.setOnClickListener {
            onItemClick(position) // Tıklanan pozisyonu geri döndürme
        }
    }
}