package com.ilham.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.ilham.presentation.add_deck.AddDeckScreen
import com.ilham.presentation.add_flashcard.AddFlashCardScreen
import com.ilham.presentation.aigenerate.GenerateAIScreen
import com.ilham.presentation.detail.DeckDetailScreen
import com.ilham.presentation.home.HomeScreen
import com.ilham.presentation.home.HomeViewModel
import com.ilham.presentation.login.LoginScreen
import com.ilham.presentation.register.RegisterScreen
import com.ilham.presentation.splash.SplashScreen
import com.ilham.presentation.study_mode.StudyModeScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navigator: Navigator
) {

    val backStack = rememberNavBackStack(AppRoute.Login)

    val scope = rememberCoroutineScope()

    SideEffect {
        navigator.setBackState(backStack)
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { count ->
            repeat(count){
                navigator.navigateBack()
            }
        },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<AppRoute.Splash>{
                SplashScreen()
            }
            entry<AppRoute.Login>{
                LoginScreen(
                    onNavigateRegister = { navigator.navigateAndPopUpTo(AppRoute.Register) },
                    onNavigateHome = { navigator.navigateAndPopUpTo(AppRoute.Home) }
                )
            }
            entry<AppRoute.Register> {
                RegisterScreen(
                    onNavigateLogin = { navigator.navigateAndPopUpTo(AppRoute.Login) }
                )
            }
            entry<AppRoute.Home> {
                HomeScreen(
                    onDeckClick = { deck ->
                        navigator.navigateTo(AppRoute.DeckDetail(deck.id))
                    },
                    onAddDeckClick = {
                        navigator.navigateTo(AppRoute.AddDeck())
                    },
                    onLogoutSuccess = {
                        navigator.navigateTo(AppRoute.Login)
                    }
                )
            }
            entry<AppRoute.StudyMode> { route ->
                StudyModeScreen(
                    deckId = route.deckId,
                    onBack = { navigator.navigateBack() }
                )
            }
            entry<AppRoute.GenerateAI> { route ->
                GenerateAIScreen(
                    deckId = route.deckId,
                    navigateUp = { navigator.navigateBack() },
                    isSaveSuccess = {
                        scope.launch {
                            navigator.navigateBack()
                        }
                    }

                )
            }
            entry<AppRoute.AddDeck> { route ->
                AddDeckScreen(
                    deckId = route.deckId ?: "",
                    navigateUp = { navigator.navigateBack() },
//                    onNavigateHome = { navigator.navigateAndPopUpTo(AppRoute.Home) }
                )
            }
            entry<AppRoute.AddFlashCard> { route ->
                AddFlashCardScreen(
                    deckId = route.deckId ?: "",
                    cardId = route.cardId ?: "",
                    navigateUp = { navigator.navigateBack() }
                )
            }
            entry<AppRoute.DeckDetail> { route ->
                DeckDetailScreen(
                    deckId = route.deckId,
                    navigateUp = { navigator.navigateBack() },
                    onAddFlashCardClick = { navigator.navigateTo(AppRoute.AddFlashCard(deckId = route.deckId)) },
                    onStudyClick = { navigator.navigateTo(AppRoute.StudyMode(route.deckId)) },
                    onGenerateClick = { navigator.navigateTo(AppRoute.GenerateAI(deckId = route.deckId)) },
                    onEditFlashCardClick = { cardId ->
                        navigator.navigateTo(AppRoute.AddFlashCard(deckId = route.deckId, cardId = cardId))
                    },
                    onEditDeckClick = { navigator.navigateTo(AppRoute.AddDeck(route.deckId)) },
                )
            }
        }
    )

}