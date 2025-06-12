package dev.lancy.drp25.ui.main.pantry

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl

class PantryNode(
    nodeContext: NodeContext,
    parent: MainNode,
) : LeafNode(nodeContext),
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    @Composable
    override fun Content(modifier: Modifier) {
        Text("Pantry")

        QRScannerView { }
    }
}
