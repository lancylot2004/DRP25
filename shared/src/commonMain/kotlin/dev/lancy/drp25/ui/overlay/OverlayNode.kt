package dev.lancy.drp25.ui.overlay

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode

class OverlayNode(nodeContext: NodeContext) : LeafNode(nodeContext) {
    @Composable
    override fun Content(modifier: Modifier) {
        Text("Overlay")
    }
}
