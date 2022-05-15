package com.sit708.coupledApp.activities
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sit708.coupledApp.databinding.ActivitySignupBinding
import com.sit708.coupledApp.util.DATA_USERS
import com.sit708.coupledApp.util.Users

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val firebaseDatabase = FirebaseDatabase.getInstance("https://coupled-b98b6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
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
        binding = ActivitySignupBinding.inflate(layoutInflater)
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

    fun onSignup(v: View) {
        if(binding.emailET.text.toString().isNotEmpty() && binding.passwordET.text.toString().isNotEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(binding.emailET.text.toString(), binding.passwordET.text.toString())
                .addOnCompleteListener { task ->
                    if(!task.isSuccessful) {
                        Toast.makeText(this, "Signup error ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    } else {
                        /*Add new user to database*/
                        val email = binding.emailET.text.toString()
                        val userId = firebaseAuth.currentUser?.uid ?: ""
                        val user = Users(userId, "", email)
                        firebaseDatabase.child(DATA_USERS).child(userId).setValue(user)
                    }
                }
        }
    }

    companion object {
        fun newIntent(context: Context?) = Intent(context, SignupActivity::class.java)
    }
}
