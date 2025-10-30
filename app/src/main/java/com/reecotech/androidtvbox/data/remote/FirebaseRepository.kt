package com.reecotech.androidtvbox.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val database: FirebaseDatabase
) {

    companion object {
        private const val PENDING_DEVICES_PATH = "pending_devices"
        private const val APPROVED_DEVICES_PATH = "approved_devices"
    }

    fun requestActivation(deviceId: String) {
        val deviceData = mapOf(
            "requestedAt" to System.currentTimeMillis(),
            "status" to "pending"
        )
        database.getReference(PENDING_DEVICES_PATH).child(deviceId).setValue(deviceData)
    }

    fun listenForActivation(deviceId: String): Flow<Boolean> = callbackFlow {
        val approvedDeviceRef = database.getReference(APPROVED_DEVICES_PATH).child(deviceId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        approvedDeviceRef.addValueEventListener(listener)

        awaitClose {
            approvedDeviceRef.removeEventListener(listener)
        }
    }
}
