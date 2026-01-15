package com.fieldreportpro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.fieldreportpro.ui.theme.AppTextStyles
import com.fieldreportpro.ui.theme.PrimaryGreen
import com.fieldreportpro.ui.theme.TextSub

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    onCreateReport: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .drawDashedCircle(Color(0xFFE0E0E0))
        ) {
            Surface(
                modifier = Modifier
                    .size(84.dp)
                    .align(Alignment.Center),
                shape = CircleShape,
                color = Color(0xFFF7F7F7),
                border = BorderStroke(1.dp, Color(0xFFE6E6E6))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier.size(40.dp)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "No reports found", style = AppTextStyles.CardTitle)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Try changing filters or create a new report.", style = AppTextStyles.Body, color = TextSub)
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryPillButton(text = "Create report", onClick = onCreateReport)
    }
}

private fun Modifier.drawDashedCircle(color: Color): Modifier {
    return drawBehind {
        val stroke = Stroke(
            width = 3.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
        )
        drawCircle(color = color, style = stroke)
    }
}
