package dev.lancy.drp25.ui.shared.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography

@Composable
fun IconText(icon: ImageVector, desc: String, text: String) {
    Row {
        Icon(
            icon,
            contentDescription = desc,
            modifier = Modifier.padding(Size.CornerSmall),
            tint = ColourScheme.onBackground,
        )

        Text(
            text,
            modifier = Modifier.align(Alignment.CenterVertically),
            style = Typography.bodyMedium,
            color = ColourScheme.onBackground,
        )
    }
}