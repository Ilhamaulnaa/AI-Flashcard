package com.ilham.domain.usecase

import com.ilham.domain.model.Deck
import com.ilham.domain.model.FlashCard
import com.ilham.domain.repository.AIFlashCardRepository
import com.ilham.domain.repository.FirebaseDeckRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenerateAIFlashCardUseCase @Inject constructor(
    private val repositoryAIFlashCard: AIFlashCardRepository,
    private val repositoryFirebaseDeck: FirebaseDeckRepository
) {

    fun observerDeck(deckId: String): Flow<Deck?> {
        return repositoryFirebaseDeck.observerDeck(deckId)
    }

    suspend fun generateCards(deckName: String, topic: String, cardCount: Int): List<FlashCard>{
        return repositoryAIFlashCard.generateFlashCards(
            deckName = deckName,
            topic = topic,
            cardCount = cardCount
        )
    }

    suspend fun saveCards(deckId: String, cards: List<FlashCard>){
        cards.forEach { card ->
            repositoryFirebaseDeck.addFlashCard(
                deckId = deckId,
                question = card.question,
                answer = card.answer
            )
        }
    }

}