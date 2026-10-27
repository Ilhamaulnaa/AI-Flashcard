package com.ilham.aiflash.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilham.common.theme.FlashcardTheme
import com.ilham.common.utils.AppStyle
import com.ilham.navigation.AppNavigation
import com.ilham.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalFoundationApi
@ExperimentalFoundationStyleApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ComposeFoundationFlags.isInheritedTextStyleEnabled = true
        setContent {
            FlashcardTheme {
                AppNavigation(
                    navigator = navigator
                )
            }
        }
    }
}

@ExperimentalFoundationStyleApi
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToMenuScreen: () -> Unit
) {

    var enable by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Title",
            fontSize = 16.sp,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Click here on down",
            fontSize = 12.sp,
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            imageVector = Icons.Filled.ArrowDownward,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
        )

        MyButtonCustom(
            text = "Continue",
            click = {
                enable = false
            },
            enabled = enable
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyOutlinedButtonCustom(
            text = "Cancel",
            click = {

            },
            enabled = enable
        )
    }

}

@ExperimentalFoundationStyleApi
@Composable
fun MyButtonCustom(
    modifier: Modifier = Modifier,
    text: String,
    click: () -> Unit,
    enabled: Boolean = true
) {

    val interactionSource = remember { MutableInteractionSource() }

    val style = AppStyle.buttonStyle

    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }

    LaunchedEffect(enabled) {
        styleState.isEnabled = enabled
    }

    Box(
        modifier = modifier
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = click,
            )
            .styleable(styleState, style),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = text
        )
    }

}

@ExperimentalFoundationStyleApi
@Composable
fun MyOutlinedButtonCustom(
    modifier: Modifier = Modifier,
    text: String,
    click: () -> Unit,
    enabled: Boolean = true
) {

    val interactionSource = remember { MutableInteractionSource() }

    val style = AppStyle.outlinedButtonStyle

    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }

    LaunchedEffect(enabled) {
        styleState.isEnabled = enabled
    }

    Box(
        modifier = modifier
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = click,
            )
            .styleable(styleState, style),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = text
        )
    }

}