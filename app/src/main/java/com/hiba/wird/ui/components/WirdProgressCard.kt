package com.hiba.wird.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiba.wird.ui.theme.*

@Composable
fun WirdProgressCard(
    title: String,
    progress: String,
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Night3
        ),
        modifier = Modifier.width(160.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                color = TextMain,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = progress,
                color = Gold,
                fontSize = 22.sp
            )
        }
    }
}