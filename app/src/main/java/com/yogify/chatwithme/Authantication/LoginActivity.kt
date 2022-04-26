package com.yogify.chatwithme.Authantication

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yogify.chatwithme.MainActivity
import com.yogify.chatwithme.R
import com.yogify.chatwithme.databinding.ActivityLoginBinding
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var TAG = "LoginActivity"
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        auth = Firebase.auth
        binding.btnlogin.setOnClickListener {
            var email = binding.etEmailid.text.toString().trim()
            var password = binding.etPassword.text.toString().trim()
            if (validation()) {
                firebaseLoginWithEmailPassword(email, password)
            }

        }
        binding.btnsignupnow.setOnClickListener {
            startActivity(Intent(applicationContext, SingUpActivity::class.java))
        }
    }

    private fun validation(): Boolean {
        var isvalid = true
        if (!validEmail(binding.etEmailid.text.toString().trim())) {
            isvalid = false
            binding.ilEmailid.isErrorEnabled = true
            binding.ilEmailid.error = "Please Enter Valid Email"


        } else if (binding.etPassword.text.toString().trim()
                .isEmpty() || isValidPassword(binding.etPassword.text.toString().trim())
        ) {
            isvalid = false
            binding.ilPassword.isErrorEnabled = true
            binding.ilPassword.error =
                "Password Contains One Capital , Number ,One Special and more then 6 digit"
            binding.ilEmailid.isErrorEnabled = false

        } else {
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

    private fun firebaseLoginWithEmailPassword(email: String, password: String) {
        binding.progessbar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "User Not Found",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }

    }

    private fun updateUI(user: FirebaseUser?) {
        binding.progessbar.visibility = View.GONE
        if (user != null) {
            finish()
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        var currentUser = auth.getCurrentUser()
        updateUI(currentUser);
    }


}