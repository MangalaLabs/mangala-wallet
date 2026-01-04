//
//  SwitchingRootView.swift
//  iosApp
//
//  Created by Ethan Nguyen on 11/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import composeApp

struct SwitchingRootView: View {
    
    
    @ObservedObject var scannerViewModel: ScannerViewModel
    @ObservedObject var openBrowserViewModel: OpenBrowserViewModel
    
//    @ObservedObject
    var viewModel: ApplicationViewModel
    
//    var mainViewController: MainViewController // TODO: Reenable
    
    private let userDefaultsPublisher = NotificationCenter.default.publisher(for: UserDefaults.didChangeNotification)
    
    private let appActivePublisher = NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)

    let scanQRCode = koin.scanQRCode
    let openBrowser = koin.openBrowser
    
    init(
        scannerViewModel: ScannerViewModel,
//        mainViewController: MainViewController
        viewModel: ApplicationViewModel,
        openBrowserViewModel: OpenBrowserViewModel
    ) {
        self.scannerViewModel = scannerViewModel
//        self.mainViewController = mainViewController
        self.viewModel = viewModel
        self.openBrowserViewModel = openBrowserViewModel
        
        scanQRCode.setScanQRCodeClick(listener: scannerViewModel)
        openBrowser.setOpenBrowserClick(listener: openBrowserViewModel)
    }
    
    var body: some View {
//        NavigationView {
            ZStack {
                Color("NavBar_Background")
                
                ComposeController(viewModel: viewModel)
                
//                NavigationLink(
//                    destination: BrowserViewUI(mainViewController: mainViewController),
//                    isActive: $scannerViewModel.isShowingScanner
//                ) {
//                    EmptyView()
//                }
                
            }.onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
                print("applicationDidBecomeActive22")
            }
            .onReceive(NotificationCenter.default.publisher(for: UIApplication.willEnterForegroundNotification)) { _ in
                print("applicationWillEnterForeground")
            }
            .sheet(isPresented: $scannerViewModel.isShowingScanner) {
//                CodeScannerView(scanQRCode: scanQRCode)
                ScanQRCodeCustomView(scanQRCode: scanQRCode)
//                PaywallView(isPresented: $scannerViewModel.isShowingScanner)
            }
            .ignoresSafeArea(edges: .all)
            .ignoresSafeArea(.keyboard)
//        }
    }
}

struct SwitchingRootView_Previews: PreviewProvider {
    static var previews: some View {
        EmptyView()
    }
}
