package com.solie.housekeeping_example.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

interface FirebaseData {
    val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    val userID
        get() = currentUser!!.uid

    val fireStoreCost
        get() = FirebaseFirestore.getInstance().collection(userID).document("Cost")

    val fireStoreIncrease
        get() = FirebaseFirestore.getInstance().collection(userID).document("MonthlyIncrease")

    val fireStoreDecrease
        get() = FirebaseFirestore.getInstance().collection(userID).document("MonthlyDecrease")

    val database
        get() = FirebaseDatabase.getInstance().getReference(userID)

    val time
        get() = Date(System.currentTimeMillis())

    val year
        get() = SimpleDateFormat("yyyy", Locale("ko", "KR"))

    val month
        get() = SimpleDateFormat("MM", Locale("ko", "KR"))

    val date
        get() = SimpleDateFormat("dd", Locale("ko", "KR"))

}

