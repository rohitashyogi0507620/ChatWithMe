package com.yogify.chatwithme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.yogify.chatwithme.databinding.ActivitySingUpBinding
import java.util.regex.Matcher
import java.util.regex.Pattern


class SingUpActivity : AppCompatActivity() {
    var TAG = "SignUpActivity"
    lateinit var binding: ActivitySingUpBinding
    lateinit var viewModule: SingUpViewModule
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sing_up)
        viewModule = ViewModelProvider(this, SingUpViewModuleFactory(1)).get(SingUpViewModule::class.java)
        auth = Firebase.auth

        binding.btnsignup.setOnClickListener {
            var name = binding.etName.text.toString().trim()
            var email = binding.etEmailid.text.toString().trim()
            var password = binding.etPassword.text.toString().trim()
            if (validation()) {
                firebaseAuthWithEmailPassword(name, email, password)
            }

        }


    }

    private fun validation(): Boolean {
        var isvalid = true
        if (binding.etName.text.toString().trim().isEmpty()) {
            isvalid = false
            binding.ilName.isErrorEnabled = true
            binding.ilName.error = "Please Enter Name"
        } else if (!validEmail(binding.etEmailid.text.toString().trim())) {
            isvalid = false
            binding.ilEmailid.isErrorEnabled = true
            binding.ilEmailid.error = "Please Enter Valid Email"
            binding.ilName.isErrorEnabled = false


        } else if (binding.etPassword.text.toString().trim()
                .isEmpty() || isValidPassword(binding.etPassword.text.toString().trim())
        ) {
            isvalid = false
            binding.ilPassword.isErrorEnabled = true
            binding.ilPassword.error =
                "Password Contains One Capital , Number ,One Special and more then 6 digit"
            binding.ilEmailid.isErrorEnabled = false

        } else {
            binding.ilName.isErrorEnabled = false
            binding.ilEmailid.isErrorEnabled = false
            binding.ilPassword.isErrorEnabled = false

        }
        return isvalid
    }

    private fun validEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }


    private fun firebaseAuthWithEmailPassword(name: String, email: String, password: String) {
        binding.progessbar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val user: FirebaseUser = auth.getCurrentUser()!!
                        updateUI(user, name)

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        updateUI(null, "")
                    }
                })
    }

    private fun uploadInDatabase(user: FirebaseUser?, username: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(user!!.uid)
        myRef.setValue(username)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    "Someting went worng try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val value = dataSnapshot.getValue(String::class.java)
//                Log.d(TAG, "Value is: $value")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//        })
    }

    private fun updateUI(user: FirebaseUser?, name: String) {
        if (user != null) {
            uploadInDatabase(user, name)
        }
        binding.progessbar.visibility = View.GONE

    }

    override fun onStart() {
        super.onStart()
        var currentUser = auth.getCurrentUser()
        if (currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }
}