//
//  BrowserViewUI.swift
//  iosApp
//
//  Created by Ethan Nguyen on 05/07/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct BrowserViewUI: UIViewControllerRepresentable {

   var mainViewController: MainViewController

   init(mainViewController: MainViewController) {
       self.mainViewController = mainViewController
   }

   func makeUIViewController(context: Context) -> MainViewController {
       return self.mainViewController
   }

   func updateUIViewController(_ uiViewController: MainViewController, context: Context) {
       uiViewController.showBars()
       uiViewController.navigationController?.setNavigationBarHidden(false, animated: false)
   }
}

extension UINavigationController: UIGestureRecognizerDelegate {
   override open func viewDidLoad() {
       super.viewDidLoad()
       interactivePopGestureRecognizer?.delegate = self
   }

   public func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
       return viewControllers.count > 1
   }
}
