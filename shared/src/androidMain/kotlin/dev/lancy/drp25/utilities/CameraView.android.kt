package dev.lancy.drp25.utilities

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
actual fun CameraView() {
    val context = LocalContext.current
    val owner   = LocalLifecycleOwner.current
    val providerF = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val scanner  = remember { BarcodeScanning.getClient() }
    val scope    = rememberCoroutineScope()

    DisposableEffect(previewView) {
        val provider = providerF.get()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also { ana ->
                ana.setAnalyzer(executor) { imgProxy ->
                    imgProxy.image?.let { mediaImage ->
                        val inputImage = InputImage.fromMediaImage(mediaImage, imgProxy.imageInfo.rotationDegrees)
                        scanner.process(inputImage)
                            .addOnSuccessListener { barcodes ->
                                barcodes.firstOrNull()?.rawValue
                                    ?.takeIf(String::isNotBlank)
                                    ?.let { code ->
                                        // emit into shared flow
                                        scope.launch(Dispatchers.Default) {
                                            BarcodeScannerManager.emit(code)
                                        }
                                    }
                            }
                            .addOnCompleteListener { imgProxy.close() }
                    } ?: imgProxy.close()
                }
            }

        provider.bindToLifecycle(owner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)

        onDispose {
            provider.unbindAll()
            executor.shutdown()
            scanner.close()
        }
    }

    AndroidView(
        factory  = { previewView },
        modifier = Modifier.fillMaxSize()
    )
}
