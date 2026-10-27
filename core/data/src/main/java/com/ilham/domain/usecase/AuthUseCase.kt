package com.ilham.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.ilham.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val repositoryAuth: AuthRepository
) {

    val authState: Flow<FirebaseUser?> = repositoryAuth.authState
    val currentUser: FirebaseUser? get() = repositoryAuth.currentUser

    suspend fun signIn(email: String, password: String){
        repositoryAuth.signIn(email, password)
    }

    suspend fun signUp(email: String, password: String){
        repositoryAuth.signUp(email, password)
    }

    suspend fun signOut(){
        repositoryAuth.signOut()
    }

}