package com.example.athleteos.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppLogo(
    size: Dp = 36.dp,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val context = LocalContext.current
    val painter = remember {
        try {
            context.assets.open("logo.png").use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) BitmapPainter(bitmap.asImageBitmap())
                else null
            }
        } catch (e: Exception) {
            null
        }
    }
    if (painter != null) {
        Image(
            painter = painter,
            contentDescription = "AthleteOS Logo",
            modifier = modifier.size(size),
            contentScale = contentScale
        )
    }
}
