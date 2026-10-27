package com.ilham.domain.usecase

import com.ilham.domain.model.Deck
import com.ilham.domain.repository.AuthRepository
import com.ilham.domain.repository.FirebaseDeckRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeUseCase @Inject constructor(
    private val repositoryFirebaseDeck: FirebaseDeckRepository,
    private val repositoryFirebaseAuth: AuthRepository
) {

    fun observeDecks(): Flow<List<Deck>> {
        return repositoryFirebaseDeck.observerDecks()
    }

    suspend fun deleteDeck(deckId: String){
        repositoryFirebaseDeck.deleteDeck(deckId = deckId)
    }

    suspend fun signOut(){
        repositoryFirebaseAuth.signOut()
    }

}