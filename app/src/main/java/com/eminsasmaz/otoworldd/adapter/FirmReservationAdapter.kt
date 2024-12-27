package com.eminsasmaz.otoworldd.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.FirmReservationItemBinding
import com.eminsasmaz.otoworldd.model.ReservationModel
import com.squareup.picasso.Picasso

class FirmReservationAdapter(
    private var items: List<ReservationModel>,
    private val onItemClick: (ReservationModel) -> Unit,
    private val onApproveClick: (ReservationModel) -> Unit,
    private val onRejectClick: (ReservationModel) -> Unit
) : RecyclerView.Adapter<FirmReservationAdapter.FirmReservationViewHolder>() {

    inner class FirmReservationViewHolder(val binding: FirmReservationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reservation: ReservationModel) {
            binding.textView1.text = reservation.selectedFirmName
            binding.textView2.text = reservation.selectedVehiclePlate
            binding.textView3.text = reservation.selectedDateTime

            // Duruma göre farklı buton davranışları
            when (reservation.appointmentStatus) {
                ReservationModel.STATUS_APPROVED -> {
                    binding.approveReservation.setImageResource(R.drawable.check_svgrepo_com_2_green)
                    binding.rejectReservation.visibility = View.INVISIBLE
                }
                ReservationModel.STATUS_WAITING -> {
                    binding.approveReservation.setImageResource(R.drawable.check_svgrepo_com_2_green)
                    binding.rejectReservation.setImageResource(R.drawable.times_svgrepo_com_1_red)
                    binding.rejectReservation.visibility = View.VISIBLE
                    binding.approveReservation.visibility = View.VISIBLE
                }
                ReservationModel.STATUS_REJECTED -> {
                    binding.rejectReservation.setImageResource(R.drawable.times_svgrepo_com_1_red)
                    binding.approveReservation.visibility = View.INVISIBLE
                    binding.rejectReservation.visibility = View.VISIBLE
                }
                else -> {
                    // Beklenmeyen bir durum
                    binding.approveReservation.visibility = View.INVISIBLE
                    binding.rejectReservation.visibility = View.INVISIBLE
                }
            }

            // Resmi Picasso ile yükleme
            Picasso.get().load(reservation.selectedFirmPhoto).into(binding.reservationFirmPhoto)

            // OnClickListener'lar
            binding.root.setOnClickListener {
                onItemClick(reservation)
            }

            binding.approveReservation.setOnClickListener {
                onApproveClick(reservation)
            }

            binding.rejectReservation.setOnClickListener {
                onRejectClick(reservation)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirmReservationViewHolder {
        val binding = FirmReservationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FirmReservationViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FirmReservationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateList(newItems: List<ReservationModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}