package com.eminsasmaz.otoworldd.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eminsasmaz.otoworldd.databinding.ProfileItemBinding
import com.eminsasmaz.otoworldd.model.ProfileItemModel

class ProfileItemAdapter(private val profileItemList: List<ProfileItemModel>):RecyclerView.Adapter<ProfileItemAdapter.ProfileItemViewHolder>() {
    class ProfileItemViewHolder(val binding:ProfileItemBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileItemViewHolder {
        val binding=ProfileItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProfileItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return profileItemList.size
    }

    override fun onBindViewHolder(holder: ProfileItemViewHolder, position: Int) {
        val currentItem = profileItemList[position]

        // Görselleri ve metinleri ilgili alanlara bağlama
        holder.binding.imageView24.setImageResource(currentItem.imageResId) // Resmi set etme
        holder.binding.serviceName.text = currentItem.name // İsmi set etme
        holder.binding.imageView45.setImageResource(currentItem.nextIconResId) // İleri ikonunu set etme
    }
}