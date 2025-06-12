package dev.lancy.drp25

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.bumble.appyx.navigation.integration.IosNodeHost
import com.bumble.appyx.navigation.integration.MainIntegrationPoint
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.shared.AppTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

val backEvents: Channel<Unit> = Channel()
private val integrationPoint = MainIntegrationPoint()

@Suppress("unused", "FunctionName")
fun MainViewController() =
    ComposeUIViewController {
        AppTheme {
            Scaffold {
                IosNodeHost(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                    onBackPressedEvents = backEvents.receiveAsFlow(),
                    integrationPoint = remember { integrationPoint },
                ) { RootNode(nodeContext = it) }
            }
        }
    }.also { vc -> integrationPoint.setViewController(vc) }
