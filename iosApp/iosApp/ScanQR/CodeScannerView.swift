//
//  CodeScannerView.swift
//  iosApp
//
//  Created by Ethan Nguyen on 17/05/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import composeApp

struct CodeScannerView: UIViewRepresentable {
   let scanQRCode: ScanQRCode
        
    func makeUIView(context: Context) -> UIView {
        let view = UIView()
        view.backgroundColor = UIColor.clear
        view.frame = CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
       scanQRCode.setPreviewView(previewView: view)
       scanQRCode.scanQRCodeOnIos()
        
        let overlay = UIView()
        overlay.frame = view.frame
        overlay.layer.borderWidth = 2
        overlay.layer.borderColor = UIColor.white.cgColor
        overlay.backgroundColor = UIColor.clear
        view.addSubview(overlay)
        
        return view
    }
    
    func updateUIView(_ uiView: UIView, context: Context) {
    }
}

