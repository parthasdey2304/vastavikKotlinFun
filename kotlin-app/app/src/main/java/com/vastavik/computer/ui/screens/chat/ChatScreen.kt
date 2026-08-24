package com.vastavik.computer.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import com.vastavik.computer.ui.theme.VastavikColors
import com.vastavik.computer.ui.theme.neoShape
import com.vastavik.computer.ui.theme.neoCircleShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.vastavik.computer.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.net.Uri

data class ChatMessage(val text: String, val isUser: Boolean)

private const val SYSTEM_PROMPT = """You are Vastavik AI, a friendly programming tutor for Indian school students (Class 5-12, CBSE/ICSE boards). You help students learn Java, Python, JavaScript, and SQL.

RULES:
- Only answer questions about programming, computers, and coding (Java, Python, JavaScript, SQL, algorithms, data structures, web development, app development)
- If asked about non-computer topics, politely say: "I can only help with programming and computer science questions!"
- If asked inappropriate or harmful questions, say: "I can only help with programming and computer science questions!"
- Keep answers CRISP and CLEAR — explain properly but don't over-explain
- Use simple language suitable for a school student
- Give code examples when helpful
- For Class 5-8 students: use very simple explanations with real-life analogies
- For Class 9-12 students: can include more technical depth
- Always be encouraging and supportive
- Format code with ```code blocks when showing examples"""

private fun callMistralApi(messages: List<ChatMessage>): String {
    val apiKey = BuildConfig.MISTRAL_API_KEY
    if (apiKey.isBlank()) return "Mistral API key not configured. Please add MISTRAL_API_KEY to local.properties."

    val url = URL("https://api.mistral.ai/v1/chat/completions")
    val conn = url.openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setRequestProperty("Authorization", "Bearer $apiKey")
    conn.doOutput = true
    conn.connectTimeout = 30000
    conn.readTimeout = 30000

    val apiMessages = JSONArray()
    apiMessages.put(JSONObject().put("role", "system").put("content", SYSTEM_PROMPT))
    for (msg in messages) {
        apiMessages.put(JSONObject().put("role", if (msg.isUser) "user" else "assistant").put("content", msg.text))
    }

    val body = JSONObject().apply {
        put("model", "mistral-small-latest")
        put("messages", apiMessages)
        put("max_tokens", 1024)
        put("temperature", 0.3)
    }

    conn.outputStream.use { it.write(body.toString().toByteArray()) }

    val responseCode = conn.responseCode
    val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
    val response = stream.bufferedReader().use { it.readText() }

    return if (responseCode in 200..299) {
        val json = JSONObject(response)
        json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
    } else {
        "Error ($responseCode): ${JSONObject(response).optJSONObject("message")?.optString("message") ?: "Unknown error"}"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onNavigate: (String) -> Unit) {
    val viewModel = remember { ChatViewModel.getInstance() }
    val messages by viewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    suspend fun askMistral(prompt: String): String {
        return withContext(Dispatchers.IO) {
            try {
                callMistralApi(messages + ChatMessage(prompt, isUser = true))
            } catch (e: Exception) {
                "Error: ${'$'}{e.message}. Check your internet connection."
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.SmartToy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Vastavik AI", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Surface(shape = neoShape(8.dp), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)) {
                        Text("Mistral Small", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            },
            actions = {
                IconButton(onClick = { viewModel.clearMessages() }) {
                    Icon(Icons.Filled.Add, contentDescription = "New Chat", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { onNavigate("profile") }) {
                    Icon(Icons.Filled.Person, contentDescription = "Profile", tint = MaterialTheme.colorScheme.primary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            windowInsets = WindowInsets(0.dp)
        )
        Column(modifier = Modifier.fillMaxSize()) {
            LazyRow(modifier = Modifier.padding(vertical = 8.dp), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf(
                    "Explain Code" to "Explain this Java code for Class 8: public class Hello { public static void main(String[] args){ System.out.println(\"hi\"); } }",
                    "Generate Quiz" to "Generate 3 MCQs about Python loops for Class 7 with 4 options each.",
                    "Find Bug" to "Help me find bug in this Python: for i in range(5) print(i)"
                )) { (label, prompt) ->
                    SuggestionChip(
                        onClick = {
                            if (!isLoading) {
                                viewModel.addMessage(ChatMessage(prompt, isUser = true))
                                isLoading = true
                                coroutineScope.launch {
                                    listState.animateScrollToItem(messages.lastIndex + 1)
                                    val resp = askMistral(prompt)
                                    viewModel.addMessage(ChatMessage(resp, isUser = false))
                                    isLoading = false
                                    listState.animateScrollToItem(messages.lastIndex)
                                }
                            }
                        },
                        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        border = null,
                        shape = neoShape(16.dp)
                    )
                }
            }

            LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(messages) { message -> ChatBubbleRow(message, onNavigate) }
                if (isLoading) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Box(modifier = Modifier.size(32.dp).clip(neoCircleShape()).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            Surface(shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp), color = MaterialTheme.colorScheme.surface) {
                                Row(modifier = Modifier.padding(16.dp)) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Thinking...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = inputText, onValueChange = { inputText = it },
                        placeholder = { Text("Ask anything (Java/Python/JS/SQL)...") },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp, max = 120.dp),
                        shape = neoShape(24.dp), singleLine = false, maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isLoading) {
                                val userText = inputText.trim()
                                viewModel.addMessage(ChatMessage(userText, isUser = true))
                                inputText = ""
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        listState.animateScrollToItem(messages.lastIndex + 1)
                                        val resp = askMistral(userText)
                                        viewModel.addMessage(ChatMessage(resp, isUser = false))
                                        listState.animateScrollToItem(messages.lastIndex)
                                    } finally { isLoading = false }
                                }
                            }
                        },
                        enabled = inputText.isNotBlank() && !isLoading,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleRow(message: ChatMessage, onNavigate: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(4.dp, neoCircleShape())
                    .clip(neoCircleShape())
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(10.dp))
        }
        Surface(
            shape = if (MaterialTheme.shapes.medium.toString().contains("0.0")) RoundedCornerShape(0.dp) else RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = if (message.isUser) 16.dp else 4.dp, bottomEnd = if (message.isUser) 4.dp else 16.dp),
            color = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            if (message.isUser) {
                Text(text = message.text, modifier = Modifier.padding(12.dp), color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
            } else {
                ParsedMarkdownText(text = message.text, modifier = Modifier, onNavigate = onNavigate)
            }
        }
        if (message.isUser) {
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(4.dp, neoCircleShape())
                    .clip(neoCircleShape())
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun ParsedMarkdownText(text: String, modifier: Modifier = Modifier, onNavigate: ((String) -> Unit)? = null) {
    val parts = text.split("`")
    Column(modifier = modifier) {
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                val lines = part.trim().lines()
                val language = lines.firstOrNull()?.trim() ?: ""
                val codeLines = if (lines.size > 1) lines.drop(1) else listOf()
                val codeContent = codeLines.joinToString("\n")
                if (onNavigate != null && codeContent.isNotBlank()) {
                    Surface(
                        onClick = {
                            val encoded = Uri.encode(codeContent, "UTF-8")
                            onNavigate("code_editor?initialCode=$encoded&language=$language")
                        },
                        shape = neoShape(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.OpenInFull, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Open in Editor", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                if (language.isNotEmpty()) {
                                    Text(language, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                } else if (codeContent.isNotBlank()) {
                    Surface(
                        shape = neoShape(8.dp), color = Color(0xFF1E1E1E),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (language.isNotEmpty()) { Text(language, fontSize = 10.sp, color = Color.Gray); Spacer(Modifier.height(8.dp)) }
                            codeLines.forEachIndexed { i, line ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "${'$'}{i + 1}", color = Color(0xFF858585), fontFamily = FontFamily.Monospace, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.End, modifier = Modifier.width(28.dp).padding(end = 8.dp))
                                    Text(text = highlightCode(if (line.isEmpty()) " " else line), color = Color(0xFFD4D4D4), fontFamily = FontFamily.Monospace, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            } else {
                if (part.trim().isNotEmpty()) {
                    Text(text = parseBasicMarkdown(part.trim()), color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, lineHeight = 20.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }
        }
    }
}

private fun parseBasicMarkdown(text: String) = buildAnnotatedString {
    val lines = text.split("\n")
    for (i in lines.indices) {
        val line = lines[i]
        val isHeader3 = line.startsWith("### ")
        val isHeader2 = line.startsWith("## ")
        val isHeader1 = line.startsWith("# ")
        val style = when {
            isHeader1 -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp)
            isHeader2 -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
            isHeader3 -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
            else -> null
        }
        val textToProcess = when {
            isHeader1 -> line.removePrefix("# ")
            isHeader2 -> line.removePrefix("## ")
            isHeader3 -> line.removePrefix("### ")
            else -> line
        }
        if (style != null) { withStyle(style) { parseInlineMarkdown(textToProcess, this@buildAnnotatedString) } } else { parseInlineMarkdown(textToProcess, this@buildAnnotatedString) }
        if (i < lines.size - 1) append("\n")
    }
}

private fun parseInlineMarkdown(text: String, builder: androidx.compose.ui.text.AnnotatedString.Builder) {
    val regex = Regex("\\*\\*(.*?)\\*\\*|`(.*?)`|\\*(.*?)\\*")
    var currentIndex = 0
    val matches = regex.findAll(text)
    for (match in matches) {
        builder.append(text.substring(currentIndex, match.range.first))
        when {
            match.groups[1] != null -> { builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(match.groups[1]!!.value) } }
            match.groups[2] != null -> { builder.withStyle(SpanStyle(background = Color(0x22888888), fontFamily = FontFamily.Monospace)) { append(match.groups[2]!!.value) } }
            match.groups[3] != null -> { builder.withStyle(SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)) { append(match.groups[3]!!.value) } }
        }
        currentIndex = match.range.last + 1
    }
    builder.append(text.substring(currentIndex))
}

private fun highlightCode(code: String) = buildAnnotatedString {
    val keywords = listOf("class", "fun", "public", "private", "protected", "override", "return", "val", "var", "import", "package", "if", "else", "for", "while", "true", "false", "null")
    val types = listOf("String", "Int", "Boolean", "Double", "Float", "Long")
    val words = code.split(Regex("(?<=\\b|\\s)|(?=\\b|\\s)"))
    for (word in words) {
        when {
            word in keywords -> { withStyle(style = SpanStyle(color = Color(0xFF569CD6))) { append(word) } }
            word in types -> { withStyle(style = SpanStyle(color = Color(0xFF4EC9B0))) { append(word) } }
            else -> { append(word) }
        }
    }
}
