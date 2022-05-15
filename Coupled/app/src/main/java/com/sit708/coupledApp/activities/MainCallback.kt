package com.sit708.coupledApp.activities
import com.google.firebase.database.DatabaseReference

interface MainCallback {
    fun onSignout()
    fun onGetUserId(): String
    fun getUserDatabase(): DatabaseReference
    fun profileComplete()
}