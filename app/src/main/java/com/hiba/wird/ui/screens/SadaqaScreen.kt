package com.hiba.wird.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import com.hiba.wird.ui.theme.*
import java.time.LocalDate

@Composable
fun SadaqaScreen() {

    val date = LocalDate.now().toString()
    var savedIdea  by remember { mutableStateOf("") }

    // load today's saved idea when screen opens
    LaunchedEffect(Unit) {
        loadSadaqaFromFirebase(date) { idea: String ->
            savedIdea = idea
        }
    }

    SadaqaScreenContent(
        date = date,
        savedIdea = savedIdea,
        onSaveIdea = { idea ->
            savedIdea = idea
            saveSadaqaToFirebase(date, idea)
        }
    )
}

@Composable
fun SadaqaScreenContent(
    date: String,
    savedIdea: String,
    onSaveIdea: (String) -> Unit
) {
    var inputText  by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Night)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 40.dp)
    ) {

        // ── Header ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, top = 52.dp, bottom = 20.dp)
        ) {
            Column {
                Text("SADAQA", color = Gold, fontSize = 11.sp, letterSpacing = 3.sp)
                Spacer(Modifier.height(6.dp))
                Text("Sadaqa", color = TextMain, fontSize = 36.sp, fontFamily = FontFamily.Serif)
                Spacer(Modifier.height(4.dp))
                Text(date, color = TextMuted, fontSize = 13.sp)
            }
        }

        // ── Saved idea card ───────────────────────────────
        Column(Modifier.padding(horizontal = 22.dp)) {
            Text(
                "IDÉE DU JOUR",
                color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp
            )
            Spacer(Modifier.height(10.dp))

            if (savedIdea.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Night3)
                        .border(0.5.dp, Gold.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Pas encore d'idée pour aujourd'hui",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Emerald.copy(alpha = 0.07f))
                        .border(0.5.dp, Emerald.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        savedIdea,
                        color = TextMain,
                        fontSize = 17.sp,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 26.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Input area ────────────────────────────────────
        Column(Modifier.padding(horizontal = 22.dp)) {
            Text(
                if (savedIdea.isEmpty()) "AJOUTER UNE IDÉE" else "MODIFIER L'IDÉE",
                color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        "Ex: Aider quelqu'un, donner à manger...",
                        color = TextMuted.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Gold.copy(alpha = 0.4f),
                    unfocusedBorderColor = Gold.copy(alpha = 0.15f),
                    focusedTextColor     = TextMain,
                    unfocusedTextColor   = TextMain,
                    cursorColor          = Gold
                ),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (inputText.isNotBlank()) {
                            onSaveIdea(inputText.trim())
                            inputText = ""
                        }
                    }
                )
            )

            Spacer(Modifier.height(10.dp))

            // Save button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Gold.copy(alpha = 0.1f))
                    .border(0.5.dp, Gold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable {
                        if (inputText.isNotBlank()) {
                            onSaveIdea(inputText.trim())
                            inputText = ""
                        }
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Enregistrer l'idée",
                    color = Gold,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}

// ── Firebase helpers ──────────────────────────────────────────────────────────

fun loadSadaqaFromFirebase(date: String, onResult: (String) -> Unit) {
    FirebaseDatabase.getInstance()
        .getReference("sadaqa")
        .child(date)
        .child("idea")
        .get()
        .addOnSuccessListener { snapshot ->
            onResult(snapshot.getValue(String::class.java) ?: "")
        }
        .addOnFailureListener {
            onResult("")
        }
}

fun saveSadaqaToFirebase(date: String, idea: String) {
    FirebaseDatabase.getInstance()
        .getReference("sadaqa")
        .child(date)
        .child("idea")
        .setValue(idea)
}

@Preview(showBackground = true)
@Composable
fun SadaqaScreenPreview() {
    WirdTheme {
        SadaqaScreenContent(
            date = "2025-01-15",
            savedIdea = "Donner un repas à une personne dans le besoin",
            onSaveIdea = {}
        )
    }
}
