package com.ilham.domain.usecase

import androidx.compose.ui.graphics.Color
import com.ilham.domain.model.Deck
import com.ilham.domain.model.FlashCard
import com.ilham.domain.repository.FirebaseDeckRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeckUseCase @Inject constructor(
    private val repositoryFirebaseDeck: FirebaseDeckRepository
) {

    fun observerDecks(): Flow<List<Deck>> {
        return repositoryFirebaseDeck.observerDecks()
    }

    fun observerDeck(deckId: String): Flow<Deck?> {
        return repositoryFirebaseDeck.observerDeck(deckId)
    }
    
    suspend fun createDeck(name: String, description: String?, color: String, emoji: String ){
        repositoryFirebaseDeck.createDeck(
            name = name,
            description = description,
            color = color,
            emoji = emoji
        )
    }
    
    suspend fun updateDeck(
        deckId: String,
        name: String,
        description: String?,
        color: String,
        emoji: String
    ){
        repositoryFirebaseDeck.updateDeck(
            deckId = deckId,
            name = name,
            description = description,
            color = color,
            emoji = emoji
        )
    }

    suspend fun deleteDeck(deckId: String){
        repositoryFirebaseDeck.deleteDeck(deckId)
    }

    suspend fun addFlashCard(
        deckId: String,
        question: String,
        answer: String
    ){
        repositoryFirebaseDeck.addFlashCard(
            deckId = deckId,
            question = question,
            answer = answer
        )
    }

    suspend fun updateFlashCard(
        deckId: String,
        card: FlashCard
    ){
        repositoryFirebaseDeck.updateFlashCard(
            deckId = deckId,
            card = card
        )
    }

    suspend fun deleteFlashCard(
        deckId: String,
        cardId: String
    ){
        repositoryFirebaseDeck.deleteFlashCard(
            deckId = deckId,
            cardId = cardId
        )
    }

}