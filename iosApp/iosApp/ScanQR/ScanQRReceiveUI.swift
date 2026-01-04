//
//  ScanQRReceiveUI.swift
//  iosApp
//
//  Created by Linh on 03/08/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import composeApp

struct ScanQRReceiveUI: UIViewControllerRepresentable {
    
    let accountId: String
    let networkType: ModelNetworkType
    let initialBlockchainUid: String?
    let onBackPressed: () -> Void
    
    func makeUIViewController(context: Context) -> UIViewController {
        // Initialize and return your UIViewController
        return ScanQRReceiveScreenController(accountId: accountId, networkType: networkType, initialBlockchainUid: initialBlockchainUid, onBackPressed: onBackPressed)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Update the view controller if needed
    }
}
