package com.vastavik.computer.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import com.vastavik.computer.ui.theme.VastavikColors
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.GenerativeModel
import com.vastavik.computer.BuildConfig

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onNavigate: (String) -> Unit) {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage("Hello! I am Vastavik AI powered by Gemini 3.7 Flash. Ask me Java/Python/JS/SQL for Class 5-12!", isUser = false)
            )
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val generativeModel = remember {
        val key = BuildConfig.GEMINI_API_KEY
        if (key.isNotBlank()) {
            GenerativeModel(
                modelName = "gemini-3.7-flash",
                apiKey = key
            )
        } else null
    }

    suspend fun askGemini(prompt: String): String {
        return try {
            if (generativeModel == null) return "Gemini API key not configured. Add GEMINI_API_KEY to local.properties."
            val response = generativeModel.generateContent(prompt)
            response.text ?: "No response"
        } catch (e: Exception) {
            "Error: ${'$'}{e.message}. Check API key and internet."
        }
    }

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.SmartToy, contentDescription = null, tint = VastavikColors.LightPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Vastavik AI", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = VastavikColors.LightAccent.copy(alpha = 0.2f)) {
                        Text("Gemini 3.7 Flash", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VastavikColors.LightAccent)
                    }
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
                                val userMsg = ChatMessage(prompt, isUser = true)
                                messages = messages + userMsg
                                isLoading = true
                                coroutineScope.launch {
                                    listState.animateScrollToItem(messages.lastIndex)
                                    val resp = askGemini(prompt)
                                    messages = messages + ChatMessage(resp, isUser = false)
                                    isLoading = false
                                    listState.animateScrollToItem(messages.lastIndex)
                                }
                            }
                        },
                        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VastavikColors.LightPrimary) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = VastavikColors.LightPrimary.copy(alpha = 0.1f)),
                        border = null,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(messages) { message -> ChatBubbleRow(message) }
                if (isLoading) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(VastavikColors.LightPrimary), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            Surface(shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp), color = MaterialTheme.colorScheme.surface) {
                                Row(modifier = Modifier.padding(16.dp)) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = VastavikColors.LightPrimary)
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
                        shape = RoundedCornerShape(24.dp), singleLine = false, maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VastavikColors.LightPrimary, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isLoading) {
                                val userMsg = ChatMessage(inputText.trim(), isUser = true)
                                val userText = inputText.trim()
                                messages = messages + userMsg
                                inputText = ""
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        listState.animateScrollToItem(messages.lastIndex)
                                        val resp = askGemini(userText)
                                        messages = messages + ChatMessage(resp, isUser = false)
                                        listState.animateScrollToItem(messages.lastIndex)
                                    } finally { isLoading = false }
                                }
                            }
                        },
                        enabled = inputText.isNotBlank() && !isLoading,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = VastavikColors.LightPrimary)
                    ) { Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleRow(message: ChatMessage) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start) {
        if (!message.isUser) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(VastavikColors.LightPrimary), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(8.dp))
        }
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = if (message.isUser) 16.dp else 4.dp, bottomEnd = if (message.isUser) 4.dp else 16.dp),
            color = if (message.isUser) VastavikColors.LightPrimary else MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            if (message.isUser) {
                Text(text = message.text, modifier = Modifier.padding(12.dp), color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
            } else {
                ParsedMarkdownText(text = message.text, modifier = Modifier)
            }
        }
        if (message.isUser) {
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(VastavikColors.LightAccent), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun ParsedMarkdownText(text: String, modifier: Modifier = Modifier) {
    val parts = text.split("`")
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                val lines = part.trim().lines()
                val language = lines.firstOrNull()?.trim() ?: ""
                val codeLines = if (lines.size > 1) lines.drop(1) else listOf()
                Surface(
                    shape = RoundedCornerShape(8.dp), color = Color(0xFF1E1E1E),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    androidx.compose.foundation.layout.Column(modifier = Modifier.padding(12.dp)) {
                        if (language.isNotEmpty()) { Text(language, fontSize = 10.sp, color = Color.Gray); Spacer(Modifier.height(8.dp)) }
                        androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxWidth()) {
                            codeLines.forEachIndexed { i, line ->
                                androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "${i + 1}", color = Color(0xFF858585), fontFamily = FontFamily.Monospace, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.End, modifier = Modifier.width(28.dp).padding(end = 8.dp))
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
    val regex = Regex("\\*\\*(.*?)\\*\\*|+" + "(.*?)|\\*(.*?)\\*")
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
