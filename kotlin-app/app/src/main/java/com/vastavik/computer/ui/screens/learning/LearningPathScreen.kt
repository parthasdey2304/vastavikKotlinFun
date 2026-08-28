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
import androidx.compose.material3.MaterialTheme
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
import com.vastavik.computer.ui.components.VastavikTopBar

@Composable
fun LearningPathScreen(onNavigate: (String) -> Unit) {
    val courses = listOf("Java", "Python", "C++", "Web Dev")
    var selectedCourse by remember { mutableStateOf("Java") }
    var showPartSheet by remember { mutableStateOf(false) }
    var selectedPart by remember { mutableStateOf("") }

    val nodes = listOf(
        "Introduction", "Variables", "Control Flow",
        "Functions", "OOP Basics", "Collections",
        "File I/O", "Project", "Final Project"
    )
    val offsets = listOf(0f, 0.4f, 0.8f, 0.4f, 0f, -0.4f, -0.8f, -0.4f, 0f)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Top bar
            VastavikTopBar(onProfileClick = { onNavigate("profile") })

            // Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Learning Path",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Course selector chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                courses.forEach { course ->
                    val isSelected = selectedCourse == course
                    Surface(
                        onClick = { selectedCourse = course },
                        shape = RoundedCornerShape(50.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                    ) {
                        Text(
                            text = course,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Unit Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Unit 1", color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Java Fundamentals", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Master the basics of Java programming", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Zigzag path
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                itemsIndexed(nodes) { index, node ->
                    val isTrophy = index == nodes.lastIndex
                    val isDone = index < 3
                    val isCurrent = index == 3
                    val xOffset = offsets[index % offsets.size]

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Connector Line
                            if (index < nodes.lastIndex) {
                                val nextXOffset = offsets[(index + 1) % offsets.size]
                                val pathColor = MaterialTheme.colorScheme.primary.copy(alpha = if (isDone || isCurrent) 1f else 0.2f)
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val startX = size.width / 2 + (xOffset * 100.dp.toPx())
                                    val endX = size.width / 2 + (nextXOffset * 100.dp.toPx())
                                    drawLine(
                                        color = pathColor,
                                        start = Offset(startX, size.height / 2),
                                        end = Offset(endX, size.height / 2),
                                        strokeWidth = 4.dp.toPx()
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
                                color = when {
                                    isDone -> MaterialTheme.colorScheme.primary
                                    isCurrent -> Color.White
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                shadowElevation = if (isDone || isCurrent) 6.dp else 1.dp,
                                modifier = Modifier
                                    .size(if (isTrophy) 72.dp else 64.dp)
                                    .offset(x = (xOffset * 80).dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isTrophy) {
                                        Icon(
                                            Icons.Filled.EmojiEvents,
                                            contentDescription = "Trophy",
                                            tint = if (isDone) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    } else {
                                        Icon(
                                            Icons.Filled.Star,
                                            contentDescription = "Star",
                                            tint = when {
                                                isDone -> Color.White
                                                isCurrent -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = node,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
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
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(subpart, fontWeight = FontWeight.W500)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
