package com.vastavik.computer.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vastavik.computer.ui.theme.VastavikColors

private val mono = FontFamily.Monospace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeEditorScreen(onNavigate: (String)->Unit) {
    var language by remember { mutableStateOf("Python") }
    var code by remember { mutableStateOf(defaultCode(language)) }
    var output by remember { mutableStateOf("") }
    var isRunning by remember { mutableStateOf(false) }

    // update code when language changes (keep if user edited? simple)
    LaunchedEffect(language) { if (code==defaultCode("Java")||code==defaultCode("Python")||code==defaultCode("JavaScript")||code==defaultCode("SQL")) code = defaultCode(language) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Code Editor", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { onNavigate("home") }) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
                actions = {
                    // language picker
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        TextButton(onClick = { expanded = true }) { Text(language, color = VastavikColors.LightPrimary, fontWeight = FontWeight.Bold) }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("Java","Python","JavaScript","SQL").forEach { lang ->
                                DropdownMenuItem(text = { Text(lang) }, onClick = { language = lang; expanded = false })
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            // output sheet
            if (output.isNotEmpty() || isRunning) {
                Surface(shadowElevation = 8.dp, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), color = MaterialTheme.colorScheme.surface) {
                    Column(modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 260.dp).padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text("Output", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = { output = "" }, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.Clear, contentDescription = "Clear") }
                            IconButton(onClick = { /* copy */ }, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.ContentCopy, contentDescription = "Copy") }
                        }
                        if (isRunning) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top=8.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Running...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            Text(output, fontFamily = mono, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.fillMaxWidth().padding(top=8.dp))
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    isRunning = true; output = ""
                    // mock run: simple echo for demo; real would call Cloud Run sandbox
                    val res = mockRun(language, code)
                    // small delay simulation
                    isRunning = false; output = res
                },
                icon = { Icon(Icons.Filled.PlayArrow, contentDescription = null) },
                text = { Text("Run") },
                containerColor = VastavikColors.LightPrimary
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // editor takes entire remaining space
            Row(modifier = Modifier.weight(1f).fillMaxWidth().background(Color(0xFF1E1E2E)).padding(start=0.dp)) {
                // line numbers gutter
                val lines = code.split("\n")
                val lineCount = lines.size
                LazyColumn(modifier = Modifier.width(48.dp).fillMaxHeight().background(Color(0xFF252526)).padding(vertical=12.dp), horizontalAlignment = Alignment.End) {
                    items(lineCount) { idx ->
                        Text(
                            text = "${idx + 1}",
                            color = Color(0xFF858585),
                            fontFamily = mono,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(end=8.dp, top=1.dp, bottom=1.dp)
                        )
                    }
                }
                // editor
                BasicTextField(
                    value = code,
                    onValueChange = { code = it },
                    textStyle = TextStyle(color = Color(0xFFD4D4D4), fontFamily = mono, fontSize = 13.sp, lineHeight = 18.sp),
                    cursorBrush = SolidColor(Color(0xFFD4D4D4)),
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(12.dp)
                )
            }
        }
    }
}

private fun defaultCode(lang: String) = when(lang) {
    "Java" -> "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello Vastavik\");\n    }\n}"
    "Python" -> "def solve():\n    print(\"Hello Vastavik\")\n\nsolve()"
    "JavaScript" -> "function greet(){\n  console.log(\"Hello Vastavik\");\n}\ngreet();"
    "SQL" -> "SELECT * FROM students\nWHERE class BETWEEN 5 AND 12;"
    else -> ""
}

private fun mockRun(lang:String, code:String): String {
    return when(lang) {
        "Python" -> if (code.contains("print")) "Hello Vastavik\n\nProcess finished (0)" else "Error: no output"
        "Java" -> if (code.contains("System.out")) "Hello Vastavik\n\nProcess finished (0)" else "Error: compilation failed"
        "JavaScript" -> if (code.contains("console.log")) "Hello Vastavik\n\nProcess finished (0)" else "Error"
        "SQL" -> "id | name | class\n1 | Aarav | 10\n2 | Diya | 11\n\n2 rows"
        else -> code.take(200)
    }
}
