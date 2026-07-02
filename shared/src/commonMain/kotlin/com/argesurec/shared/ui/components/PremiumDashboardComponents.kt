package com.argesurec.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.argesurec.shared.model.Task
import com.argesurec.shared.model.TaskPriority
import com.argesurec.shared.ui.theme.ArgepColors
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@Composable
fun ExecutiveStatCard(
    label: String,
    value: String,
    trend: String,
    icon: ImageVector,
    iconColor: Color = ArgepColors.ChartBlue,
    iconBg: Color = ArgepColors.ChartBlueBg,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
            spotColor = iconColor.copy(alpha = 0.6f)
        ),
        colors = CardDefaults.cardColors(containerColor = iconColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        ) {
            // Watermark icon in top right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 16.dp, y = (-16).dp)
                    .size(72.dp)
                    .background(Color.White.copy(alpha = 0.15f), shape = androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    modifier = Modifier.size(32.dp).offset(x = (-8).dp, y = 8.dp), 
                    tint = Color.White.copy(alpha = 0.9f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = value,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ExecutiveProjectRow(
    name: String,
    phase: String,
    progress: Float,
    eta: String = "Nov 12"
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate100)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = phase.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = ArgepColors.ChartBlue
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = ArgepColors.Slate400
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ArgepColors.ExecutivePrimary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ArgepColors.ChartBlue,
                trackColor = ArgepColors.ChartBlueBg,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(progress * 100).toInt()}% Complete",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArgepColors.Slate500
                )
                Text(
                    text = "ETA: $eta",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArgepColors.Slate500
                )
            }
        }
    }
}

@Composable
fun ExecutiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.ExecutivePrimary),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.02.sp),
            color = ArgepColors.White
        )
    }
}

@Composable
fun PremiumStatCard(
    label: String,
    value: String,
    delta: String,
    icon: String,
    iconBg: Color = ArgepColors.Navy50,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(9.dp),
                color = iconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(icon, fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = ArgepColors.Slate500
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 28.sp
                ),
                color = ArgepColors.Navy900
            )
            Text(
                text = delta,
                style = MaterialTheme.typography.labelSmall,
                color = if (delta.contains("↑")) ArgepColors.Phase3 else ArgepColors.Slate500
            )
        }
    }
}

@Composable
fun PremiumTaskRow(
    task: Task,
    onClick: () -> Unit
) {
    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> ArgepColors.Error
        TaskPriority.MEDIUM -> ArgepColors.Phase2
        TaskPriority.LOW -> ArgepColors.Phase3
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(32.dp)
                .background(priorityColor, RoundedCornerShape(2.dp))
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                color = ArgepColors.Navy800
            )
            Text(
                text = "Proje Detayı · Bitiş: 2 Mayıs", // Placeholder metadata
                style = MaterialTheme.typography.bodySmall,
                color = ArgepColors.Slate500
            )
        }

        Surface(
            color = when(task.priority) {
                TaskPriority.HIGH -> ArgepColors.Error.copy(alpha = 0.1f)
                TaskPriority.MEDIUM -> ArgepColors.Phase2Light
                else -> ArgepColors.Phase3Light
            },
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = task.priority.name,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = when(task.priority) {
                    TaskPriority.HIGH -> ArgepColors.Error
                    TaskPriority.MEDIUM -> ArgepColors.Phase2
                    else -> ArgepColors.Phase3
                }
            )
        }
    }
}

@Composable
fun ProjectProgressRow(
    name: String,
    phase: String,
    progress: Float,
    phaseColor: Color,
    phaseBg: Color
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        // TOP SECTION: Percentage and Category Name
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ArgepColors.Slate500
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = ArgepColors.Navy900
                )
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // PROGRESS BAR: Thick, Rounded, Modern
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = phaseColor,
            trackColor = phaseColor.copy(alpha = 0.12f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // BOTTOM SECTION: Spent / Total or Phase logic
        Text(
            text = "Durum: $phase",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = ArgepColors.Slate500
            )
        )
    }
}
