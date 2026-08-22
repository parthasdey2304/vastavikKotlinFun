package com.vastavik.computer.ui.screens.learning

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vastavik.computer.ui.theme.VastavikColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningPathScreen(onNavigate: (String) -> Unit) {
    val courses = listOf("Java", "Python", "C++", "Web Dev")
    var selectedCourse by remember { mutableStateOf("Java") }
    var showPartSheet by remember { mutableStateOf(false) }
    var selectedPart by remember { mutableStateOf("") }

    val nodes = listOf(
        "Introduction", "Variables & Types", "Control Flow",
        "Arrays & Lists", "OOP Basics", "Inheritance",
        "Polymorphism", "File Handling", "Final Project"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learning Path") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Course selector chips
            ScrollableTabRow(
                selectedTabIndex = courses.indexOf(selectedCourse).coerceAtLeast(0),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.background,
                divider = {}
            ) {
                courses.forEach { course ->
                    Tab(
                        selected = selectedCourse == course,
                        onClick = { selectedCourse = course },
                        text = {
                            FilterChip(
                                selected = selectedCourse == course,
                                onClick = { selectedCourse = course },
                                label = { Text(course) }
                            )
                        }
                    )
                }
            }

            // Zigzag path
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                item {
                    // Unit Header
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = VastavikColors.LightPrimary
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Unit 1", color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Java Fundamentals", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Learn the basics of variables, loops, and OOP.", color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                itemsIndexed(nodes) { index, node ->
                    val isTrophy = index == nodes.lastIndex
                    // Zigzag calculation
                    val offsets = listOf(0f, 0.4f, 0.8f, 0.4f, 0f, -0.4f, -0.8f, -0.4f)
                    val xOffset = offsets[index % offsets.size]
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Connector Line (drawn behind)
                            if (index < nodes.lastIndex) {
                                val nextXOffset = offsets[(index + 1) % offsets.size]
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val startX = size.width / 2 + (xOffset * 100.dp.toPx())
                                    val endX = size.width / 2 + (nextXOffset * 100.dp.toPx())
                                    drawPath(
                                        path = Path().apply {
                                            moveTo(startX, size.height / 2)
                                            cubicTo(
                                                startX, size.height,
                                                endX, 0f,
                                                endX, size.height * 1.5f
                                            )
                                        },
                                        color = VastavikColors.LightPrimary.copy(alpha = if (index < 3) 1f else 0.3f),
                                        style = Stroke(width = 12.dp.toPx())
                                    )
                                }
                            }
                            
                            // Node Button
                            Surface(
                                onClick = {
                                    if (!isTrophy) {
                                        selectedPart = node
                                        showPartSheet = true
                                    }
                                },
                                shape = CircleShape,
                                color = if (index < 3) VastavikColors.LightPrimary else Color(0xFFE5E7EB),
                                shadowElevation = if (index < 3) 8.dp else 2.dp,
                                modifier = Modifier
                                    .size(if (isTrophy) 80.dp else 72.dp)
                                    .offset(x = (xOffset * 100).dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isTrophy) {
                                        Icon(
                                            Icons.Filled.EmojiEvents,
                                            contentDescription = "Trophy",
                                            tint = if (index < 3) Color.White else Color.Gray,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    } else {
                                        Icon(
                                            Icons.Filled.Star,
                                            contentDescription = "Star",
                                            tint = if (index < 3) Color.White else Color.Gray,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Part bottom sheet
    if (showPartSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPartSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = selectedPart,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                val subparts = listOf("Video Lesson", "Practice Quiz", "Coding Exercise", "Notes")
                subparts.forEach { subpart ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                showPartSheet = false
                                onNavigate("video_lesson/1/1/1/1")
                            },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                when (subpart) {
                                    "Video Lesson" -> Icons.Filled.PlayCircle
                                    "Practice Quiz" -> Icons.Filled.Quiz
                                    "Coding Exercise" -> Icons.Filled.Code
                                    else -> Icons.Filled.Note
                                },
                                contentDescription = null,
                                tint = VastavikColors.LightPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(subpart, fontWeight = FontWeight.W500)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
