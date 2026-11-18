package com.aritxonly.bdfun.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
internal fun ActionSheetRow(label: String, enabled: Boolean = true, icon: ImageVector? = null, onClick: () -> Unit) {
    val colors = COUITheme.colorScheme
    Row(Modifier.fillMaxWidth().clickable(enabled = enabled, onClick = onClick)
        .padding(horizontal = 18.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            Icon(icon, null, Modifier.size(24.dp), tint = colors.onSurfaceContainer)
            Spacer(Modifier.width(14.dp))
        }
        Text(label, color = if (enabled) colors.onSurfaceContainer else colors.onBackgroundVariant, fontSize = 16.sp)
    }
}
