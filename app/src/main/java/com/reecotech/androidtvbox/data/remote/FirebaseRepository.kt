package com.reecotech.androidtvbox.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reecotech.androidtvbox.data.model.DeviceStatus
import com.reecotech.androidtvbox.domain.FirebaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : FirebaseRepository {

    companion object {
        private const val DEVICES_PATH = "devices"
    }

    override fun requestActivation(deviceId: String) {
        val deviceData = mapOf(
            "status" to "pending",
            "createDay" to System.currentTimeMillis(),
            "updateDate" to System.currentTimeMillis(),
        )
        database.getReference(DEVICES_PATH).child(deviceId).setValue(deviceData)
    }

    override fun listenForDeviceStatus(deviceId: String): Flow<DeviceStatus> = callbackFlow {
        val deviceRef = database.getReference(DEVICES_PATH).child(deviceId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val deviceStatus = snapshot.getValue(DeviceStatus::class.java)
                trySend(deviceStatus ?: DeviceStatus(status = "pending")) // Default to pending if null
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        deviceRef.addValueEventListener(listener)

        awaitClose {
            deviceRef.removeEventListener(listener)
        }
    }
}
