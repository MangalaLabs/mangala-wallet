//
//  SwitchNetworkController.swift
//  iosApp
//
//  Created by Ethan Nguyen on 31/07/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import composeApp

protocol SwitchNetworkControllerDelegate {
    
    func chainIdCallback(callbackId: Int)
}

class SwitchNetworkController: UIViewController{
    
    private var hostingController: UIViewController? = nil
    
    var delegate: SwitchNetworkControllerDelegate?
    
    init(){
        super.init(nibName: nil, bundle: nil)
        
        hostingController = MainViewControllerKt.selectNetworkController(
            chainIdCallback: { chainIdCallback in
                // callbackId and signHex are available here.
                // You can use them to handle the successful signing of the message.
                DispatchQueue.main.async {
                    print("SwitchNetworkController successful. callbackId: \(chainIdCallback) ")
                    self.delegate?.chainIdCallback(callbackId: Int(truncating: chainIdCallback))
                    self.dismiss(animated: true)
                    // Other code here...
                }
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
