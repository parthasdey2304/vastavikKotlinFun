package com.vastavik.computer.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.vastavik.computer.ui.theme.VastavikColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTakingScreen(
    quizId: String,
    onNavigate: (String) -> Unit = {}
) {
    var currentQuestion by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    val questions = listOf(
        QuizQuestionData(
            question = "What is the keyword used to inherit a class in Java?",
            options = listOf("implements", "extends", "inherits", "derives"),
            correctIndex = 1
        ),
        QuizQuestionData(
            question = "Which of the following is not a primitive data type?",
            options = listOf("int", "boolean", "String", "char"),
            correctIndex = 2
        ),
        QuizQuestionData(
            question = "What is the default value of an int variable?",
            options = listOf("null", "0", "1", "undefined"),
            correctIndex = 1
        )
    )

    if (showResult) {
        // Results screen
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = VastavikColors.LightAccent
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Quiz Complete!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "You scored $score out of ${questions.size}",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { score.toFloat() / questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = VastavikColors.LightSuccess,
                    trackColor = MaterialTheme.colorScheme.surface
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { onNavigate("home") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back to Home")
                }
            }
        }
    } else {
        // Quiz taking
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Quiz") },
                    navigationIcon = {
                        IconButton(onClick = { onNavigate("home") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
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
                // Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Question ${currentQuestion + 1} of ${questions.size}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        "${((currentQuestion + 1).toFloat() / questions.size * 100).toInt()}%",
                        color = VastavikColors.LightPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (currentQuestion + 1).toFloat() / questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = VastavikColors.LightPrimary,
                    trackColor = MaterialTheme.colorScheme.surface
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Question card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            questions[currentQuestion].question,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Options
                questions[currentQuestion].options.forEachIndexed { index, option ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { selectedAnswer = index },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                selectedAnswer == index -> VastavikColors.LightPrimary.copy(alpha = 0.1f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = CardDefaults.outlinedCardBorder().takeIf { selectedAnswer == index }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (selectedAnswer == index) VastavikColors.LightPrimary
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    ('A' + index).toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                option,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentQuestion > 0) {
                                currentQuestion--
                                selectedAnswer = -1
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = currentQuestion > 0,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Previous")
                    }
                    Button(
                        onClick = {
                            if (selectedAnswer == questions[currentQuestion].correctIndex) {
                                score++
                            }
                            if (currentQuestion < questions.lastIndex) {
                                currentQuestion++
                                selectedAnswer = -1
                            } else {
                                showResult = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedAnswer >= 0,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (currentQuestion == questions.lastIndex) "Submit" else "Next")
                    }
                }
            }
        }
    }
}

private data class QuizQuestionData(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)
