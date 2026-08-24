package com.vastavik.computer.ui.screens.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vastavik.computer.ui.theme.VastavikColors
import com.vastavik.computer.ui.theme.neoShape
import com.vastavik.computer.ui.theme.neoCircleShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreen(
    topic: String,
    onNavigate: (String) -> Unit = {}
) {
    var questionCount by remember { mutableIntStateOf(10) }
    var difficulty by remember { mutableStateOf("Medium") }
    var isGenerating by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Setup") },
                navigationIcon = {
                    IconButton(onClick = { onNavigate("home") }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = neoShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Topic", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(topic, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Number of Questions", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(10, 20, 30).forEach { count ->
                    FilterChip(
                        selected = questionCount == count,
                        onClick = { questionCount = count },
                        label = { Text("$count") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Difficulty", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Easy", "Medium", "Hard").forEach { diff ->
                    FilterChip(
                        selected = difficulty == diff,
                        onClick = { difficulty = diff },
                        label = { Text(diff) },
                        leadingIcon = if (difficulty == diff) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null,
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (diff) {
                                "Easy" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                                "Medium" -> VastavikColors.LightWarning.copy(alpha = 0.1f)
                                else -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            }
                        )
                    )
                }
            }

            if (errorMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    isGenerating = true
                    errorMsg = ""
                    coroutineScope.launch {
                        val result = withContext(Dispatchers.IO) {
                            QuizManager.generateQuiz(topic, questionCount, difficulty)
                        }
                        isGenerating = false
                        if (result == "ok") {
                            onNavigate("quiz_taking/quiz_1")
                        } else {
                            errorMsg = result
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = neoShape(16.dp),
                enabled = !isGenerating
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Generating $questionCount questions...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Generate Quiz", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
