//
//  UnlockPinScreenController.swift
//  iosApp
//
//  Created by Ethan Nguyen on 07/07/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import composeApp
import UIKit
import SwiftUI

struct UnlockPinScreenController: UIViewControllerRepresentable {
    
    @Binding var isSuccess: Bool

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.unlockScreenController { kotlinBoolean in
            DispatchQueue.main.async {
                self.isSuccess = kotlinBoolean.boolValue
            }
        }
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
