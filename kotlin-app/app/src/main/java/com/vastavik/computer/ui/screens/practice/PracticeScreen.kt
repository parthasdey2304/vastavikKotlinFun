package com.vastavik.computer.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vastavik.computer.ui.components.VastavikTopBar

private val DarkBg = Color(0xFF0F172A)
private val DarkCard = Color(0xFF1E293B)
private val DarkBorder = Color(0xFF334155)
private val DarkText = Color(0xFFF8FAFC)
private val DarkMuted = Color(0xFF94A3B8)

@Composable
fun PracticeScreen(onNavigate: (String) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(1) } // Default to Coding tab (index 1)
    val tabs = listOf("MCQs", "Coding", "PYQs")

    Scaffold(
        containerColor = DarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Top bar
            VastavikTopBar(onProfileClick = { onNavigate("profile") })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBg)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Practice",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Pill tabs
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            onClick = { selectedTab = index },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color.White else Color.Transparent
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) DarkBg else DarkMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> MCQTab(onNavigate = onNavigate)
                    1 -> CodingTab(onNavigate = onNavigate)
                    2 -> PYQTab(onNavigate = onNavigate)
                }
            }
        }
    }
}

@Composable
private fun MCQTab(onNavigate: (String) -> Unit) {
    val quizzes = listOf(
        Triple("OOP Concepts", "10 questions", Icons.Filled.Quiz),
        Triple("Arrays & Lists", "15 questions", Icons.Filled.Quiz),
        Triple("Sorting", "12 questions", Icons.Filled.Quiz),
        Triple("File Handling", "8 questions", Icons.Filled.Quiz)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(quizzes) { (title, sub, icon) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("quiz_setup/$title") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkText)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(sub, fontSize = 12.sp, color = DarkMuted)
                    }
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = DarkMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CodingTab(onNavigate: (String) -> Unit) {
    val challenges = listOf(
        Triple("Reverse a String", "Easy", "Strings"),
        Triple("Two Sum", "Medium", "Arrays"),
        Triple("Merge Intervals", "Hard", "Intervals")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(challenges) { (title, difficulty, topic) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("code_editor") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkText)
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = when (difficulty) {
                                "Easy" -> Color(0xFF065F46).copy(alpha = 0.5f)
                                "Medium" -> Color(0xFF92400E).copy(alpha = 0.5f)
                                else -> Color(0xFF991B1B).copy(alpha = 0.5f)
                            }
                        ) {
                            Text(
                                text = difficulty,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = when (difficulty) {
                                    "Easy" -> Color(0xFF34D399)
                                    "Medium" -> Color(0xFFFBBF24)
                                    else -> Color(0xFFF87171)
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(topic, fontSize = 12.sp, color = DarkMuted)
                }
            }
        }
    }
}

@Composable
private fun PYQTab(onNavigate: (String) -> Unit) {
    val pyqs = listOf(
        Triple("ICSE 2023", "45 questions", Icons.Filled.Article),
        Triple("CBSE 2022", "50 questions", Icons.Filled.Article),
        Triple("ICSE 2022", "40 questions", Icons.Filled.Article)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pyqs) { (title, questions, icon) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("pyq") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkText)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(questions, fontSize = 12.sp, color = DarkMuted)
                    }
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = DarkMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
