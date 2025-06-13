package dev.lancy.drp25.ui.main.me

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.Settings
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.ColourScheme
import kotlinx.coroutines.launch

class MeNode(
    nodeContext: NodeContext,
    parent: MainNode
) : LeafNode(nodeContext),
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {

    @Composable
    override fun Content(modifier: Modifier) {
        val scope = rememberCoroutineScope()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        this@MeNode
                            .navParent
                            .superNavigate<RootNode.RootTarget>(RootNode.RootTarget.Settings)
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Lucide.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }

            // Dummy content, but leave the settings button in place above.

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF444444)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.CircleUserRound,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "DRP 25",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatItem("Cooked", "2")
                    StatItem("Saved", "8")
                    StatItem("Streak", "1d")
                }

                Spacer(Modifier.height(32.dp))

                Divider(
                    color = ColourScheme.outlineVariant,
                    thickness = 2.dp,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.LightGray, fontSize = 13.sp)
    }
}