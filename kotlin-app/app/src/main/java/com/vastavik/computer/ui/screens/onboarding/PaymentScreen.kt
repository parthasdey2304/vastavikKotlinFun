package com.vastavik.computer.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vastavik.computer.ui.theme.VastavikColors
import com.vastavik.computer.ui.theme.neoShape
import com.vastavik.computer.ui.theme.neoCircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(onNavigate: (String) -> Unit) {
    var selectedPlan by remember { mutableStateOf("monthly") }
    var gateway by remember { mutableStateOf("Razorpay") } // Razorpay / PhonePe
    val promoActive = true // from Firestore promotions where isActive
    val promoDiscount = 50

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Premium Plans") },
                navigationIcon = {
                    IconButton(onClick = { onNavigate("profile") }) {
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Premium header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = neoShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFF6366F1))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Vastavik Premium",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Unlock your full potential",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gateway toggle PhonePe / Razorpay
            Text("Payment Gateway", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Razorpay","PhonePe").forEach { g ->
                    val sel = gateway == g
                    Card(
                        modifier = Modifier.weight(1f).clickable{ gateway = g },
                        shape = neoShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if(sel) MaterialTheme.colorScheme.primary.copy(alpha=0.12f) else MaterialTheme.colorScheme.surface),
                        border = if(sel) CardDefaults.outlinedCardBorder().copy(width=2.dp) else null
                    ) { Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment=Alignment.Center){ Text(g, fontWeight=FontWeight.Bold, color= if(sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) } }
                }
            }
            Spacer(Modifier.height(16.dp))
            if (promoActive) {
                Card(shape = neoShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), modifier=Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment=Alignment.CenterVertically){
                        Icon(Icons.Filled.LocalOffer, contentDescription=null, tint=MaterialTheme.colorScheme.tertiary)
                        Spacer(Modifier.width(8.dp))
                        Text("50% OFF applied!", fontWeight=FontWeight.Bold, color=MaterialTheme.colorScheme.tertiary)
                        Spacer(Modifier.weight(1f))
                        Text("Diwali Sale", fontSize=11.sp, color=MaterialTheme.colorScheme.tertiary)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Plans with slashed price when promo
            PlanCard(
                title = "Monthly",
                price = if(promoActive) "\u20B9149" else "\u20B9299",
                period = "/month",
                originalPrice = if(promoActive) "\u20B9299" else null,
                isSelected = selectedPlan == "monthly",
                onClick = { selectedPlan = "monthly" }
            )
            Spacer(modifier = Modifier.height(12.dp))
            PlanCard(
                title = "Yearly",
                price = if(promoActive) "\u20B9999" else "\u20B91,999",
                period = "/year",
                originalPrice = if(promoActive) "\u20B91,999" else null,
                badge = if(promoActive) "50% OFF" else "Save 44%",
                isSelected = selectedPlan == "yearly",
                onClick = { selectedPlan = "yearly" }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Features
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = neoShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "What you get:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    listOf(
                        "Unlimited video lessons",
                        "All coding challenges",
                        "PYQ access",
                        "AI chat assistant",
                        "Priority support"
                    ).forEach { feature ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(feature, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* createMandate via gateway, then webhook */ onNavigate("payment_history") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = neoShape(16.dp)
            ) {
                Text(
                    if(promoActive) "Pay with ${gateway} - UPI AutoPay (50% OFF)" else "Subscribe with ${gateway} - UPI AutoPay",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("Monthly UPI AutoPay mandate. Access revoked if not paid after 3-day grace. Cancel anytime.", fontSize=11.sp, color=MaterialTheme.colorScheme.onSurfaceVariant, textAlign=TextAlign.Center, modifier=Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    period: String,
    originalPrice: String? = null,
    badge: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = neoShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().takeIf { isSelected }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (originalPrice != null) { Text(originalPrice, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough), modifier=Modifier.padding(end=6.dp)) }
                    Text(price, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                }
                Text(period, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (badge != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = neoShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiary
                ) {
                    Text(
                        badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
