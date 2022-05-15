package com.sit708.coupledApp.activities
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sit708.coupledApp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser
        if(user != null) {
            startActivity(MainActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    fun onLogin(v: View) {
        if(binding.emailET.text.toString().isNotEmpty() && binding.passwordET.text.toString().isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(binding.emailET.text.toString(), binding.passwordET.text.toString())
                .addOnCompleteListener { task ->
                    if(!task.isSuccessful) {
                        Toast.makeText(this, "Login error ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    companion object {
        fun newIntent(context: Context?) = Intent(context, LoginActivity::class.java)
    }
}
