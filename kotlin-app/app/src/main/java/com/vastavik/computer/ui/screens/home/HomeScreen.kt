package com.vastavik.computer.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vastavik.computer.ui.components.VastavikTopBar
import com.vastavik.computer.ui.components.PromoPopup
import com.vastavik.computer.ui.components.PromoData
import com.vastavik.computer.ui.theme.neoShape

private var promoShown = false

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showPromo by remember { mutableStateOf(!promoShown) }

    val sampleCourses = listOf(
        Triple("Java Programming", Color(0xFF8B5CF6) to Color(0xFF6366F1), "42 lessons"),
        Triple("Python Basics", Color(0xFF10B981) to Color(0xFF14B8A6), "36 lessons"),
        Triple("Data Structures", Color(0xFFF59E0B) to Color(0xFFF97316), "28 lessons"),
        Triple("Web Development", Color(0xFF06B6D4) to Color(0xFF3B82F6), "51 lessons")
    )

    if (showPromo) {
        promoShown = true
        PromoPopup(
            promo = PromoData(title="50% OFF Premium!", body="Get full access to Java/Python/JS/SQL + AI Chat & papers. UPI AutoPay Rs 149/mo.", ctaText="Grab Now"),
            onDismiss = { showPromo = false },
            onCta = { showPromo = false; onNavigate("payment") }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .consumeWindowInsets(padding)
        ) {
            when (selectedIndex) {
                0 -> HomeTab(
                    modifier = Modifier,
                    onNavigate = onNavigate,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    courses = sampleCourses
                )
                1 -> com.vastavik.computer.ui.screens.learning.LearningPathScreen(onNavigate = onNavigate)
                2 -> com.vastavik.computer.ui.screens.practice.PracticeScreen(onNavigate = onNavigate)
                3 -> com.vastavik.computer.ui.screens.chat.ChatScreen(onNavigate = onNavigate)
            }
        }
    }
}

@Composable
private fun HomeTab(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    courses: List<Triple<String, Pair<Color, Color>, String>>
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Top Bar
        item {
            VastavikTopBar(onProfileClick = { onNavigate("profile") })
        }

        // Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFF06B6D4))
                        )
                    )
            ) {
                Box {
                    // Decorative blur circles
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .offset(x = 100.dp, y = (-40).dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .offset(x = (-20).dp, y = 80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF06B6D4).copy(alpha = 0.2f))
                    )

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "WELCOME BACK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hello, Student \uD83D\uDC4B",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ready to write some code? Pick up where you left off.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Search bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color.White)
                                .clickable { onNavigate("search") }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (searchQuery.isEmpty()) "Search courses, topics, lessons\u2026" else searchQuery,
                                    fontSize = 14.sp,
                                    color = if (searchQuery.isEmpty()) Color.Gray else Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }

            // Stats bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "7 day streak",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "  |  ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "65% avg progress",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Continue Learning
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Continue Learning",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "View all \u2192",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigate("learning_path") }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            ContinueLearningCard(onNavigate = onNavigate)
        }

        item { Spacer(modifier = Modifier.height(28.dp)) }

        // Course Catalog
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Course Catalog",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Browse all \u2192",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigate("learning_path") }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            CourseCatalogGrid(courses = courses, onNavigate = onNavigate)
        }

        item { Spacer(modifier = Modifier.height(28.dp)) }

        // Stats section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    modifier = Modifier.weight(1f),
                    icon = "\u25D0",
                    value = "12.4k",
                    label = "Active learners",
                    iconBg = Color(0xFFEEF2FF),
                    iconTint = Color(0xFF6366F1)
                )
                StatsCard(
                    modifier = Modifier.weight(1f),
                    icon = "\u2713",
                    value = "500+",
                    label = "Hands-on lessons",
                    iconBg = Color(0xFFECFDF5),
                    iconTint = Color(0xFF10B981)
                )
                StatsCard(
                    modifier = Modifier.weight(1f),
                    icon = "\u2726",
                    value = "4.8/5",
                    label = "Avg rating",
                    iconBg = Color(0xFFFFFBE6),
                    iconTint = Color(0xFFF59E0B)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Popular Topics
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Popular Topics",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            val topics = listOf("OOP Concepts", "Arrays & Lists", "Sorting Algorithms", "File Handling")
            topics.forEach { topic ->
                PopularTopicItem(title = topic, subject = "CS", duration = "15 min")
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun ContinueLearningCard(onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onNavigate("video_lesson/1/1/1/1") },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF334155))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .then(Modifier.padding(2.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("</>", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Java Programming",
                        color = Color.White,
                        fontWeight = FontWeight.Semi,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "In progress",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Object-Oriented Programming \u2014 OOP Concepts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Lesson 6 of 12 \u2022 18 min",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress 65%",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Surface(
                        onClick = { onNavigate("video_lesson/1/1/1/1") },
                        shape = RoundedCornerShape(50.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "Continue \u2192",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseCatalogGrid(
    courses: List<Triple<String, Pair<Color, Color>, String>>,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            courses.take(2).forEach { (title, colors, lessons) ->
                CourseCatalogCard(
                    modifier = Modifier.weight(1f),
                    title = title,
                    colors = colors,
                    lessons = lessons,
                    onClick = { onNavigate("learning_path") }
                )
            }
        }
        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            courses.drop(2).forEach { (title, colors, lessons) ->
                CourseCatalogCard(
                    modifier = Modifier.weight(1f),
                    title = title,
                    colors = colors,
                    lessons = lessons,
                    onClick = { onNavigate("learning_path") }
                )
            }
        }
    }
}

@Composable
private fun CourseCatalogCard(
    modifier: Modifier = Modifier,
    title: String,
    colors: Pair<Color, Color>,
    lessons: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(colors = listOf(colors.first, colors.second))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    when (title) {
                        "Java Programming" -> "\u2615"
                        "Python Basics" -> "\uD83D\uDC0D"
                        "Data Structures" -> "\u25C8"
                        else -> "</>"
                    },
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = lessons,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Explore \u2192",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun StatsCard(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String,
    iconBg: Color,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 16.sp, color = iconTint)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground, lineHeight = 16.sp)
                Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 12.sp)
            }
        }
    }
}

@Composable
private fun PopularTopicItem(title: String, subject: String, duration: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$subject \u2022 $duration",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BottomNavBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf(
        Triple(Icons.Filled.Home, "Home", 0),
        Triple(Icons.Filled.Map, "Learn", 1),
        Triple(Icons.Filled.Assignment, "Practice", 2),
        Triple(Icons.Filled.SmartToy, "AI Chat", 3)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items.forEach { (icon, label, index) ->
                    val isSelected = selectedIndex == index
                    Surface(
                        onClick = { onItemSelected(index) },
                        shape = RoundedCornerShape(50.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = if (isSelected) 16.dp else 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                icon,
                                contentDescription = label,
                                tint = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    label,
                                    color = MaterialTheme.colorScheme.background,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
