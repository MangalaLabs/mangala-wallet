//
//  BackgroundCrashWorkaroundController.swift
//  iosApp
//
//  Created by Ethan Nguyen on 11/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import composeApp

class BackgroundCrashWorkaroundController: UIViewController {
    
    let viewModel: ApplicationViewModel
    let composeController: UIViewController
    
    init(viewModel: ApplicationViewModel) {
        self.viewModel = viewModel
        
        composeController = MainViewControllerKt.MainViewController(viewModel: viewModel)
        
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if composeController.parent == nil {
            addChild(composeController)
//            composeController.view.frame = view.bounds
            composeController.view.backgroundColor = UIColor.clear
            
            view.addSubview(composeController.view)
            
            view.backgroundColor = UIColor.clear
            
            composeController.view.translatesAutoresizingMaskIntoConstraints = false
            composeController.view.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
            composeController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
            composeController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
            composeController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
                    
            
            composeController.didMove(toParent: self)
        }
    }
}
