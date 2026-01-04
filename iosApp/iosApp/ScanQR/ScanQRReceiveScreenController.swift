//
//  ScanQRReceiveScreenController.swift
//  iosApp
//
//  Created by Linh on 03/08/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import composeApp

class ScanQRReceiveScreenController: UIViewController {
    
    private var hostingController: UIViewController? = nil
    
    var delegate: SwitchNetworkControllerDelegate?
    
    init(accountId: String, networkType: ModelNetworkType, initialBlockchainUid: String?, onBackPressed: @escaping () -> Void) {
        super.init(nibName: nil, bundle: nil)
        
        hostingController = MainViewControllerKt.scanQrCodeReceiveController(
            accountId: accountId,
            networkType: networkType,
            initialBlockchainUid: initialBlockchainUid,
            onBackPressed: {
                onBackPressed()
            }
        )
    }
    
    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
//        view.setNeedsLayout()
        
        if hostingController?.parent == nil {
            addChild(hostingController!)
//            composeController.view.frame = view.bounds
            hostingController?.view.backgroundColor = UIColor.clear
            
            view.addSubview(hostingController!.view)
            
            view.backgroundColor = UIColor.clear
            
            hostingController?.view.translatesAutoresizingMaskIntoConstraints = false
            hostingController?.view.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
            hostingController?.view.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
            hostingController?.view.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
            hostingController?.view.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
                    
            
            hostingController?.didMove(toParent: self)
        }
    }
}
