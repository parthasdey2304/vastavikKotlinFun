package com.vastavik.computer.ui.screens.video

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vastavik.computer.ui.theme.VastavikColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLessonScreen(
    lessonId: String,
    courseId: String,
    partId: String,
    subpartId: String,
    onNavigate: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Lesson") },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Video player placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PlayCircle,
                    contentDescription = "Play",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(72.dp)
                )
            }

            // Title and description
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Object-Oriented Programming Basics",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Learn the fundamentals of OOP including classes, objects, inheritance, and polymorphism.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 3-format tabs: VS Code 16:9, Whiteboard 16:9, Shorts 9:16 1-2m
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = VastavikColors.LightPrimary
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("VS Code", fontSize=12.sp) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Whiteboard", fontSize=12.sp) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Shorts", fontSize=12.sp) })
            }

            when (selectedTab) {
                0 -> CodeNotesTab()
                1 -> WhiteboardTab()
                2 -> ShortsTab()
            }
        }
    }
}

@Composable
private fun CodeNotesTab() {
    Column(modifier = Modifier.padding(16.dp)) {
        // Code sample
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = VastavikColors.CodeBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Code Sample",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """class Animal {
    String name;
    
    void speak() {
        System.out.println(name + " makes a sound");
    }
}

class Dog extends Animal {
    void speak() {
        System.out.println(name + " barks");
    }
}""",
                    color = VastavikColors.CodeText,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Notes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Inheritance allows a class to inherit properties and methods from another class. The extends keyword is used to establish inheritance.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
private fun WhiteboardTab() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Draw,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Whiteboard content will appear here",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ShortsTab() {
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.width(200.dp).height(360.dp).clip(RoundedCornerShape(16.dp)).background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(androidx.compose.material.icons.Icons.Filled.PlayCircle, contentDescription=null, tint=Color.White.copy(alpha=0.8f), modifier=Modifier.size(64.dp))
                Spacer(Modifier.height(8.dp))
                Text("Short 1-2 min vertical", color=Color.White.copy(alpha=0.7f), fontSize=11.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        Card(shape=RoundedCornerShape(12.dp), colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(16.dp)){
                Text("Quick Revision", fontWeight=FontWeight.Bold, fontSize=16.sp, color=MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(8.dp))
                Text("1-2 min fast explanation of OOP concepts - perfect for revision before exams.", fontSize=14.sp, color=MaterialTheme.colorScheme.onSurfaceVariant, lineHeight=22.sp)
            }
        }
    }
}