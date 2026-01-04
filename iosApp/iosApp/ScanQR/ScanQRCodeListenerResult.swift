//
//  ScanQRCodeListenerResult.swift
//  iosApp
//
//  Created by Ethan Nguyen on 18/05/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import composeApp

class ScanQRCodeListenerResult: ScanQRCodeListener{

   func onScanQRCodeResult(result: String) {
       print("result " + result)
   }
    
}
