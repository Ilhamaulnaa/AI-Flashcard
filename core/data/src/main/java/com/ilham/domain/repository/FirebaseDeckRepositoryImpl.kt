package com.ilham.domain.repository

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ilham.common.utils.getColorFromName
import com.ilham.common.utils.getEmojiFromName
import com.ilham.domain.model.Deck
import com.ilham.domain.model.FlashCard
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDeckRepositoryImpl @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
): FirebaseDeckRepository {

    override fun observerDecks(): Flow<List<Deck>> = callbackFlow {
        val uid = getCurrentUid()
        val registration = decksRef(uid)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null){
                    close(error)
                    return@addSnapshotListener
                }
                val documents = snapshots?.documents.orEmpty()
                documents
                    .filter { !it.contains("cardCount") }
                    .forEach { document ->
                        launch {
                            backfillCardCountIfMissing(document.reference)
                        }
                    }

                val decks = documents.map { document ->
                    Deck(
                        id = document.id,
                        name = document.getString("name").orEmpty(),
                        emoji = getEmojiFromName(document.getString("emoji").orEmpty()),
                        color = getColorFromName(document.getString("color").orEmpty()),
                        cards = emptyList(),
                        cardCount = document.getLong("cardCount")?.toInt() ?: 0
                    )
                }
                trySend(decks)
            }
        awaitClose { registration.remove() }
    }

    override fun observerDeck(deckId: String): Flow<Deck?> = callbackFlow {
        val uid = getCurrentUid()
        val deckDocument = decksRef(uid).document(deckId)
        val cardsCollection = deckDocument.collection(CARDS_COLLECTION)

        var currentDeckName: String? = null
        var currentDeckDescription: String? = null
        var currentDeckEmoji: String? = null
        var currentColor: String? = null
        var currentCards: List<FlashCard> = emptyList()

        fun sendDeck(){
            val name = currentDeckName
            val description = currentDeckDescription
            val emoji = currentDeckEmoji
            val color = currentColor

            trySend(
                if (name == null){
                    null
                } else {
                    Deck(
                        id = deckId,
                        name = name,
                        emoji = emoji!!,
                        description = description,
                        color = getColorFromName(color!!),
                        cards = currentCards,
                        cardCount = currentCards.size
                    )
                }
            )
        }

        val deckRegistration = deckDocument.addSnapshotListener { snapshot, error ->
            if (error != null){
                close(error)
                return@addSnapshotListener
            }
            currentDeckName = snapshot?.takeIf { it.exists() }?.getString("name")
            currentDeckDescription = snapshot?.takeIf { it.exists() }?.getString("description")
            currentDeckEmoji = snapshot?.takeIf { it.exists() }?.getString("emoji")
            currentColor = snapshot?.takeIf { it.exists() }?.getString("color")
            sendDeck()
        }

        val cardRegistration = cardsCollection
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error -> 
                if (error != null){
                    close(error)
                    return@addSnapshotListener
                }
                
                currentCards = snapshots?.documents.orEmpty().map { document ->
                    FlashCard(
                        id = document.id,
                        question = document.getString("question").orEmpty(),
                        answer = document.getString("answer").orEmpty()
                    )
                }
                sendDeck()
            }
        awaitClose {
            deckRegistration.remove()
            cardRegistration.remove()
        }
    }

    override suspend fun createDeck(
        name: String,
        description: String?,
        color: String,
        emoji: String
    ) {
        val uid = getCurrentUid()
        val now = Timestamp.now()
        val newDocRef = decksRef(uid).document()

        val deckData = mapOf(
            "id" to newDocRef.id,
            "name" to name,
            "description" to description,
            "color" to color,
            "emoji" to emoji,
            "cardCount" to 0,
            "createdAt" to now,
            "updateAt" to now
        )

        try {
            // Coba jalankan dengan timeout atau pastikan task-nya selesai
            newDocRef.set(deckData).await()
        } catch (e: Exception) {
            // Cetak error ke Logcat agar kita tahu kenapa .await() tidak tembus
            android.util.Log.e("FirebaseDebug", "Gagal createDeck: ${e.message}", e)
            throw e
        }
    }

//    override suspend fun createDeck(
//        name: String,
//        description: String?,
//        color: String,
//        emoji: String
//    ) {
//        val uid = getCurrentUid()
//        val now = Timestamp.now()
//
//        val newDocRef = decksRef(uid).document()
//
//        decksRef(uid).add(
//            mapOf(
//                "id" to newDocRef.id,
//                "name" to name,
//                "description" to description,
//                "color" to color,
//                "emoji" to emoji,
//                "cardCount" to 0,
//                "createdAt" to now,
//                "updateAt" to now
//            )
//        )
//
//    }

    override suspend fun updateDeck(
        deckId: String,
        name: String,
        description: String?,
        color: String,
        emoji: String
    ) {

        val uid = getCurrentUid()
        decksRef(uid).document(deckId).update(
            mapOf(
                "name" to name.trim(),
                "description" to description?.trim(),
                "color" to color,
                "emoji" to emoji,
                "updateAt" to Timestamp.now()
            )
        ).await()

    }

    override suspend fun deleteDeck(deckId: String) {
        val uid = getCurrentUid()
        val deckDocument = decksRef(uid).document(deckId)
        val cardDocuments = deckDocument.collection(CARDS_COLLECTION).get().await().documents
        firebaseFireStore.batch().apply {
            cardDocuments.forEach { cardDocument ->
                delete(cardDocument.reference)
            }
            delete(deckDocument)
        }.commit().await()
    }

    override suspend fun addFlashCard(
        deckId: String,
        question: String,
        answer: String
    ) {

        val uid = getCurrentUid()
        val now = Timestamp.now()
        val deckDocument = decksRef(uid).document(deckId)
        val newCardDocument = deckDocument.collection(CARDS_COLLECTION).document()
        firebaseFireStore.batch().apply {
            set(
                newCardDocument,
                mapOf(
                    "question" to question.trim(),
                    "answer" to answer.trim(),
                    "createdAt" to now,
                    "updateAt" to now
                )
            )
            update(
                deckDocument,
                mapOf(
                    "cardCount" to FieldValue.increment(1),
                    "updateAt" to now
                )
            )
        }.commit().await()
        
    }

    override suspend fun updateFlashCard(
        deckId: String,
        card: FlashCard
    ) {
        val uid = getCurrentUid()
        val now = Timestamp.now()
        decksRef(uid).document(deckId).collection(CARDS_COLLECTION).document(card.id).update(
            mapOf(
                "question" to card.question.trim(),
                "answer" to card.answer.trim(),
                "updateAt" to now
            )
        ).await()
    }

    override suspend fun deleteFlashCard(deckId: String, cardId: String) {
        val uid = getCurrentUid()
        val now = Timestamp.now()
        val deckDocument = decksRef(uid).document(deckId)
        val cardDocument = deckDocument.collection(CARDS_COLLECTION).document(cardId)

        firebaseFireStore.runTransaction { transaction ->
            val cardSnapshot = transaction.get(cardDocument)
            if (cardSnapshot.exists()){
                transaction.delete(cardDocument)
                transaction.update(
                    deckDocument,
                    mapOf(
                        "cardCount" to FieldValue.increment(-1),
                        "updateAt" to now
                    )
                )
            }
        }.await()
    }

    private fun decksRef(uid: String) =
        firebaseFireStore.collection(USERS_COLLECTION).document(uid).collection(DECKS_COLLECTION)

    private fun getCurrentUid(): String =
        firebaseAuth.currentUser?.uid ?: error("User must be signed in before accessing decks.")

    private suspend fun backfillCardCountIfMissing(deckDocument: DocumentReference){
        runCatching {
            val cardCount = deckDocument.collection(CARDS_COLLECTION).get().await().size()
            deckDocument.update(
                mapOf(
                    "cardCount" to cardCount,
                    "updateAt" to Timestamp.now()
                )
            ).await()
        }
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val DECKS_COLLECTION = "decks"
        const val CARDS_COLLECTION = "cards"
    }

}