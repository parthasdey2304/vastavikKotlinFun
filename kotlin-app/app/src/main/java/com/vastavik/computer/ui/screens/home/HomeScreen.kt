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
import com.vastavik.computer.ui.screens.chat.ChatScreen
import com.vastavik.computer.ui.screens.learning.LearningPathScreen
import com.vastavik.computer.ui.screens.practice.PracticeScreen
import com.vastavik.computer.ui.theme.VastavikColors
import com.vastavik.computer.ui.theme.neoShape
import com.vastavik.computer.ui.theme.neoCircleShape
import com.vastavik.computer.ui.components.PromoPopup
import com.vastavik.computer.ui.components.PromoData

private var promoShown = false

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showPromo by remember { mutableStateOf(!promoShown) }

    val sampleCourses = listOf("Java Programming", "Python Basics", "Data Structures", "Web Development")
    val sampleTopics = listOf("OOP Concepts", "Arrays & Lists", "Sorting Algorithms", "File Handling")

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
                    courses = sampleCourses,
                    topics = sampleTopics
                )
                1 -> LearningPathScreen(onNavigate = onNavigate)
                2 -> PracticeScreen(onNavigate = onNavigate)
                3 -> ChatScreen(onNavigate = onNavigate)
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
    courses: List<String>,
    topics: List<String>
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = if (MaterialTheme.shapes.medium.toString().contains("0.0")) RoundedCornerShape(0.dp) else RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hello, Student",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ready to write some code?",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onNavigate("notifications") }, modifier = Modifier.size(48.dp).clip(neoCircleShape()).background(MaterialTheme.colorScheme.surfaceVariant)) {
                                Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(neoCircleShape())
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clickable { onNavigate("profile") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search courses, topics...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = neoShape(16.dp),
                        singleLine = true
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            SectionTitle(title = "Continue Learning", action = "View All")
            Spacer(modifier = Modifier.height(16.dp))
            ContinueLearningCard(onNavigate = onNavigate)
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item {
            SectionTitle(title = "Promotions", action = "See All")
            Spacer(modifier = Modifier.height(16.dp))
            PromoBanner(color = 0xFF6366F1L, title = "Diwali 50% OFF!", subtitle = "Premium Rs 149/mo - UPI AutoPay", onClick = { onNavigate("payment") })
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item {
            SectionTitle(title = "More Practice", action = "")
            Spacer(modifier = Modifier.height(16.dp))
            PyqEntryCard(onNavigate = onNavigate)
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item {
            SectionTitle(title = "Course Catalog", action = "Explore")
            Spacer(modifier = Modifier.height(16.dp))
            CourseCatalog(courses = courses, onNavigate = onNavigate)
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item {
            SectionTitle(title = "Popular Topics", action = "")
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(topics) { topic ->
            PopularTopicItem(title = topic, subject = "CS", duration = "15 min")
            Spacer(modifier = Modifier.height(12.dp))
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun SectionTitle(title: String, action: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (action.isNotEmpty()) {
            Text(
                text = action,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
private fun ContinueLearningCard(onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onNavigate("video_lesson/1/1/1/1") },
        shape = neoShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFF6366F1))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(neoShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Code, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Java Programming",
                        color = Color.White,
                        fontWeight = FontWeight.W600
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Object-Oriented Programming (OOP) Concepts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Progress", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.65f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(neoShape(4.dp)),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = Color.White.copy(alpha = 0.24f)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        onClick = { onNavigate("video_lesson/1/1/1/1") },
                        shape = neoShape(20.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "Continue",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromoBanner(color: Long, title: String, subtitle: String, onClick: ()->Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick),
        shape = neoShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(color))
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun PyqEntryCard(onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onNavigate("pyq") },
        shape = neoShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Article,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Past Year Questions (PYQ)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Practice previous board exam papers",
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
private fun CourseCatalog(courses: List<String>, onNavigate: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(courses) { course ->
            CourseCard(title = course, onClick = { onNavigate("learning_path") })
        }
    }
}

@Composable
private fun CourseCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = neoShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(neoCircleShape())
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Code,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PopularTopicItem(title: String, subject: String, duration: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = neoShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(neoShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$subject \u2022 $duration",
                    fontSize = 13.sp,
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

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { (icon, label, index) ->
                val isSelected = selectedIndex == index
                Surface(
                    onClick = { onItemSelected(index) },
                    shape = neoShape(20.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            icon,
                            contentDescription = label,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                label,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
