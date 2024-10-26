package com.eminsasmaz.otoworldd.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.adapter.ReservationAdapter
import com.eminsasmaz.otoworldd.databinding.FragmentReservationBinding
import com.eminsasmaz.otoworldd.model.ProfileItemModel
import com.eminsasmaz.otoworldd.model.ReservationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ReservationFragment : Fragment() {

    private lateinit var binding: FragmentReservationBinding
    private lateinit var reservationAdapter: ReservationAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // RecyclerView ayarları
        reservationAdapter = ReservationAdapter(emptyList()) { reservation ->
            // Tıklama olayını burada yönetebilirsiniz (Örneğin detay sayfasına geçiş)
        }
        binding.reservationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.reservationRecyclerView.adapter = reservationAdapter

        fetchReservations() // Firebase'den verileri çekmek için
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Firebase Firestore'dan rezervasyon verilerini çekmek için metod
    private fun fetchReservations() {
        firestore.collection("Users").document(userId!!)
            .collection("Reservations")
            .get()
            .addOnSuccessListener { snapshot ->
                val reservations = snapshot.toObjects(ReservationModel::class.java)
                reservationAdapter = ReservationAdapter(reservations) { reservation ->
                    // Tıklama işlevi
                }
                binding.reservationRecyclerView.adapter = reservationAdapter
            }
            .addOnFailureListener { e ->
                // Hata durumunda mesaj göster
            }

    }
}