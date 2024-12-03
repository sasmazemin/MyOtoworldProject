package com.eminsasmaz.otoworldd.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.FragmentMyAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyAccountFragment : Fragment() {


    private lateinit var binding: FragmentMyAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        binding.imageView47.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_profileFragment2)
        }

        if (userId != null) {
            // Kullanıcı verilerini Firestore'dan çek
            firestore.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Firestore'dan çekilen verileri EditText'lere yerleştir
                        val userName = document.getString("userName")
                        val userEmail = document.getString("userEmail")
                        val userPhone = document.getString("userPhone")
                        val userLocation = document.getString("userLocation")

                        binding.profileNameText.setText(userName)
                        binding.profileEmailText.setText(userEmail)
                        binding.profilePhoneNumberText.setText(userPhone)
                        binding.profileLocationText.setText(userLocation)
                    } else {
                        Toast.makeText(activity, "No such document", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "Error fetching user data: ${e.message}", Toast.LENGTH_LONG).show()
                }

            // Kaydet butonuna tıklanma olayını ele al
            binding.profileSaveChanges.setOnClickListener {
                val updatedName = binding.profileNameText.text.toString()
                val updatedEmail = binding.profileEmailText.text.toString()
                val updatedPhone = binding.profilePhoneNumberText.text.toString()
                val updatedLocation = binding.profileLocationText.text.toString()

                if (updatedName.isNotEmpty() && updatedEmail.isNotEmpty()) {
                    // Firestore'da güncelleme yap
                    val userUpdates = mapOf(
                        "userName" to updatedName,
                        "userEmail" to updatedEmail,
                        "userPhone" to updatedPhone,
                        "userLocation" to updatedLocation
                    )

                    firestore.collection("Users").document(userId)
                        .update(userUpdates)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(activity, "Error updating profile: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(activity, "Name and Email are required", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(activity, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

}