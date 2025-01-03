package com.example.authlistapp.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.authlistapp.R
import com.example.authlistapp.databinding.ActivityLogInBinding
import com.example.authlistapp.utils.PasswordValidator
import com.example.authlistapp.utils.isValidEmailId
import com.example.myfirebaseapplication.utils.Preference
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLogInBinding.inflate(layoutInflater)

        val isUserLogin = Preference.getInstance(this).getData(Preference.Keys.isUserLogin, false)
        if (isUserLogin == true) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            binding.loader.visibility = View.VISIBLE
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            binding.loader.visibility = View.INVISIBLE
                            Preference.getInstance(this).putData(Preference.Keys.isUserLogin, true)
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finishAffinity()
                        } else {
                            binding.loader.visibility = View.INVISIBLE
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                binding.loader.visibility = View.INVISIBLE
                validation()
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun validation(): Boolean {
        when {
            binding.etEmail.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                return false
            }

            !isValidEmailId(binding.etEmail.text.toString().trim()) -> {
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
                return false
            }

            binding.etPassword.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                return false
            }

            !PasswordValidator.isValid(binding.etPassword.text.toString().trim()) -> {
                binding.tvError.text = getString(R.string.password_validation)
                binding.tvError.visibility = View.VISIBLE
                return false
            }

            else -> return true
        }
        return true
    }
}