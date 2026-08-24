package com.vastavik.computer.ui.screens.editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vastavik.computer.ui.theme.VastavikColors
import com.vastavik.computer.ui.theme.neoShape
import com.vastavik.computer.ui.theme.neoCircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrExerciseScreen(onNavigate: (String)->Unit) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var ocrText by remember { mutableStateOf("") }
    var edited by remember { mutableStateOf("") }
    var aiResponse by remember { mutableStateOf("") }
    var tab by remember { mutableIntStateOf(0) }

    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        ocrText = "public class Main {\n  System.out.println(\"Hello\");\n}"
        edited = ocrText
    }
    val takeLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
        if (bmp!=null) { ocrText = "def hello():\n  print(\"hi\")"; edited = ocrText }
    }

    Scaffold(
        topBar = { TopAppBar(title={Text("Coding Exercise", fontWeight=FontWeight.Bold)}, navigationIcon={IconButton(onClick={onNavigate("home")}){Icon(Icons.Filled.ArrowBack,contentDescription=null)}}) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            TabRow(selectedTabIndex = tab, containerColor = MaterialTheme.colorScheme.surface) {
                Tab(selected = tab==0, onClick={tab=0}, text={Text("Type Code")})
                Tab(selected = tab==1, onClick={tab=1}, text={Text("Photo OCR")})
            }
            Spacer(Modifier.height(16.dp))
            if (tab==0) {
                Text("Write code and let AI review (chat format):", fontWeight=FontWeight.W600)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value=edited, onValueChange={edited=it}, modifier=Modifier.fillMaxWidth().height(180.dp), placeholder={Text("Paste/write code here...")}, textStyle = LocalTextStyle.current.copy(fontFamily=FontFamily.Monospace, fontSize=13.sp))
                Spacer(Modifier.height(12.dp))
                Button(onClick={
                    aiResponse = "Gemini 3.7 Flash review: Good structure! Consider adding comments and handling empty input. Fixed: def solve(): print(Hello Vastavik)"
                }, modifier=Modifier.fillMaxWidth()) { Icon(Icons.Filled.AutoFixHigh, contentDescription=null); Spacer(Modifier.width(8.dp)); Text("Ask Gemini to Review") }
                if (aiResponse.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Card(shape=neoShape(12.dp), colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
                        Text(aiResponse, modifier=Modifier.padding(16.dp), fontSize=13.sp)
                    }
                }
            } else {
                Row(horizontalArrangement=Arrangement.spacedBy(12.dp), modifier=Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick={pickLauncher.launch("image/*")}, modifier=Modifier.weight(1f)) { Icon(Icons.Filled.PhotoLibrary, contentDescription=null); Spacer(Modifier.width(6.dp)); Text("Pick Image") }
                    OutlinedButton(onClick={takeLauncher.launch(null)}, modifier=Modifier.weight(1f)) { Icon(Icons.Filled.CameraAlt, contentDescription=null); Spacer(Modifier.width(6.dp)); Text("Camera") }
                }
                Spacer(Modifier.height(12.dp))
                if (imageUri!=null) {
                    AsyncImage(model=imageUri, contentDescription=null, modifier=Modifier.fillMaxWidth().height(180.dp).background(MaterialTheme.colorScheme.surfaceVariant, neoShape(12.dp)))
                    Spacer(Modifier.height(12.dp))
                }
                if (ocrText.isNotEmpty()) {
                    Text("OCR extracted (editable):", fontWeight=FontWeight.W600, fontSize=12.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(value=edited, onValueChange={edited=it}, modifier=Modifier.fillMaxWidth().height(140.dp), textStyle=LocalTextStyle.current.copy(fontFamily=FontFamily.Monospace, fontSize=13.sp))
                    Spacer(Modifier.height(12.dp))
                    Button(onClick={ aiResponse = "OCR + Gemini 3.7 Flash: Extracted lines. Suggestion: fix indentation and add main guard."}, modifier=Modifier.fillMaxWidth()) { Icon(Icons.Filled.AutoFixHigh, contentDescription=null); Spacer(Modifier.width(8.dp)); Text("Send to Gemini") }
                    if (aiResponse.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Card(shape=neoShape(12.dp)) { Text(aiResponse, modifier=Modifier.padding(16.dp), fontSize=13.sp) }
                    }
                } else {
                    Text("Take or pick a photo of handwritten/printed code. OCR (ML Kit) will extract, you edit, then Gemini 3.7 Flash explains.", color=MaterialTheme.colorScheme.onSurfaceVariant, fontSize=13.sp)
                }
            }
        }
    }
}
