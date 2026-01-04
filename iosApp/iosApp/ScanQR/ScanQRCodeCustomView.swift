//
//  ScanQRCodeCustomView.swift
//  iosApp
//
//  Created by Ethan Nguyen on 18/05/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import composeApp

struct ScanQRCodeCustomView: View {
    
   let scanQRCode: ScanQRCode

    @State private var activeScreen: ActiveScreen?
    @State var isPresentingScanner: Bool = false
    @State private var selectedImage: UIImage?
    @State private var isImagePickerPresented: Bool = false

    var body: some View {
        ZStack {
            if isPresentingScanner {
             CodeScannerView(scanQRCode: scanQRCode)
               .onAppear {
                   scanQRCode.startScanning()
               }
               .onDisappear {
                   scanQRCode.stopScanning()
               }
            }

            VStack {
                Spacer()
                
                Spacer()
                
                HStack {
                    VStack {
                        Button(action: {
                            activeScreen = .receive
                        }) {
                            Image("receive")
                                .padding(18)
                                .background(Color("QrScanButtonBackground"))
                                .clipShape(Circle())
                                .frame(width: 64, height: 64)
                                .cornerRadius(32)
                        }
                       .disabled(scanQRCode.accountId.isEmpty)
                        
                       let allReceiveResource = MR.strings().all_receive
                       Text(LocalizedStringKey(allReceiveResource.resourceId), bundle: allReceiveResource.bundle)
                           .foregroundColor(.white)
                           .padding(.top, 8)
                    }
                    .padding(.leading, 60)
                    .padding(.bottom, 90)
                    
                    Spacer()
                    
                    VStack {
                        Button(action: {
                            activeScreen = .imagePicker
                            isImagePickerPresented = true
                        }) {
                            Image("album")
                                .padding(18)
                                .background(Color("QrScanButtonBackground"))
                                .clipShape(Circle())
                                .frame(width: 64, height: 64)
                                .cornerRadius(32)
                        }
                       let buttonScanQrCodeAlbumResource = MR.strings().button_scan_qr_code_album
                       Text(LocalizedStringKey(buttonScanQrCodeAlbumResource.resourceId), bundle: buttonScanQrCodeAlbumResource.bundle)
                           .foregroundColor(.white)
                           .padding(.top, 8)
                    }
                    .padding(.trailing, 60)
                    .padding(.bottom, 90)
                }
            }
        }
        .onAppear {
            self.isPresentingScanner = true
        }
        .fullScreenCover(item: $activeScreen) { item in
           switch item {
           case .imagePicker:
                ImagePickerView() { image in
                    activeScreen = nil
                    let scanner = QRCodeScanner()
                    let result = scanner.scanQRCode(image: image)
                    scanQRCode.onScanQrCodeResult(result: result)
                }
           case .receive:
               ScanQRReceiveUI(
                   accountId: scanQRCode.accountId,
                   networkType: scanQRCode.networkType,
                   initialBlockchainUid: scanQRCode.initialBlockchainUid
               ) {
                   activeScreen = nil
               }
           }
        }
    }
}

enum ActiveScreen: Identifiable {
    case imagePicker, receive

    var id: Int {
        hashValue
    }
}
