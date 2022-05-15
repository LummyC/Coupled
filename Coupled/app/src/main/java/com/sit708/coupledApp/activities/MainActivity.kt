package com.sit708.coupledApp.activities
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sit708.coupledApp.R
import com.sit708.coupledApp.databinding.ActivityMainBinding
import com.sit708.coupledApp.fragments.DialogFlowFragment
import com.sit708.coupledApp.fragments.ProfileFragment
import com.sit708.coupledApp.util.DATA_USERS

class MainActivity : AppCompatActivity(), MainCallback {

    private lateinit var binding: ActivityMainBinding

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser?.uid

    private lateinit var userDatabase: DatabaseReference

    private var profileFragment: ProfileFragment? = null
    private var dialogflowFragment: DialogFlowFragment? = null

    private var profileTab: TabLayout.Tab? = null
    private var botTab: TabLayout.Tab? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(userId.isNullOrEmpty()) {
            onSignout()
        }

        /*Entry point for accessing the Firebase Database*/
        userDatabase = FirebaseDatabase.getInstance("https://coupled-b98b6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child(DATA_USERS)

        /*Make tabs that will hold fragments*/
        profileTab = binding.navigationTabs.newTab()
        botTab = binding.navigationTabs.newTab()

        /*Draw tab icons*/
        profileTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_profile)
        botTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_bot)

        /*Inserts tabs*/
        binding.navigationTabs.addTab(profileTab!!)
        binding.navigationTabs.addTab(botTab!!)

        /*Listen for user trying to change tabs*/
        binding.navigationTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab) {
                    profileTab -> {
                        if (profileFragment == null) {
                            profileFragment = ProfileFragment()
                            profileFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(profileFragment!!)
                    }
                    botTab -> {
                        if (dialogflowFragment == null) {
                            dialogflowFragment = DialogFlowFragment()
                            dialogflowFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(dialogflowFragment!!)
                    }
                }
            }

        })
        profileTab?.select()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onSignout() {
        firebaseAuth.signOut()
        startActivity(StartupActivity.newIntent(this))
        finish()
    }

    override fun onGetUserId(): String = userId!!

    override fun getUserDatabase(): DatabaseReference = userDatabase

    override fun profileComplete() {
        botTab?.select()
    }

    /*Companion objects provide a mechanism for defining variables or functions that are linked conceptually to a type but are not tied
    to a particular object. Companion objects are similar to using Java's static keyword for variables and methods.*/
    companion object {
        fun newIntent(context: Context?) = Intent(context, MainActivity::class.java)
    }
}
