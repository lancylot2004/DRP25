package dev.lancy.drp25.ui.main.me

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import dev.lancy.drp25.ui.main.MainNode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.collectAsState
import dev.lancy.drp25.utilities.rememberPersisted
import dev.lancy.drp25.data.MeasurementSystem

class MeNode(nodeContext: NodeContext, parent: MainNode): LeafNode(nodeContext) {

    @Composable
    override fun Content(modifier: Modifier) {
        val systemPersistence = rememberPersisted("measurement_system") { MeasurementSystem.METRIC }
        val isMetric = systemPersistence.state.collectAsState().value == MeasurementSystem.METRIC

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Me")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = isMetric,
                    onClick = { systemPersistence.update { MeasurementSystem.METRIC } }
                )
                Text("Metric")
                RadioButton(
                    selected = !isMetric,
                    onClick = { systemPersistence.update { MeasurementSystem.IMPERIAL } }
                )
                Text("Imperial")
            }
            Text("move to settings page later", modifier = Modifier.padding(top = 8.dp))
        }
    }
}
