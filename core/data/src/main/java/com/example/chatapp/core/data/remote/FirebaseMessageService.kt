package com.example.chatapp.core.data.remote

import com.example.chatapp.core.data.remote.model.MessageDto
import com.example.chatapp.core.domain.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseMessageService @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val messagesRef = database.getReference("messages")

    suspend fun sendMessage(message: Message) {
        val dto = MessageDto.fromDomain(message)
        messagesRef.child(message.id).setValue(dto).await()
    }

    fun sendMessageNonSuspend(message: Message) {
        val dto = MessageDto.fromDomain(message)
        messagesRef.child(message.id).setValue(dto)
    }

    fun getMessages(limit: Int = 50): Flow<List<Message>> = callbackFlow {
        val query = messagesRef.orderByChild("timestamp").limitToLast(limit)
        
        android.util.Log.d("FirebaseService", "Setting up messages listener")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                android.util.Log.d("FirebaseService", "onDataChange: ${snapshot.childrenCount} messages")
                val messages = snapshot.children.mapNotNull { 
                    it.getValue(MessageDto::class.java)?.toDomain() 
                }
                trySend(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("FirebaseService", "onCancelled: ${error.message}")
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun getOlderMessages(lastTimestamp: Long, limit: Int = 20): List<Message> {
        val snapshot = messagesRef.orderByChild("timestamp")
            .endBefore(lastTimestamp.toDouble())
            .limitToLast(limit)
            .get()
            .await()

        return snapshot.children.mapNotNull { 
            it.getValue(MessageDto::class.java)?.toDomain() 
        }
    }

    suspend fun deleteMessage(messageId: String) {
        messagesRef.child(messageId).removeValue().await()
    }

    fun getTypingUsers(): Flow<List<String>> = callbackFlow {
        val typingRef = database.getReference("typing")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                trySend(users)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        typingRef.addValueEventListener(listener)
        awaitClose { typingRef.removeEventListener(listener) }
    }
}
