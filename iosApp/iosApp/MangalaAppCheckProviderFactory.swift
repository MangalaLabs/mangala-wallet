//
//  MangalaAppCheckProviderFactory.swift
//  iosApp
//
//  Created by Macgala on 16/12/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import FirebaseAppCheck
import Foundation
import FirebaseCore

class MangalaAppCheckProviderFactory: NSObject, AppCheckProviderFactory {
  func createProvider(with app: FirebaseApp) -> AppCheckProvider? {
    return AppAttestProvider(app: app)
  }
}
