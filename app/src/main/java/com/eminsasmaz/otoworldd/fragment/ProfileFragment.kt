package com.eminsasmaz.otoworldd.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.adapter.ProfileItemAdapter
import com.eminsasmaz.otoworldd.databinding.FragmentProfileBinding
import com.eminsasmaz.otoworldd.model.ProfileItemModel
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.eminsasmaz.otoworldd.view.LoginActivity

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var profileItemAdapter: ProfileItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // RecyclerView için verileri hazırlama
        val profileItems = listOf(
            ProfileItemModel(R.drawable.userred, "My Account", R.drawable.next),
            ProfileItemModel(R.drawable.maycars, "My Cars", R.drawable.next),
            ProfileItemModel(R.drawable.paymentred, "My Payment Tools", R.drawable.next),
            ProfileItemModel(R.drawable.reservationred, "Reservation", R.drawable.next),
            ProfileItemModel(R.drawable.helpandsupportred, "Help and Support", R.drawable.next),
            ProfileItemModel(R.drawable.signoutred, "Logout", R.drawable.next)
        )



        // Adapter'ı ve RecyclerView'i ayarlama
        profileItemAdapter = ProfileItemAdapter(profileItems) { position ->
            //Eğer ilk item'a tıklandıysa
            if(position==0){
                if (position == 0) {
                    // İlk item tıklandığında MyAccountFragment'a git
                    findNavController().navigate(R.id.action_profileFragment2_to_myAccountFragment)
                }
            }
            if(position==3){
                //Reservation'a tıklandıysa
                findNavController().navigate(R.id.action_profileFragment2_to_reservationFragment)
            }
            // Eğer son item'e tıklandıysa (Logout)
            if (position == profileItems.size - 1) {
                FirebaseAuth.getInstance().signOut() // Firebase'den çıkış yap
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

                // LoginActivity'e geçiş yapma
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                // Mevcut aktiviteyi sonlandırma
                activity?.finish()
            }
        }

        binding.profileItemRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.profileItemRecyclerView.adapter = profileItemAdapter
    }
}
