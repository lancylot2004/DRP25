package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


// Camera Manager for getting access to the camera
// -> implement separately for android and iOS
@Composable
expect fun CameraView()


// Barcode strings scanner
object BarcodeScannerManager {
    private val _barcodeFlow = MutableSharedFlow<String>(replay = 0)
    val barcodeFlow = _barcodeFlow.asSharedFlow()

    // INTERNAL: platform code calls this to emit a new scan
    internal suspend fun emit(barcode: String) {
        _barcodeFlow.emit(barcode)
    }
}
