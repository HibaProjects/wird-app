package com.hiba.wird.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import com.hiba.wird.ui.theme.*
import java.time.LocalDate

// Data

data class Salat(val key: String, val name: String, val arabic: String, val time: String)

val salatList = listOf(
    Salat("fajr",    "Fajr",    "الفجر", "05:02"),
    Salat("dhohr",   "Dhohr",   "الظهر", "13:14"),
    Salat("asr",     "Asr",     "العصر", "16:48"),
    Salat("maghrib", "Maghrib", "المغرب","20:30"),
    Salat("isha",    "Isha",    "العشاء", "21:38"),
)

// ── Firebase helper ───────────────────────────────────────────────────────────

// charge state des priere daujourdui a partir de Firebase.
// Returns a map : { "fajr" -> true, "dhohr" -> false, ... }
fun loadPrayersFromFirebase(
    date: String,
    onResult: (Map<String, Boolean>) -> Unit
) {
    FirebaseDatabase.getInstance()
        .getReference("prayers")
        .child(date)
        .get()
        .addOnSuccessListener { snapshot ->
            val result = salatList.associate { salat ->
                salat.key to (snapshot.child(salat.key).getValue(Boolean::class.java) ?: false)
            }
            onResult(result)
        }
        .addOnFailureListener {
            // si Firebase echoue
            onResult(salatList.associate { it.key to false })
        }
}

// Saves a single prayer toggle to Firebase.
fun savePrayerToFirebase(date: String, key: String, value: Boolean) {
    FirebaseDatabase.getInstance()
        .getReference("prayers")
        .child(date)
        .child(key)
        .setValue(value)
}

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun PrayersScreen() {
    val date = LocalDate.now().toString() // e.g. "2025-01-15"

    // Loading flag — hides the list until Firebase responds
    var isLoading by remember { mutableStateOf(true) }

    val checked = remember { mutableStateListOf(false, false, false, false, false) }

    // Load saved state from Firebase when the screen first opens
    LaunchedEffect(Unit) {
        loadPrayersFromFirebase(date) { savedMap ->
            salatList.forEachIndexed { index, salat ->
                checked[index] = savedMap[salat.key] ?: false
            }
            isLoading = false
        }
    }

    PrayersScreenContent(
        date = date,
        isLoading = isLoading,
        salatList = salatList,
        checked = checked.toList(),
        onToggle = { index ->
            val newValue = !checked[index]
            checked[index] = newValue
            savePrayerToFirebase(date, salatList[index].key, newValue)
        }
    )
}

@Composable
fun PrayersScreenContent(
    date: String,
    isLoading: Boolean,
    salatList: List<Salat>,
    checked: List<Boolean>,
    onToggle: (Int) -> Unit
) {
    val completedCount = checked.count { it }

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
                        listOf(Gold.copy(alpha = 0.07f), Night.copy(alpha = 0f))
                    )
                )
                .padding(start = 22.dp, end = 22.dp, top = 52.dp, bottom = 20.dp)
        ) {
            Column {
                Text("SALAT", color = Gold, fontSize = 11.sp, letterSpacing = 3.sp)
                Spacer(Modifier.height(6.dp))
                Text("Prières", color = TextMain, fontSize = 36.sp, fontFamily = FontFamily.Serif)
                Spacer(Modifier.height(4.dp))
                Text(date, color = TextMuted, fontSize = 13.sp)
            }
        }


        // ── Prayer list ───────────────────────────────────
        if (isLoading) {
            // Simple loading placeholder while Firebase responds
            Box(
                Modifier.fillMaxWidth().padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("...", color = TextMuted, fontSize = 20.sp)
            }
        } else {
            Column(
                Modifier.padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                salatList.forEachIndexed { index, salat ->
                    PrayerRow(
                        salat = salat,
                        isDone = checked[index],
                        onToggle = { onToggle(index) }
                    )
                }
            }
        }

        // ── Completion message ────────────────────────────
        if (completedCount == 5) {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 22.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Emerald.copy(alpha = 0.08f))
                    .border(0.5.dp, Emerald.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Toutes les prières accomplies !", color = Emerald, fontSize = 15.sp, fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}

// ── Prayer row component ──────────────────────────────────────────────────────

@Composable
private fun PrayerRow(salat: Salat, isDone: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (isDone) Emerald.copy(alpha = 0.07f) else Night3)
            .border(
                0.5.dp,
                if (isDone) Emerald.copy(0.4f) else Gold.copy(0.1f),
                RoundedCornerShape(18.dp)
            )
            .clickable { onToggle() }
            .padding(18.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Checkbox
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDone) Emerald else Night2)
                        .border(1.dp, if (isDone) Emerald else Gold.copy(0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) Text("✓", color = Night, fontSize = 14.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        salat.name,
                        color = if (isDone) TextMuted else TextMain,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        salat.arabic,
                        color = if (isDone) TextMuted.copy(alpha = 0.5f) else Gold.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
            Text(
                salat.time,
                color = if (isDone) TextMuted else Gold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrayersScreenPreview() {
    WirdTheme {
        PrayersScreenContent(
            date = "2025-01-15",
            isLoading = false,
            salatList = salatList,
            checked = listOf(true, true, false, false, false),
            onToggle = {}
        )
    }
}
