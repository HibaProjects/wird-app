package com.hiba.wird.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiba.wird.ui.theme.*

@Composable
fun TasbihCard(
    count: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit
) {

    val scale = animateFloatAsState(
        targetValue = 1f,
        label = ""
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Night3
        ),

        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "Tasbih",
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "سُبْحَانَ اللَّهِ",
                    color = Gold,
                    fontSize = 22.sp
                )
            }

            Text(
                text = "$count",

                color = GoldLight,

                fontSize = 42.sp,

                modifier = Modifier.scale(
                    scale.value
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = onReset
                ) {
                    Text(
                        text = "Reset",
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                FloatingActionButton(
                    onClick = onIncrement,
                    containerColor = Gold
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TasbihCardPreview() {
    WirdTheme {
        TasbihCard(
            count = 0,
            onIncrement = {},
            onReset = {}
        )
    }
}
