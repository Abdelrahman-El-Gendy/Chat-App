package com.example.chatapp.core.data.remote

import com.example.chatapp.core.domain.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserService @Inject constructor(
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) {
    private val usersRef = database.getReference("users")

    suspend fun updateProfile(user: User) {
        val profileData = mapOf(
            "username" to user.username,
            "profilePictureUrl" to (user.profilePictureUrl ?: ""),
            "isOnline" to user.isOnline
        )
        usersRef.child(user.id).updateChildren(profileData).await()
    }

    fun getUserProfile(deviceId: String): Flow<User> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").getValue(String::class.java) ?: ""
                val avatarUrl = snapshot.child("profilePictureUrl").getValue(String::class.java)
                val isOnline = snapshot.child("isOnline").getValue(Boolean::class.java) ?: false
                trySend(
                    User(
                        id = deviceId,
                        username = username,
                        profilePictureUrl = avatarUrl,
                        isOnline = isOnline
                    )
                )
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        usersRef.child(deviceId).addValueEventListener(listener)
        awaitClose { usersRef.child(deviceId).removeEventListener(listener) }
    }

    suspend fun uploadAvatar(deviceId: String, fileBytes: ByteArray): String {
        val avatarRef = storage.getReference("avatars/$deviceId")
        avatarRef.putBytes(fileBytes).await()
        return avatarRef.downloadUrl.await().toString()
    }
}
