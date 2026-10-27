package com.ilham.presentation.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.common.theme.FlashcardTheme
import com.ilham.common.ui.ConfirmationButton
import com.ilham.common.ui.EmptyState
import com.ilham.common.ui.FlashButton
import com.ilham.component.DeckCard
import com.ilham.common.utils.rememberSnackBarHostState
import com.ilham.domain.model.Deck

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    onDeckClick: (Deck) -> Unit,
    onAddDeckClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) = with(viewModel) {

    val uiState by uiState.collectAsStateWithLifecycle()
    val snackBarHostState = rememberSnackBarHostState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.showSnackbar.collect { message ->
            focusManager.clearFocus(force = true)
            snackBarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut){
            onLogoutSuccess()
        }
    }
    
    HomeScreenContent(
        modifier = modifier,
        uiState = uiState,
        onDeckClick = onDeckClick,
        onAddDeckClick = onAddDeckClick,
        onLogoutSuccess = onLogoutSuccess,
        snackbarHostState = snackBarHostState
    )


}

@ExperimentalMaterial3Api
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    onDeckClick: (Deck) -> Unit,
    onAddDeckClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    snackbarHostState: SnackbarHostState = rememberSnackBarHostState()
) {

    var showLogoutConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FlashColors.Background,
        floatingActionButton = {
            FlashButton(text = "+ Deck Baru", onClick = onAddDeckClick)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        paddingValues ->

        if (uiState.decks.isEmpty()){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HomeHeader(
                    countDeck = uiState.decks.size,
                    onLogoutClick = { showLogoutConfirmation = true },
                    modifier = Modifier.padding(FlashSpacing.lg)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(FlashSpacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EmptyState(
                        title = "List deck is empty",
                        description = "please cek your deck or add new deck"
                    )
                }
            }
            return@Scaffold
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FlashColors.Background)
                    .padding(paddingValues),
                contentPadding = PaddingValues(FlashSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(FlashSpacing.md)
            ) {
                item {
                    HomeHeader(
                        countDeck = uiState.decks.size,
                        onLogoutClick = { showLogoutConfirmation = true}
                    )
                }
                itemsIndexed(
                    uiState.decks,
                    key = { _, deck ->
                        deck.id
                    }
                ){ index, deck ->
                    DeckCard(
                        deck = deck,
                        onClick = {
                            Log.d("DeckDebug", "Clicked Deck ID: ${deck.id}")
                            onDeckClick(deck)
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(FlashSpacing.xxl))
                }
            }
        }
    }

    if (showLogoutConfirmation){
        ConfirmationButton(
            title = "Logout",
            message = "anda yakin ingin logout?",
            onDismiss = {
                showLogoutConfirmation = false
            },
            onConfirm = {
                showLogoutConfirmation = false
                onLogoutSuccess()
            }
        )
    }
}

@Composable
fun HomeHeader(
    countDeck: Int,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Halo, Selamat Belajar!👋",
                modifier = Modifier.weight(1f),
                style = FlashTypography.displayLarge,
                color = FlashColors.Gray900
            )
            IconButton(
                onClick = onLogoutClick,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = FlashColors.Gray600
                )
            }
        }
        Spacer(modifier = Modifier.height(FlashSpacing.xs))
        Text(
            text = "Kamu punya ${countDeck} deck active",
            fontSize = 12.sp,
            color = FlashColors.Gray400,
            style = FlashTypography.bodyMedium
        )
        Spacer(modifier = Modifier.height(FlashSpacing.md))
    }

}

@Preview
@Composable
private fun HomeHeaderPreview() {
    FlashcardTheme {
        Surface{
            HomeHeader(
                1,
                {},
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }

}