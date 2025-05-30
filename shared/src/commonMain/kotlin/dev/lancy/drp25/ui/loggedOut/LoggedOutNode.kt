package dev.lancy.drp25.ui.loggedOut

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode

class LoggedOutNode(nodeContext: NodeContext) : LeafNode(nodeContext) {
    @Composable
    override fun Content(modifier: Modifier) {
        Text("Logged Out")
    }
}
