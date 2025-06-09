package dev.lancy.drp25.utilities

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.*
import platform.AVFoundation.*
import platform.CoreFoundation.dispatch_get_main_queue
import platform.CoreGraphics.CGRect
import platform.QuartzCore.*
import platform.UIKit.UIView
import kotlinx.coroutines.*
import kotlinx.coroutines.MainScope

@OptIn(ExperimentalForeignApi::class)
actual fun CameraView() {
    val session = AVCaptureSession().apply {
        sessionPreset = AVCaptureSessionPresetPhoto
        val device = AVCaptureDevice
            .devicesWithMediaType(AVMediaTypeVideo)
            .first { (it as AVCaptureDevice).position == AVCaptureDevicePositionBack } as AVCaptureDevice
        addInput(AVCaptureDeviceInput.deviceInputWithDevice(device, null) as AVCaptureDeviceInput)
    }

    val output = AVCaptureMetadataOutput().also { out ->
        session.addOutput(out)
        out.setMetadataObjectsDelegate(
            delegate = object : AVCaptureMetadataOutputObjectsDelegateProtocol {
                override fun metadataOutput(
                    output: AVCaptureMetadataOutput,
                    metadataObjects: List<*>,
                    connection: AVCaptureConnection
                ) {
                    (metadataObjects.firstOrNull() as? AVMetadataMachineReadableCodeObject)
                        ?.stringValue
                        ?.takeIf { it.isNotBlank() }
                        ?.let { code ->
                            // emit on a coroutine
                            MainScope().launch {
                                BarcodeScannerManager.emit(code)
                            }
                        }
                }
            },
            queue = dispatch_get_main_queue()
        )
        out.metadataObjectTypes = listOf(
            AVMetadataObjectTypeEAN13Code,
            AVMetadataObjectTypeEAN8Code,
            AVMetadataObjectTypeCode128Code,
            AVMetadataObjectTypeQRCode
        )
    }

    val previewLayer = AVCaptureVideoPreviewLayer.session(session).also {
        it.videoGravity = AVLayerVideoGravityResizeAspectFill
    }

    session.startRunning()

    UIKitView(
        modifier = Modifier.fillMaxSize(),
        background = Color.Black,
        factory = {
            val view = UIView()
            view.layer.addSublayer(previewLayer)
            view
        },
        onResize = { view: UIView, frame: CValue<CGRect> ->
            CATransaction.begin().apply {
                setValue(true, kCATransactionDisableActions)
                previewLayer.frame = frame
                view.layer.frame = frame
                commit()
            }
        }
    )
}
