package com.payoff.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OfflineBadge(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
        contentColor = MaterialTheme.colorScheme.tertiary
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Offline Mode • *99#",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NumPad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonModifier = Modifier
        .aspectRatio(1.5f)
        .fillMaxWidth()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9")
        )

        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (number in row) {
                    NumPadButton(
                        text = number,
                        onClick = { onNumberClick(number) },
                        modifier = Modifier.weight(1f).then(buttonModifier)
                    )
                }
            }
        }

        // Bottom row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f).then(buttonModifier))
            
            NumPadButton(
                text = "0",
                onClick = { onNumberClick("0") },
                modifier = Modifier.weight(1f).then(buttonModifier)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(buttonModifier)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onDeleteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun NumPadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun PinDots(
    pinLength: Int,
    currentLength: Int,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pinLength) {
            val isFilled = i < currentLength
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isError -> MaterialTheme.colorScheme.error
                            isFilled -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                        }
                    )
            )
        }
    }
}
