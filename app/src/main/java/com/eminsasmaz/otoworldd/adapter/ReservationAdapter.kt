package com.eminsasmaz.otoworldd.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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

            // Duruma göre farklı metin ve renkler
            when (reservation.appointmentStatus) {
                "approved" -> {
                    binding.appointmentStatusImage.setImageResource(R.drawable.check_svgrepo_com_2_green)
                    binding.textView4.text = "Approved"
                    binding.textView4.setTextColor(ContextCompat.getColor(binding.root.context, R.color.successColor))
                }
                "waiting for approval" -> {
                    binding.appointmentStatusImage.setImageResource(R.drawable.waiting_svgrepo_com_1_yellow)
                    binding.textView4.text = "Waiting for Approval"
                    binding.textView4.setTextColor(ContextCompat.getColor(binding.root.context, R.color.waitingColor))
                }
                "canceled" -> {
                    binding.appointmentStatusImage.setImageResource(R.drawable.times_svgrepo_com_1_red)
                    binding.textView4.text = "Canceled"
                    binding.textView4.setTextColor(ContextCompat.getColor(binding.root.context, R.color.mainColor))
                }
            }

            // Resmi Picasso ile yükleme
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

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
