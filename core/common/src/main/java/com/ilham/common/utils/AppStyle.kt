package com.ilham.common.utils

import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.checked
import androidx.compose.foundation.style.disabled
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.selected
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@ExperimentalFoundationStyleApi
object AppStyle {

    val buttonStyle = Style {
        background(Color.Green)
        shape(RoundedCornerShape(12.dp))
        fontSize(16.sp)
        fontWeight(FontWeight.Black)

        contentColor(Color.Black)

        contentPadding(16.dp)
        textAlign(TextAlign.Center)

        pressed {
            animate(
                spec = tween(durationMillis = 1000)
            ) {
                contentColor(Color.White)
                background(Color.Red)
            }
        }

        disabled {
            contentColor(Color.White)
            background(Color.Gray)
        }

        selected {

        }

        hovered {

        }

        checked {

        }

        focused {

        }

    }

    val outlinedButtonStyle = Style {
        border(width = 1.dp, Color.Blue)
        shape(RoundedCornerShape(12.dp))
        fontSize(16.sp)
        fontWeight(FontWeight.Black)

        contentColor(Color.Blue)

        contentPadding(16.dp)
        textAlign(TextAlign.Center)

        pressed {
            animate(
                spec = tween(durationMillis = 1000)
            ) {
                contentColor(Color.White)
                border(width = 1.dp, Color.Red)
            }
        }

        disabled {
            contentColor(Color.Blue)
            border(1.dp,Color.Gray)
        }

    }

}