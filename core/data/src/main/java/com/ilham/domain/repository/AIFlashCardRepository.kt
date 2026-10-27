package com.ilham.domain.repository

import com.ilham.domain.model.FlashCard

interface AIFlashCardRepository {

    suspend fun generateFlashCards(
        deckName: String,
        topic: String,
        cardCount: Int
    ) : List<FlashCard>
}