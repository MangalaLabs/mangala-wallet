/*
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:OptIn(ExperimentalForeignApi::class, ExperimentalForeignApi::class)

package com.mangala.wallet.scanqr

import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.desc.desc
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readValue
import platform.AVFoundation.*
import platform.CoreGraphics.CGFloat
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectGetMaxY
import platform.CoreGraphics.CGRectGetMinX
import platform.CoreGraphics.CGRectGetWidth
import platform.CoreGraphics.CGRectMake
import platform.CoreImage.*
import platform.QuartzCore.CAShapeLayer
import platform.QuartzCore.kCAFillRuleEvenOdd
import platform.UIKit.*
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue

//CAN OPEN CAMERA

//        val qrCodeReader = QRCodeReader()
//        val imagePicker: UIImagePickerController = UIImagePickerController()
//        imagePicker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
//        imagePicker.delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol,
//            UINavigationControllerDelegateProtocol {
//            override fun imagePickerController(
//                picker: UIImagePickerController,
//                didFinishPickingMediaWithInfo: kotlin.collections.Map<kotlin.Any?, *>
//            ) {
//                val image = didFinishPickingMediaWithInfo?.get(UIImagePickerControllerOriginalImage) as? UIImage
//                println("get Image")
//                if (image != null) {
//                    val result = qrCodeReader.readQRCode(image)
//                    println("result: $result")
//                    listener?.onScanQRCodeResult(qrCodeReader.readQRCode(image) ?: "")
//                }
//                // TODO: Dismiss the image picker
//                picker.dismissViewControllerAnimated(true, null)
//            }
//
//            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
//                picker.dismissViewControllerAnimated(true, null)
//            }
//        }
//
//        viewController?.presentViewController(imagePicker, true, null)

actual class ScanQRCode() {
    private var listener: ScanQRCodeListener? = null
    private var scanQRCodeClick: ScanQRCodeClick? = null
    private var viewController: UIViewController? = null

    var accountId: String = ""
    lateinit var networkType: NetworkType
    var initialBlockchainUid: String? = null

    private var previewView: UIView? = null
    var captureSession: AVCaptureSession? = null

    fun setPreviewView(previewView: UIView) {
        this.previewView = previewView
    }

    fun setViewController(viewController: UIViewController) {
        this.viewController = viewController
    }

    fun setScanQRCodeClick(listener: ScanQRCodeClick) {
        this.scanQRCodeClick = listener
    }
    actual fun scanQRCode(scanQRCodeListener: ScanQRCodeListener) {
        listener = scanQRCodeListener
        scanQRCodeClick?.onScanQRCodeClick(true)
    }

    actual fun scanQRCode(
        scanQRCodeListener: ScanQRCodeListener,
        currentAccountId: String,
        networkType: NetworkType,
        initialBlockchainUid: String?
    ) {
        listener = scanQRCodeListener
        scanQRCodeClick?.onScanQRCodeClick(true)
        accountId = currentAccountId
        this.networkType = networkType
        this.initialBlockchainUid = initialBlockchainUid
    }

    @OptIn(ExperimentalForeignApi::class)
    fun scanQRCodeOnIos() {
        val captureSession = AVCaptureSession()
        this.captureSession = captureSession

        val captureDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) as? AVCaptureDevice
        captureDevice?.let {
            val input = AVCaptureDeviceInput.deviceInputWithDevice(captureDevice!!, error = null) as? AVCaptureDeviceInput
            input?.let {
                captureSession.addInput(input!!)

                val output = AVCaptureMetadataOutput()
                captureSession.addOutput(output)

                output.setMetadataObjectsDelegate(
                    object : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {
                        override fun captureOutput(
                            captureOutput: AVCaptureOutput,
                            didOutputMetadataObjects: List<*>,
                            fromConnection: AVCaptureConnection
                        ) {
                            val metadataObjects = didOutputMetadataObjects as? List<AVMetadataMachineReadableCodeObject>
                            val qrCode = metadataObjects?.firstOrNull { it.type == AVMetadataObjectTypeQRCode }
                            val result = qrCode?.stringValue ?: ""
                            onScanQrCodeResult(result)
                        }
                    },
                    queue = dispatch_get_main_queue()
                )

                output.metadataObjectTypes = listOf(AVMetadataObjectTypeQRCode)

                val previewLayer = AVCaptureVideoPreviewLayer(session = captureSession)

                previewView?.let {
                    previewLayer.frame = previewView!!.layer.bounds
                    previewLayer.backgroundColor = UIColor.clearColor.CGColor
                    previewView?.layer?.backgroundColor = UIColor.clearColor.CGColor
                    previewView?.layer?.addSublayer(previewLayer)

                    // Define the size and position of the square scan area
                    val scanSize = 0.5 * CGRectGetWidth(previewLayer.bounds) // 40% of the width
                    val topMargin = 150.0 // 50dp from the top
                    val leftMargin = (CGRectGetWidth(previewLayer.bounds) - scanSize) / 2.0 // Centered horizontally

                    // Define the scan area in the preview layer's coordinate system
                    val convertedScanRect = CGRectMake(leftMargin, topMargin, scanSize, scanSize)

                    // Convert the scan area to the rectOfInterest's coordinate system
                    val scanRect = CGRectMake(
                        leftMargin / CGRectGetWidth(previewLayer.bounds),
                        topMargin / CGRectGetHeight(previewLayer.bounds),
                        scanSize / CGRectGetWidth(previewLayer.bounds),
                        scanSize / CGRectGetHeight(previewLayer.bounds)
                    )
                    val rectOfInterest = CGRectMake(
                        CGRectGetMinX(scanRect),
                        CGRectGetMaxY(scanRect), // Flip Y coordinate
                        CGRectGetWidth(scanRect),
                        CGRectGetHeight(scanRect)
                    )
//                    output.rectOfInterest = rectOfInterest

                    // Create a view to represent the scan area
                    val scanAreaView = UIView(frame = convertedScanRect)

                    // Create a custom layer to draw the brackets
                    val bracketLayer = CAShapeLayer()
                    bracketLayer.frame = scanAreaView.bounds
                    bracketLayer.strokeColor = UIColor.whiteColor.CGColor
                    val lineWidth = 15.0 / 2
                    val inset: CGFloat = lineWidth / 2
                    bracketLayer.lineWidth = lineWidth
                    bracketLayer.fillColor = UIColor.clearColor.CGColor

                    // Create a path to draw the brackets
                    val bracketPath = UIBezierPath()
                    val cornerRadius: CGFloat = 32.0
                    val bracketSize: CGFloat = cornerRadius * 2

                    // Top-left bracket
                    bracketPath.moveToPoint(CGPointMake(cornerRadius + inset, inset))
                    bracketPath.addLineToPoint(CGPointMake(bracketSize + inset, inset))
                    bracketPath.moveToPoint(CGPointMake(inset, bracketSize + inset))
                    bracketPath.addLineToPoint(CGPointMake(inset, cornerRadius + inset))
                    bracketPath.addArcWithCenter(CGPointMake(cornerRadius + inset, cornerRadius + inset), cornerRadius, kotlin.math.PI, 3 * kotlin.math.PI / 2, true)

                    // Top-right bracket
                    bracketPath.moveToPoint(CGPointMake(CGRectGetWidth(scanAreaView.bounds) - bracketSize - inset, inset))
                    bracketPath.addLineToPoint(CGPointMake(CGRectGetWidth(scanAreaView.bounds) - bracketSize + cornerRadius - inset, inset))
                    bracketPath.addArcWithCenter(CGPointMake(CGRectGetWidth(scanAreaView.bounds) - cornerRadius - inset, cornerRadius + inset), cornerRadius, -kotlin.math.PI / 2, 0.0, true)
                    bracketPath.addLineToPoint(CGPointMake(CGRectGetWidth(scanAreaView.bounds) - inset, bracketSize + inset))

                    // Bottom-right bracket
                    bracketPath.moveToPoint(CGPointMake(CGRectGetWidth(scanAreaView.bounds) - inset, CGRectGetHeight(scanAreaView.bounds) - bracketSize - inset))
                    bracketPath.addLineToPoint(CGPointMake(CGRectGetWidth(scanAreaView.bounds) - inset, CGRectGetHeight(scanAreaView.bounds) - bracketSize + cornerRadius - inset))
                    bracketPath.addArcWithCenter(CGPointMake(CGRectGetWidth(scanAreaView.bounds) - cornerRadius - inset, CGRectGetHeight(scanAreaView.bounds) - cornerRadius - inset), cornerRadius, 0.0, kotlin.math.PI / 2, true)
                    bracketPath.addLineToPoint(CGPointMake(CGRectGetWidth(scanAreaView.bounds) - bracketSize - inset, CGRectGetHeight(scanAreaView.bounds) - inset))

                    // Bottom-left bracket
                    bracketPath.moveToPoint(CGPointMake(bracketSize + inset, CGRectGetHeight(scanAreaView.bounds) - inset))
                    bracketPath.addLineToPoint(CGPointMake(cornerRadius + inset, CGRectGetHeight(scanAreaView.bounds) - inset))
                    bracketPath.addArcWithCenter(CGPointMake(cornerRadius + inset, CGRectGetHeight(scanAreaView.bounds) - cornerRadius - inset), cornerRadius, kotlin.math.PI / 2, kotlin.math.PI, true)
                    bracketPath.addLineToPoint(CGPointMake(inset, CGRectGetHeight(scanAreaView.bounds) - bracketSize - inset))

                    bracketLayer.path = bracketPath.CGPath

                    // Add the custom layer to the scan area view
                    scanAreaView.layer.addSublayer(bracketLayer)
                    previewView?.addSubview(scanAreaView)

                    // Create a mask layer to darken the area outside the scan area
                    val maskLayer = CAShapeLayer()
                    maskLayer.frame = previewLayer.bounds
                    val maskColor = UIColor.colorWithRed(
                        red = 0x48 / 255.0,
                        green = 0x48 / 255.0,
                        blue = 0x48 / 255.0,
                        alpha = 0xB3 / 255.0
                    )
                    maskLayer.fillColor = maskColor.CGColor

                    // Calculate the outer bounds of the brackets
                    val outerBracketInset = inset - (lineWidth / 2) // Adjust by half the line width
                    val outerBracketLeftMargin = leftMargin + outerBracketInset
                    val outerBracketTopMargin = topMargin + outerBracketInset
                    val outerBracketSize = scanSize - 2 * outerBracketInset

                    // Create a path that covers the entire preview layer
                    val path = UIBezierPath.bezierPathWithRect(previewLayer.bounds)

                    // Create a rounded rect path for the scan area that matches the outer edges of the brackets
                    val scanAreaPath = UIBezierPath.bezierPathWithRoundedRect(CGRectMake(outerBracketLeftMargin, outerBracketTopMargin, outerBracketSize, outerBracketSize), cornerRadius + (lineWidth / 2))

                    // Append the scan area path to create a "hole" in the mask
                    path.appendPath(scanAreaPath)

                    // Set the even-odd fill rule
                    path.usesEvenOddFillRule = true

                    // Apply the path to the mask layer
                    maskLayer.path = path.CGPath
                    maskLayer.fillRule = kCAFillRuleEvenOdd

                    previewView?.layer?.addSublayer(maskLayer)

                    if (accountId.isNotEmpty()) {
                        val textLabel = UILabel()
                        textLabel.text = MR.strings.message_scan_qr_code.desc().localized()
                        textLabel.textColor = UIColor.whiteColor
                        textLabel.textAlignment = NSTextAlignmentCenter
                        textLabel.setFrame(CGRectMake(leftMargin, CGRectGetMaxY(scanAreaView.frame) + 10, CGRectGetWidth(previewLayer.bounds) - 2 * leftMargin, 30.0))

                        previewView?.addSubview(textLabel)
                    }
                }

                captureSession.startRunning()
            }
        }
    }

    fun stopScanning() {
        if (captureSession?.isRunning() == true) {
            captureSession?.stopRunning()
        }
    }

    fun startScanning() {
        if (captureSession?.isRunning() == false) {
            captureSession?.startRunning()
        }
    }

    fun onScanQrCodeResult(result: String) {
        listener?.onScanQRCodeResult(result)
        captureSession?.stopRunning()
        scanQRCodeClick?.onScanQRCodeClick(false)
    }

    private fun CGPoint.convertToPointCValue(): CValue<CGPoint> {
        return memScoped {
            val result = alloc<CGPoint>()
            result.x = this@convertToPointCValue.x
            result.y = this@convertToPointCValue.y
            result.readValue()
        }
    }

//    fun scanQRCodeOnIos() {
//        val captureSession = AVCaptureSession()
//
//        val captureDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) as? AVCaptureDevice
//        captureDevice?.let {
//            val input = AVCaptureDeviceInput.deviceInputWithDevice(captureDevice!!, error = null) as? AVCaptureDeviceInput
//            input?.let {
//                captureSession.addInput(input!!)
//
//                val output = AVCaptureMetadataOutput()
//                captureSession.addOutput(output)
//
//                output.setMetadataObjectsDelegate(
//                    object : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {
//                        override fun captureOutput(
//                            captureOutput: AVCaptureOutput,
//                            didOutputMetadataObjects: List<*>,
//                            fromConnection: AVCaptureConnection
//                        ) {
//                            val metadataObjects = didOutputMetadataObjects as? List<AVMetadataMachineReadableCodeObject>
//                            val qrCode = metadataObjects?.firstOrNull { it.type == AVMetadataObjectTypeQRCode }
//                            val result = qrCode?.stringValue ?: ""
//                            println("result $result")
//                            listener?.onScanQRCodeResult(result)
//                            captureSession.stopRunning()
//                            scanQRCodeClick?.onScanQRCodeClick(false)
//                        }
//                    },
//                    queue = dispatch_get_main_queue()
//                )
//
//                output.metadataObjectTypes = listOf(AVMetadataObjectTypeQRCode)
//
//                val previewLayer = AVCaptureVideoPreviewLayer(session = captureSession)
//
//                previewView?.let {
//                    previewLayer.frame = previewView!!.layer.bounds
//                    previewLayer.backgroundColor = UIColor.clearColor.CGColor
//                    previewView?.layer?.backgroundColor = UIColor.clearColor.CGColor
//                    previewView?.layer?.addSublayer(previewLayer)
//
//                    val size = 0.4 // Size of the square scan area
//                    val topMargin = 0.2 // Top margin
//
//                    // Define the scan area (in preview layer's coordinate system)
//                    val scanRect = CGRectMake(
//                        (1.0 - size) / 2.0,
//                        topMargin,
//                        size,
//                        size
//                    )
//
//                    // Convert the scanRect to the rectOfInterest's coordinate system
//                    val rectOfInterest = CGRectMake(
//                        CGRectGetMinX(scanRect),
//                        1.0 - CGRectGetMaxY(scanRect), // Flip Y coordinate
//                        CGRectGetWidth(scanRect),
//                        CGRectGetHeight(scanRect)
//                    )
//                    output.rectOfInterest = rectOfInterest
//
//                    // Convert the scanRect to the preview layer's coordinate system
//                    val convertedScanRect = CGRectMake(
//                        CGRectGetMinX(scanRect) * CGRectGetWidth(previewLayer.bounds),
//                        CGRectGetMinY(scanRect) * CGRectGetHeight(previewLayer.bounds),
//                        CGRectGetWidth(scanRect) * CGRectGetWidth(previewLayer.bounds),
//                        CGRectGetHeight(scanRect) * CGRectGetHeight(previewLayer.bounds)
//                    )
//                    // Create a view to represent the scan area
//                    val scanAreaView = UIView(frame = convertedScanRect)
//                    scanAreaView.layer.borderColor = UIColor.greenColor.CGColor
//                    scanAreaView.layer.borderWidth = 2.0
//                    previewView?.addSubview(scanAreaView)
//
//                    // Create a mask layer to darken the area outside the scan area
//                    val maskLayer = CAShapeLayer()
//                    maskLayer.frame = previewLayer.bounds
//                    maskLayer.fillColor = UIColor.blackColor.CGColor
//                    maskLayer.opacity = 0.6f
//
//                    // Create a path that includes the entire preview layer, minus the scan area
//                    val path = UIBezierPath.bezierPathWithRect(previewLayer.bounds)
//                    val scanAreaPath = UIBezierPath.bezierPathWithRect(convertedScanRect)
//                    path.appendPath(scanAreaPath)
//                    path.usesEvenOddFillRule = true
//                    maskLayer.path = path.CGPath
//                    maskLayer.fillRule = kCAFillRuleEvenOdd
//
//                    previewView?.layer?.addSublayer(maskLayer)
//                }
//
//                captureSession.startRunning()
//            }
//        }
//    }
}

private class QRCodeReader {
    fun readQRCode(image: UIImage): String? {
        val ciImage = image.CIImage ?: CIImage.imageWithCGImage(image.CGImage!!)
        val detector = CIDetector.detectorOfType(
            CIDetectorTypeQRCode,
            context = null,
            options = mapOf(CIDetectorAccuracy to CIDetectorAccuracyHigh)
        )
        val features = detector?.featuresInImage(ciImage) as? List<CIQRCodeFeature>
        val qrCode = features?.firstOrNull()?.messageString
        return qrCode
    }
}
