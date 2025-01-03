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
import com.example.authlistapp.databinding.ActivitySignUpBinding
import com.example.authlistapp.utils.PasswordValidator
import com.example.authlistapp.utils.isValidEmailId
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener { v->
            binding.loader.visibility = View.VISIBLE
            val email: String = binding.etEmail.getText().toString()
            val password: String = binding.etPassword.getText().toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                binding.loader.visibility = View.INVISIBLE
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LogInActivity::class.java))
                                finishAffinity()
                            } else {
                                binding.loader.visibility = View.INVISIBLE
                                Toast.makeText(
                                    this,
                                    "Registration failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

            } else {
                binding.loader.visibility = View.INVISIBLE
                validation()

            }
        }

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
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