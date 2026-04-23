package com.example.a207945_salsabilaa_izwan_lab3

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Shared navigation bar item component.
 * Fixed layout using Box stacking to ensure labels never disappear when selected.
 */
@Composable
fun NavBarItem(label: String, icon: String, isSelected: Boolean = false, onClick: () -> Unit) {
    // Reduced offset to bring bubble closer to the text
    val offsetAnimation by animateDpAsState(targetValue = if (isSelected) (-25).dp else 0.dp)

    Box(
        modifier = Modifier
            .width(85.dp)
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.BottomCenter
    ) {
        // The Icon Bubble Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 18.dp), // Reserve space for the label, brought bubble area down slightly
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .offset(y = offsetAnimation)
                    .size(if (isSelected) 58.dp else 30.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                    .then(
                        if (isSelected) Modifier.border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = if (isSelected) 28.sp else 22.sp
                )
            }
        }

        // The Label - Stays fixed at bottom
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(bottom = 12.dp) // Adjusted to reduce gap
        )
    }
}
