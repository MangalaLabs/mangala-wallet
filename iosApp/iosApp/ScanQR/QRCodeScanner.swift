//
//  QRCodeScanner.swift
//  iosApp
//
//  Created by Linh on 03/08/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI

class QRCodeScanner: ObservableObject {
    
    func scanQRCode(image: UIImage?) -> String {
        guard let image = image, let ciImage = CIImage(image: image) else { return "" }
        let context = CIContext()
        let options = [CIDetectorAccuracy: CIDetectorAccuracyHigh]
        guard let detector = CIDetector(ofType: CIDetectorTypeQRCode, context: context, options: options) else { return "" }
        let features = detector.features(in: ciImage)

        
        if let qrCode = features.first as? CIQRCodeFeature, let qrCodeString = qrCode.messageString {
            return qrCodeString
        }
        
        return ""
    }
}
