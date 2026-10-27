package com.ilham.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeneratedFlashCardsDto(
    val cards: List<GeneratedFlashCardDto>,
)

@Serializable
data class GeneratedFlashCardDto(
    val question: String,
    val answer: String,
)