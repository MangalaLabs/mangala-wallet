//
//  ScannerViewModel.swift
//  iosApp
//
//  Created by Ethan Nguyen on 18/05/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import composeApp

class ScannerViewModel: ObservableObject, ScanQRCodeClick {
    
    @Published var isShowingScanner: Bool = false
    
    func onScanQRCodeClick(isClick: Bool) {
        isShowingScanner = isClick
    }
}
