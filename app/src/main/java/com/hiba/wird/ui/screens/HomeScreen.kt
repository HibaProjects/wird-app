package com.hiba.wird.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiba.wird.ui.components.AyahCard
import com.hiba.wird.ui.components.TasbihCard
import com.hiba.wird.ui.components.WirdProgressCard
import com.hiba.wird.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(

) {
    var tasbihCount by rememberSaveable { mutableIntStateOf(0) }


    var sabahDone  by remember { mutableIntStateOf(0) }
    var masaaDone  by remember { mutableIntStateOf(0) }
    var prayersDone by remember { mutableIntStateOf(0) }
    val sabahTotal = sabahList.size   // 5
    val masaaTotal = masaaList.size   // 5

    val today = LocalDate.now()
    val date  = today.toString()
    val dateFormatted = today.format(
        DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH)
    )

//  LaunchedEffect
    var sadaqaIdea by remember { mutableStateOf("") }

    // runs once when HomeScreen opens, loads adkar progress from Firebase
    LaunchedEffect(Unit) {
        loadAdkarFromFirebase(date, "sabah", sabahList) {
            saved -> sabahDone = saved.count {
                (key, count) -> val dhikr = sabahList.find { it.key == key }
                count >= (dhikr?.maxCount ?: 1)
            }
        }
        loadAdkarFromFirebase(date, "masaa", masaaList) { saved ->
            masaaDone = saved.count { (key, count) ->
                val dhikr = masaaList.find { it.key == key }
                count >= (dhikr?.maxCount ?: 1)
            }
        }
//        savedMap ressemble à :
//{
//        "fajr" = true,
//        "dhohr" = false,
//        "asr" = true
//}
        loadPrayersFromFirebase(date) { savedMap ->
            prayersDone = savedMap.count { it.value }
        }
        loadSadaqaFromFirebase(date) { idea ->
            sadaqaIdea = idea
        }


    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Night)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // ── Header ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Gold.copy(alpha = 0.07f),
                            Night.copy(alpha = 0f)
                        )
                    )
                )
                .padding(start = 22.dp, end = 22.dp, top = 52.dp, bottom = 20.dp)
        ) {
            Column {
                Text(
                    text = "السلام عليكم",
                    color = Gold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Wird",
                    color = TextMain,
                    fontSize = 36.sp,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = dateFormatted.replaceFirstChar { it.uppercase() },
                    color = TextMuted,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }


        Spacer(Modifier.height(22.dp))

        // ── Verset du jour ────────────────────────────────
        Column(Modifier.padding(horizontal = 22.dp)) {
            SectionLabel("Verset du jour")
            Spacer(Modifier.height(10.dp))
            AyahCard()
        }

        Spacer(Modifier.height(24.dp))

        // ── Adkar ─────────────────────────────────────────
        Column {
            SectionLabel("Adkar", modifier = Modifier.padding(horizontal = 22.dp))
            Spacer(Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    WirdProgressCard(
                        title = "Adkar Matin",
                        progress = "$sabahDone / $sabahTotal",
                    )
                }
                item {
                    WirdProgressCard(
                        title = "Adkar Soir",
                        progress = "$masaaDone / $masaaTotal",
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        // ── Tasbih ────────────────────────────────────────
        Column(Modifier.padding(horizontal = 22.dp)) {
            SectionLabel("Tasbih")
            Spacer(Modifier.height(10.dp))
            TasbihCard(
                count = tasbihCount,
                onIncrement = { tasbihCount++ },
                onReset = { tasbihCount = 0 }
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Stats rapides ─────────────────────────────────
        Column(Modifier.padding(horizontal = 22.dp)) {
            SectionLabel("Aujourd'hui")
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickStat(
                    modifier = Modifier.weight(1f),
                    icon = "🌙",
                    label = "Prières",
                    value = "$prayersDone/5"                )

                QuickStat(
                    modifier = Modifier.weight(1f),
                    icon = "🎁",
                    label = "Sadaqa",
                    value = if (sadaqaIdea.isEmpty()) "—"
                    else sadaqaIdea.take(10) + if (sadaqaIdea.length > 10) "…" else ""
                )            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = TextMuted,
        fontSize = 10.sp,
        letterSpacing = 2.sp,
        modifier = modifier
    )
}

@Composable
private fun QuickStat(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Gold.copy(alpha = 0.05f))
            .border(0.5.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(Modifier.height(6.dp))
        Text(text = value, color = TextMain, fontSize = 14.sp,
            fontFamily = FontFamily.Serif, textAlign = TextAlign.Center)
        Spacer(Modifier.height(2.dp))
        Text(text = label, color = TextMuted, fontSize = 10.sp, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WirdTheme {
        HomeScreen()
    }
}
