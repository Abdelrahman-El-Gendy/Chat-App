package com.example.chatapp.core.data.remote

import com.example.chatapp.core.domain.model.Channel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseChannelService @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val channelsRef = database.getReference("channels")

    fun getChannels(): Flow<List<Channel>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val channels = snapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    val name = child.child("name").getValue(String::class.java) ?: ""
                    val description = child.child("description").getValue(String::class.java) ?: ""
                    val lastMessage = child.child("lastMessage").getValue(String::class.java)
                    val lastTimestamp = child.child("lastMessageTimestamp").getValue(Long::class.java) ?: 0L
                    val memberCount = child.child("memberCount").getValue(Int::class.java) ?: 0
                    Channel(
                        id = id,
                        name = name,
                        description = description,
                        lastMessage = lastMessage,
                        lastMessageTimestamp = lastTimestamp,
                        memberCount = memberCount
                    )
                }.sortedByDescending { it.lastMessageTimestamp }
                trySend(channels)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        channelsRef.addValueEventListener(listener)
        awaitClose { channelsRef.removeEventListener(listener) }
    }

    suspend fun createChannel(name: String, description: String): Channel {
        val key = channelsRef.push().key ?: throw Exception("Failed to create channel key")
        val channelData = mapOf(
            "name" to name,
            "description" to description,
            "lastMessage" to null,
            "lastMessageTimestamp" to System.currentTimeMillis(),
            "memberCount" to 1
        )
        channelsRef.child(key).setValue(channelData).await()
        return Channel(
            id = key,
            name = name,
            description = description,
            lastMessageTimestamp = System.currentTimeMillis(),
            memberCount = 1
        )
    }

    suspend fun getChannel(channelId: String): Channel? {
        val snapshot = channelsRef.child(channelId).get().await()
        if (!snapshot.exists()) return null
        val name = snapshot.child("name").getValue(String::class.java) ?: ""
        val description = snapshot.child("description").getValue(String::class.java) ?: ""
        return Channel(id = channelId, name = name, description = description)
    }

    suspend fun seedDefaultChannel() {
        val generalSnapshot = channelsRef.child("general").get().await()
        if (!generalSnapshot.exists()) {
            val channelData = mapOf(
                "name" to "General",
                "description" to "General discussion",
                "lastMessage" to "Welcome to the chat!",
                "lastMessageTimestamp" to System.currentTimeMillis(),
                "memberCount" to 0
            )
            channelsRef.child("general").setValue(channelData).await()
        }
    }
}
