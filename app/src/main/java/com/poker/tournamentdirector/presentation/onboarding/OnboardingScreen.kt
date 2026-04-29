package com.poker.tournamentdirector.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
) {
    var pageIndex by remember { mutableIntStateOf(0) }
    val page = OnboardingPages[pageIndex]
    val isLastPage = pageIndex == OnboardingPages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = page.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OnboardingPages.forEachIndexed { index, _ ->
                    val selected = index == pageIndex
                    Surface(
                        modifier = Modifier
                            .size(width = if (selected) 28.dp else 8.dp, height = 8.dp)
                            .clip(CircleShape),
                        color = if (selected) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        shape = CircleShape,
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = onFinished,
                    enabled = !isLastPage,
                ) {
                    Text("Skip")
                }
                Button(
                    onClick = {
                        if (isLastPage) {
                            onFinished()
                        } else {
                            pageIndex += 1
                        }
                    },
                ) {
                    Text(if (isLastPage) "Choose plan" else "Next")
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

private data class OnboardingPage(
    val title: String,
    val body: String,
    val icon: ImageVector,
)

private val OnboardingPages = listOf(
    OnboardingPage(
        title = "Season control",
        body = "Manage league dates, rosters, money pools, guests, and standings from one offline-first workspace.",
        icon = Icons.Filled.Groups,
    ),
    OnboardingPage(
        title = "Night operations",
        body = "Track buy-ins, rebuys, add-ons, seats, eliminations, bounties, and payouts during live play.",
        icon = Icons.Filled.Star,
    ),
    OnboardingPage(
        title = "Poker clock",
        body = "Run blind levels, breaks, antes, and a clean TV display for the table.",
        icon = Icons.Filled.Timer,
    ),
    OnboardingPage(
        title = "Reports",
        body = "Share CSV and WhatsApp-ready summaries for results, season payouts, and player stats.",
        icon = Icons.Filled.BarChart,
    ),
)
