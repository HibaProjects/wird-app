package com.hiba.wird.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import com.hiba.wird.ui.theme.*
import java.time.LocalDate

// One dhikr item — key is used to save in Firebase
data class Dhikr(
    val key: String,
    val arabic: String,
    val translation: String,
    val maxCount: Int
)

// Morning adkar list
val sabahList = listOf(
    Dhikr("ayat_kursi",  "آية الكرسي",                    "Ayat Al-Kursi",               1),
    Dhikr("tasbih",      "سبحان الله وبحمده",              "Subhan Allah wa bihamdihi",   100),
    Dhikr("istighfar",   "أستغفر الله",                   "Astaghfirullah",              3),
    Dhikr("salawat",     "اللهم صل على محمد",              "Allahumma salli ala Muhammad", 10),
    Dhikr("tawhid",      "لا إله إلا الله وحده لا شريك له","La ilaha illallah wahdahu",   3),
)

// Evening adkar list
val masaaList = listOf(
    Dhikr("ayat_kursi",  "آية الكرسي",                    "Ayat Al-Kursi",               1),
    Dhikr("ikhlas",      "قُلْ هُوَ اللَّهُ أَحَدٌ",      "Sourate Al-Ikhlas",           3),
    Dhikr("tasbih",      "سبحان الله وبحمده",              "Subhan Allah wa bihamdihi",   100),
    Dhikr("dua_sleep",   "اللهم باسمك أموت وأحيا",         "Dua avant de dormir",         1),
    Dhikr("istighfar",   "أستغفر الله",                   "Astaghfirullah",              3),
)

@Composable
fun AdkarScreen() {

    val date = LocalDate.now().toString()

    // which tab is open: "sabah" or "masaa"
    var selectedTab by remember { mutableStateOf("sabah") }

    // counts for each dhikr — key is dhikr.key, value is how many times tapped
    val sabahCounts = remember { mutableStateMapOf<String, Int>() }
    val masaaCounts = remember { mutableStateMapOf<String, Int>() }

    // set all counts to 0 on first load
    LaunchedEffect(Unit) {
        sabahList.forEach { sabahCounts[it.key] = 0 }
        masaaList.forEach { masaaCounts[it.key] = 0 }
        loadAdkarFromFirebase(date, "sabah", sabahList) { saved ->
            saved.forEach { (k, v) -> sabahCounts[k] = v }
        }
        loadAdkarFromFirebase(date, "masaa", masaaList) { saved ->
            saved.forEach { (k, v) -> masaaCounts[k] = v }
        }
    }

    // pick the right list and counts based on selected tab
    val currentList   = if (selectedTab == "sabah") sabahList   else masaaList
    val currentCounts = if (selectedTab == "sabah") sabahCounts else masaaCounts

    val completedCount = currentList.count { (currentCounts[it.key] ?: 0) >= it.maxCount }

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
                .padding(start = 22.dp, end = 22.dp, top = 52.dp, bottom = 20.dp)
        ) {
            Column {
                Text("ADKAR", color = Gold, fontSize = 11.sp, letterSpacing = 3.sp)
                Spacer(Modifier.height(6.dp))
                Text("Dhikr", color = TextMain, fontSize = 36.sp, fontFamily = FontFamily.Serif)
                Spacer(Modifier.height(4.dp))
                Text(date, color = TextMuted, fontSize = 13.sp)
            }
        }

        // ── Sabah / Masaa tabs ────────────────────────────
        Row(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Night3)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("sabah" to "Sabah — صباح", "masaa" to "Masaa — مساء").forEach { (key, label) ->
                val isSelected = selectedTab == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            if (isSelected) Night2 else Night3
                        )
                        .border(
                            0.5.dp,
                            if (isSelected) Gold.copy(alpha = 0.2f) else Night3,
                            RoundedCornerShape(9.dp)
                        )
                        .clickable { selectedTab = key }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        color = if (isSelected) Gold else TextMuted,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))


        // ── Dhikr list ────────────────────────────────────
        Column(
            Modifier.padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            currentList.forEach { dhikr ->
                val count  = currentCounts[dhikr.key] ?: 0
                val isDone = count >= dhikr.maxCount

                DhikrCard(
                    dhikr  = dhikr,
                    count  = count,
                    isDone = isDone,
                    onTap  = {
                        if (count < dhikr.maxCount) {
                            val newCount = count + 1
                            currentCounts[dhikr.key] = newCount
                            // save to Firebase every tap
                            saveAdkarToFirebase(date, selectedTab, dhikr.key, newCount)
                        }
                    }
                )
            }
        }

        // ── Completion message ────────────────────────────
        if (completedCount == currentList.size && currentList.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 22.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Emerald.copy(alpha = 0.08f))
                    .border(0.5.dp, Emerald.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Adkar accomplis !", color = Emerald, fontSize = 15.sp, fontFamily = FontFamily.Serif)

                }
            }
        }
    }
}

// ── Dhikr card ────────────────────────────────────────────────────────────────

@Composable
fun DhikrCard(dhikr: Dhikr, count: Int, isDone: Boolean, onTap: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDone) Emerald.copy(alpha = 0.07f) else Night3)
            .border(
                0.5.dp,
                if (isDone) Emerald.copy(alpha = 0.35f) else Gold.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .clickable { onTap() }
            .padding(16.dp, 16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Counter badge on the left
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isDone) Emerald.copy(alpha = 0.1f)
                        else Gold.copy(alpha = 0.08f)
                    )
                    .border(
                        0.5.dp,
                        if (isDone) Emerald.copy(alpha = 0.3f)
                        else Gold.copy(alpha = 0.2f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "$count",
                    color = if (isDone) Emerald else Gold,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    "/ ${dhikr.maxCount}",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            // Arabic text + translation on the right (RTL)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    dhikr.arabic,
                    color = if (isDone) TextMuted else TextMain,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.End
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    dhikr.translation,
                    color = TextMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun AdkarScreenPreview() {
    WirdTheme {
        AdkarScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun DhikrCardPreview() {
    WirdTheme {
        DhikrCard(
            dhikr = sabahList[1],
            count = 50,
            isDone = false,
            onTap = {}
        )
    }
}

// ── Firebase helpers ──────────────────────────────────────────────────────────

fun loadAdkarFromFirebase(
    date: String,
    period: String,   // "sabah" ou "masaa"
    list: List<Dhikr>,
    onResult: (Map<String, Int>) -> Unit
) {
    FirebaseDatabase.getInstance()
        .getReference("adkar")
        .child(date)
        .child(period)
        .get()
        .addOnSuccessListener { snapshot ->
            val result = list.associate { dhikr ->
                dhikr.key to (snapshot.child(dhikr.key).getValue(Int::class.java) ?: 0)
            }
            onResult(result)
        }
        .addOnFailureListener {
            // si Firebase fails alors default to 0
            onResult(list.associate { it.key to 0 })
        }
}

fun saveAdkarToFirebase(date: String, period: String, key: String, count: Int) {
    FirebaseDatabase.getInstance()
        .getReference("adkar")
        .child(date)
        .child(period)
        .child(key)
        .setValue(count)
}
