package com.example.fitnfocus.ui.focus

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.R
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.viewmodel.FocusViewModel


/**
 * Focus screen showing collected coins and a preview of focus types.
 * Timer logic lives in SessionTimerRoute.
 */
@Composable
fun FocusScreen(
    onNavigateToCollection: () -> Unit = {},
    viewModel: FocusViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val completedSessionsCount by viewModel.completedSessionsCount.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTodaySessions()
    }

    FocusContent(
        coinsTotal = completedSessionsCount,
        onNavigateToCollection = onNavigateToCollection
    )
}

@Composable
private fun FocusContent(
    coinsTotal: Int,
    onNavigateToCollection: () -> Unit,
) {
    var lastCoins by remember { mutableStateOf(coinsTotal) }
    var scaleTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(coinsTotal) {
        if (coinsTotal > lastCoins) {
            scaleTrigger = true
        }
        lastCoins = coinsTotal
    }
    // Scale animation emphasizes newly earned coins.
    val scale by animateFloatAsState(
        targetValue = if (scaleTrigger) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "coinScale"
    )

    LaunchedEffect(scaleTrigger) {
        if (scaleTrigger) {
            kotlinx.coroutines.delay(150)
            scaleTrigger = false
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F1E8), Color(0xFFE6DAC8))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Focus",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Normal,

                )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8639E0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Deine Erfolge",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBFBFC)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Münzen gesamt",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE5E5EC)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_collectible_coin),
                            contentDescription = "Münze",
                            modifier = Modifier
                                .size(34.dp)
                                .scale(scale)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = coinsTotal.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB300),
                            modifier = Modifier.scale(scale)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8639E0)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Deine Sammlung",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBFBFC)
                    )

                    Text(
                        text = "Finde deinen Fokus-Typ. Das sind feste Archetypen zur Orientierung.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE5E5EC)
                    )

                    val types = FocusTypes.staticTypes()
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(space = 10.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FocusTypePreviewCard(types[0], Modifier.weight(1f))
                            FocusTypePreviewCard(types[1], Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FocusTypePreviewCard(types[2], Modifier.weight(1f))
                            FocusTypePreviewCard(types[3], Modifier.weight(1f))
                        }
                    }

                    Button(
                        onClick = onNavigateToCollection,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC107),
                        )
                    ) {
                        Text(
                            "Zur Sammlung",
                            color = Color(0xFF34087A)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FocusTypePreviewCard(
    type: FocusTypeUi,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFFFFFF)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                type.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                type.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
