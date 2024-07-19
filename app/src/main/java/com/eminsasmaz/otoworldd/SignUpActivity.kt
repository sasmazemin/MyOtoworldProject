
package com.eminsasmaz.otoworldd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.eminsasmaz.otoworldd.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var binding:ActivitySignUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        val view =binding.root
        setContentView(view)

        auth=Firebase.auth



    }

    fun alreadyHaveClicked(view: View){
        val intent=Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun signUpClicked(view: View){
        val email=binding.emailText.text.toString()
        val password=binding.passwordText.text.toString()
        val userName=binding.userNameText.text.toString()
        val confirmPassword=binding.confirmPasswordText.text.toString()

        if(email.equals("") || password.equals("") || userName.equals("") || confirmPassword.equals("")){
            Toast.makeText(this,"Enter email and password!",Toast.LENGTH_LONG).show()
        }else if(confirmPassword==password){
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent =Intent(this,HomeScreenActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }
}



/*

 */
