//
//  SharedCodeKt.swift
//  iosApp
//
//  Created by Ethan Nguyen on 06/05/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

extension SharedCodeKt {
    @objc static func isDeviceSecured() -> Bool {
        let context = LAContext()
        var error: NSError?

        if context.canEvaluatePolicy(.deviceOwnerAuthentication, error: &error) {
            return true
        }

        return false
    }
}
