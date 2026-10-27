package com.ilham.domain.repository

import androidx.compose.ui.graphics.Color
import com.ilham.domain.model.Deck
import com.ilham.domain.model.FlashCard
import kotlinx.coroutines.flow.Flow

interface FirebaseDeckRepository {

    fun observerDecks(): Flow<List<Deck>>

    fun observerDeck(deckId: String): Flow<Deck?>

    suspend fun createDeck( name: String, description: String?, color: String, emoji: String )

    suspend fun updateDeck(
        deckId: String,
        name: String,
        description: String?,
        color: String,
        emoji: String
    )

    suspend fun deleteDeck(deckId: String)

    suspend fun addFlashCard(deckId: String, question: String, answer: String)

    suspend fun updateFlashCard(deckId: String, card: FlashCard)

    suspend fun deleteFlashCard(deckId: String, cardId: String)

}