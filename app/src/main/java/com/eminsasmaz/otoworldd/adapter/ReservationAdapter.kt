package com.eminsasmaz.otoworldd.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.ReservationItemBinding
import com.eminsasmaz.otoworldd.model.ReservationModel
import com.squareup.picasso.Picasso

class ReservationAdapter(
    private var items: List<ReservationModel>,
    private val onItemClick: (ReservationModel) -> Unit
) : RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    inner class ReservationViewHolder(val binding: ReservationItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reservation: ReservationModel) {
            binding.textView1.text = reservation.selectedFirmName
            binding.textView2.text = reservation.selectedVehiclePlate
            binding.textView3.text = reservation.selectedDateTime
            binding.textView4.text = if (reservation.appointmentStatus) "Approved" else "Pending"
            Picasso.get().load(reservation.selectedFirmPhoto).into(binding.reservationFirmPhoto)

            binding.root.setOnClickListener {
                onItemClick(reservation)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val binding = ReservationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        holder.bind(items[position])
    }

}