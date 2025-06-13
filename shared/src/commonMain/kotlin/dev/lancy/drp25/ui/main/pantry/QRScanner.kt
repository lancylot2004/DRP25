package dev.lancy.drp25.ui.main.pantry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Flashlight
import com.composables.icons.lucide.FlashlightOff
import com.composables.icons.lucide.GalleryThumbnails
import com.composables.icons.lucide.Lucide
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import org.publicvalue.multiplatform.qrcode.CameraPosition
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions

@Composable
fun QRScannerView(onNavigate: (String) -> Unit) {
    var qrCode by remember { mutableStateOf("") }
    var flashlightOn by remember { mutableStateOf(false) }
    var openImagePicker by remember { mutableStateOf(value = false) }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(ColourScheme.primary)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(Shape.RoundedMedium)
                    .clipToBounds()
                    .border(Size.Spacing, ColourScheme.outlineVariant, Shape.RoundedMedium),
                contentAlignment = Alignment.Center,
            ) {
                ScannerWithPermissions(
                    modifier = Modifier.fillMaxSize(),
                    onScanned = {
                        qrCode = it
                        true
                    },
                    types = listOf(CodeType.EAN13, CodeType.EAN8),
                    cameraPosition = CameraPosition.BACK,
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = Size.BigPadding, end = Size.BigPadding, top = Size.BigPadding)
                    .background(
                        color = ColourScheme.primaryContainer,
                        shape = Shape.RoundedLarge,
                    ).height(Size.BarMedium),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = Size.Spacing, horizontal = Size.BigPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Size.Padding),
                ) {
                    Icon(
                        if (flashlightOn) Lucide.Flashlight else Lucide.FlashlightOff,
                        contentDescription = "flash",
                        modifier = Modifier
                            .size(Size.IconMedium)
                            .clickable { flashlightOn = !flashlightOn },
                    )

                    VerticalDivider(
                        modifier = Modifier,
                        thickness = 1.dp,
                        color = ColourScheme.onPrimary,
                    )

                    Icon(
                        Lucide.GalleryThumbnails,
                        contentDescription = "open gallery",
                        modifier = Modifier
                            .size(Size.IconMedium)
                            .clickable { openImagePicker = true },
                    )
                }
            }
        }

        if (qrCode.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .padding(bottom = 22.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = qrCode,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f),
                )
            }
        }
    }
}
