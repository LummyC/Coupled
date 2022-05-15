package com.sit708.coupledApp.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.sit708.coupledApp.R
import com.sit708.coupledApp.util.Users
import com.sit708.coupledApp.activities.MainCallback
import com.sit708.coupledApp.databinding.FragmentProfileBinding
import com.sit708.coupledApp.util.DATA_EMAIL
import com.sit708.coupledApp.util.DATA_NAME

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private var callback: MainCallback? = null

    fun setCallback(callback: MainCallback) {
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase().child(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentProfileBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateInfo()

        binding.applyButton.setOnClickListener { onApply() }
        binding.signoutButton.setOnClickListener { callback?.onSignout() }
    }

    /*Populate profile fragment*/
    private fun populateInfo() {
        binding.progressLayout.visibility = View.VISIBLE
        userDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                binding.progressLayout.visibility = View.GONE
            }
            /*Modify name or email in database*/
            override fun onDataChange(p0: DataSnapshot) {
                if (isAdded) {
                    val user = p0.getValue(Users::class.java)
                    binding.nameET.setText(user?.name, TextView.BufferType.EDITABLE)
                    binding.emailET.setText(user?.email, TextView.BufferType.EDITABLE)
                    binding.progressLayout.visibility = View.GONE
                }
            }
        })
    }
    /*Apply button functionality*/
    private fun onApply() {
        if (binding.nameET.text.toString().isEmpty() ||
            binding.emailET.text.toString().isEmpty()
        ) {
            Toast.makeText(context, getString(R.string.error_profile_incomplete), Toast.LENGTH_SHORT).show()
        } else {
            val name = binding.nameET.text.toString()
            val email = binding.emailET.text.toString()

            userDatabase.child(DATA_NAME).setValue(name)
            userDatabase.child(DATA_EMAIL).setValue(email)

            callback?.profileComplete()
        }
    }
}
