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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient? = null
    private var RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLogInBinding.inflate(layoutInflater)

        initGoogle()

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
                            Toast.makeText(
                                this,
                                "Login failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
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

        binding.btnSignIn.setOnClickListener {
            signIn()
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

    private fun signIn() {
        val intent = googleSignInClient?.signInIntent
        if (intent != null) {
            startActivityForResult(intent, RC_SIGN_IN)
        } else {
            Toast.makeText(this, "Google Sign-In client is not initialized", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                } else {
                    Toast.makeText(this, "Google Sign-In failed: Account is null", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed: ${e.statusCode}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        if (account == null) {
            Toast.makeText(this, "Google account is null", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                    Preference.getInstance(this).putData(Preference.Keys.isUserLogin, true)
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                } else {
                    Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


}