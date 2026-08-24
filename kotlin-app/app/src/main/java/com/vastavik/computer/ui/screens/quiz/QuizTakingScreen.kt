package com.vastavik.computer.ui.screens.quiz

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vastavik.computer.ui.theme.VastavikColors
import com.vastavik.computer.ui.theme.neoShape
import com.vastavik.computer.ui.theme.neoCircleShape
import com.vastavik.computer.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTakingScreen(
    quizId: String,
    onNavigate: (String) -> Unit = {}
) {
    var currentQuestion by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }
    var showReview by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val questions = remember { QuizManager.getQuestions().ifEmpty {
        listOf(
            QuizQuestionData("What is the keyword used to inherit a class in Java?", listOf("implements", "extends", "inherits", "derives"), 1),
            QuizQuestionData("Which is not a primitive data type?", listOf("int", "boolean", "String", "char"), 2),
            QuizQuestionData("Default value of int?", listOf("null", "0", "1", "undefined"), 1)
        )
    }}

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No questions generated. Go back and try again.")
        }
        return
    }

    if (showReview) {
        ReviewAnswersScreen(
            questions = questions,
            userAnswers = userAnswers,
            onBack = { showReview = false },
            onNavigate = onNavigate
        )
        return
    }

    if (showResult) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.EmojiEvents, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Quiz Complete!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("You scored $score out of ${questions.size}", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { score.toFloat() / questions.size },
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(neoShape(6.dp)),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surface
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onNavigate("home") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = neoShape(12.dp)
                ) {
                    Icon(Icons.Filled.Home, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Back to Home")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { downloadQuestions(context, questions) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = neoShape(12.dp)
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Download Questions")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showReview = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = neoShape(12.dp)
                ) {
                    Icon(Icons.Filled.Quiz, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Review Answers")
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Quiz") },
                    navigationIcon = {
                        IconButton(onClick = { onNavigate("home") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Question ${currentQuestion + 1} of ${questions.size}", fontWeight = FontWeight.Bold)
                        Text("${((currentQuestion + 1).toFloat() / questions.size * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (currentQuestion + 1).toFloat() / questions.size },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(neoShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(modifier = Modifier.fillMaxWidth(), shape = neoShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(questions[currentQuestion].question, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    questions[currentQuestion].options.forEachIndexed { index, option ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedAnswer = index },
                            shape = neoShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    selectedAnswer == index -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            ),
                            border = CardDefaults.outlinedCardBorder().takeIf { selectedAnswer == index }
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(28.dp).clip(neoShape(6.dp))
                                        .background(if (selectedAnswer == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(('A' + index).toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(option, fontSize = 14.sp)
                            }
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.background, shadowElevation = 8.dp) {
                    Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { if (currentQuestion > 0) { currentQuestion--; selectedAnswer = userAnswers[currentQuestion] ?: -1 } },
                            modifier = Modifier.weight(1f),
                            enabled = currentQuestion > 0,
                            shape = neoShape(12.dp)
                        ) { Text("Previous") }
                        Button(
                            onClick = {
                                userAnswers[currentQuestion] = selectedAnswer
                                if (selectedAnswer == questions[currentQuestion].correctIndex) score++
                                if (currentQuestion < questions.lastIndex) {
                                    currentQuestion++
                                    selectedAnswer = userAnswers[currentQuestion] ?: -1
                                } else {
                                    showResult = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = selectedAnswer >= 0,
                            shape = neoShape(12.dp)
                        ) { Text(if (currentQuestion == questions.lastIndex) "Submit" else "Next") }
                    }
                }
            }
        }
    }
}

private fun downloadQuestions(context: Context, questions: List<QuizQuestionData>) {
    val sb = StringBuilder()
    sb.appendLine("=== Vastavik Quiz Questions ===")
    sb.appendLine()
    questions.forEachIndexed { i, q ->
        sb.appendLine("Q${i + 1}. ${q.question}")
        q.options.forEachIndexed { j, opt ->
            val marker = if (j == q.correctIndex) " ✓" else ""
            sb.appendLine("  ${('A' + j)}) $opt$marker")
        }
        sb.appendLine()
    }

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        putExtra(Intent.EXTRA_SUBJECT, "Vastavik Quiz Questions")
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Questions"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewAnswersScreen(
    questions: List<QuizQuestionData>,
    userAnswers: Map<Int, Int>,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var explanations by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        val results = mutableMapOf<Int, String>()
        withContext(Dispatchers.IO) {
            questions.forEachIndexed { i, q ->
                val userAns = userAnswers[i] ?: -1
                val correct = q.correctIndex
                val userText = if (userAns >= 0) q.options[userAns] else "No answer"
                val correctText = q.options[correct]
                val prompt = """Explain in 2-3 short sentences why "$correctText" is the correct answer for: "${q.question}". The student chose "$userText". Be encouraging."""
                val explanation = callMistralBrief(prompt)
                results[i] = explanation
            }
        }
        explanations = results
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Answers") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Generating explanations...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
                questions.forEachIndexed { i, q ->
                    val userAns = userAnswers[i] ?: -1
                    val correct = q.correctIndex
                    val isCorrect = userAns == correct

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        shape = neoShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                    contentDescription = null,
                                    tint = if (isCorrect) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Q${i + 1}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(q.question, fontSize = 14.sp, fontWeight = FontWeight.Medium)

                            Spacer(Modifier.height(12.dp))

                            q.options.forEachIndexed { j, opt ->
                                val bg = when {
                                    j == correct -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                    j == userAns && j != correct -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                    else -> Color.Transparent
                                }
                                val textColor = when {
                                    j == correct -> MaterialTheme.colorScheme.tertiary
                                    j == userAns && j != correct -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    shape = neoShape(8.dp),
                                    color = bg
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(('A' + j).toString(), color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(24.dp))
                                        Text(opt, color = textColor, fontSize = 13.sp)
                                        if (j == correct) {
                                            Spacer(Modifier.weight(1f))
                                            Text("✓", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                                        }
                                        if (j == userAns && j != correct) {
                                            Spacer(Modifier.weight(1f))
                                            Text("✗", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            Surface(shape = neoShape(8.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(explanations[i] ?: "Loading...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { onNavigate("home") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = neoShape(12.dp)
                ) {
                    Icon(Icons.Filled.Home, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Back to Home")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

private fun callMistralBrief(prompt: String): String {
    val apiKey = BuildConfig.MISTRAL_API_KEY
    if (apiKey.isBlank()) return "API key not configured."
    return try {
        val url = URL("https://api.mistral.ai/v1/chat/completions")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $apiKey")
        conn.doOutput = true
        conn.connectTimeout = 30000
        conn.readTimeout = 30000
        val body = JSONObject().apply {
            put("model", "mistral-small-latest")
            put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", prompt)))
            put("max_tokens", 150)
            put("temperature", 0.3)
        }
        conn.outputStream.use { it.write(body.toString().toByteArray()) }
        val responseCode = conn.responseCode
        val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
        val response = stream.bufferedReader().use { it.readText() }
        if (responseCode in 200..299) {
            JSONObject(response).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim()
        } else "Could not generate explanation."
    } catch (e: Exception) { "Explanation unavailable." }
}
